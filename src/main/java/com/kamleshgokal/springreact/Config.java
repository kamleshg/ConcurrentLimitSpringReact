package com.kamleshgokal.springreact;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.Environment;
import reactor.bus.EventBus;
import reactor.core.config.DispatcherType;

@Configuration
public class Config {

    @Bean
    Environment env() {
        return Environment.initializeIfEmpty().assignErrorJournal();
    }

    @Bean
    EventBus createEventBus(Environment env) {
        EventBus evBus;
//        evBus = EventBus.create(env, Environment.THREAD_POOL);
        evBus = EventBus.create(env, Environment.newDispatcher(20, 20, DispatcherType.THREAD_POOL_EXECUTOR));
        return evBus;
    }
    
}
