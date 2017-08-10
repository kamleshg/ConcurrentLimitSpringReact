package com.kamleshgokal.springreact;

import com.kamleshgokal.springreact.consumer.Consumer1;
import com.kamleshgokal.springreact.consumer.Consumer2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.bus.EventBus;

import static reactor.bus.selector.Selectors.$;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@Import(Config.class)
public class Application implements CommandLineRunner {

    @Autowired
    private EventBus eventBus;

    @Autowired
    private Consumer1 consumer1;

    @Autowired
    private Consumer2 consumer2;

    @Override
    public void run(String... args) throws Exception {
        eventBus.on($("consumer1"), consumer1);
        eventBus.on($("consumer2"), consumer2);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
