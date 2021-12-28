package com.example.ktor.ktorspringexample

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import io.ktor.samples.sandbox.*
import org.litote.kmongo.KMongo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.litote.kmongo.* //NEEDED! import KMongo extensions


@Configuration
class MongoConfiguration {

    @Bean(destroyMethod = "close")
    fun mongoClient() = KMongo.createClient("mongodb://localhost:27017")

    @Bean
    fun mongoDatabase(mongoClient: MongoClient) = mongoClient.getDatabase("test")

    @Bean
    fun mongoJediCollection(mongoDatabase: MongoDatabase) = mongoDatabase.getCollection<Jedi>()
}