package io.ktor.samples.sandbox

import io.ktor.sessions.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.ByteArrayOutputStream
import kotlin.coroutines.coroutineContext
import redis.clients.jedis.JedisPool

abstract class SimplifiedSessionStorage : SessionStorage {
    abstract suspend fun read(id: String): String?
    abstract suspend fun write(id: String, data: String?): Unit

    override suspend fun <R> read(id: String, consumer: suspend (ByteReadChannel) -> R): R {
        val data = read(id) ?: throw NoSuchElementException("Session $id not found")
        return consumer(ByteReadChannel(data))
    }

    override suspend fun write(id: String, provider: suspend (ByteWriteChannel) -> Unit) {
        return provider(CoroutineScope(Dispatchers.IO).reader(coroutineContext, autoFlush = true) {
            write(id, channel.readAvailable())
        }.channel)
    }
}

private suspend fun ByteReadChannel.readAvailable(): String {
    val data = ByteArrayOutputStream()
    val temp = ByteArray(1024)
    while (!isClosedForRead) {
        val read = readAvailable(temp)
        if (read <= 0) break
        data.write(temp, 0, read)
    }
    return String(data.toByteArray())
}

class RedisSessionStorage(private val jedisPool: JedisPool) : SimplifiedSessionStorage() {

    override suspend fun read(id: String): String? {
        jedisPool.resource.use {
            return it.get(id)
        }
    }

    override suspend fun write(id: String, data: String?) {
        jedisPool.resource.use {
            it.set(id, data)
        }
    }

    override suspend fun invalidate(id: String) {
        jedisPool.resource.use {
            it.del(id)
        }
    }
}