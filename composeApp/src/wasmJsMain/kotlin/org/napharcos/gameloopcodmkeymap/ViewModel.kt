package org.napharcos.gameloopcodmkeymap

import androidx.lifecycle.ViewModel
import gameloopcodmkeymap.composeapp.generated.resources.Res
import gameloopcodmkeymap.composeapp.generated.resources.br_key4
import gameloopcodmkeymap.composeapp.generated.resources.br_keyn
import gameloopcodmkeymap.composeapp.generated.resources.dmz_keyn
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.w3c.dom.set
import org.w3c.dom.get

class ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(
            screenRatio = initScreenRatio(),
            replaceFire = initReplaceFire(),
            brArmorButton = initSeparateArmor().also {
                if (it) addArmorKey()
            },
            separateBuyStationBR = initSeparateBuyStation(Mod.BR).also { if (it) addBuyStationKey(Mod.BR) },
            separateBuyStationDMZ = initSeparateBuyStation(Mod.DMZ).also { if (it) addBuyStationKey(Mod.DMZ) }
        )
    )

    val uiState = _uiState.asStateFlow()

    fun showCopied(showing: Boolean) {
        _uiState.update {
            it.copy(showCopied = showing)
        }
    }

    fun onTopElementClick(selected: Mod) {
        _uiState.update {
            it.copy(selectedTopElement = selected)
        }
    }

    fun changeScreenRatio(ratio: ScreenRatio) {
        _uiState.update {
            it.copy(screenRatio = ratio)
        }

        saveScreenRatio(ratio)
    }

    fun onReplaceFireClick(replacedFire: Boolean) {
        _uiState.update {
            it.copy(replaceFire = replacedFire)
        }

        saveReplaceFire(replacedFire)
    }

    fun onLibrariesClick(showing: Boolean) {
        _uiState.update {
            it.copy(showingLibraries = showing)
        }
    }

    fun onLicenseClick(showing: Boolean) {
        _uiState.update {
            it.copy(showingLicense = showing)
        }
    }

    fun switchArmorButton(enable: Boolean) {
        _uiState.update {
            it.copy(brArmorButton = enable)
        }

        if (enable) addArmorKey() else removeArmorKey()

        saveSeparateArmor(enable)
    }

    fun switchBuyStationButton(enable: Boolean) {
        _uiState.update {
            if (it.selectedTopElement == Mod.DMZ)
                it.copy(
                    separateBuyStationDMZ = enable
                )
            else it.copy(
                separateBuyStationBR = enable
            )
        }

        if (enable)
            addBuyStationKey(uiState.value.selectedTopElement)
        else removeBuyStationKey(uiState.value.selectedTopElement)

        saveSeparateBuyStation(enable, uiState.value.selectedTopElement)
    }

    fun changeBrKey(id: String, key: String, code: Int) {
        val newList = brKeys.map {
            when {
                it.id == id -> {
                    saveBrKey(id, key, code)
                    it.copy(currentKey = key, currentCode = code)
                }
                it.currentCode == code -> {
                    saveBrKey(it.id, "", -1)
                    it.copy(currentKey = "", currentCode = -1)
                }
                else -> it
            }
        }
        brKeys.clear()
        brKeys.addAll(newList)
    }

    private fun removeArmorKey() {
        brKeys.firstOrNull { it.id == key4 }?.let { brKeys.remove(it) }
    }

    private fun addArmorKey() {
        val armor = KeyData(key4, Res.string.br_key4, "4", 52, loadBrKey(key4) ?: "4", loadBrKeyCode(key4) ?: 52)

        brKeys.firstOrNull { it.id == armor.id } ?: run { brKeys.add(armor) }
    }

    private fun removeBuyStationKey(mod: Mod) {
        val list = when (mod) {
            Mod.MP -> mpKeys
            Mod.BR -> brKeys
            Mod.DMZ -> dmzKeys
        }

        list.firstOrNull { it.id == nKey }?.let { list.remove(it) }
    }

    private fun addBuyStationKey(mod: Mod) {
        val brKeyData = KeyData(nKey, Res.string.br_keyn, "N", 78, loadBrKey(nKey) ?: "N", loadBrKeyCode(nKey) ?: 78)
        val dmzKeyData = KeyData(nKey, Res.string.dmz_keyn, "N", 78, loadDmzKey(nKey) ?: "N", loadDmzKeyCode(nKey) ?: 78)

        val list = when (mod) {
            Mod.MP -> mpKeys
            Mod.BR -> brKeys
            Mod.DMZ -> dmzKeys
        }

        val keyData = when (mod) {
            Mod.MP -> null
            Mod.BR -> brKeyData
            Mod.DMZ -> dmzKeyData
        }

        list.firstOrNull { it.id == keyData?.id } ?: run { keyData?.let { list.add(it) } }
    }

    fun changeMpKey(id: String, key: String, code: Int) {
        val newList = mpKeys.map {
            when {
                it.id == id -> {
                    saveKey(id, key, code)
                    it.copy(currentKey = key, currentCode = code)
                }
                it.currentCode == code -> {
                    saveKey(it.id, "", -1)
                    it.copy(currentKey = "", currentCode = -1)
                }
                else -> it
            }
        }
        mpKeys.clear()
        mpKeys.addAll(newList)
    }

    fun changeDmzKey(id: String, key: String, code: Int) {
        val newList = dmzKeys.map {
            when {
                it.id == id -> {
                    saveDMZKey(id, key, code)
                    it.copy(currentKey = key, currentCode = code)
                }
                it.currentCode == code -> {
                    saveDMZKey(it.id, "", -1)
                    it.copy(currentKey = "", currentCode = -1)
                }
                else -> it
            }
        }
        dmzKeys.clear()
        dmzKeys.addAll(newList)
    }

    fun updateApplyWheel(mod: Mod, id: String, apply: Boolean) {
        val modList = when (mod) {
            Mod.MP -> mpKeys
            Mod.BR -> brKeys
            Mod.DMZ -> dmzKeys
        }
        val newList = modList.map {
            when {
                it.id == id -> {
                    saveWheel(mod, id, apply)
                    it.copy(applyWheel = apply)
                }
                else -> it
            }
        }
        modList.clear()
        modList.addAll(newList)
    }

    fun saveWheel(mod: Mod, id: String, apply: Boolean) {
        window.localStorage["${mod.displayName}_${id}_wheel"] = apply.toString()
    }

    private fun saveBrKey(id: String, key: String, keyCode: Int) {
        window.localStorage[BR + id] = key
        window.localStorage[BR + id + code] = keyCode.toString()
    }

    private fun saveDMZKey(id: String, key: String, keyCode: Int) {
        window.localStorage[DMZ + id] = key
        window.localStorage[DMZ + id + code] = keyCode.toString()
    }

    private fun saveKey(id: String, key: String, keyCode: Int) {
        window.localStorage[id] = key
        window.localStorage[id + code] = keyCode.toString()
    }

    private fun initScreenRatio(): ScreenRatio {
        val ratio = window.localStorage["screenRatio"] ?: ScreenRatio.R_16_9.name
        return ScreenRatio.valueOf(ratio)
    }

    fun saveScreenRatio(ratio: ScreenRatio) {
        window.localStorage["screenRatio"] = ratio.name
    }

    private fun initSeparateArmor(): Boolean {
        val ratio = window.localStorage["separateArmor"] ?: "false"
        return ratio.toBooleanStrict()
    }

    private fun initSeparateBuyStation(mod: Mod): Boolean {
        val ratio = window.localStorage["separateBuyStation_${mod.displayName}"] ?: "false"
        return ratio.toBooleanStrict()
    }

    private fun initReplaceFire(): Boolean {
        val ratio = window.localStorage["replaceFire"] ?: "true"
        return ratio.toBooleanStrict()
    }

    fun saveReplaceFire(replace: Boolean) {
        window.localStorage["replaceFire"] = replace.toString()
    }

    fun saveSeparateArmor(replace: Boolean) {
        window.localStorage["separateArmor"] = replace.toString()
    }

    fun saveSeparateBuyStation(separate: Boolean, mod: Mod) {
        window.localStorage["separateBuyStation_${mod.displayName}"] = separate.toString()
    }

    fun onDownloadClick(replaceFire: Boolean, mod: Mod, ratio: ScreenRatio, brArmor: Boolean, buyStation: Boolean) =
        ManageFile.downloadFile(replaceFire, mod, ratio, brArmor, buyStation)
}