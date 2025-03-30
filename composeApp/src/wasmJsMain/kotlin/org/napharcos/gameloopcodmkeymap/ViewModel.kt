package org.napharcos.gameloopcodmkeymap

import androidx.lifecycle.ViewModel
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.w3c.dom.set

class ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onTopElementClick(selected: Int) {
        _uiState.update {
            it.copy(selectedTopElement = selected)
        }
    }

    fun onReplaceMpFireClick(replacedMpFire: Boolean) {
        _uiState.update {
            it.copy(replaceMpFire = replacedMpFire)
        }
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

    private fun saveBrKey(id: String, key: String, keyCode: Int) {
        window.localStorage[BR + id] = key
        window.localStorage[BR + id + code] = keyCode.toString()
    }

    private fun saveKey(id: String, key: String, keyCode: Int) {
        window.localStorage[id] = key
        window.localStorage[id + code] = keyCode.toString()
    }

    fun onUploadClick() = ManageFile.uploadFile()

    fun onDownloadClick(replaceMpFire: Boolean) = ManageFile.downloadFile(replaceMpFire)
}