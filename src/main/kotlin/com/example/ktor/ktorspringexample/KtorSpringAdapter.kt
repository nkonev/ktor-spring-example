package com.example.ktor.ktorspringexample;

import io.ktor.config.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.util.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.SmartLifecycle
import org.springframework.stereotype.Component
import kotlin.concurrent.thread

public val SpringApplicationContextKey: AttributeKey<ApplicationContext> = AttributeKey<ApplicationContext>("SpringApplicationContext")

@Component
class KtorSpringAdapter(val args: ApplicationArguments, val configurableApplicationContext : ConfigurableApplicationContext)
    :
//        CommandLineRunner,
//        ApplicationContextAware,
        SmartLifecycle {
//    lateinit var configurableApplicationContext: ConfigurableApplicationContext

    private val log = LoggerFactory.getLogger(KtorSpringAdapter::class.java)

    @Volatile
    private var running: Boolean = false


    private fun CIOApplicationEngine.Configuration.loadConfiguration(config: ApplicationConfig) {
        val deploymentConfig = config.config("ktor.deployment")
        loadCommonConfiguration(deploymentConfig)
        deploymentConfig.propertyOrNull("connectionIdleTimeoutSeconds")?.getString()?.toInt()?.let {
            connectionIdleTimeoutSeconds = it
        }
    }

//    @Autowired
//    lateinit var args: ApplicationArguments

//    override fun run(args: Array<String>) {
//        this.args = args
//    }

//    override fun setApplicationContext(applicationContext: ApplicationContext) {
//        this.configurableApplicationContext = applicationContext as ConfigurableApplicationContext
//    }

    /**
     * CIO engine entry point
     */
    override fun start() {
        val applicationEnvironment = commandLineEnvironment(args.sourceArgs)
        applicationEnvironment.application.attributes.put(SpringApplicationContextKey, configurableApplicationContext)
        val engine = CIOApplicationEngine(applicationEnvironment) { loadConfiguration(applicationEnvironment.config) }
        engine.addShutdownHook {
            log.info("Stopping Ktor engine")
            engine.stop(3000, 5000)
            log.info("Stopping Spring context")
            configurableApplicationContext.close()
        }
        this.running = true

        // prevents java process of shutdown
        thread(name = "ktorHolder") {
            engine.start(true)
        }
    }

    override fun stop() {
        this.running = false
    }

    override fun isRunning(): Boolean {
       return running
    }
}
