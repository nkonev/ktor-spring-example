package com.example.ktor.ktorspringexample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
class MyApplication

fun main(args: Array<String>) {
    SpringApplicationBuilder()
            .sources(MyApplication::class.java)
            .run(*args)
}
