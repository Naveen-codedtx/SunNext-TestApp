package com.example.sunnxt_testapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.example.sunnxt_testapplication.core.device.DeviceType

private val SunNxtDarkColorScheme = darkColorScheme(
    primary = SunNxtRed,
    onPrimary = SunNxtTextPrimary,
    primaryContainer = SunNxtRedDark,
    onPrimaryContainer = SunNxtTextPrimary,
    secondary = SunNxtRedLight,
    onSecondary = SunNxtTextPrimary,
    background = SunNxtBackground,
    onBackground = SunNxtTextPrimary,
    surface = SunNxtSurfaceDark,
    onSurface = SunNxtTextPrimary,
    surfaceVariant = SunNxtCardSurface,
    onSurfaceVariant = SunNxtTextSecondary,
    error = Color(0xFFCF6679),
    onError = Color.Black,
)

private val SunNxtLightColorScheme = lightColorScheme(
    primary = SunNxtRed,
    onPrimary = SunNxtTextPrimary,
    primaryContainer = SunNxtRedLight,
    onPrimaryContainer = SunNxtTextPrimary,
    background = SunNxtBackground,
    onBackground = SunNxtTextPrimary,
    surface = SunNxtCardSurface,
    onSurface = SunNxtTextPrimary,
)

@Composable
fun SunNxtTestApplicationTheme(
    deviceType: DeviceType = DeviceType.Mobile,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    when (deviceType) {
        is DeviceType.TV -> TvAppTheme(darkTheme = darkTheme, content = content)
        else -> MobileAppTheme(darkTheme = darkTheme, content = content)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
internal fun TvAppTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) {
        androidx.tv.material3.darkColorScheme(
            primary = SunNxtRed,
            onPrimary = SunNxtTextPrimary,
            background = SunNxtBackground,
            onBackground = SunNxtTextPrimary,
            surface = SunNxtSurfaceDark,
            onSurface = SunNxtTextPrimary,
        )
    } else {
        androidx.tv.material3.lightColorScheme(
            primary = SunNxtRed,
            onPrimary = SunNxtTextPrimary,
            background = SunNxtBackground,
            onBackground = SunNxtTextPrimary,
            surface = SunNxtSurfaceDark,
            onSurface = SunNxtTextPrimary,
        )
    }
    androidx.tv.material3.MaterialTheme(
        colorScheme = colorScheme,
        typography = TvTypography,
        content = content
    )
}

@Composable
internal fun MobileAppTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) SunNxtDarkColorScheme else SunNxtLightColorScheme
    androidx.compose.material3.MaterialTheme(
        colorScheme = colorScheme,
        typography = MobileTypography,
        content = content
    )
}
