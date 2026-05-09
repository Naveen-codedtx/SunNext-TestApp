@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.example.sunnxt_testapplication.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface as TvSurface
import com.example.sunnxt_testapplication.core.device.DeviceType
import com.example.sunnxt_testapplication.core.device.FoldState
import com.example.sunnxt_testapplication.data.model.NavMenuItem
import com.example.sunnxt_testapplication.ui.theme.SunNxtBackground
import com.example.sunnxt_testapplication.ui.theme.SunNxtCardSurface
import com.example.sunnxt_testapplication.ui.theme.SunNxtDivider
import com.example.sunnxt_testapplication.ui.theme.SunNxtRed
import com.example.sunnxt_testapplication.ui.theme.SunNxtRedDark
import com.example.sunnxt_testapplication.ui.theme.SunNxtRedLight
import com.example.sunnxt_testapplication.ui.theme.SunNxtTextPrimary
import com.example.sunnxt_testapplication.ui.theme.SunNxtTextSecondary

private val NAV_EXPANDED_WIDTH = 228.dp
private val NAV_COLLAPSED_WIDTH = 68.dp
private val ITEM_HEIGHT = 54.dp
private val HEADER_HEIGHT = 76.dp

private val NAV_GRADIENT = Brush.verticalGradient(
    colors = listOf(Color(0xFF200808), Color(0xFF110202), Color(0xFF0D0000))
)

private val SECTIONS = listOf(
    Triple("Movies & Shows", Color(0xFF1A0A28), Color(0xFF2A0A18)),
    Triple("Live TV", Color(0xFF0A1828), Color(0xFF0A2228)),
    Triple("Featured", Color(0xFF1A1A08), Color(0xFF280A08)),
)

@Composable
fun HomeScreen(
    deviceType: DeviceType,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isTV = deviceType == DeviceType.TV

    var isExpanded by rememberSaveable {
        mutableStateOf(
            when (deviceType) {
                DeviceType.Mobile -> false
                DeviceType.TV -> false
                DeviceType.Tablet -> true
                is DeviceType.Foldable -> deviceType.foldState == FoldState.FLAT
            }
        )
    }
    var isNavFocused by remember { mutableStateOf(false) }
    val effectiveExpanded = if (isTV) isNavFocused else isExpanded

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(SunNxtBackground)
    ) {
        SideNavRail(
            uiState = uiState,
            isExpanded = effectiveExpanded,
            showToggle = !isTV,
            deviceType = deviceType,
            onToggle = { if (!isTV) isExpanded = !isExpanded },
            onItemSelect = viewModel::selectMenuItem,
            onRetry = viewModel::fetchNavMenu,
            onNavFocusChanged = { hasFocus -> if (isTV) isNavFocused = hasFocus }
        )

        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(SunNxtDivider)
        )

        ContentArea(
            deviceType = deviceType,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

// ─── Side nav ────────────────────────────────────────────────────────────────

@Composable
private fun SideNavRail(
    uiState: HomeUiState,
    isExpanded: Boolean,
    showToggle: Boolean,
    deviceType: DeviceType,
    onToggle: () -> Unit,
    onItemSelect: (String) -> Unit,
    onRetry: () -> Unit,
    onNavFocusChanged: (Boolean) -> Unit
) {
    val navWidth by animateDpAsState(
        targetValue = if (isExpanded) NAV_EXPANDED_WIDTH else NAV_COLLAPSED_WIDTH,
        animationSpec = tween(durationMillis = 280),
        label = "navWidth"
    )

    Column(
        modifier = Modifier
            .width(navWidth)
            .fillMaxHeight()
            .clip(RectangleShape)
            .background(NAV_GRADIENT)
            .onFocusChanged { onNavFocusChanged(it.hasFocus) }
    ) {
        NavHeader(
            isExpanded = isExpanded,
            showToggle = showToggle,
            onToggle = onToggle
        )

        // Divider
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(SunNxtRed.copy(alpha = 0.6f), Color.Transparent)
                    )
                )
        )

        Spacer(Modifier.height(8.dp))

        when (uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = SunNxtRed,
                        modifier = Modifier.size(26.dp),
                        strokeWidth = 2.dp
                    )
                }
            }

            is HomeUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SunNxtCardSurface)
                            .clickable(onClick = onRetry),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Refresh, "Retry", tint = SunNxtRed, modifier = Modifier.size(20.dp))
                    }
                    if (isExpanded) {
                        Spacer(Modifier.height(6.dp))
                        Text("Tap to retry", color = SunNxtTextSecondary, fontSize = 11.sp)
                    }
                }
            }

            is HomeUiState.Success -> {
                uiState.menuItems.forEach { item ->
                    NavItemRow(
                        item = item,
                        isSelected = item.actionUrl == uiState.selectedActionUrl,
                        isExpanded = isExpanded,
                        deviceType = deviceType,
                        onClick = { onItemSelect(item.actionUrl) }
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Bottom gradient fade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, SunNxtBackground.copy(alpha = 0.6f))
                    )
                )
        )
    }
}

@Composable
private fun NavHeader(
    isExpanded: Boolean,
    showToggle: Boolean,
    onToggle: () -> Unit
) {
    val labelAlpha by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = tween(if (isExpanded) 220 else 100),
        label = "labelAlpha"
    )
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HEADER_HEIGHT)
    ) {
        // Logo — always visible at the left
        Box(
            modifier = Modifier
                .size(NAV_COLLAPSED_WIDTH, HEADER_HEIGHT)
                .align(Alignment.CenterStart)
                .then(
                    if (showToggle) Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onToggle
                    ) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            LogoMark()
        }

        // Brand text + toggle chevron — fades in/out
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = NAV_COLLAPSED_WIDTH, end = 4.dp)
                .alpha(labelAlpha)
                .align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "SUN NXT",
                    color = SunNxtTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Stream Unlimited",
                    color = SunNxtTextSecondary,
                    fontSize = 10.sp,
                    letterSpacing = 0.4.sp
                )
            }
            if (showToggle) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SunNxtCardSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowLeft else Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = SunNxtTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LogoMark() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Brush.linearGradient(listOf(SunNxtRedLight, SunNxtRedDark))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SN",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp
        )
    }
}

@Composable
private fun NavItemRow(
    item: NavMenuItem,
    isSelected: Boolean,
    isExpanded: Boolean,
    deviceType: DeviceType,
    onClick: () -> Unit
) {
    if (deviceType == DeviceType.TV) {
        TvNavItemRow(item, isSelected, isExpanded, onClick)
    } else {
        MobileNavItemRow(item, isSelected, isExpanded, onClick)
    }
}

@Composable
private fun MobileNavItemRow(
    item: NavMenuItem,
    isSelected: Boolean,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val labelAlpha by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = tween(180),
        label = "itemLabel"
    )

    // Outer box provides the margin; inner Row owns the visual styling
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        // Left accent bar
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(3.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SunNxtRed)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ITEM_HEIGHT)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isSelected)
                        Brush.horizontalGradient(listOf(SunNxtRed.copy(alpha = 0.18f), Color.Transparent))
                    else
                        SolidColor(Color.Transparent)
                )
                .clickable(onClick = onClick)
                .padding(start = if (isSelected) 16.dp else 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSelected) item.actionUrl.toFilledIcon()
                              else item.actionUrl.toOutlinedIcon(),
                contentDescription = item.title,
                tint = if (isSelected) SunNxtRed else Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(14.dp))
            Text(
                text = item.title,
                color = if (isSelected) SunNxtTextPrimary else Color.White.copy(alpha = 0.75f),
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                modifier = Modifier.alpha(labelAlpha)
            )
        }
    }
}

@Composable
private fun TvNavItemRow(
    item: NavMenuItem,
    isSelected: Boolean,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val labelAlpha by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = tween(180),
        label = "tvItemLabel"
    )

    TvSurface(
        onClick = onClick,
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.04f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = SunNxtRed.copy(alpha = 0.18f)
        ),
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(12.dp)),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(BorderStroke(1.5.dp, SunNxtRed))
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        // Outer box provides margin only; inner Row owns height and visual styling
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(3.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(SunNxtRed)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ITEM_HEIGHT)
                    .padding(start = if (isSelected) 14.dp else 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isSelected) item.actionUrl.toFilledIcon()
                                  else item.actionUrl.toOutlinedIcon(),
                    contentDescription = item.title,
                    tint = if (isSelected) SunNxtRed else Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(14.dp))
                Text(
                    text = item.title,
                    color = if (isSelected) SunNxtTextPrimary else Color.White.copy(alpha = 0.75f),
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    modifier = Modifier.alpha(labelAlpha)
                )
            }
        }
    }
}

private fun String.toFilledIcon(): ImageVector {
    val key = lowercase()
    return when {
        key.contains("home") -> Icons.Filled.Home
        key.contains("live") -> Icons.Filled.LiveTv
        key.contains("movie") || key.contains("film") || key.contains("cinema") -> Icons.Filled.Movie
        key.contains("tv") || key.contains("show") || key.contains("serial") -> Icons.Filled.Tv
        key.contains("free") || key.contains("gift") -> Icons.Filled.CardGiftcard
        key.contains("music") || key.contains("song") || key.contains("audio") -> Icons.Filled.MusicNote
        key.contains("short") || key.contains("clip") || key.contains("reel") -> Icons.Filled.VideoLibrary
        else -> Icons.Filled.PlayCircle
    }
}

private fun String.toOutlinedIcon(): ImageVector {
    val key = lowercase()
    return when {
        key.contains("home") -> Icons.Outlined.Home
        key.contains("live") -> Icons.Outlined.LiveTv
        key.contains("movie") || key.contains("film") || key.contains("cinema") -> Icons.Outlined.Movie
        key.contains("tv") || key.contains("show") || key.contains("serial") -> Icons.Outlined.Tv
        key.contains("free") || key.contains("gift") -> Icons.Outlined.CardGiftcard
        key.contains("music") || key.contains("song") || key.contains("audio") -> Icons.Outlined.MusicNote
        key.contains("short") || key.contains("clip") || key.contains("reel") -> Icons.Outlined.VideoLibrary
        else -> Icons.Outlined.PlayCircle
    }
}

// ─── Content area ────────────────────────────────────────────────────────────

@Composable
private fun ContentArea(deviceType: DeviceType, modifier: Modifier = Modifier) {
    val isTV = deviceType == DeviceType.TV
    val cardWidth = if (isTV) 210.dp else 155.dp
    val cardHeight = if (isTV) 126.dp else 95.dp

    LazyColumn(
        modifier = modifier.background(SunNxtBackground),
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        items(SECTIONS) { (title, colorA, colorB) ->
            SectionCarousel(
                title = title,
                cardColorA = colorA,
                cardColorB = colorB,
                deviceType = deviceType,
                cardWidth = cardWidth,
                cardHeight = cardHeight
            )
        }
    }
}

@Composable
private fun SectionCarousel(
    title: String,
    cardColorA: Color,
    cardColorB: Color,
    deviceType: DeviceType,
    cardWidth: Dp,
    cardHeight: Dp
) {
    Column {
        // Section header with red accent bar
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SunNxtRed)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = title,
                color = SunNxtTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(3) { index ->
                ContentCard(
                    label = "$title ${index + 1}",
                    cardGradient = Brush.linearGradient(
                        colors = listOf(
                            cardColorA,
                            if (index % 2 == 0) cardColorB else cardColorA.copy(alpha = 0.6f)
                        )
                    ),
                    accentColor = when (index) {
                        0 -> SunNxtRed
                        1 -> Color(0xFF4A90D9)
                        else -> Color(0xFF4A9B6F)
                    },
                    deviceType = deviceType,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight
                )
            }
        }
    }
}

@Composable
private fun ContentCard(
    label: String,
    cardGradient: Brush,
    accentColor: Color,
    deviceType: DeviceType,
    cardWidth: Dp,
    cardHeight: Dp
) {
    if (deviceType == DeviceType.TV) {
        TvSurface(
            onClick = {},
            scale = ClickableSurfaceDefaults.scale(focusedScale = 1.06f),
            colors = ClickableSurfaceDefaults.colors(
                containerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(10.dp)),
            border = ClickableSurfaceDefaults.border(
                focusedBorder = Border(BorderStroke(2.dp, accentColor))
            ),
            modifier = Modifier.size(width = cardWidth, height = cardHeight)
        ) {
            CardContent(label = label, gradient = cardGradient, accentColor = accentColor)
        }
    } else {
        Box(
            modifier = Modifier
                .size(width = cardWidth, height = cardHeight)
                .clip(RoundedCornerShape(10.dp))
                .clickable {}
        ) {
            CardContent(label = label, gradient = cardGradient, accentColor = accentColor)
        }
    }
}

@Composable
private fun CardContent(label: String, gradient: Brush, accentColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        // Top accent stripe
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    Brush.horizontalGradient(listOf(accentColor, Color.Transparent))
                )
                .align(Alignment.TopStart)
        )

        // Bottom gradient scrim
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color(0x99000000)))
                )
                .align(Alignment.BottomStart)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Text(
                text = label,
                color = SunNxtTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                text = "Watch now",
                color = accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
