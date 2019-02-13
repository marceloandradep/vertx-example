package com.pismo.vertxexample.vertx

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.pismo.vertxexample.vertx.factories.SpringVerticleFactory
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient
import io.vertx.reactivex.ext.asyncsql.MySQLClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class VertxConfiguration {

    @Bean
    Vertx vertx(SpringVerticleFactory springVerticleFactory) {
        Vertx vertx = Vertx.vertx()
        vertx.getDelegate().registerVerticleFactory(springVerticleFactory)
        vertx
    }

    @Value('${database.host}')
    String host
    
    @Value('${database.port}')
    int port
    
    @Value('${database.name}')
    String database
    
    @Value('${database.username}')
    String username
    
    @Value('${database.password}')
    String password

    @Bean
    AsyncSQLClient dataSource(Vertx vertx) {

        def dbConfig = [
                host: host,
                port: port,
                database: database,
                username: username,
                password: password
        ]

        MySQLClient.createShared(vertx, dbConfig as JsonObject)
    }
    
    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper()
        
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        
        mapper
    }

}