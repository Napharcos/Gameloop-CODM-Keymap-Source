package org.napharcos.gameloopcodmkeymap

import gameloopcodmkeymap.composeapp.generated.resources.Res
import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

@OptIn(ExperimentalWasmJsInterop::class)
object ManageFile {
    private lateinit var defaultMPText: String
    private lateinit var defaultBRText: String
    private lateinit var defaultDMZText: String
    
    private const val SIMPLE_END = """</KeyMapping>"""
    private const val WHEEL_END = """</KeyMappingEx>"""

    private val removableMPKeys = listOf(
        RemoveButtonData(
            id = xKey,
            description = "Mark",
            simpleStart = $$"""<KeyMapping ItemName="$xName"""",
            wheelStart = $$"""<KeyMappingEx ItemName="$xName""""
        )
    )

    private val removableBRKeys = listOf(
        RemoveButtonData(
            id = xKey,
            description = "Mark",
            simpleStart = $$"""<KeyMapping ItemName="$xName"""",
            wheelStart = $$"""<KeyMappingEx ItemName="$xName""""
        ),
        RemoveButtonData(
            id = capsKey,
            description = "Scorestreaks",
            simpleStart = $$"""<KeyMapping ItemName="$capsName"""",
            wheelStart = $$"""<KeyMappingEx ItemName="$capsName""""
        ),
        RemoveButtonData(
            id = key3,
            description = "Grenades",
            simpleStart = $$"""<KeyMapping ItemName="$3Name"""",
            wheelStart = $$"""<KeyMappingEx ItemName="$3Name""""
        )
    )

    private val removableDMZKeys = listOf(
        RemoveButtonData(
            id = xKey,
            description = "Mark",
            simpleStart = $$"""<KeyMapping ItemName="$xName"""",
            wheelStart = $$"""<KeyMappingEx ItemName="$xName""""
        ),
        RemoveButtonData(
            id = key3,
            description = "Grenades",
            simpleStart = $$"""<KeyMapping ItemName="$3Name"""",
            wheelStart = $$"""<KeyMappingEx ItemName="$3Name""""
        ),
        RemoveButtonData(
            id = hKey,
            description = "Armor",
            simpleStart = $$"""<KeyMapping ItemName="$hName"""",
            wheelStart = $$"""<KeyMappingEx ItemName="$hName""""
        ),
        RemoveButtonData(
            id = jKey,
            description = "Scorestreaks",
            simpleStart = $$"""<KeyMapping ItemName="$jName"""",
            wheelStart = $$"""<KeyMappingEx ItemName="$jName""""
        )
    )

    suspend fun initTexts() {
        defaultMPText = Res.readBytes("files/mp-default.xml").decodeToString()
        defaultBRText = Res.readBytes("files/br-default.xml").decodeToString()
        defaultDMZText = Res.readBytes("files/dmz-default.xml").decodeToString()
    }

    fun downloadFile(replaceFire: Boolean, mod: Mod, ratio: ScreenRatio, removeArmor: Boolean) {
        val text = createCodmText(replaceFire, mod, removeArmor)
        @Suppress("RedundantNullableReturnType")
        val content: JsAny? = text.toJsString()
        val blob = Blob(arrayOf(content).toJsArray(), BlobPropertyBag(type = "text/plain"))
        val url = URL.createObjectURL(blob)
        val link = document.createElement("a") as HTMLAnchorElement
        link.href = url
        link.download = "${mod.displayName}_${ratio.fileName}.txt"
        document.body?.appendChild(link)
        link.click()
        document.body?.removeChild(link)
        URL.revokeObjectURL(url)
    }

    private fun createCodmText(replaceFire: Boolean, mod: Mod, removeArmor: Boolean): String {
        var editedCodmText = when (mod) {
            Mod.MP -> defaultMPText
            Mod.BR -> defaultBRText
            Mod.DMZ -> defaultDMZText
        }
        
        editedCodmText = editedCodmText.removeDuplicateButtons(mod)

        if (!removeArmor)
            editedCodmText = removeArmorButton(editedCodmText)

        editedCodmText = applyMode(
            baseText = editedCodmText,
            keys = mod.getKeys()
        )

        if (replaceFire && mod != Mod.DMZ)
            editedCodmText = replaceFire(editedCodmText)

        return editedCodmText
    }

    private fun applyMode(
        baseText: String,
        keys: List<KeyData>
    ): String = keys.fold(baseText) { acc, k -> acc.replaceKeys(k) }

    private fun String.removeDuplicateButtons(mod: Mod): String {
        val (modList, removeList) = when (mod) {
            Mod.MP -> mpKeys to removableMPKeys
            Mod.BR -> brKeys to removableBRKeys
            Mod.DMZ -> dmzKeys to removableDMZKeys
        }

        return removeList.fold(this) { acc, removeData ->
            val applyWheel = modList.firstOrNull { it.id == removeData.id }?.applyWheel

            if (applyWheel != false)
                removeButton(acc, removeData.simpleStart, SIMPLE_END)
            else removeButton(acc, removeData.wheelStart, WHEEL_END)
        }
    }

    private fun replaceFire(codmText: String): String {
        val defaultFire = """Point_X="0.854688" Point_Y="0.745833""""
        val newFire = """Point_X="0.060937" Point_Y="0.519444""""

        return codmText.replace(defaultFire, newFire)
    }

    private fun removeArmorButton(codmText: String): String {
        val armorButtonStart = """<KeyMapping ItemName="$4Name""""
        val armorButtonEnd = """</KeyMapping>"""

        return removeButton(codmText, armorButtonStart, armorButtonEnd)
    }

    private fun removeButton(codmText: String, start: String, end: String): String {
        val startIndex = codmText.indexOf(start)
        if (startIndex == -1) return codmText

        val endIndex = codmText.indexOf(end, startIndex)
        if (endIndex == -1) return codmText

        val fullEndIndex = endIndex + end.length

        return codmText.substring(0, startIndex) + codmText.substring(fullEndIndex)
    }

    fun String.replaceKeys(keyData: KeyData): String {
        var text = this
        text = text.replace(("$" + keyData.id.substringBefore(key) + name), keyData.currentKey)
        text = text.replace(("$" + keyData.id.substringBefore(key) + CODE), keyData.currentCode.toString())
        return text
    }
    
    private data class RemoveButtonData(
        val id: String,
        val description: String,
        val simpleStart: String,
        val wheelStart: String
    )
}