package org.napharcos.gameloopcodmkeymap

enum class Mod(
    val displayName: String,
    val getKeys: () -> List<KeyData>
) {
    MP(
        displayName = "MP",
        getKeys = { mpKeys }
    ),
    BR(
        displayName = "BR",
        getKeys = { brKeys }
    ),
    DMZ(
        displayName = "DMZ",
        getKeys = { dmzKeys }
    )
}