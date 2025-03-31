package org.napharcos.gameloopcodmkeymap.theme

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import gameloopcodmkeymap.composeapp.generated.resources.*
import kotlinx.browser.window
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import org.napharcos.gameloopcodmkeymap.theme.res.currentComposeLanguage
import org.napharcos.gameloopcodmkeymap.theme.res.getStringResource

val LocalLocalization = staticCompositionLocalOf { "en" }

@Composable
fun Localization (
    language: String = "en",
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalLocalization provides language,
        content = content
    )
}

val languages = listOf(
    "en" to Res.drawable.us,
    "hu" to Res.drawable.hu,
)

fun changeLanguage(language: String) {
    currentComposeLanguage = language
    saveLang(language)
}

fun saveLang(language: String) = window.localStorage.setItem("lang", language)


@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun LanguageElement(
    lang: String,
    changeLanguage: (String) -> Unit
) {
    val density = LocalDensity.current

    var onEnter by remember { mutableStateOf(false) }

    var isExpanded by remember { mutableStateOf(false) }

    var position by remember { mutableStateOf(IntOffset.Zero) }

    var elementsHeight by remember { mutableStateOf(0.dp) }

    Column(
        modifier = Modifier
            .height(64.dp)
            .onSizeChanged {
                elementsHeight = with(density) { it.height.toDp() }
            }
            .onGloballyPositioned {
                position = it.positionInRoot().run {
                    IntOffset(x.toInt(), y.toInt())
                }
            }
            .onClick { isExpanded = !isExpanded }
            .background(
                if (onEnter || isExpanded) MaterialTheme.colorScheme.surfaceContainerHigh
                else Color.Transparent
            )
            .padding(
                top = Padding.mini,
                bottom = Padding.mini,
                end = Padding.small,
                start = Padding.small
            )
            .onPointerEvent(PointerEventType.Enter) { onEnter = true }
            .onPointerEvent(PointerEventType.Exit) { onEnter = false }
            .pointerHoverIcon(PointerIcon.Hand),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.5f),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = when (lang) {
                    "en" -> vectorResource(Res.drawable.us)
                    "hu" -> vectorResource(Res.drawable.hu)
                    else -> vectorResource(Res.drawable.us)
                },
                contentDescription = getStringResource(Res.string.language),
                tint = Color.Unspecified
            )
        }
        Box(
            modifier = Modifier
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = lang.uppercase(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        if (isExpanded) {
            Popup(
                alignment = Alignment.TopStart,
                onDismissRequest = { isExpanded = false },
                properties = PopupProperties(focusable = true),
                offset = IntOffset(x = (-with(density) { Padding.small.toPx().toInt() }), y = position.y + position.y + 12)
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Spacer(Modifier.size(Padding.mini))
                    languages.forEach {
                        LanguageDropDownElement(
                            onClick = {
                                isExpanded = false
                                changeLanguage(it.first)
                            },
                            lang = it.first,
                            currentHeight = elementsHeight,
                            vector = it.second
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun LanguageDropDownElement(
    onClick: () -> Unit,
    lang: String,
    currentHeight: Dp,
    vector: DrawableResource
) {
    var onEnter by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .height(currentHeight)
            .onClick { onClick() }
            .background(
                if (onEnter) MaterialTheme.colorScheme.surfaceContainerHigh
                else MaterialTheme.colorScheme.surfaceContainer
            )
            .padding(
                top = Padding.mini,
                bottom = Padding.mini,
                end = Padding.small,
                start = Padding.small
            )
            .onPointerEvent(PointerEventType.Enter) { onEnter = true }
            .onPointerEvent(PointerEventType.Exit) { onEnter = false }
            .pointerHoverIcon(PointerIcon.Hand),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.5f),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(vector),
                contentDescription = getStringResource(Res.string.language),
                tint = Color.Unspecified
            )
        }
        Box(
            modifier = Modifier
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = lang.uppercase(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}