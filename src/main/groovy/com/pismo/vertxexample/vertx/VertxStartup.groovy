package com.pismo.vertxexample.vertx

import com.pismo.vertxexample.vertx.http.HttpServerVerticle
import com.pismo.vertxexample.vertx.mysql.MySqlVerticle
import groovy.util.logging.Slf4j
import io.vertx.reactivex.core.Vertx
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
@Slf4j
class VertxStartup {

    @Autowired
    Vertx vertx

    @PostConstruct
    void initVerticles() {
        log.info('Initiating verticles...')

        vertx
                .rxDeployVerticle("spring:${MySqlVerticle.name}")

                .flatMap {
                    vertx.rxDeployVerticle("spring:${HttpServerVerticle.name}")
                }

                .subscribe({
                    log.info('Verticles up!')
                })

                { e ->
                    log.error('Error while initing verticles.', e)
                    System.exit(-1)
                }
    }

}
