package org.napharcos.gameloopcodmkeymap.theme.res

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.MissingResourceException
import org.jetbrains.compose.resources.Qualifier
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.fetch.Response
import org.w3c.files.Blob
import kotlin.js.Promise
import kotlin.wasm.unsafe.UnsafeWasmMemoryApi
import kotlin.wasm.unsafe.withScopedMemoryAllocator

@Immutable
sealed class Resource
@InternalResourceApi constructor(
    internal val id: String,
    internal val items: Set<ResourceItem>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Resource

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

@InternalResourceApi
@Immutable
data class ResourceItem(
    internal val qualifiers: Set<Qualifier>,
    internal val path: String,
    internal val offset: Long,
    internal val size: Long,
)

object WebResourcesConfiguration {
    internal var getResourcePath: (path: String) -> String = { "./$it" }

    fun resourcePathMapping(map: (path: String) -> String) {
        getResourcePath = map
    }
}

internal interface ResourceReader {
    suspend fun read(path: String): ByteArray
    suspend fun readPart(path: String, offset: Long, size: Long): ByteArray
    fun getUri(path: String): String
}

@JsFun(
    """ (src, size, dstAddr) => {
        const mem8 = new Int8Array(wasmExports.memory.buffer, dstAddr, size);
        mem8.set(src);
    }
"""
)
private external fun jsExportInt8ArrayToWasm(src: Int8Array, size: Int, dstAddr: Int)

@JsFun("(blob) => blob.arrayBuffer()")
private external fun jsExportBlobAsArrayBuffer(blob: Blob): Promise<ArrayBuffer>

internal fun getPlatformResourceReader(): ResourceReader = object : ResourceReader {
    override suspend fun read(path: String): ByteArray {
        return readAsBlob(path).asByteArray()
    }

    override suspend fun readPart(path: String, offset: Long, size: Long): ByteArray {
        val part = readAsBlob(path).slice(offset.toInt(), (offset + size).toInt())
        return part.asByteArray()
    }

    override fun getUri(path: String): String {
        val location = window.location
        return getResourceUrl(location.origin, location.pathname, path)
    }

    private suspend fun readAsBlob(path: String): Blob {
        val resPath = WebResourcesConfiguration.getResourcePath(path)
        val response = window.fetch(resPath).await<Response>()
        if (!response.ok) {
            throw MissingResourceException(resPath)
        }
        return response.blob().await()
    }

    private suspend fun Blob.asByteArray(): ByteArray {
        val buffer: ArrayBuffer = jsExportBlobAsArrayBuffer(this).await()
        return Int8Array(buffer).asByteArray()
    }

    private fun Int8Array.asByteArray(): ByteArray {
        val array = this
        val size = array.length

        @OptIn(UnsafeWasmMemoryApi::class)
        return withScopedMemoryAllocator { allocator ->
            val memBuffer = allocator.allocate(size)
            val dstAddress = memBuffer.address.toInt()
            jsExportInt8ArrayToWasm(array, size, dstAddress)
            ByteArray(size) { i -> (memBuffer + i).loadByte() }
        }
    }
}

internal fun getResourceUrl(windowOrigin: String, windowPathname: String, resourcePath: String): String {
    val path = WebResourcesConfiguration.getResourcePath(resourcePath)
    return when {
        path.startsWith("/") -> windowOrigin + path
        path.startsWith("http://") || path.startsWith("https://") -> path
        else -> windowOrigin + windowPathname + path
    }
}

internal val DefaultResourceReader = getPlatformResourceReader()

internal val LocalResourceReader = staticCompositionLocalOf { DefaultResourceReader }

internal val ProvidableCompositionLocal<ResourceReader>.currentOrPreview: ResourceReader
    @Composable get() = current

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun <T> rememberResourceState(
    key1: Any,
    key2: Any,
    getDefault: () -> T,
    block: suspend (Environment) -> T
): State<T> {
    val environment = LocalComposeEnvironment.current.rememberEnvironment()
    val scope = rememberCoroutineScope()
    return remember(key1, key2) {
        val mutableState = mutableStateOf(getDefault())
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            mutableState.value = block(environment)
        }
        mutableState
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun <T> rememberResourceState(
    key1: Any,
    key2: Any,
    key3: Any,
    getDefault: () -> T,
    block: suspend (Environment) -> T
): State<T> {
    val environment = LocalComposeEnvironment.current.rememberEnvironment()
    val scope = rememberCoroutineScope()
    return remember(key1, key2, key3) {
        val mutableState = mutableStateOf(getDefault())
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            mutableState.value = block(environment)
        }
        mutableState
    }
}