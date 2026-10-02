package org.napharcos.gameloopcodmkeymap

data class UiState(
    val selectedTopElement: Mod = Mod.MP,
    val screenRatio: ScreenRatio,
    val replaceFire: Boolean,
    val brArmorButton: Boolean,
    val showingLibraries: Boolean = false,
    val showingLicense: Boolean = false,
    val showCopied: Boolean = false
)