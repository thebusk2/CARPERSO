package com.autolauncher.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "autolauncher_prefs")

class Preferences(private val context: Context) {

    companion object {
        val KEY_INTERFACE_COLOR = longPreferencesKey("interface_color")
        val KEY_VEHICLE_ID = stringPreferencesKey("vehicle_id")
        val KEY_VEHICLE_COLOR = intPreferencesKey("vehicle_color_index")
        val KEY_APP_ORDER = stringPreferencesKey("app_order") // JSON de lista de paquetes
        val KEY_PINNED_APPS = stringPreferencesKey("pinned_apps") // JSON lista de paquetes del dock
        val KEY_DARK_MODE = intPreferencesKey("dark_mode") // 0=auto, 1=light, 2=dark
        val KEY_SHOW_CLOCK = intPreferencesKey("show_clock") // 0=no, 1=si
        val KEY_GRID_COLUMNS = intPreferencesKey("grid_columns") // 4 o 5

        // Colores de interfaz predefinidos (ARGB en Long)
        val PRESET_COLORS = listOf(
            0xFF1565C0, // Azul Profundo (default)
            0xFF880E4F, // Rojo Vino
            0xFF1B5E20, // Verde Oscuro
            0xFF4A148C, // Púrpura
            0xFFE65100, // Naranja
            0xFF004D40, // Verde Teal
            0xFF263238, // Gris Azulado (oscuro)
            0xFFB71C1C, // Rojo Deportivo
            0xFF0D47A1, // Azul Marino
            0xFF37474F, // Gris Acero
        )
    }

    val interfaceColor: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[KEY_INTERFACE_COLOR] ?: PRESET_COLORS[0]
    }

    val vehicleId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_VEHICLE_ID] ?: "toyota_corolla"
    }

    val vehicleColorIndex: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_VEHICLE_COLOR] ?: 0
    }

    val appOrderJson: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_APP_ORDER] ?: ""
    }

    val pinnedAppsJson: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_PINNED_APPS] ?: ""
    }

    val darkMode: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_DARK_MODE] ?: 0
    }

    val gridColumns: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_GRID_COLUMNS] ?: 4
    }

    suspend fun setInterfaceColor(color: Long) =
        context.dataStore.edit { it[KEY_INTERFACE_COLOR] = color }

    suspend fun setVehicleId(id: String) =
        context.dataStore.edit { it[KEY_VEHICLE_ID] = id }

    suspend fun setVehicleColorIndex(index: Int) =
        context.dataStore.edit { it[KEY_VEHICLE_COLOR] = index }

    suspend fun setAppOrderJson(json: String) =
        context.dataStore.edit { it[KEY_APP_ORDER] = json }

    suspend fun setPinnedAppsJson(json: String) =
        context.dataStore.edit { it[KEY_PINNED_APPS] = json }

    suspend fun setDarkMode(mode: Int) =
        context.dataStore.edit { it[KEY_DARK_MODE] = mode }

    suspend fun setGridColumns(cols: Int) =
        context.dataStore.edit { it[KEY_GRID_COLUMNS] = cols }
}
