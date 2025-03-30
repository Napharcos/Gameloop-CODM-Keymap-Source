package org.napharcos.gameloopcodmkeymap

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import gameloopcodmkeymap.composeapp.generated.resources.Res
import gameloopcodmkeymap.composeapp.generated.resources.*
import kotlinx.browser.window
import org.jetbrains.compose.resources.StringResource
import org.w3c.dom.get

var startTime = "startTime" to "1"
var defaultId = "id" to "1"
var tips = "tips" to "1"
var gameKey = "gameKey" to "1"
var transparent = "transparent" to "0.500000"
var lightness = "lightness" to "1.000000"
var switch = "switch" to "1"

var exitFullScreen = mutableStateOf(KeyData(fullScreenKey, Res.string.full_screen, "F11", 122, "F11", 122))

val topElements = listOf(
    1 to Res.string.mp,
    2 to Res.string.br,
    //3 to Res.string.z
)

//replaceKey, text,  baseKey, baseCode, currentKey, currentCode
val mpKeys = mutableStateListOf(
    KeyData(lockMouseKey, Res.string.lock_mouse, "Control", 17, loadKey(lockMouseKey) ?: "Control", loadKeyCode(lockMouseKey) ?: 17),
    KeyData(tabKey, Res.string.performance_panel, "Tab", 9, loadKey(tabKey) ?: "Tab", loadKeyCode(tabKey) ?: 9),
    KeyData(escKey, Res.string.menu, "Escape", 27, loadKey(escKey) ?: "Escape", loadKeyCode(escKey) ?: 27),
    KeyData(spaceKey, Res.string.jump, "Space", 32, loadKey(spaceKey) ?: "Space", loadKeyCode(spaceKey) ?: 32),
    KeyData(key1, Res.string.key1, "1", 49, loadKey(key1) ?: "1", loadKeyCode(key1) ?: 49),
    KeyData(key2, Res.string.key2, "2", 50, loadKey(key2) ?: "2", loadKeyCode(key2) ?: 50),
    KeyData(key3, Res.string.key3, "3", 51, loadKey(key3) ?: "3", loadKeyCode(key3) ?: 51),
    KeyData(key4, Res.string.key4, "4", 52, loadKey(key4) ?: "4", loadKeyCode(key4) ?: 52),
    KeyData(key5, Res.string.key5, "5", 53, loadKey(key5) ?: "5", loadKeyCode(key5) ?: 53),
    KeyData(key6, Res.string.key6, "6", 54, loadKey(key6) ?: "6", loadKeyCode(key6) ?: 54),
    KeyData(bKey, Res.string.keyb, "B", 66, loadKey(bKey) ?: "B", loadKeyCode(bKey) ?: 66),
    KeyData(cKey, Res.string.keyc, "C", 67, loadKey(cKey) ?: "C", loadKeyCode(cKey) ?: 67),
    KeyData(eKey, Res.string.keye, "E", 69, loadKey(eKey) ?: "E", loadKeyCode(eKey) ?: 69),
    KeyData(fKey, Res.string.keyf, "F", 70, loadKey(fKey) ?: "F", loadKeyCode(fKey) ?: 70),
    KeyData(gKey, Res.string.keyg, "G", 71, loadKey(gKey) ?: "G", loadKeyCode(gKey) ?: 71),
    KeyData(hKey, Res.string.keyh, "H", 72, loadKey(hKey) ?: "H", loadKeyCode(hKey) ?: 72),
    KeyData(mKey, Res.string.keym, "M", 77, loadKey(mKey) ?: "M", loadKeyCode(mKey) ?: 77),
    KeyData(qKey, Res.string.keyq, "Q", 81, loadKey(qKey) ?: "Q", loadKeyCode(qKey) ?: 81),
    KeyData(rKey, Res.string.keyr, "R", 82, loadKey(rKey) ?: "R", loadKeyCode(rKey) ?: 82),
    KeyData(vKey, Res.string.keyv, "V", 86, loadKey(vKey) ?: "V", loadKeyCode(vKey) ?: 86),
    KeyData(yKey, Res.string.keyy, "Z", 90, loadKey(yKey) ?: "Z", loadKeyCode(yKey) ?: 90),
    KeyData(zKey, Res.string.keyz, "Y", 89, loadKey(zKey) ?: "Y", loadKeyCode(zKey) ?: 89),
    KeyData(f4Key, Res.string.keyf4, "F4", 115, loadKey(f4Key) ?: "F4", loadKeyCode(f4Key) ?: 115),
    KeyData(oKey, Res.string.keyo, "=", 187, loadKey(oKey) ?: "=", loadKeyCode(oKey) ?: 187),
    KeyData(xKey, Res.string.keyx, "X", 88, loadKey(xKey) ?: "X", loadKeyCode(xKey) ?: 88),
    KeyData(tKey, Res.string.keyt, "T", 84, loadKey(tKey) ?: "T", loadKeyCode(tKey) ?: 84)
)

val brKeys = mutableStateListOf(
    KeyData(tabKey, Res.string.br_tabkey, "Tab", 9, loadBrKey(tabKey) ?: "Tab", loadBrKeyCode(tabKey) ?: 9),
    KeyData(shiftKey, Res.string.br_shiftkey, "Shift", 16, loadBrKey(shiftKey) ?: "Shift", loadBrKeyCode(shiftKey) ?: 16),
    KeyData(escKey, Res.string.br_esckey, "Escape", 27, loadBrKey(escKey) ?: "Escape", loadBrKeyCode(escKey) ?: 27),
    KeyData(spaceKey, Res.string.br_spacekey, "Space", 32, loadBrKey(spaceKey) ?: "Space", loadBrKeyCode(spaceKey) ?: 32),
    KeyData(key1, Res.string.br_key1, "1", 49, loadBrKey(key1) ?: "1", loadBrKeyCode(key1) ?: 49),
    KeyData(key2, Res.string.br_key2, "2", 50, loadBrKey(key2) ?: "2", loadBrKeyCode(key2) ?: 50),
    KeyData(bKey, Res.string.br_keyb, "B", 66, loadBrKey(bKey) ?: "B", loadBrKeyCode(bKey) ?: 66),
    KeyData(cKey, Res.string.br_keyc, "C", 67, loadBrKey(cKey) ?: "C", loadBrKeyCode(cKey) ?: 67),
    KeyData(eKey, Res.string.br_keye, "E", 69, loadBrKey(eKey) ?: "E", loadBrKeyCode(eKey) ?: 69),
    KeyData(fKey, Res.string.br_keyf, "F", 70, loadBrKey(fKey) ?: "F", loadBrKeyCode(fKey) ?: 70),
    KeyData(hKey, Res.string.br_keyh, "H", 72, loadBrKey(hKey) ?: "H", loadBrKeyCode(hKey) ?: 72),
    KeyData(mKey, Res.string.br_keym, "M", 77, loadBrKey(mKey) ?: "M", loadBrKeyCode(mKey) ?: 77),
    KeyData(qKey, Res.string.br_keyq, "Q", 81, loadBrKey(qKey) ?: "Q", loadBrKeyCode(qKey) ?: 81),
    KeyData(rKey, Res.string.br_keyr, "R", 82, loadBrKey(rKey) ?: "R", loadBrKeyCode(rKey) ?: 82),
    KeyData(vKey, Res.string.br_keyv, "V", 86, loadBrKey(vKey) ?: "V", loadBrKeyCode(vKey) ?: 86),
    KeyData(yKey, Res.string.br_keyy, "Z", 90, loadBrKey(yKey) ?: "Z", loadBrKeyCode(yKey) ?: 90),
    KeyData(zKey, Res.string.br_keyz, "Y", 89, loadBrKey(zKey) ?: "Y", loadBrKeyCode(zKey) ?: 89),
    KeyData(f4Key, Res.string.br_keyf4, "F4", 115, loadBrKey(f4Key) ?: "F4", loadBrKeyCode(f4Key) ?: 115),
    KeyData(oKey, Res.string.br_keyo, "=", 187, loadBrKey(oKey) ?: "=", loadBrKeyCode(oKey) ?: 187),
    KeyData(lockMouseKey, Res.string.br_lock_mouse, "Control", 17, loadBrKey(lockMouseKey) ?: "Control", loadBrKeyCode(lockMouseKey) ?: 17),
    KeyData(altKey, Res.string.br_alt_key, "Alt", 18, loadBrKey(altKey) ?: "Alt", loadBrKeyCode(altKey) ?: 18),
    KeyData(xKey, Res.string.br_keyx, "X", 88, loadBrKey(xKey) ?: "X", loadBrKeyCode(xKey) ?: 88),
    KeyData(tKey, Res.string.br_keyt, "T", 84, loadBrKey(tKey) ?: "T", loadBrKeyCode(tKey) ?: 84),
    KeyData(key4, Res.string.br_key4, "4", 52, loadBrKey(key4) ?: "4", loadBrKeyCode(key4) ?: 52),
    KeyData(key3, Res.string.br_key3, "3", 51, loadBrKey(key3) ?: "3", loadBrKeyCode(key3) ?: 51),
    KeyData(gKey, Res.string.br_keyg, "G", 71, loadBrKey(gKey) ?: "G", loadBrKeyCode(gKey) ?: 71),
)

fun loadKeyCode(id: String) = window.localStorage[id + code]?.toInt()

fun loadBrKeyCode(id: String) = window.localStorage[BR + id + code]?.toInt()

fun loadKey(id: String) = window.localStorage[id]

fun loadBrKey(id: String) = window.localStorage[BR + id]

data class KeyData(
    val id: String,
    val text: StringResource,
    val baseKey: String,
    val baseCode: Int,
    val currentKey: String,
    val currentCode: Int
)