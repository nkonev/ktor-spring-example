package com.example.ktor.ktorspringexample

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool

@Configuration
class RedisConfiguration {

    private val log = LoggerFactory.getLogger(RedisConfiguration::class.java)

    @Bean(destroyMethod = "close")
    fun jedisPool() : JedisPool {
        val jedisPoolConfig: GenericObjectPoolConfig<Jedis> = GenericObjectPoolConfig<Jedis>()
        // TODO timeouts

        log.info("Configuring redis pool")
        val closeLoggingPool = object : JedisPool(jedisPoolConfig, "localhost", 36379) {
            override fun close() {
                log.info("Closing redis pool")
                super.close()
            }
        }

        return closeLoggingPool
    }
}