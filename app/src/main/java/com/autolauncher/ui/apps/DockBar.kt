package com.autolauncher.ui.apps

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.autolauncher.data.AppItem

/**
 * Barra de dock inferior con las apps ancladas.
 * Muestra hasta 7 apps; con edición activada permite quitar apps y reordenarlas.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DockBar(
    pinnedApps: List<AppItem>,
    isEditing: Boolean,
    onLaunch: (String) -> Unit,
    onUnpin: (String) -> Unit,
    onReorder: (from: Int, to: Int) -> Unit,
    onToggleEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(80.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Apps ancladas en el dock
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(
                    items = pinnedApps,
                    key = { _, app -> app.packageName }
                ) { index, app ->
                    DockAppItem(
                        app = app,
                        isEditing = isEditing,
                        onLaunch = { onLaunch(app.packageName) },
                        onUnpin = { onUnpin(app.packageName) },
                        modifier = Modifier.animateItemPlacement()
                    )
                }

                // Placeholder vacío si no hay apps
                if (pinnedApps.isEmpty()) {
                    item {
                        DockEmptyHint()
                    }
                }
            }

            // Botón de edición del dock
            IconButton(
                onClick = onToggleEdit,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                    contentDescription = if (isEditing) "Finalizar edición" else "Editar dock",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DockAppItem(
    app: AppItem,
    isEditing: Boolean,
    onLaunch: () -> Unit,
    onUnpin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopEnd
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .combinedClickable(
                    onClick = { if (!isEditing) onLaunch() },
                    onLongClick = onUnpin
                )
                .padding(4.dp)
        ) {
            AppIcon(
                drawable = app.icon,
                modifier = Modifier.size(44.dp)
            )
            Text(
                text = app.label,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(52.dp)
            )
        }

        // Botón de quitar (modo edición)
        if (isEditing) {
            IconButton(
                onClick = onUnpin,
                modifier = Modifier
                    .size(20.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(MaterialTheme.colorScheme.error)
                    .offset(x = 4.dp, y = (-4).dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    null,
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun DockEmptyHint() {
    Box(
        modifier = Modifier
            .width(200.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Add,
                null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
            Text(
                "Abre Apps → mantén presionado → pin",
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
