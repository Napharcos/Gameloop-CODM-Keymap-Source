package org.napharcos.gameloopcodmkeymap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import gameloopcodmkeymap.composeapp.generated.resources.Res
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.get
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
    private const val END_TEXT = "</Item>"

    private const val MP_START = "<KeyMapMode ModeID=\"1\""
    private const val BR_START = "<KeyMapMode ModeID=\"2\""
    private const val GD_START = "<KeyMapMode ModeID=\"3\""
    private const val MODE_END = "</KeyMapMode>"

    private lateinit var defaultCodmText: String
    private lateinit var mpText: String
    private lateinit var brText: String
    private lateinit var gdText: String

    var startText by mutableStateOf<String?>(null)
    var endText by mutableStateOf<String?>(null)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            defaultCodmText = Res.readBytes("files/DefaultKeyMap.xml").decodeToString()
            mpText = MP_START + defaultCodmText.substringAfter(MP_START).substringBefore(MODE_END) + MODE_END
            brText = BR_START + defaultCodmText.substringAfter(BR_START).substringBefore(MODE_END) + MODE_END
            gdText = GD_START + defaultCodmText.substringAfter(GD_START).substringBefore(MODE_END) + MODE_END

            loadLocalData()
        }
    }

    fun downloadFile(replaceMpFire: Boolean, replaceBrFire: Boolean) {
        val text = createCodmText(replaceMpFire, replaceBrFire)
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
        var editedMpText = mpText
        var editedBrText = brText
        var editedGdText = gdText

        mpKeys.forEach {
            editedMpText = editedMpText.replace(("$" + it.id.substringBefore(key)+name), it.currentKey)
            editedMpText = editedMpText.replace(("$" + it.id.substringBefore(key)+CODE), it.currentCode.toString())
        }

        editedCodmText = editedCodmText.replace(mpText, editedMpText)

        brKeys.forEach {
            editedBrText = editedBrText.replace(("$" + it.id.substringBefore(key)+name), it.currentKey)
            editedBrText = editedBrText.replace(("$" + it.id.substringBefore(key)+ CODE), it.currentCode.toString())
        }

        editedCodmText = editedCodmText.replace(brText, editedBrText)

        gundamKeys.forEach {
            editedGdText = editedGdText.replace(("$" + it.id.substringBefore(key) + name), it.currentKey)
            editedGdText = editedGdText.replace(("$" + it.id.substringBefore(key)+CODE), it.currentCode.toString())
        }

        editedCodmText = editedCodmText.replace(gdText, editedGdText)

        if (replaceMpFire) editedCodmText = replaceMPFire(editedCodmText)
        if (replaceBrFire) editedCodmText = replaceBRFire(editedCodmText)

        editedCodmText = editedCodmText.replace("$" + startTime.first, startTime.second)
        editedCodmText = editedCodmText.replace("$" + defaultId.first, defaultId.second)
        editedCodmText = editedCodmText.replace("$" + tips.first, tips.second)
        editedCodmText = editedCodmText.replace("$" + gameKey.first, gameKey.second)
        editedCodmText = editedCodmText.replace("$" + transparent.first, transparent.second)
        editedCodmText = editedCodmText.replace("$" + lightness.first, lightness.second)
        editedCodmText = editedCodmText.replace("$" + switch.first, switch.second)
        editedCodmText = editedCodmText.replace("$" + exitFullScreen.value.id, exitFullScreen.value.currentCode.toString())

        return if (startText != null && endText != null)
            startText + editedCodmText + endText
        else editedCodmText
    }

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
                startText = content.substringBefore(START_TEXT)
                val codmText = START_TEXT + content.substringAfter(START_TEXT).substringBefore(END_TEXT) + END_TEXT
                endText = content.substringAfter(codmText)

                startTime = startTime.first to codmText.substringAfter(startTimes).substringBefore(end)
                defaultId = defaultId.first to codmText.substringAfter(modeId).substringBefore(end)
                tips = tips.first to codmText.substringAfter(enableTips).substringBefore(end)
                gameKey = gameKey.first to codmText.substringAfter(gameKeyDT).substringBefore(end)
                transparent = transparent.first to codmText.substringAfter(tipsTransparent).substringBefore(end)
                lightness = lightness.first to codmText.substringAfter(Lightness).substringBefore(end)
                switch = switch.first to codmText.substringAfter(enableSwitch).substringBefore(end)


                startText?.let { window.localStorage[START] = it }
                endText?.let { window.localStorage[END] = it }
                window.localStorage[startTime.first] = startTime.second
                window.localStorage[defaultId.first] = defaultId.second
                window.localStorage[tips.first] = tips.second
                window.localStorage[gameKey.first] = gameKey.second
                window.localStorage[transparent.first] = transparent.second
                window.localStorage[lightness.first] = lightness.second
                window.localStorage[switch.first] = switch.second
            }
        }
        reader.readAsText(file)
    }

    private fun loadLocalData() {
        window.localStorage[START_TEXT]?.let { startText = it }
        window.localStorage[END_TEXT]?.let { endText = it }

        window.localStorage[startTime.first]?.let { startTime = startTime.first to it }
        window.localStorage[defaultId.first]?.let { defaultId = defaultId.second to it }
        window.localStorage[tips.first]?.let { tips = tips.first to it }
        window.localStorage[gameKey.first]?.let { gameKey = gameKey.second to it }
        window.localStorage[transparent.first]?.let { transparent = transparent.first to it }
        window.localStorage[lightness.first]?.let { lightness = lightness.second to it }
        window.localStorage[switch.first]?.let { switch = switch.second to it }
    }
}