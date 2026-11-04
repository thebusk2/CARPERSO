package com.autolauncher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/** Item de aplicación instalada */
data class AppItem(
    val packageName: String,
    val label: String,
    val icon: Drawable,
    /** Posición en el orden del usuario (Int.MAX_VALUE = al final) */
    val order: Int = Int.MAX_VALUE,
    /** Si está anclada en el dock */
    val isPinned: Boolean = false
)

class AppRepository(private val context: Context) {

    private val pm: PackageManager = context.packageManager

    /** Lista en vivo de todas las apps lanzables instaladas */
    val installedApps: Flow<List<AppItem>> = flow {
        emit(loadApps())
    }.flowOn(Dispatchers.IO)

    fun loadApps(): List<AppItem> {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        return pm.queryIntentActivities(intent, 0)
            .filter { it.activityInfo.packageName != context.packageName } // Excluir el launcher mismo
            .map { resolveInfo ->
                AppItem(
                    packageName = resolveInfo.activityInfo.packageName,
                    label = resolveInfo.loadLabel(pm).toString(),
                    icon = resolveInfo.loadIcon(pm)
                )
            }
            .sortedBy { it.label.lowercase() }
    }

    /** Lanza una aplicación por su nombre de paquete */
    fun launch(packageName: String) {
        pm.getLaunchIntentForPackage(packageName)?.let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /** Aplica el orden guardado en preferencias a la lista de apps */
    fun applyOrder(apps: List<AppItem>, orderedPackages: List<String>): List<AppItem> {
        if (orderedPackages.isEmpty()) return apps
        val indexMap = orderedPackages.withIndex().associate { (i, pkg) -> pkg to i }
        return apps.sortedBy { indexMap[it.packageName] ?: Int.MAX_VALUE }
    }

    /** Serializa lista de paquetes a JSON simple */
    fun packagesToJson(packages: List<String>): String {
        if (packages.isEmpty()) return "[]"
        return "[\"" + packages.joinToString("\",\"") + "\"]"
    }

    /** Deserializa JSON simple de paquetes */
    fun jsonToPackages(json: String): List<String> {
        if (json.isBlank() || json == "[]") return emptyList()
        return json.trim().removePrefix("[\"").removeSuffix("\"]")
            .split("\",\"")
            .filter { it.isNotBlank() }
    }
}
