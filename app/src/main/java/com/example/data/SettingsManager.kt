package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    companion object {
        val IS_RIGHT_SWIPE_UNINSTALL = booleanPreferencesKey("is_right_swipe_uninstall")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val THEME_PREFERENCE = stringPreferencesKey("theme_preference")
        val SHOW_STORAGE_SIZE = booleanPreferencesKey("show_storage_size")
        val SHOW_LAST_TIME_USED = booleanPreferencesKey("show_last_time_used")
        val ANIMATION_SPEED = stringPreferencesKey("animation_speed")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val isOnboardingCompletedFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[ONBOARDING_COMPLETED] ?: false }

    val isRightSwipeUninstallFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_RIGHT_SWIPE_UNINSTALL] ?: true
        }

    val hapticFeedbackFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[HAPTIC_FEEDBACK] ?: true }

    val sortOrderFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[SORT_ORDER] ?: "Size" }

    val themePreferenceFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[THEME_PREFERENCE] ?: "System" }

    val showStorageSizeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[SHOW_STORAGE_SIZE] ?: true }

    val showLastTimeUsedFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[SHOW_LAST_TIME_USED] ?: true }

    val animationSpeedFlow: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[ANIMATION_SPEED] ?: "Default" }

    suspend fun setRightSwipeUninstall(isUninstall: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_RIGHT_SWIPE_UNINSTALL] = isUninstall
        }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { it[HAPTIC_FEEDBACK] = enabled }
    }

    suspend fun setSortOrder(order: String) {
        context.dataStore.edit { it[SORT_ORDER] = order }
    }

    suspend fun setThemePreference(theme: String) {
        context.dataStore.edit { it[THEME_PREFERENCE] = theme }
    }

    suspend fun setShowStorageSize(show: Boolean) {
        context.dataStore.edit { it[SHOW_STORAGE_SIZE] = show }
    }

    suspend fun setShowLastTimeUsed(show: Boolean) {
        context.dataStore.edit { it[SHOW_LAST_TIME_USED] = show }
    }

    suspend fun setAnimationSpeed(speed: String) {
        context.dataStore.edit { it[ANIMATION_SPEED] = speed }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }
}
