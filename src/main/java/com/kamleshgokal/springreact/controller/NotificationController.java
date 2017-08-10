package com.kamleshgokal.springreact.controller;

import com.kamleshgokal.springreact.domain.NotificationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.bus.Event;
import reactor.bus.EventBus;

import java.util.stream.IntStream;

@Controller
public class NotificationController {

    @Autowired
    private EventBus eventBus;

    @GetMapping({"/fire"})
    @ResponseBody
    public void startNotification() {
        IntStream.rangeClosed(1,20)
                .forEach(j -> {
                    NotificationData data = new NotificationData();
                    data.setId(j);
                    eventBus.notify("notificationConsumer", Event.wrap(data));
                });

        System.out.println("Notification task submitted successfully");
    }

    @GetMapping("/list")
    public void getList() {
        System.out.println("BACKLOG SIZE: " + eventBus.getDispatcher().backlogSize());

    }
}
