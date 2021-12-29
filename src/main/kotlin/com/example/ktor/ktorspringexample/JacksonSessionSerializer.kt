package io.ktor.samples.sandbox

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.sessions.*

class JacksonSessionSerializer<T>(
    private val type: Class<T>
): SessionSerializer<T> {
    private val jackson = ObjectMapper().registerModule(KotlinModule.Builder()
            .withReflectionCacheSize(512)
            .configure(KotlinFeature.NullToEmptyCollection, false)
            .configure(KotlinFeature.NullToEmptyMap, false)
            .configure(KotlinFeature.NullIsSameAsDefault, false)
            .configure(KotlinFeature.SingletonSupport, false)
            .configure(KotlinFeature.StrictNullChecks, false)
            .build())
    override fun deserialize(text: String): T {
        return jackson.readValue(text, type) as T
    }

    override fun serialize(session: T): String {
        return jackson.writeValueAsString(session)
    }
}