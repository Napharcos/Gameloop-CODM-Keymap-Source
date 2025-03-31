package org.napharcos.gameloopcodmkeymap.theme.res

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.compose.resources.InternalResourceApi
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private val SimpleStringFormatRegex = Regex("""%(\d)\$[ds]""")
internal fun String.replaceWithArgs(args: List<String>) = SimpleStringFormatRegex.replace(this) { matchResult ->
    args[matchResult.groupValues[1].toInt() - 1]
}

internal sealed interface StringItem {
    data class Value(val text: String) : StringItem
    data class Plurals(val items: Map<PluralCategory, String>) : StringItem
    data class Array(val items: List<String>) : StringItem
}

private val stringItemsCache = AsyncCache<String, StringItem>()

@OptIn(InternalResourceApi::class)
internal suspend fun getStringItem(
    resourceItem: ResourceItem,
    resourceReader: ResourceReader
): StringItem = stringItemsCache.getOrLoad(
    key = "${resourceItem.path}/${resourceItem.offset}-${resourceItem.size}"
) {
    val record = resourceReader.readPart(
        resourceItem.path,
        resourceItem.offset,
        resourceItem.size
    ).decodeToString()
    val recordItems = record.split('|')
    val recordType = recordItems.first()
    val recordData = recordItems.last()
    when (recordType) {
        "plurals" -> recordData.decodeAsPlural()
        "string-array" -> recordData.decodeAsArray()
        else -> recordData.decodeAsString()
    }
}

@OptIn(ExperimentalEncodingApi::class)
private fun String.decodeAsString(): StringItem.Value = StringItem.Value(
    Base64.decode(this).decodeToString()
)

@OptIn(ExperimentalEncodingApi::class)
private fun String.decodeAsArray(): StringItem.Array = StringItem.Array(
    split(",").map { item ->
        Base64.decode(item).decodeToString()
    }
)

@OptIn(ExperimentalEncodingApi::class)
private fun String.decodeAsPlural(): StringItem.Plurals = StringItem.Plurals(
    split(",").associate { item ->
        val category = item.substringBefore(':')
        val valueBase64 = item.substringAfter(':')
        PluralCategory.fromString(category)!! to Base64.decode(valueBase64).decodeToString()
    }
)

internal enum class PluralCategory {
    ZERO,
    ONE,
    TWO,
    FEW,
    MANY,
    OTHER;

    companion object {
        fun fromString(name: String): PluralCategory? {
            return entries.firstOrNull {
                it.name.equals(name, true)
            }
        }
    }
}

internal class AsyncCache<K, V> {
    private val mutex = Mutex()
    private val cache = mutableMapOf<K, Deferred<V>>()

    suspend fun getOrLoad(key: K, load: suspend () -> V): V = coroutineScope {
        val deferred = mutex.withLock {
            var cached = cache[key]
            if (cached == null || cached.isCancelled) {
                //LAZY - to free the mutex lock as fast as possible
                cached = async(start = CoroutineStart.LAZY) { load() }
                cache[key] = cached
            }
            cached
        }
        deferred.await()
    }

    //@TestOnly
    fun clear() {
        cache.clear()
    }
}