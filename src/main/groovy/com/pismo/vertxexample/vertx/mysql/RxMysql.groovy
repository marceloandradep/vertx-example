package com.pismo.vertxexample.vertx.mysql

import com.pismo.vertxexample.vertx.exceptions.DataNotFoundException
import groovy.util.logging.Slf4j
import io.reactivex.Single
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.eventbus.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class RxMysql {

    @Autowired
    Vertx vertx

    Single select(String sql, Object[] params) {

        def message = [
                sql: sql,
                params: params as JsonArray
        ] as JsonObject

        Single.create { emitter ->
            vertx
                    .eventBus()
                    .send(MySqlVerticle.MYSQL_SELECT_QUEUE, message, [:] as DeliveryOptions, { reply ->
                        if (reply.succeeded()) {
                            Message<JsonObject> result = reply.result() as Message<JsonObject>
                            Map body = result.body().map

                            if (body.isEmpty()) {
                                emitter.onError(new DataNotFoundException())
                            } else {
                                emitter.onSuccess(body)
                            }
                        } else {
                            emitter.onError(reply.cause())
                        }
                    })
        }
    }

    Single insert(String sql, Object[] params) {
        def message = [
                sql: sql,
                params: params as JsonArray
        ] as JsonObject

        Single.create { emitter ->
            vertx
                    .eventBus()
                    .send(MySqlVerticle.MYSQL_INSERT_QUEUE, message, [:] as DeliveryOptions, { reply ->
                        if (reply.succeeded()) {
                            Message<JsonObject> response = reply.result() as Message<JsonObject>
                            emitter.onSuccess(response.body())
                        } else {
                            emitter.onError(reply.cause())
                        }
                    })
        }
    }

}
