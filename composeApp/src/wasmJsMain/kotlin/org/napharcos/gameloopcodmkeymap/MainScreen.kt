package org.napharcos.gameloopcodmkeymap

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.napharcos.gameloopcodmkeymap.theme.res.getStringResource
import org.napharcos.gameloopcodmkeymap.theme.Padding
import org.napharcos.gameloopcodmkeymap.theme.greenButton
import org.napharcos.gameloopcodmkeymap.theme.greenButtonText
import org.napharcos.gameloopcodmkeymap.theme.textButtonLightBlue
import gameloopcodmkeymap.composeapp.generated.resources.Res
import gameloopcodmkeymap.composeapp.generated.resources.*
import kotlinx.browser.window
import kotlinx.coroutines.awaitCancellation
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

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
            Upload(viewModel)

            if (ManageFile.showingOverrideMpAndBr) {
                OverrideMpBr()
            }

            TopElements(
                uiState = uiState,
                viewModel = viewModel
            )
            ChangeInfo(
                when (uiState.selectedTopElement) {
                    1 -> getStringResource(Res.string.mp_change)
                    2 -> getStringResource(Res.string.br_change)
                    3 -> getStringResource(Res.string.gd_change)
                    4 -> getStringResource(Res.string.dmz_change)
                    else -> ""
                }
            )
            if (uiState.selectedTopElement < 3) {
                SelectableElements(
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
            Elements(
                elements = when (uiState.selectedTopElement) {
                    1 -> mpKeys
                    2 -> brKeys
                    3 -> gundamKeys
                    4 -> dmzKeys
                    else -> emptyList()
                },
                viewModel = viewModel,
                uiState = uiState
            )
            ChangeInfo(
                text = getStringResource(Res.string.download_info)
            )
            DownloadButton(
                uiState = uiState,
                viewModel = viewModel
            )
            ChangeInfo(
                text = getStringResource(Res.string.tip),
                small = true
            )
        }
    }
}

@Composable
fun OverrideMpBr() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = ManageFile.overrideMpAndBr,
            onCheckedChange = { ManageFile.overrideMpAndBr = it },
        )
        SelectionContainer {
            Text(
                text = getStringResource(Res.string.override_text),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

@Composable
fun SelectableElements(
    uiState: UiState,
    viewModel: ViewModel
) {
    SelectableCardElement(
        text = when (uiState.selectedTopElement) {
            1 -> getStringResource(Res.string.hip_fire)
            2 -> getStringResource(Res.string.hip_fire_br)
            else -> ""
        },
        checked = when (uiState.selectedTopElement) {
            1 -> uiState.replaceMpFire
            2 -> uiState.replaceBrFire
            else -> false
        },
        onCheckedChange = {
            when (uiState.selectedTopElement) {
                1 -> viewModel.onReplaceMpFireClick(it)
                2 -> viewModel.onReplaceBrFireClick(it)
            }
        },
    )
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
                    1 -> viewModel.changeMpKey(
                        id = keys.id,
                        key = if (event.key != " ") event.key.replaceFirstChar { c -> c.uppercaseChar() } else event.code,
                        code = event.which
                    )
                    2 -> viewModel.changeBrKey(
                        id = keys.id,
                        key = if (event.key != " ") event.key.replaceFirstChar { c -> c.uppercaseChar() } else event.code,
                        code = event.which
                    )
                    3 -> viewModel.changeGdKey(
                        id = keys.id,
                        key = if (event.key != " ") event.key.replaceFirstChar { c -> c.uppercaseChar() } else event.code,
                        code = event.which
                    )
                    4 -> viewModel.changeDmzKey(
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
                        text = getStringResource(keys.text),
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
                            1 -> viewModel.changeMpKey(keys.id, keys.baseKey, keys.baseCode)
                            2 -> viewModel.changeBrKey(keys.id, keys.baseKey, keys.baseCode)
                            3 -> viewModel.changeGdKey(keys.id, keys.baseKey, keys.baseCode)
                            4 -> viewModel.changeDmzKey(keys.id, keys.baseKey, keys.baseCode)
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
                        text = getStringResource(Res.string.reset),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = greenButtonText,
                        )
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
                viewModel.onDownloadClick(uiState.replaceMpFire, uiState.replaceBrFire)
                logDownloadEvent()
            },
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = greenButton,
                contentColor = greenButtonText
            ),
            enabled = !mpKeys.any { it.currentKey == "" },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = getStringResource(Res.string.download),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = greenButtonText,
                )
            )
        }
    }
}

fun logDownloadEvent() {
    js("gtag('event', 'download')")
}

@Composable
fun ChangeInfo(
    text: String,
    small: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = Padding.small,
                bottom = Padding.small
            ),
    ) {
        SelectionContainer {
            Text(
                text = text,
                style = if (small) MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ) else MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
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
                title = getStringResource(it.second)
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
fun Upload(
    viewModel: ViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.77f),
            ) {
                SelectionContainer {
                    Text(
                        text = getStringResource(Res.string.upload_info_1),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .padding(Padding.small)
            ) {
                Button(
                    onClick = { viewModel.onUploadClick() },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = greenButton,
                        contentColor = greenButtonText
                    )
                ) {
                    Text(
                        text = getStringResource(Res.string.upload),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = greenButtonText,
                        )
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Padding.small)
        ) {
            SelectionContainer {
                Text(
                    text = getStringResource(Res.string.upload_info_2),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
        if (ManageFile.startText != null && ManageFile.endText != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getStringResource(Res.string.upload_complete),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = textButtonLightBlue,
                    )
                )
            }
        }
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
                text = getStringResource(Res.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    }
}