package com.pismo.vertxexample.tests

import com.pismo.vertxexample.Application
import com.pismo.vertxexample.fixtures.FixtureLoader
import com.pismo.vertxexample.vertx.mysql.MySqlVerticle
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
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.jdbc.datasource.init.ScriptUtils
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import spock.lang.Specification

import javax.annotation.PostConstruct
import javax.sql.DataSource

@Configuration
@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(classes = [
        DBIntegrationBase, Application ])
@ActiveProfiles('test')
abstract class DBIntegrationBase extends Specification {

    static final JDBC_DRIVER_CLASS = 'org.mariadb.jdbc.Driver'

    static final DB_CONFIG = [
            host: 'localhost',
            port: 3306,
            database: 'pismolabs',
            username: 'pismo',
            password: 'pismo'
    ]

    @Autowired
    Vertx vertx

    @Autowired
    DataSource jdbcDataSource

    @Value('classpath:schema.sql')
    Resource schema

    @PostConstruct
    void init() {
        vertx
                .rxDeployVerticle("spring:${MySqlVerticle.name}")
                .blockingGet()

        FixtureLoader.loadTemplates()

        def connection = jdbcDataSource.getConnection()
        ScriptUtils.executeSqlScript(connection, schema)
    }

    @Bean
    AsyncSQLClient dataSource(Vertx vertx) {
        MySQLClient.createShared(vertx, DB_CONFIG as JsonObject)
    }

    @Bean
    DataSource jdbcDataSource() {
        new DriverManagerDataSource([
                driverClassName: JDBC_DRIVER_CLASS,
                url: "jdbc:mysql://${DB_CONFIG.host}:${DB_CONFIG.port}/${DB_CONFIG.database}",
                username: DB_CONFIG.username,
                password: DB_CONFIG.password,
        ])
    }

    @Bean
    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() throws IOException {
        Resource configFile =
                new FileSystemResource(
                        DBIntegrationBase.class.getClassLoader()
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

}
