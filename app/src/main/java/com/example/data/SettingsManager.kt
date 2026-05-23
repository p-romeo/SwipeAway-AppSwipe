package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    companion object {
        val IS_RIGHT_SWIPE_UNINSTALL = booleanPreferencesKey("is_right_swipe_uninstall")
    }

    val isRightSwipeUninstallFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_RIGHT_SWIPE_UNINSTALL] ?: true
        }

    suspend fun setRightSwipeUninstall(isUninstall: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_RIGHT_SWIPE_UNINSTALL] = isUninstall
        }
    }
}
