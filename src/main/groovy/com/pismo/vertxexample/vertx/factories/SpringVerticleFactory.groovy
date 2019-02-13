package com.pismo.vertxexample.vertx.factories

import io.vertx.core.Verticle
import io.vertx.core.spi.VerticleFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class SpringVerticleFactory implements VerticleFactory {

    static final PREFIX = 'spring'

    @Autowired
    ApplicationContext context

    @Override
    String prefix() {
        'spring'
    }

    @Override
    Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
        String className = verticleName.substring(PREFIX.length() + 1)
        context.getBean(Class.forName(className) as Class<Verticle>) as Verticle
    }
}
