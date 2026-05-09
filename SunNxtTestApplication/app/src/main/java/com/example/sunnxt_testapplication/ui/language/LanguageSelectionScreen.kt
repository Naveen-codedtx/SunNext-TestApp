package com.example.sunnxt_testapplication.ui.language

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.example.sunnxt_testapplication.core.device.DeviceType
import com.example.sunnxt_testapplication.core.device.FoldState
import com.example.sunnxt_testapplication.data.model.Language
import com.example.sunnxt_testapplication.ui.theme.SunNxtCardBackground
import com.example.sunnxt_testapplication.ui.theme.SunNxtRed
import com.example.sunnxt_testapplication.ui.theme.SunNxtRedDark
import com.example.sunnxt_testapplication.ui.theme.SunNxtRedLight
import com.example.sunnxt_testapplication.ui.theme.SunNxtTextPrimary
import com.example.sunnxt_testapplication.ui.theme.SunNxtTextSecondary
import com.example.sunnxt_testapplication.ui.theme.SunNxtTile
import com.example.sunnxt_testapplication.ui.theme.SunNxtTileFocused
import com.example.sunnxt_testapplication.ui.theme.SunNxtTileSelected

// ─────────────────────────────────────────────────────────────────────────────
// Entry point
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun LanguageSelectionScreen(
    deviceType: DeviceType,
    viewModel: LanguageViewModel = viewModel(factory = LanguageViewModel.Factory),
    onLanguagesSelected: (Set<String>) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (deviceType) {
        is DeviceType.TV -> TvLanguageSelectionScreen(
            uiState = uiState,
            onToggle = viewModel::toggleLanguage,
            onDone = {
                (uiState as? LanguageUiState.Success)?.let { onLanguagesSelected(it.selectedIds) }
            },
            onRetry = viewModel::fetchLanguages,
        )
        is DeviceType.Foldable -> FoldableLanguageSelectionScreen(
            foldState = deviceType.foldState,
            uiState = uiState,
            onToggle = viewModel::toggleLanguage,
            onDone = {
                (uiState as? LanguageUiState.Success)?.let { onLanguagesSelected(it.selectedIds) }
            },
            onRetry = viewModel::fetchLanguages,
        )
        else -> MobileTabletLanguageSelectionScreen(
            isTablet = deviceType is DeviceType.Tablet,
            uiState = uiState,
            onToggle = viewModel::toggleLanguage,
            onDone = {
                (uiState as? LanguageUiState.Success)?.let { onLanguagesSelected(it.selectedIds) }
            },
            onRetry = viewModel::fetchLanguages,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Mobile / Tablet
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun MobileTabletLanguageSelectionScreen(
    isTablet: Boolean,
    uiState: LanguageUiState,
    onToggle: (String) -> Unit,
    onDone: () -> Unit,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF7A1020), Color(0xFF4A0010), Color(0xFF0D0000))
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0x44000000)))

        Box(
            modifier = Modifier
                .widthIn(max = if (isTablet) 820.dp else 440.dp)
                .fillMaxWidth(if (isTablet) 0.85f else 0.92f)
                .clip(RoundedCornerShape(16.dp))
                .background(SunNxtCardBackground)
                .padding(horizontal = 24.dp, vertical = 28.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                SunNxtLogo(size = if (isTablet) 72.dp else 64.dp)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Welcome!",
                    color = SunNxtTextPrimary,
                    fontSize = if (isTablet) 22.sp else 20.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Select Your Content Language",
                    color = SunNxtTextSecondary,
                    fontSize = if (isTablet) 15.sp else 14.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(24.dp))

                when (uiState) {
                    is LanguageUiState.Loading -> {
                        CircularProgressIndicator(
                            color = SunNxtRed,
                            modifier = Modifier.padding(vertical = 32.dp),
                        )
                    }
                    is LanguageUiState.Error -> {
                        ErrorContent(message = uiState.message, onRetry = onRetry)
                    }
                    is LanguageUiState.Success -> {
                        if (isTablet) {
                            TabletLanguageRow(
                                languages = uiState.languages,
                                selectedIds = uiState.selectedIds,
                                onToggle = onToggle,
                            )
                        } else {
                            MobileLanguageGrid(
                                languages = uiState.languages,
                                selectedIds = uiState.selectedIds,
                                onToggle = onToggle,
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                        DoneButton(
                            enabled = uiState.selectedIds.isNotEmpty(),
                            onClick = onDone,
                            isLarge = isTablet,
                        )
                    }
                }
            }
        }
    }
}

/** Horizontal single-row layout for tablets — tile width is computed from available space. */
@Composable
private fun TabletLanguageRow(
    languages: List<Language>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit,
) {
    val gapCount = (languages.size - 1).coerceAtLeast(0)
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val tileSize = ((maxWidth - (gapCount * 8).dp) / languages.size.coerceAtLeast(1))
            .coerceAtLeast(60.dp)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            languages.forEach { language ->
                LanguageTile(
                    language = language,
                    isSelected = language.id in selectedIds,
                    onClick = { onToggle(language.id) },
                    modifier = Modifier.size(tileSize),
                )
            }
        }
    }
}

/** 4-column grid for phones — each cell is square via aspectRatio. */
@Composable
private fun MobileLanguageGrid(
    languages: List<Language>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit,
) {
    val columnCount = 4
    val gapDp = 8.dp
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val totalGapWidth = gapDp * (columnCount - 1)
        val tileSize = ((maxWidth - totalGapWidth) / columnCount).coerceAtLeast(56.dp)
        val rows = (languages.size + columnCount - 1) / columnCount
        val gridHeight = tileSize * rows + gapDp * (rows - 1)

        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            horizontalArrangement = Arrangement.spacedBy(gapDp),
            verticalArrangement = Arrangement.spacedBy(gapDp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(gridHeight),
        ) {
            items(languages, key = { it.id }) { language ->
                LanguageTile(
                    language = language,
                    isSelected = language.id in selectedIds,
                    onClick = { onToggle(language.id) },
                    modifier = Modifier.size(tileSize),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Foldable
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FoldableLanguageSelectionScreen(
    foldState: FoldState,
    uiState: LanguageUiState,
    onToggle: (String) -> Unit,
    onDone: () -> Unit,
    onRetry: () -> Unit,
) {
    when (foldState) {
        FoldState.HALF_OPEN -> HalfOpenFoldableLayout(
            uiState = uiState,
            onToggle = onToggle,
            onDone = onDone,
            onRetry = onRetry,
        )
        else -> MobileTabletLanguageSelectionScreen(
            isTablet = true,
            uiState = uiState,
            onToggle = onToggle,
            onDone = onDone,
            onRetry = onRetry,
        )
    }
}

@Composable
private fun HalfOpenFoldableLayout(
    uiState: LanguageUiState,
    onToggle: (String) -> Unit,
    onDone: () -> Unit,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF7A1020), Color(0xFF0D0000)))
            ),
    ) {
        // Top pane: branding
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0x22000000)),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SunNxtLogo(size = 72.dp)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Welcome!",
                    color = SunNxtTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "Select Your Content Language",
                    color = SunNxtTextSecondary,
                    fontSize = 14.sp,
                )
            }
        }

        // Fold-line indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color(0x44FFFFFF))
        )

        // Bottom pane: selection
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(SunNxtCardBackground)
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            when (uiState) {
                is LanguageUiState.Loading -> CircularProgressIndicator(color = SunNxtRed)
                is LanguageUiState.Error -> ErrorContent(uiState.message, onRetry)
                is LanguageUiState.Success -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    MobileLanguageGrid(uiState.languages, uiState.selectedIds, onToggle)
                    Spacer(Modifier.height(16.dp))
                    DoneButton(enabled = uiState.selectedIds.isNotEmpty(), onClick = onDone)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TV
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvLanguageSelectionScreen(
    uiState: LanguageUiState,
    onToggle: (String) -> Unit,
    onDone: () -> Unit,
    onRetry: () -> Unit,
) {
    androidx.tv.material3.Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RectangleShape,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF7A1020), Color(0xFF4A0010), Color(0xFF0D0000))
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 1040.dp)
                    .fillMaxWidth(0.75f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SunNxtCardBackground)
                    .padding(horizontal = 40.dp, vertical = 36.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    SunNxtLogo(size = 88.dp)
                    Spacer(Modifier.height(20.dp))
                    androidx.tv.material3.Text(
                        text = "Welcome!",
                        color = SunNxtTextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(8.dp))
                    androidx.tv.material3.Text(
                        text = "Select Your Content Language",
                        color = SunNxtTextSecondary,
                        fontSize = 18.sp,
                    )
                    Spacer(Modifier.height(32.dp))

                    when (uiState) {
                        is LanguageUiState.Loading -> CircularProgressIndicator(
                            color = SunNxtRed,
                            modifier = Modifier.padding(vertical = 32.dp),
                        )
                        is LanguageUiState.Error -> TvErrorContent(uiState.message, onRetry)
                        is LanguageUiState.Success -> {
                            TvLanguageRow(
                                languages = uiState.languages,
                                selectedIds = uiState.selectedIds,
                                onToggle = onToggle,
                            )
                            Spacer(Modifier.height(32.dp))
                            TvDoneButton(
                                enabled = uiState.selectedIds.isNotEmpty(),
                                onClick = onDone,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TvLanguageRow(
    languages: List<Language>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        languages.forEach { language ->
            TvLanguageTile(
                language = language,
                isSelected = language.id in selectedIds,
                onClick = { onToggle(language.id) },
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvLanguageTile(
    language: Language,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    androidx.tv.material3.Surface(
        onClick = onClick,
        modifier = Modifier.size(120.dp),
        colors = androidx.tv.material3.ClickableSurfaceDefaults.colors(
            containerColor = if (isSelected) SunNxtTileSelected else SunNxtTile,
            focusedContainerColor = SunNxtTileFocused,
            pressedContainerColor = SunNxtRedDark,
        ),
        shape = androidx.tv.material3.ClickableSurfaceDefaults.shape(
            shape = RoundedCornerShape(12.dp),
        ),
        border = androidx.tv.material3.ClickableSurfaceDefaults.border(
            focusedBorder = androidx.tv.material3.Border(
                border = BorderStroke(3.dp, Color.White),
                shape = RoundedCornerShape(12.dp),
            ),
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        ) {
            androidx.tv.material3.Text(
                text = language.nativeText,
                color = SunNxtTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(6.dp))
            androidx.tv.material3.Text(
                text = language.displayName,
                color = SunNxtTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvDoneButton(enabled: Boolean, onClick: () -> Unit) {
    androidx.tv.material3.Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .widthIn(min = 160.dp)
            .height(52.dp),
        colors = androidx.tv.material3.ButtonDefaults.colors(
            containerColor = SunNxtRed,
            contentColor = SunNxtTextPrimary,
            focusedContainerColor = SunNxtRedLight,
            focusedContentColor = SunNxtTextPrimary,
            disabledContainerColor = Color(0xFF4A1010),
            disabledContentColor = Color(0xFF666666),
        ),
    ) {
        androidx.tv.material3.Text(
            text = "Done",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvErrorContent(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        androidx.tv.material3.Text(text = message, color = Color(0xFFFF6B6B), textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        androidx.tv.material3.Button(
            onClick = onRetry,
            colors = androidx.tv.material3.ButtonDefaults.colors(containerColor = SunNxtRed),
        ) {
            androidx.tv.material3.Text("Retry", fontWeight = FontWeight.Bold)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LanguageTile(
    language: Language,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tileColor by animateColorAsState(
        targetValue = if (isSelected) SunNxtTileSelected else SunNxtTile,
        animationSpec = tween(durationMillis = 150),
        label = "tile_color_${language.id}",
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(tileColor)
            .then(
                if (isSelected) Modifier.border(2.dp, Color.White.copy(0.8f), RoundedCornerShape(8.dp))
                else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
        ) {
            Text(
                text = language.nativeText,
                color = SunNxtTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = language.displayName,
                color = SunNxtTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun DoneButton(
    enabled: Boolean,
    onClick: () -> Unit,
    isLarge: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .widthIn(min = if (isLarge) 160.dp else 140.dp)
            .height(if (isLarge) 48.dp else 44.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SunNxtRed,
            contentColor = SunNxtTextPrimary,
            disabledContainerColor = Color(0xFF4A1010),
            disabledContentColor = Color(0xFF666666),
        ),
    ) {
        Text(
            text = "Done",
            fontSize = if (isLarge) 16.sp else 15.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = message,
            color = Color(0xFFFF6B6B),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
        )
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onRetry) {
            Text(text = "Retry", color = SunNxtRed, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SunNxtLogo(size: Dp = 64.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.22f))
            .background(
                Brush.linearGradient(listOf(Color(0xFFE01530), Color(0xFF8B0000)))
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "SUN",
                color = Color.White,
                fontSize = (size.value * 0.22f).sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
            )
            Text(
                text = "NXT",
                color = Color.White,
                fontSize = (size.value * 0.26f).sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
            )
        }
    }
}
