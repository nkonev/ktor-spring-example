package com.example.ktor.ktorspringexample;

import io.ktor.samples.sandbox.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.util.*
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.SmartLifecycle
import org.springframework.stereotype.Component
import kotlin.concurrent.thread

public val SpringApplicationContextKey: AttributeKey<ApplicationContext> = AttributeKey<ApplicationContext>("SpringApplicationContext")

@Component
class KtorSpringAdapter(val configurableApplicationContext : ConfigurableApplicationContext) : SmartLifecycle {

    private val log = LoggerFactory.getLogger(KtorSpringAdapter::class.java)

    @Volatile
    private var running: Boolean = false

    private lateinit var embeddedServer: CIOApplicationEngine

    /**
     * CIO engine entry point
     */
    override fun start() {
        embeddedServer = embeddedServer(CIO, port = 8098, configure = {  } ) {
            springConfig(configurableApplicationContext)
            webConfig()
            routes()
        }

        thread(name = "ktorHolder") {
            embeddedServer.start(wait = true)
        }
        this.running = true
    }

    override fun stop() {
        log.info("Stopping Ktor engine")
        embeddedServer.stop(3000, 5000)
        log.info("Ktor engine has been stopped")
        this.running = false
    }

    override fun isRunning(): Boolean {
       return running
    }
}
