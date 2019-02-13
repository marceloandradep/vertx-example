package com.pismo.vertxexample.tests

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.pismo.vertxexample.Application
import com.pismo.vertxexample.fixtures.FixtureLoader
import com.pismo.vertxexample.vertx.http.HttpServerVerticle
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient
import io.vertx.reactivex.ext.asyncsql.MySQLClient
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.env.YamlPropertySourceLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import javax.annotation.PostConstruct

@Configuration
@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(classes = [
        HttpIntegrationBase, Application ])
@ActiveProfiles('test')
abstract class HttpIntegrationBase extends Specification {

    static final DB_CONFIG = [
            host: 'localhost',
            port: 3306,
            database: 'pismolabs',
            username: 'pismo',
            password: 'pismo'
    ]

    @Autowired
    Vertx vertx

    @Value('${http.port}')
    int port

    @PostConstruct
    void init() {
        vertx
                .rxDeployVerticle("spring:${HttpServerVerticle.name}")
                .blockingGet()

        FixtureLoader.loadTemplates()
    }

    @Bean
    AsyncSQLClient dataSource(Vertx vertx) {
        MySQLClient.createShared(vertx, DB_CONFIG as JsonObject)
    }

    @Bean
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate()

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory()
        restTemplate.setRequestFactory(requestFactory)

        restTemplate
                .getMessageConverters()

                .find { HttpMessageConverter c ->
                    c instanceof MappingJackson2HttpMessageConverter
                }

                .each { MappingJackson2HttpMessageConverter c ->
                    c.getObjectMapper()
                            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                }

        restTemplate
    }

    @Bean
    ObjectMapper objectMapper(RestTemplate restTemplate) {
        restTemplate
                .getMessageConverters()

                .find { HttpMessageConverter c ->
                    c instanceof MappingJackson2HttpMessageConverter
                }

                .with { HttpMessageConverter c ->
                    (c as MappingJackson2HttpMessageConverter)
                            .getObjectMapper()
                }
    }

    @Bean
    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() throws IOException {
        Resource configFile =
                new FileSystemResource(
                        HttpIntegrationBase.class.getClassLoader()
                                .getResource('application.yml').getPath())

        YamlPropertySourceLoader propertySourceLoader = new YamlPropertySourceLoader()

        def propertiesSources =
                propertySourceLoader.load('application', configFile)

        MutablePropertySources mps = new MutablePropertySources()

        propertiesSources.each {
            mps.addFirst(it)
        }

        PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer()
        c.setPropertySources(mps)

        return c
    }

    String urlBase() {
        "http://localhost:${port}"
    }

}
