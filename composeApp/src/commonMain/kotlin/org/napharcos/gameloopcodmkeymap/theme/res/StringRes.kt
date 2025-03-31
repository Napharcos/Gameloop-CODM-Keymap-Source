package org.napharcos.gameloopcodmkeymap.theme.res

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale
import kotlinx.browser.window
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.InternalResourceApi

var currentComposeLanguage by mutableStateOf(window.localStorage.getItem("lang") ?: Locale.current.language)

@OptIn(InternalResourceApi::class)
@Immutable
class StringResource
@InternalResourceApi constructor(id: String, val key: String, items: Set<ResourceItem>) : Resource(id, items)

@OptIn(ExperimentalResourceApi::class, InternalResourceApi::class)
@Composable
fun getStringResource(resource: StringResource): String {
    val resourceReader = LocalResourceReader.currentOrPreview
    val str by rememberResourceState(currentComposeLanguage, resource, { "" }) { env ->
        loadString(resource, resourceReader, env)
    }
    return str
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun getStringResource(resource: StringResource, vararg formatArgs: Any): String {
    val resourceReader = LocalResourceReader.currentOrPreview
    val args = formatArgs.map { it.toString() }
    val str by rememberResourceState(currentComposeLanguage, resource, args, { "" }) { env ->
        loadString(resource, args, resourceReader, env)
    }
    return str
}

@OptIn(ExperimentalResourceApi::class, InternalResourceApi::class)
private suspend fun loadString(
    resource: StringResource,
    resourceReader: ResourceReader,
    environment: Environment
): String {
    val resourceItem = resource.getResourceItemByEnvironment(environment)
    val item = getStringItem(resourceItem, resourceReader) as StringItem.Value
    return item.text
}

@OptIn(ExperimentalResourceApi::class)
private suspend fun loadString(
    resource: StringResource,
    args: List<String>,
    resourceReader: ResourceReader,
    environment: Environment
): String {
    val str = loadString(resource, resourceReader, environment)
    return str.replaceWithArgs(args)
}