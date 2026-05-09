package com.example.sunnxt_testapplication.core.device

sealed class DeviceType {
    data object Mobile : DeviceType()
    data object Tablet : DeviceType()
    data object TV : DeviceType()
    data class Foldable(val foldState: FoldState) : DeviceType()
}

enum class FoldState {
    FLAT,
    HALF_OPEN,
    CLOSED
}
