package org.napharcos.gameloopcodmkeymap

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import gameloopcodmkeymap.composeapp.generated.resources.Res
import gameloopcodmkeymap.composeapp.generated.resources.copyright
import gameloopcodmkeymap.composeapp.generated.resources.libraries
import gameloopcodmkeymap.composeapp.generated.resources.license
import gameloopcodmkeymap.composeapp.generated.resources.source
import kotlinx.browser.window
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.napharcos.gameloopcodmkeymap.theme.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun App() {
    val darkTheme by remember { mutableStateOf(true) }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = AppTypography(),
    ) {
        val scrollState = rememberScrollState(0)

        val viewModel = viewModel { ViewModel() }

        val uiState by viewModel.uiState.collectAsState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .width(1152.dp)
                        .padding(
                            top = Padding.medium,
                            bottom = Padding.medium
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                other = Modifier
                                    .border(1.dp, MaterialTheme.colorScheme.surfaceContainerHighest, Shape.small)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!uiState.showingLibraries && !uiState.showingLicense)
                            MainScreen(
                                uiState = uiState,
                                viewModel = viewModel
                            )
                        else if (uiState.showingLibraries) About()
                        else if (uiState.showingLicense) LicensePage()
                    }
                    Licenses(viewModel)
                }
            }
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
            if (uiState.showingLibraries)
                Box(
                    modifier = Modifier
                        .width(768.dp)
                        .padding(top = Padding.large,),
                    contentAlignment = Alignment.TopEnd
                ) {
                    var onEnter by remember { mutableStateOf(false) }

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .onPointerEvent(PointerEventType.Enter) {
                                onEnter = true
                            }
                            .onPointerEvent(PointerEventType.Exit) {
                                onEnter = false
                            }
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (onEnter) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent)
                            .padding(Padding.small)
                            .onClick {
                                viewModel.onLibrariesClick(false)
                                viewModel.onLicenseClick(false)
                            }
                    )
                }
        }
    }
}

@Composable
fun Licenses(
    viewModel: ViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            ClickableElement(
                text = stringResource(Res.string.license),
                onClick = {
                    viewModel.onLicenseClick(true)
                }
            )
            Text(
                text = stringResource(Res.string.copyright),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
        Column {
            ClickableElement(
                text = stringResource(Res.string.libraries),
                onClick = {
                    viewModel.onLibrariesClick(true)
                }
            )
            ClickableElement(
                text = stringResource(Res.string.source),
                onClick = {
                    window.open("https://github.com/Napharcos/Gameloop-CODM-Keymap", "_blank")
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClickableElement(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = textButtonLightBlue,
            textDecoration = TextDecoration.Underline
        ),
        modifier = Modifier
            .pointerHoverIcon(PointerIcon.Hand)
            .onClick { onClick() }
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LicensePage() {
    var license by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        license = Res.readBytes("files/LICENSE.txt").decodeToString()
    }

    Box(
        modifier = Modifier
            .width(672.dp)
            .padding(Padding.medium),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = license,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
        )
    }
}