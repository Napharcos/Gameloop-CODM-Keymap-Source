package org.napharcos.gameloopcodmkeymap

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import org.napharcos.gameloopcodmkeymap.theme.Padding
import org.napharcos.gameloopcodmkeymap.theme.greenButton
import org.napharcos.gameloopcodmkeymap.theme.greenButtonText
import gameloopcodmkeymap.composeapp.generated.resources.Res
import gameloopcodmkeymap.composeapp.generated.resources.*
import kotlinx.browser.window
import kotlinx.coroutines.awaitCancellation
import org.jetbrains.compose.resources.stringResource
import org.napharcos.gameloopcodmkeymap.SelectableRadioButton
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import kotlin.js.Promise

@Composable
fun MainScreen(
    uiState: UiState,
    viewModel: ViewModel
) {
    Box(
        modifier = Modifier
            .width(672.dp)
            .padding(Padding.medium),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Title()

            TopElements(
                uiState = uiState,
                viewModel = viewModel
            )
            ChangeInfo(
                when (uiState.selectedTopElement) {
                    Mod.MP -> stringResource(Res.string.mp_change_log)
                    Mod.BR -> stringResource(Res.string.br_change_log)
                    Mod.DMZ -> stringResource(Res.string.dmz_change_log)
                }
            )
            RadioCardElement(
                uiState = uiState,
                viewModel = viewModel
            )
            SelectableElements(
                uiState = uiState,
                viewModel = viewModel
            )
            Elements(
                elements = when (uiState.selectedTopElement) {
                    Mod.MP -> mpKeys
                    Mod.BR -> brKeys
                    Mod.DMZ -> dmzKeys
                },
                viewModel = viewModel,
                uiState = uiState
            )
            ChangeInfo(
                text = stringResource(Res.string.download_info)
            )
            DownloadButton(
                uiState = uiState,
                viewModel = viewModel
            )
            Tip(
                text = stringResource(Res.string.tip),
                small = true,
                copyText = stringResource(Res.string.copy),
                path = "\"C:\\Program Files\\Tencent\\GameLoop\\Application\\GameLoopLauncher.exe\" --launch-proc-name GameLoopEmulator.exe --launch-pkg-name com.activision.callofduty.shooter --from 8",
                viewModel = viewModel
            )
            Tip(
                text = stringResource(Res.string.tip_2),
                small = true,
                copyText = stringResource(Res.string.copy),
                path = "\"C:\\Program Files\\Tencent\\GameLoop\\Application\\GameLoopLauncher.exe\" --launch-proc-name GameLoopEmulator.exe --launch-pkg-name com.android.settings --from 8",
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun SelectableElements(
    uiState: UiState,
    viewModel: ViewModel
) {
    if (uiState.selectedTopElement != Mod.DMZ)
        SelectableCardElement(
            text = when (uiState.selectedTopElement) {
                Mod.MP -> stringResource(Res.string.hip_fire)
                Mod.BR -> stringResource(Res.string.hip_fire_br)
            },
            checked = uiState.replaceFire
        ) { viewModel.onReplaceFireClick(!uiState.replaceFire) }

    if (uiState.selectedTopElement == Mod.BR) {
        SelectableCardElement(
            text = stringResource(Res.string.br_armor_text),
            checked = uiState.brArmorButton
        ) { viewModel.switchArmorButton(!uiState.brArmorButton) }
        SelectableCardElement(
            text = stringResource(Res.string.buy_station),
            checked = uiState.separateBuyStationBR
        ) { viewModel.switchBuyStationButton(!uiState.separateBuyStationBR) }
    } else if (uiState.selectedTopElement == Mod.DMZ) {
        SelectableCardElement(
            text = stringResource(Res.string.buy_station),
            checked = uiState.separateBuyStationDMZ
        ) { viewModel.switchBuyStationButton(!uiState.separateBuyStationDMZ) }
    }
}

@Composable
fun SelectableCardElement(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
        colors = CardDefaults.cardColors().copy(
            containerColor = Color.Transparent,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(
                top = Padding.mini,
                bottom = Padding.mini,
            )
    ) {
        Row(
            modifier = Modifier
                .padding(Padding.small)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight()
                    .padding(
                        start = Padding.small,
                        end = Padding.small,
                    ),
                contentAlignment = Alignment.Center
            ) {
                SelectionContainer {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = Padding.small,
                        end = Padding.small,
                    ),
                contentAlignment = Alignment.Center
            ) {
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            }
        }
    }
}

@Composable
fun RadioCardElement(
    uiState: UiState,
    viewModel: ViewModel
) {
    Card(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
        colors = CardDefaults.cardColors().copy(
            containerColor = Color.Transparent,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(
                top = Padding.mini,
                bottom = Padding.mini,
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Padding.large),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScreenRatio.entries.forEach {
                if (it == ScreenRatio.R_16_9)
                    SelectableRadioButton(
                        text = it.displayName,
                        selected = uiState.screenRatio == it,
                        onClick = {
                            viewModel.changeScreenRatio(it)
                        }
                    )
            }
            Spacer(modifier = Modifier.width(1.dp))
            Spacer(modifier = Modifier.width(1.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectableRadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        SelectionContainer(
            modifier = Modifier.onClick(onClick = onClick)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    }
}

@Composable
fun Elements(
    elements: List<KeyData>,
    viewModel: ViewModel,
    uiState: UiState
) {
    elements.forEach {
        CardElement(
            keys = it,
            viewModel = viewModel,
            uiState = uiState
        )
    }
}

@Composable
fun CardElement(
    keys: KeyData,
    uiState: UiState,
    viewModel: ViewModel
) {
    var focused by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(focused) {
        if (focused) {
            val keyListener: (Event) -> Unit = { event ->
                event as KeyboardEvent
                when (uiState.selectedTopElement) {
                    Mod.MP -> viewModel.changeMpKey(
                        id = keys.id,
                        key = if (event.key != " ") event.key.replaceFirstChar { c -> c.uppercaseChar() } else event.code,
                        code = event.which
                    )
                    Mod.BR -> viewModel.changeBrKey(
                        id = keys.id,
                        key = if (event.key != " ") event.key.replaceFirstChar { c -> c.uppercaseChar() } else event.code,
                        code = event.which
                    )
                    Mod.DMZ -> viewModel.changeDmzKey(
                        id = keys.id,
                        key = if (event.key != " ") event.key.replaceFirstChar { c -> c.uppercaseChar() } else event.code,
                        code = event.which
                    )
                }

                focusManager.clearFocus()
                focused = false
            }

            window.addEventListener("keydown", keyListener)

            try {
                awaitCancellation()
            } finally {
                window.removeEventListener("keydown", keyListener)
            }
        }
    }

    Card(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
        colors = CardDefaults.cardColors().copy(
            containerColor = Color.Transparent,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(if (keys.wheelAvailable) 110.dp else 70.dp)
            .padding(
                top = Padding.mini,
                bottom = Padding.mini,
            )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(Padding.mini)
                    .height(60.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .fillMaxHeight()
                        .padding(
                            start = Padding.small,
                            end = Padding.small,
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    SelectionContainer {
                        Text(
                            text = stringResource(keys.text),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight()
                        .padding(
                            start = Padding.small,
                            end = Padding.small,
                        )
                ) {
                    OutlinedTextField(
                        value = keys.currentKey,
                        onValueChange = {},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focused = it.isFocused }
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = Padding.small,
                            end = Padding.small,
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            when (uiState.selectedTopElement) {
                                Mod.MP -> viewModel.changeMpKey(keys.id, keys.baseKey, keys.baseCode)
                                Mod.BR -> viewModel.changeBrKey(keys.id, keys.baseKey, keys.baseCode)
                                Mod.DMZ -> viewModel.changeDmzKey(keys.id, keys.baseKey, keys.baseCode)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(10),
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = Color.Blue
                        )
                    ) {
                        Text(
                            text = stringResource(Res.string.reset),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = greenButtonText,
                            ),
                            maxLines = 1
                        )
                    }
                }
            }

            if (keys.wheelAvailable) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(end = Padding.large),
                    horizontalArrangement = Arrangement.End
                ) {
                    SelectableRadioButton(
                        text = stringResource(Res.string.rotary_key),
                        selected = keys.applyWheel,
                        onClick = { viewModel.updateApplyWheel(uiState.selectedTopElement, keys.id, true) }
                    )
                    SelectableRadioButton(
                        text = stringResource(Res.string.simple_key),
                        selected = !keys.applyWheel,
                        onClick = { viewModel.updateApplyWheel(uiState.selectedTopElement, keys.id, false) }
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadButton(
    uiState: UiState,
    viewModel: ViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = Padding.small,
                bottom = Padding.small,
            ),
    ) {
        Button(
            onClick = {
                val mod = uiState.selectedTopElement
                viewModel.onDownloadClick(
                    replaceFire = uiState.replaceFire,
                    mod = mod,
                    ratio = uiState.screenRatio,
                    brArmor = mod == Mod.BR && uiState.brArmorButton,
                    buyStation = (mod == Mod.BR && uiState.separateBuyStationBR) || (mod == Mod.DMZ && uiState.separateBuyStationDMZ)
                )
                logDownloadEvent(uiState.selectedTopElement)
            },
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = greenButton,
                contentColor = greenButtonText
            ),
            enabled = !uiState.selectedTopElement.getKeys().any { it.currentKey == "" },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(Res.string.download),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = greenButtonText,
                )
            )
        }
    }
}

fun logDownloadEvent(mod: Mod) {
    val name = mod.displayName
//    gtag("event", "download-$name")
}

@Composable
fun ChangeInfo(
    text: String,
    small: Boolean = false,
) {
    val scrollState = rememberScrollState(0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 250.dp)
            .padding(
                top = Padding.medium,
                bottom = Padding.medium
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 0.dp, max = 250.dp)
                .verticalScroll(scrollState)
        ) {
            SelectionContainer {
                Text(
                    text = text.trimIndent(),
                    style = if (small) MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ) else MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
        if (scrollState.maxValue != 0)
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState),
                style = LocalScrollbarStyle.current.copy(
                    unhoverColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    hoverColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
            )
    }
}

@Composable
fun TopElements(
    uiState: UiState,
    viewModel: ViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = Padding.small,
                bottom = Padding.small
            ),
        horizontalArrangement = Arrangement.spacedBy(Padding.small),
    ) {
        topElements.forEach {
            TopElement(
                onClick = { viewModel.onTopElementClick(it.first) },
                selected = uiState.selectedTopElement == it.first,
                title = stringResource(it.second)
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun TopElement(
    onClick: () -> Unit,
    selected: Boolean,
    title: String
) {
    var onEnter by remember { mutableStateOf(false) }

    val borderColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .onClick { onClick() }
            .onPointerEvent(PointerEventType.Enter) { onEnter = true }
            .onPointerEvent(PointerEventType.Exit) { onEnter = false }
            .pointerHoverIcon(PointerIcon.Hand)
            .then(
                if (onEnter || selected) Modifier.drawBehind {
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 2.dp.toPx()
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(Padding.small)
        )
    }
}

@Composable
fun Title() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = Padding.large,
                bottom = Padding.large,
            ),
        contentAlignment = Alignment.Center
    ) {
        SelectionContainer {
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
@Composable
fun Tip(
    text: String,
    copyText: String,
    path: String,
    small: Boolean = false,
    viewModel: ViewModel
) {
    val annotatedString = buildAnnotatedString {
        append(text.substringBefore(copyText))

        withLink(
            LinkAnnotation.Clickable(
                tag = "copy_path",
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = Color(0xFF2196F3),
                        fontWeight = FontWeight.Bold
                    )
                ),
                linkInteractionListener = {
                    try {
                        window.navigator.clipboard.writeText(path)
                        viewModel.showCopied(true)
                    } catch (e: Exception) {
                        println(e)
                    }
                }
            )
        ) {
            append(copyText)
        }
        append(text.substringAfter(copyText))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = Padding.medium,
                bottom = Padding.mini
            )
    ) {
        SelectionContainer {
            Text(
                text = annotatedString,
                style = if (small) MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ) else MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}