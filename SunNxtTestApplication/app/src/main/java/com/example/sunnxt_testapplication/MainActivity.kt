package com.example.sunnxt_testapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.sunnxt_testapplication.core.device.DeviceType
import com.example.sunnxt_testapplication.core.device.rememberDeviceType
import com.example.sunnxt_testapplication.navigation.AppNavHost
import com.example.sunnxt_testapplication.ui.theme.SunNxtTestApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val deviceType = rememberDeviceType()
            SunNxtTestApplicationTheme(deviceType = deviceType) {
                SunNxtApp(deviceType = deviceType)
            }
        }
    }
}

@Composable
private fun SunNxtApp(deviceType: DeviceType) {
    val navController = rememberNavController()
    AppNavHost(
        navController = navController,
        deviceType = deviceType,
        modifier = Modifier.fillMaxSize(),
    )
}
