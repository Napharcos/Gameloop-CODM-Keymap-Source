package org.napharcos.gameloopcodmkeymap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import gameloopcodmkeymap.composeapp.generated.resources.Res
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.set
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import org.w3c.files.File
import org.w3c.files.FileReader
import org.w3c.files.get

@OptIn(ExperimentalResourceApi::class)
object ManageFile {

    private const val START_TEXT = "<Item ApkName=\"com.activision.callofduty.shooter\""
    private const val START_TEXT_GARENA = "<Item ApkName=\"com.garena.game.codm\""
    private const val END_TEXT = "</Item>"
    private const val VERSION_INFO = "<VersionInfo Version=\"55\" ReqFeatureVer=\"7\" CurFeatureVer=\"8\"/>"
    private const val VERSION_INFO_GARENA = "<VersionInfo Version=\"49\" ReqFeatureVer=\"7\" CurFeatureVer=\"8\"/>"
    private const val VERSION_CODE = "VersionCode=\"4400\""
    private const val VERSION_CODE_GARENA = "VersionCode=\"23800\""

    private const val MP_START = "<KeyMapMode ModeID=\"1\""
    private const val BR_START = "<KeyMapMode ModeID=\"2\""
    private const val GD_START = "<KeyMapMode ModeID=\"3\""
    private const val DMZ_START = "<KeyMapMode ModeID=\"4\""
    private const val MODE_END = "</KeyMapMode>"

    private lateinit var defaultCodmText: String
    private lateinit var mpText: String
    private lateinit var brText: String
    private lateinit var gdText: String
    private lateinit var dmzText: String

    var showingOverrideMpAndBr by mutableStateOf(false)
    var overrideMpAndBr by mutableStateOf(true)
    var contentText by mutableStateOf<String?>(null)
    var privateMpText by mutableStateOf<String?>(null)
    var privateBrText by mutableStateOf<String?>(null)
    var privateMpTextGarena by mutableStateOf<String?>(null)
    var privateBrTextGarena by mutableStateOf<String?>(null)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            defaultCodmText = Res.readBytes("files/DefaultKeyMap.xml").decodeToString()
            mpText = MP_START + defaultCodmText.substringAfter(MP_START).substringBefore(MODE_END) + MODE_END
            brText = BR_START + defaultCodmText.substringAfter(BR_START).substringBefore(MODE_END) + MODE_END
            gdText = GD_START + defaultCodmText.substringAfter(GD_START).substringBefore(MODE_END) + MODE_END
            dmzText = DMZ_START + defaultCodmText.substringAfter(DMZ_START).substringBefore(MODE_END) + MODE_END

            //loadLocalData()
        }
    }

    fun downloadFile(replaceMpFire: Boolean, replaceBrFire: Boolean) {
        val text = createCodmText(replaceMpFire, replaceBrFire)
        @Suppress("RedundantNullableReturnType")
        val content: JsAny? = text.toJsString()
        val blob = Blob(arrayOf(content).toJsArray(), BlobPropertyBag(type = "application/xml"))
        val url = URL.createObjectURL(blob)
        val link = document.createElement("a") as HTMLAnchorElement
        link.href = url
        link.download = "TVM_100.xml"
        document.body?.appendChild(link)
        link.click()
        document.body?.removeChild(link)
        URL.revokeObjectURL(url)
    }

    fun uploadFile() {
        val inputFile = document.createElement("input") as HTMLInputElement
        inputFile.type = "file"
        inputFile.accept = "*.xml"

        inputFile.addEventListener("change") {
            inputFile.files?.get(0)?.let {
                readFile(it)
            }
        }

        inputFile.click()
    }

    private fun createCodmText(replaceMpFire: Boolean, replaceBrFire: Boolean): String {
        var editedCodmText = defaultCodmText
        var editedGdText = gdText
        var editedDmzText = dmzText

        editedCodmText = applyMode(
            codm = editedCodmText,
            baseText = mpText,
            keys = mpKeys,
            privateText = privateMpText,
            privateGarenaText = privateMpTextGarena
        )

        editedCodmText = applyMode(
            codm = editedCodmText,
            baseText = brText,
            keys = brKeys,
            privateText = privateBrText,
            privateGarenaText = privateBrTextGarena
        )

        gundamKeys.forEach { editedGdText = editedGdText.replaceKeys(it) }
        editedCodmText = editedCodmText.replace(gdText, editedGdText)

        dmzKeys.forEach { editedDmzText = editedDmzText.replaceKeys(it) }
        editedCodmText = editedCodmText.replace(dmzText, editedDmzText)

        if (replaceMpFire) editedCodmText = replaceMPFire(editedCodmText)
        if (replaceBrFire) editedCodmText = replaceBRFire(editedCodmText)

        listOf(
            startTime, defaultId, tips, gameKey,
            transparent, lightness, switch
        ).forEach { editedCodmText = editedCodmText.replace("$${it.first}", it.second) }

        editedCodmText = editedCodmText.replace("$" + exitFullScreen.value.id, exitFullScreen.value.currentCode.toString())

        val garena = editedCodmText
            .replace(START_TEXT, START_TEXT_GARENA)
            .replace(VERSION_INFO, VERSION_INFO_GARENA)
            .replace(VERSION_CODE, VERSION_CODE_GARENA)

        return (contentText ?: "") + editedCodmText + garena
    }

    private fun applyMode(
        codm: String,
        baseText: String,
        keys: SnapshotStateList<KeyData>,
        privateText: String?,
        privateGarenaText: String?
    ): String =
        if (overrideMpAndBr || (privateText == null && privateGarenaText == null))
            codm.replace(baseText, keys.fold(baseText) { acc, k -> acc.replaceKeys(k) })
        else codm.replace(baseText, privateText ?: privateGarenaText ?: baseText)

    private fun replaceMPFire(codmText: String): String {
        val line1 = """<SwitchOperation Description="射击" EnableSwitch="SetUp" DisableSwitch="InSetUp|BreastPatting|ReturnSetUp|Knife2|Diamond1|Diamond2|Diamond3|Diamond4|HangUp|WatchTeammates|Cartoon" Point_X="0.854688" Point_Y="0.745833" HideTips="1"/>"""
        val line2 = """<SwitchOperation Description="射击" EnableSwitch="Reload" DisableSwitch="InSetUp|BreastPatting|ReturnSetUp|Knife2|Diamond1|Diamond2|Diamond3|Diamond4|HangUp|WatchTeammates|SelectFireMode|Cartoon" Point_X="0.854688" Point_Y="0.745833" HideTips="1"/>"""

        val newLine1 = """<SwitchOperation Description="射击" EnableSwitch="SetUp" DisableSwitch="InSetUp|BreastPatting|ReturnSetUp|Knife2|Diamond1|Diamond2|Diamond3|Diamond4|HangUp|WatchTeammates|Cartoon" Point_X="0.060937" Point_Y="0.519444" HideTips="1"/>"""
        val newLine2 = """<SwitchOperation Description="射击" EnableSwitch="Reload" DisableSwitch="InSetUp|BreastPatting|ReturnSetUp|Knife2|Diamond1|Diamond2|Diamond3|Diamond4|HangUp|WatchTeammates|SelectFireMode|Cartoon" Point_X="0.060937" Point_Y="0.519444" HideTips="1"/>"""

        var newCodmText = codmText.replace(line1, newLine1)
        newCodmText = newCodmText.replace(line2, newLine2)

        return newCodmText
    }

    private fun replaceBRFire(codmText: String): String {
        val line = """<SwitchOperation Description="射击" EnableSwitch="SetUp" DisableSwitch="XBtn|MapOpenFlag|InSetUp|BreastPatting|ReturnSetUp|SkillX|Shield|WatchTeammates" Point_X="0.854688" Point_Y="0.745833" HideTips="1"/>"""
        val newLine = """<SwitchOperation Description="射击" EnableSwitch="SetUp" DisableSwitch="XBtn|MapOpenFlag|InSetUp|BreastPatting|ReturnSetUp|SkillX|Shield|WatchTeammates" Point_X="0.060937" Point_Y="0.519444" HideTips="1"/>"""

        return codmText.replace(line, newLine)
    }

    private fun readFile(file: File) {
        val reader = FileReader()
        reader.onload = {
            val content = reader.result?.toString()
            if (content != null) {
                val codmText = if (content.contains(START_TEXT)) START_TEXT + content.substringAfter(START_TEXT).substringBefore(END_TEXT) + END_TEXT else ""
                val codmTextGarena = if (content.contains(START_TEXT_GARENA)) START_TEXT_GARENA + content.substringAfter(START_TEXT_GARENA).substringBefore(END_TEXT) + END_TEXT else ""

                if (codmText.isNotEmpty()) {
                    privateMpText = MP_START + codmText.substringAfter(MP_START).substringBefore(MODE_END) + MODE_END
                    privateBrText = BR_START + codmText.substringAfter(BR_START).substringBefore(MODE_END) + MODE_END
                }
                if (codmTextGarena.isNotEmpty()) {
                    privateMpTextGarena = MP_START + codmTextGarena.substringAfter(MP_START).substringBefore(MODE_END) + MODE_END
                    privateBrTextGarena = BR_START + codmTextGarena.substringAfter(BR_START).substringBefore(MODE_END) + MODE_END
                }

                if ((privateMpText != null && privateBrText != null) || (privateBrTextGarena != null && privateMpTextGarena != null)) {
                    showingOverrideMpAndBr = true
                    overrideMpAndBr = false
                }

                contentText = content.replace(codmText, "").replace(codmTextGarena, "")

                when {
                    codmText.isNotEmpty() -> applySettings(codmText)
                    codmTextGarena.isNotEmpty() -> applySettings(codmTextGarena)
                }

                listOf(startTime, defaultId, tips, gameKey, transparent, lightness, switch)
                    .forEach { window.localStorage[it.first] = it.second }
            }
        }
        reader.readAsText(file)
    }

    private fun applySettings(src: String) {
        startTime = startTime.first to src.substringAfter(startTimes).substringBefore(end)
        defaultId = defaultId.first to src.substringAfter(modeId).substringBefore(end)
        tips = tips.first to src.substringAfter(enableTips).substringBefore(end)
        gameKey = gameKey.first to src.substringAfter(gameKeyDT).substringBefore(end)
        transparent = transparent.first to src.substringAfter(tipsTransparent).substringBefore(end)
        lightness = lightness.first to src.substringAfter(Lightness).substringBefore(end)
        switch = switch.first to src.substringAfter(enableSwitch).substringBefore(end)
    }

//    private fun loadLocalData() {
//        window.localStorage[startTime.first]?.let { startTime = startTime.first to it }
//        window.localStorage[defaultId.first]?.let { defaultId = defaultId.second to it }
//        window.localStorage[tips.first]?.let { tips = tips.first to it }
//        window.localStorage[gameKey.first]?.let { gameKey = gameKey.second to it }
//        window.localStorage[transparent.first]?.let { transparent = transparent.first to it }
//        window.localStorage[lightness.first]?.let { lightness = lightness.second to it }
//        window.localStorage[switch.first]?.let { switch = switch.second to it }
//    }

    fun String.replaceKeys(keyData: KeyData): String {
        var text = this
        text = text.replace(("$" + keyData.id.substringBefore(key) + name), keyData.currentKey)
        text = text.replace(("$" + keyData.id.substringBefore(key) + CODE), keyData.currentCode.toString())
        return text
    }
}