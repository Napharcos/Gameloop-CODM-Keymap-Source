package org.napharcos.gameloopcodmkeymap

enum class ScreenRatio(
    val displayName: String,
    val fileName: String
) {
    R_16_9(
        displayName = "16:9 - 1920x1080",
        fileName = "16_9"
    ),
    R_16_10(
        displayName = "16:10 - 1920x1200",
        fileName = "16_10"
    )
}