package com.example.ktor.ktorspringexample;

import io.ktor.samples.sandbox.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.SmartLifecycle
import org.springframework.stereotype.Component
import java.util.concurrent.Executors

public val SpringApplicationContextKey: AttributeKey<ApplicationContext> = AttributeKey<ApplicationContext>("SpringApplicationContext")

@Component
class KtorSpringAdapter(val configurableApplicationContext : ConfigurableApplicationContext) : SmartLifecycle {

    private val log = LoggerFactory.getLogger(KtorSpringAdapter::class.java)

    @Volatile
    private var running: Boolean = false

    private lateinit var embeddedServer: CIOApplicationEngine

    // Initialize Ktor engine
    override fun start() {
        // we use non-daemonized threads for Ktor's coroutines in order to application won't exit by itself until user calls kill -2
        val nonDaemonizedCoroutineDispatcher = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).asCoroutineDispatcher()

        CoroutineScope(nonDaemonizedCoroutineDispatcher).launch {
            embeddedServer = embeddedServer(CIO, port = 8098, configure = {  } ) {
                springConfig(configurableApplicationContext)
                webConfig()
                routes()
            }
            embeddedServer.start(wait = false)
            running = true
        }
        log.info("Ktor engine is staring in another threads")
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
