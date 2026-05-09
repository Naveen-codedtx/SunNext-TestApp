package com.example.sunnxt_testapplication.core.device

import android.app.Activity
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import kotlinx.coroutines.flow.collectLatest

@Composable
fun rememberDeviceType(): DeviceType {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val isTV = remember(context) {
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    }
    if (isTV) return DeviceType.TV

    val activity = context as? Activity
    var foldingFeature by remember { mutableStateOf<FoldingFeature?>(null) }

    LaunchedEffect(activity) {
        activity ?: return@LaunchedEffect
        WindowInfoTracker.getOrCreate(activity)
            .windowLayoutInfo(activity)
            .collectLatest { layoutInfo ->
                foldingFeature = layoutInfo.displayFeatures
                    .filterIsInstance<FoldingFeature>()
                    .firstOrNull()
            }
    }

    foldingFeature?.let { fold ->
        val foldState = when (fold.state) {
            FoldingFeature.State.HALF_OPENED -> FoldState.HALF_OPEN
            FoldingFeature.State.FLAT -> FoldState.FLAT
            else -> FoldState.CLOSED
        }
        return DeviceType.Foldable(foldState)
    }

    return if (configuration.screenWidthDp >= 600) DeviceType.Tablet else DeviceType.Mobile
}
