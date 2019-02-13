package com.pismo.vertxexample.vertx.mysql.handlers

import io.reactivex.Single
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.sql.ResultSet
import io.vertx.reactivex.core.eventbus.Message
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SelectHandler implements Handler<Message<JsonObject>> {

    @Autowired
    AsyncSQLClient dataSource

    @Override
    void handle(Message<JsonObject> message) {
        Map body = message.body().map

        String sql = body.sql
        JsonArray params = body.params as JsonArray

        Single<ResultSet> single

        if (params) {
            single = dataSource.rxQueryWithParams(sql, params)
        } else {
            single = dataSource.rxQuery(sql)
        }

        single
                .subscribe({ ResultSet resultSet ->
                    if (resultSet.getNumRows() > 0) {
                        return message.reply(resultSet.getRows().first())
                    }
        
                    message.reply([:] as JsonObject)
                }, { Throwable t ->
                    message.fail(-1, t.message)
                })
    }

}
