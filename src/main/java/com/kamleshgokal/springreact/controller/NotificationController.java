package com.kamleshgokal.springreact.controller;

import com.kamleshgokal.springreact.doman.NotificationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.bus.Event;
import reactor.bus.EventBus;

import java.util.Optional;

@Controller
public class NotificationController {

    @Autowired
    private EventBus eventBus;

    @GetMapping({"/rollupStatus/{requestId}/dcs/{dcs}",
            "/rollupStatus/{requestId}/dcs/{dcs}/wait/{wait}"})
    public void startNotification(@PathVariable String requestId, @PathVariable String dcs, @PathVariable Optional<Long> wait) {

        NotificationData data = new NotificationData();
        data.setRequestId(requestId);
        data.setDcs(dcs);
        data.setWait(wait.orElse(Long.valueOf(1000)));

        eventBus.notify("notificationConsumer", Event.wrap(data));

//        System.out.println("Notification " + requestId + ": notification task submitted successfully");
    }

    @GetMapping("/list")
    public void getList() {
        System.out.println("BACKLOG SIZE: " + eventBus.getDispatcher().backlogSize());

    }
}
