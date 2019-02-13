package com.pismo.vertxexample.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import com.pismo.vertxexample.domain.Brewery
import com.pismo.vertxexample.services.BreweryService
import io.vertx.reactivex.ext.web.RoutingContext
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BreweryHandler {
    
    @Autowired
    BreweryService breweryService
    
    @Autowired
    ObjectMapper objectMapper

    def post = { RoutingContext context ->
        Brewery brewery = objectMapper.readValue(context.getBodyAsString('utf-8'), Brewery)
        
        breweryService
                .createBrewery(brewery)
                
                .subscribe({ Brewery result ->
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
        
        breweryService
                .findByName(breweryName)

                .subscribe({ Brewery result ->
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
