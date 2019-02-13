package com.pismo.vertxexample.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import com.pismo.vertxexample.domain.Beer
import com.pismo.vertxexample.services.BeerService
import io.vertx.reactivex.ext.web.RoutingContext
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BeerHandler {

    @Autowired
    BeerService beerService

    @Autowired
    ObjectMapper objectMapper

    def post = { RoutingContext context ->
        String breweryName = context.request().getParam('name')
        Beer beer = objectMapper.readValue(context.getBodyAsString('utf-8'), Beer)

        beerService
                .createBeer(breweryName, beer)

                .subscribe({ Beer result ->
                    String asString = objectMapper.writeValueAsString(result)
        
                    context
                            .response()
                            .setStatusCode(HttpStatus.SC_CREATED)
                            .end(asString)
                }) { Throwable t ->
                    context.fail(t)
                }
    }

    def get = { RoutingContext context ->
        String breweryName = context.request().getParam('name')
        String beerName = context.request().getParam('beer')

        beerService
                .findByName(breweryName, beerName)

                .subscribe({ Beer result ->
                    String asString = objectMapper.writeValueAsString(result)
        
                    context
                            .response()
                            .setStatusCode(HttpStatus.SC_OK)
                            .end(asString)
                }) { Throwable t ->
                    context.fail(t)
                }
    }
    
}
