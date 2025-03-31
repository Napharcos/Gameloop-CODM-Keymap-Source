package org.napharcos.gameloopcodmkeymap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import gameloopcodmkeymap.composeapp.generated.resources.Res
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.napharcos.gameloopcodmkeymap.theme.Padding

@Composable
fun About() {
    var libraries by remember { mutableStateOf<LibrariesResponse?>(null) }

    LaunchedEffect(Unit) {
        libraries = getAboutData()
    }

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
            libraries?.libraries.orEmpty().forEach {
                AboutElements(it)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AboutElements(
    libraries: Library,
) {
    var enter by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onPointerEvent(PointerEventType.Enter) {
                enter = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                enter = false
            }
            .background(if (enter) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.small),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Box {
                    SelectionContainer {
                        Text(
                            text = libraries.name,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
                Row {
                    libraries.developers.forEach {
                        val isLast = libraries.developers.last() == it
                        Box {
                            SelectionContainer {
                                Text(
                                    text = if (isLast) it.name else it.name + "; ",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    ),
                                )
                            }
                        }
                    }
                }
                Row {
                    libraries.licenses.forEach {
                        Box(
                            modifier = Modifier
                                .background(color = Color.Blue, shape = RoundedCornerShape(50))
                                .padding(
                                    start = Padding.mini,
                                    end = Padding.mini,
                                    top = Padding.mini / 2,
                                    bottom = Padding.mini / 2,
                                )
                        ) {
                            SelectionContainer {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(Padding.small).background(Color.Transparent))
                    }
                }
            }
            SelectionContainer {
                Text(
                    text = libraries.artifactVersion,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
suspend fun getAboutData(): LibrariesResponse? {
    val string = Res.readBytes("files/aboutlibraries.json").decodeToString()
    val json = Json { ignoreUnknownKeys = true }
    return json.decodeFromString<LibrariesResponse>(string)
}

@Serializable
data class LibrariesResponse(
    val libraries: List<Library>,
    val licenses: Map<String, License>
)

@Serializable
data class Library(
    val uniqueId: String,
    val funding: List<String> = emptyList(),
    val developers: List<Developer>,
    val artifactVersion: String,
    val description: String,
    val scm: Scm,
    val name: String,
    val website: String,
    val licenses: List<String>
)

@Serializable
data class Developer(
    val name: String,
    val organisationUrl: String? = null
)

@Serializable
data class Scm(
    val connection: String? = null,
    val url: String,
    val developerConnection: String? = null
)

@Serializable
data class License(
    val content: String,
    val hash: String,
    val internalHash: String,
    val url: String,
    val spdxId: String,
    val name: String
)