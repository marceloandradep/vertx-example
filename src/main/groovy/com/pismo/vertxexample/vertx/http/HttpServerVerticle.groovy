package com.pismo.vertxexample.vertx.http

import com.pismo.vertxexample.domain.Beer
import com.pismo.vertxexample.domain.Brewery
import com.pismo.vertxexample.handlers.BeerHandler
import com.pismo.vertxexample.handlers.BreweryHandler
import com.pismo.vertxexample.handlers.FailureHandler
import groovy.util.logging.Slf4j
import io.vertx.core.Future
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.core.http.HttpServer
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.api.validation.HTTPRequestValidationHandler
import io.vertx.reactivex.ext.web.handler.BodyHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
@Slf4j
class HttpServerVerticle extends AbstractVerticle {

    static final CONTENT_TYPE_APPLICATION_JSON = 'application/json'

    @Value('${http.port}')
    int httpPort
    
    @Autowired
    FailureHandler failureHandler
    
    @Autowired
    BreweryHandler breweryHandler
    
    @Autowired
    BeerHandler beerHandler

    @Override
    void start(Future<Void> startFuture) throws Exception {
        HttpServer server = this.@vertx.createHttpServer()
        Router router = Router.router(this.@vertx)

        router.route()
                .handler(BodyHandler.create())
                .failureHandler(failureHandler.handle)

        router
                .post('/v1/breweries')
                .consumes(CONTENT_TYPE_APPLICATION_JSON)
                .produces(CONTENT_TYPE_APPLICATION_JSON)
                .handler(HTTPRequestValidationHandler.create().addJsonBodySchema(Brewery.POST_SCHEMA))
                .handler(breweryHandler.post)

        router
                .get('/v1/breweries/:name')
                .produces(CONTENT_TYPE_APPLICATION_JSON)
                .handler(breweryHandler.get)

        router
                .post('/v1/breweries/:name/beers')
                .consumes(CONTENT_TYPE_APPLICATION_JSON)
                .produces(CONTENT_TYPE_APPLICATION_JSON)
                .handler(HTTPRequestValidationHandler.create().addJsonBodySchema(Beer.POST_SCHEMA))
                .handler(beerHandler.post)

        router
                .get('/v1/breweries/:name/beers/:beer')
                .produces(CONTENT_TYPE_APPLICATION_JSON)
                .handler(beerHandler.get)

        server
                .requestHandler(router.&accept)
                .rxListen(httpPort)

                .subscribe({ s ->
                    log.info("HTTP server running on port ${httpPort}")
                    startFuture.complete()
                })

                { e ->
                    log.error('Could not start HTTP server', e)
                    startFuture.fail(e)
                }
    }
}
