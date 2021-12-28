package io.ktor.samples.sandbox

import com.example.ktor.ktorspringexample.SpringApplicationContextKey
import com.mongodb.client.MongoCollection
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.ResolvableType
import redis.clients.jedis.JedisPool

data class Customer(val id: Int, val firstName: String, val lastName: String)

data class UserSession(val id: String, val count: Int)

data class Jedi(val name: String, val age: Int)

public fun Application.springContext() = attributes[SpringApplicationContextKey]

public fun <T>Application.getBean(requiredType: Class<T>) : T {
    return springContext().getBean(requiredType)
}

public fun <T>Application.getGenericBean(clazz: Class<*>, vararg generics : Class<*>) : T {
    val type : ResolvableType = ResolvableType.forClassWithGenerics(clazz, *generics)
    val provider: ObjectProvider<T> = springContext().getBeanProvider(type)
    return provider.getObject()
}

/**
 * Module that just registers the root path / and replies with a text.
 */
fun Application.springConfig(configurableApplicationContext: ConfigurableApplicationContext) {
    attributes.put(SpringApplicationContextKey, configurableApplicationContext)
}

fun Application.webConfig() {
    val env = environment.config.propertyOrNull("ktor.custom")?.getString()
    log.info("Got custom variable $env")

    // This adds automatically Date and Server headers to each response, and would allow you to configure
    // additional headers served to each response.
    install(DefaultHeaders)

    install(ContentNegotiation) {
        jackson()
    }
    install(Sessions) {
        val redisPool = getBean(JedisPool::class.java)

        cookie<UserSession>("user_session", storage = RedisSessionStorage(redisPool)) {
            serializer = JacksonSessionSerializer(UserSession::class.java)
        }
    }
}


fun Application.routes() {
    // can use kodein here
    routing {
        val collection: MongoCollection<Jedi> = getGenericBean(MongoCollection::class.java, Jedi::class.java)

        get("/") {
            call.respondText("Hello World!")
        }
        post("/customer") {
            call.respond(Customer(1, "Jet", "Brains"))
        }
        get("/login") {
            call.sessions.set(UserSession(id = "123abc", count = 0))
            call.respondRedirect("/")
        }
        get("/session") {
            val userSession: UserSession? = call.sessions.get<UserSession>()
            if (userSession != null) {
                call.respond(userSession)
            } else {
                call.respond(HttpStatusCode.Gone)
            }
        }
        get("/logout") {
            call.sessions.clear<UserSession>()
            call.respondRedirect("/")
        }

        post("/mongo") {
            log.info("Got mongo collection $collection")
            collection.insertOne(Jedi("Luke Skywalker", 19))
            call.respond(HttpStatusCode.OK)
        }

        get("/mongo") {
            val yoda : Jedi? = collection.findOne(Jedi::name eq "Luke Skywalker")
            if (yoda == null) {
                call.respond(HttpStatusCode.Gone)
            } else {
                call.respond(yoda)
            }
        }
    }
}