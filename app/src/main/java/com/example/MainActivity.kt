package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.example.data.AppSwipeDatabase
import com.example.data.SwipeRepository
import com.example.ui.AppSwipeApp
import com.example.ui.SwipeViewModel
import com.example.ui.SwipeViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    val database = AppSwipeDatabase.getDatabase(this)
    val repository = SwipeRepository(this, database)
    val settingsManager = com.example.data.SettingsManager(this)
    val viewModel = ViewModelProvider(
      this, 
      SwipeViewModelFactory(repository, settingsManager)
    )[SwipeViewModel::class.java]
    
    setContent {
      val themePref by viewModel.themePreference.collectAsStateWithLifecycle()
      val darkTheme = when (themePref) {
          "Light" -> false
          "Dark" -> true
          else -> androidx.compose.foundation.isSystemInDarkTheme()
      }

      MyApplicationTheme(darkTheme = darkTheme, dynamicColor = false) {
        AppSwipeApp(viewModel)
      }
    }
  }
}

