package com.pismo.vertxexample.handlers

import groovy.util.logging.Slf4j
import io.vertx.ext.web.api.validation.ValidationException
import io.vertx.reactivex.ext.web.RoutingContext
import org.apache.http.HttpStatus
import org.springframework.stereotype.Component

@Component
@Slf4j
class FailureHandler {

    def handle = { RoutingContext context ->
        def failure = context.failure()

        if (failure instanceof ValidationException) {
            ValidationException ve = failure as ValidationException

            context
                    .response()
                    .setStatusCode(HttpStatus.SC_BAD_REQUEST)
                    .end(ve.message)
        } else {
            log.error('Unexpected error', failure)
            
            context
                    .response()
                    .setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .end('Unexpected error')
        }
    }
}
