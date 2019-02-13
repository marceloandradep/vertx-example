package com.pismo.vertxexample.vertx.mysql.consumers

import com.pismo.vertxexample.vertx.mysql.handlers.InsertHandler
import com.pismo.vertxexample.vertx.mysql.handlers.SelectHandler
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.eventbus.Message
import io.vertx.reactivex.core.eventbus.MessageConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

import javax.annotation.PreDestroy

@Component
class MySqlConsumers {

    enum Queues {
        MYSQL_SELECT_UNIQUE_QUEUE('mysql-select-unique', SelectHandler),
        MYSQL_INSERT_QUEUE('mysql-insert-queue', InsertHandler)

        String name
        Class<Handler<Message<JsonObject>>> consumerClass

        private Queues(String name, Class<Handler<Message<JsonObject>>> consumerClass) {
            this.name = name
            this.consumerClass = consumerClass
        }
    }

    @Autowired
    ApplicationContext context

    List<MessageConsumer> consumers = []

    void registerConsumers(Vertx vertx) {
        Queues.values().each { Queues q ->
            Handler<Message<JsonObject>> consumer = context.getBean(q.consumerClass)
            registerConsumer(vertx, q.name, consumer)
        }
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
