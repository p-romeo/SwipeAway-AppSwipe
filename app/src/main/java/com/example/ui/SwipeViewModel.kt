package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppSwipeDatabase
import com.example.data.SwipeRecord
import com.example.data.SwipeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class SwipeViewModel(
    private val repository: SwipeRepository,
    private val settingsManager: com.example.data.SettingsManager
) : ViewModel() {

    private val _undoMessage = MutableSharedFlow<String>(replay = 0)
    val undoMessage: SharedFlow<String> = _undoMessage.asSharedFlow()

    private val _uninstallStatus = MutableSharedFlow<String>(replay = 0)
    val uninstallStatus: SharedFlow<String> = _uninstallStatus.asSharedFlow()

    val isIgnoreSystemApps = MutableStateFlow(repository.isIgnoreSystemApps())

    fun toggleIgnoreSystemApps() {
        val newVal = !isIgnoreSystemApps.value
        repository.setIgnoreSystemApps(newVal)
        isIgnoreSystemApps.value = newVal
    }

    val isRightSwipeUninstall: StateFlow<Boolean> = settingsManager.isRightSwipeUninstallFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun toggleRightSwipeUninstall() {
        viewModelScope.launch {
            settingsManager.setRightSwipeUninstall(!isRightSwipeUninstall.value)
        }
    }

    // UI States
    val allRecords: StateFlow<List<SwipeRecord>> = repository.allRecordsFlow
        .combine(isIgnoreSystemApps) { list, ignore ->
            if (ignore) list.filter { !it.isSystemApp } else list
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingApps: StateFlow<List<SwipeRecord>> = repository.pendingRecordsFlow
        .combine(isIgnoreSystemApps) { list, ignore ->
            if (ignore) list.filter { !it.isSystemApp } else list
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val keptApps: StateFlow<List<SwipeRecord>> = repository.keptRecordsFlow
        .combine(isIgnoreSystemApps) { list, ignore ->
            if (ignore) list.filter { !it.isSystemApp } else list
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val queuedApps: StateFlow<List<SwipeRecord>> = repository.queuedRecordsFlow
        .combine(isIgnoreSystemApps) { list, ignore ->
            if (ignore) list.filter { !it.isSystemApp } else list
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uninstalledApps: StateFlow<List<SwipeRecord>> = repository.uninstalledRecordsFlow
        .combine(isIgnoreSystemApps) { list, ignore ->
            if (ignore) list.filter { !it.isSystemApp } else list
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Statistics state derived from the flows
    val storageStatistics = allRecords.map { list ->
        val totalApps = list.size
        val pendingCount = list.count { it.swipeStatus == "PENDING" }
        val keptCount = list.count { it.swipeStatus == "KEPT" }
        val queuedCount = list.count { it.swipeStatus == "QUEUED" }
        val uninstalledCount = list.count { it.swipeStatus == "UNINSTALLED" }
        
        val totalStorageMb = list.sumOf { it.storageSizeMb }
        val queuedStorageMb = list.filter { it.swipeStatus == "QUEUED" }.sumOf { it.storageSizeMb }
        val savedStorageMb = list.filter { it.swipeStatus == "KEPT" }.sumOf { it.storageSizeMb }
        val uninstalledStorageMb = list.filter { it.swipeStatus == "UNINSTALLED" }.sumOf { it.storageSizeMb }

        StorageStats(
            totalApps = totalApps,
            pendingCount = pendingCount,
            keptCount = keptCount,
            queuedCount = queuedCount,
            uninstalledCount = uninstalledCount,
            totalStorageMb = totalStorageMb,
            queuedStorageMb = queuedStorageMb,
            savedStorageMb = savedStorageMb,
            uninstalledStorageMb = uninstalledStorageMb
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StorageStats())

    init {
        viewModelScope.launch {
            repository.syncInstalledApps()
        }
    }

    fun syncApps() {
        viewModelScope.launch {
            repository.syncInstalledApps()
        }
    }

    fun keepApp(packageName: String) {
        viewModelScope.launch {
            repository.keepApp(packageName)
        }
    }

    fun queueUninstall(packageName: String) {
        viewModelScope.launch {
            repository.queueUninstall(packageName)
        }
    }

    fun updateCategory(packageName: String, category: String) {
        viewModelScope.launch {
            repository.updateCategory(packageName, category)
        }
    }

    fun undoLastSwipe() {
        viewModelScope.launch {
            val undone = repository.undoLastSwipe()
            if (undone != null) {
                _undoMessage.emit("Brought back '${undone.appName}'")
            } else {
                _undoMessage.emit("No action to undo")
            }
        }
    }

    fun resetAll() {
        viewModelScope.launch {
            repository.resetSwipes()
        }
    }

    fun uninstallMany(context: Context, records: List<SwipeRecord>) {
        viewModelScope.launch {
            records.forEach { record ->
                try {
                    val intent = Intent(Intent.ACTION_DELETE).apply {
                        data = Uri.parse("package:${record.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    _uninstallStatus.emit("Uninstalling ${record.appName}...")
                    delay(250) // Small delay to avoid OEM intent coalescing
                } catch (e: Exception) {
                    _uninstallStatus.emit("Failed to uninstall: ${e.localizedMessage}")
                }
            }
        }
    }

    fun handoffUninstall(context: Context, record: SwipeRecord) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:${record.packageName}")
                putExtra(Intent.EXTRA_RETURN_RESULT, true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            
            // Note: Since ACTION_DELETE callback behaves asynchronously, we can let user click a button to 
            // set it as uninstalled, or check again on app sync!
            viewModelScope.launch {
                _uninstallStatus.emit("Uninstalling ${record.appName}...")
            }
        } catch (e: Exception) {
            viewModelScope.launch {
                _uninstallStatus.emit("Failed to uninstall: ${e.localizedMessage}")
            }
        }
    }


    fun buildInlineInstallIntent(context: Context, appPackageName: String): Intent? {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setPackage("com.android.vending")
            data = Uri.parse("https://play.google.com/d?id=$appPackageName")
            putExtra("overlay", true)
            putExtra("callerId", context.packageName)
        }
        val packageManager = context.packageManager
        return if (intent.resolveActivity(packageManager) != null) {
            intent
        } else {
            null
        }
    }

    fun launchFallbackStore(context: Context, appPackageName: String) {
        val fallbackIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(fallbackIntent)
        } catch (e: Exception) {
            viewModelScope.launch {
                _uninstallStatus.emit("Failed to open Play Store: ${e.localizedMessage}")
            }
        }
    }

    fun removeFromQueue(packageName: String) {
        viewModelScope.launch {
            repository.skipApp(packageName)
        }
    }
}

data class StorageStats(
    val totalApps: Int = 0,
    val pendingCount: Int = 0,
    val keptCount: Int = 0,
    val queuedCount: Int = 0,
    val uninstalledCount: Int = 0,
    val totalStorageMb: Int = 0,
    val queuedStorageMb: Int = 0,
    val savedStorageMb: Int = 0,
    val uninstalledStorageMb: Int = 0
)

class SwipeViewModelFactory(
    private val repository: SwipeRepository,
    private val settingsManager: com.example.data.SettingsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SwipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SwipeViewModel(repository, settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
