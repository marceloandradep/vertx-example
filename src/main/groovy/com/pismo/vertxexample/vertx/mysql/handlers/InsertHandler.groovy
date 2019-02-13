package com.pismo.vertxexample.vertx.mysql.handlers


import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.sql.UpdateResult
import io.vertx.reactivex.core.eventbus.Message
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class InsertHandler implements Handler<Message<JsonObject>> {

    @Autowired
    AsyncSQLClient dataSource

    @Override
    void handle(Message<JsonObject> message) {
        Map body = message.body().map

        String sql = body.sql
        JsonArray params = body.params as JsonArray

        dataSource
                .rxUpdateWithParams(sql, params)
        
                .subscribe({ UpdateResult updateResult ->
                    if (updateResult.getUpdated() > 0) {
                        return message.reply(updateResult.getKeys().first())
                    }
        
                    message.reply([:] as JsonObject)
                },
                
                { Throwable t ->
                    message.fail(-1, t.message)
                })
                
    }
}
