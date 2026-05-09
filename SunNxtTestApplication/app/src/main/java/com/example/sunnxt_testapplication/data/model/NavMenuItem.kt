package com.example.sunnxt_testapplication.data.model

data class NavMenuItem(
    val name: String,
    val title: String,
    val actionUrl: String,
    val iconUrl: String
)

fun NavMenuResult.toDomain(): NavMenuItem {
    val icon = images.firstOrNull { it.profile == "xxhdpi" } ?: images.firstOrNull()
    return NavMenuItem(
        name = name,
        title = title,
        actionUrl = actionUrl,
        iconUrl = icon?.link?.replace("{ICON}", "white") ?: ""
    )
}
