package com.pismo.vertxexample.vertx.mysql

import com.pismo.vertxexample.vertx.mysql.handlers.InsertHandler
import com.pismo.vertxexample.vertx.mysql.handlers.SelectHandler
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.eventbus.Message
import io.vertx.reactivex.core.eventbus.MessageConsumer
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PreDestroy

@Component
class MySqlVerticle extends AbstractVerticle {

    static final String MYSQL_SELECT_QUEUE = 'mysql-select-queue'
    static final String MYSQL_INSERT_QUEUE = 'mysql-insert-queue'
    
    static final HEALTH_CHECK_SQL = '''
        SELECT 1 FROM DUAL
    '''

    @Autowired
    AsyncSQLClient dataSource
    
    @Autowired
    InsertHandler insertHandler
    
    @Autowired
    SelectHandler selectHandler

    List<MessageConsumer> consumers = []

    @Override
    void start(Future<Void> startFuture) throws Exception {

        dataSource

                .rxQuery(HEALTH_CHECK_SQL)

                .subscribe({
                    registerConsumer(this.@vertx, MYSQL_INSERT_QUEUE, insertHandler)        
                    registerConsumer(this.@vertx, MYSQL_SELECT_QUEUE, selectHandler)        
            
                    startFuture.complete()
                }, startFuture.&fail)

    }

    void registerConsumer(Vertx vertx, String name, Handler<Message<JsonObject>> consumer) {
        consumers << vertx.eventBus().consumer(name, consumer)
    }

    def unregisterConsumer = { MessageConsumer consumer ->
        consumer.rxUnregister().subscribe()
    }

    @PreDestroy
    void destroy() {
        consumers.each(unregisterConsumer)
    }
}
