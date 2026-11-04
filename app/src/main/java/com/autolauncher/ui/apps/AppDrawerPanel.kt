package com.autolauncher.ui.apps

import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.autolauncher.data.AppItem

/**
 * Cajón de aplicaciones con:
 * - Grid reordenable mediante arrastre largo
 * - Apps ancladas al dock
 * - Modo edición para arrastrar y fijar apps
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppDrawerPanel(
    apps: List<AppItem>,
    pinnedApps: List<AppItem>,
    isEditing: Boolean,
    onLaunch: (String) -> Unit,
    onPin: (String) -> Unit,
    onUnpin: (String) -> Unit,
    onReorder: (from: Int, to: Int) -> Unit,
    onToggleEdit: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredApps = remember(apps, searchQuery) {
        if (searchQuery.isBlank()) apps
        else apps.filter { it.label.contains(searchQuery, ignoreCase = true) }
    }

    Box(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // ── Barra de título ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Aplicaciones",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    IconButton(onClick = onToggleEdit) {
                        Icon(
                            if (isEditing) Icons.Default.Apps else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Ver normal" else "Editar orden"
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Cerrar")
                    }
                }
            }

            if (isEditing) {
                Text(
                    "Mantén presionado y arrastra para reordenar · Toca el pin para anclar al dock",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // ── Búsqueda ──
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar app...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(12.dp))

            // ── Grid de apps ──
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(
                    items = filteredApps,
                    key = { _, app -> app.packageName }
                ) { index, app ->
                    val isPinned = pinnedApps.any { it.packageName == app.packageName }
                    AppIconItem(
                        app = app,
                        isEditing = isEditing,
                        isPinned = isPinned,
                        onLaunch = { onLaunch(app.packageName) },
                        onPin = {
                            if (isPinned) onUnpin(app.packageName) else onPin(app.packageName)
                        },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconItem(
    app: AppItem,
    isEditing: Boolean,
    isPinned: Boolean,
    onLaunch: () -> Unit,
    onPin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .combinedClickable(
                onClick = { if (!isEditing) onLaunch() else onPin() },
                onLongClick = { onPin() }
            )
            .padding(4.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            AppIcon(
                drawable = app.icon,
                modifier = Modifier
                    .size(52.dp)
                    .scale(if (isEditing) 0.9f else 1f)
            )
            if (isPinned) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .offset(x = 2.dp, y = (-2).dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PushPin,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
            if (isEditing && !isPinned) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .offset(x = 2.dp, y = (-2).dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PushPin,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = app.label,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Renderiza el ícono de la app desde su Drawable usando un Bitmap */
@Composable
fun AppIcon(
    drawable: Drawable,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(drawable) {
        drawable.toBitmap(width = 128, height = 128).asImageBitmap()
    }
    Image(
        bitmap = bitmap,
        contentDescription = null,
        modifier = modifier.clip(RoundedCornerShape(12.dp))
    )
}
