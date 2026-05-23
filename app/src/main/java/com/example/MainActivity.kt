package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
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
      MyApplicationTheme {
        AppSwipeApp(viewModel)
      }
    }
  }
}

