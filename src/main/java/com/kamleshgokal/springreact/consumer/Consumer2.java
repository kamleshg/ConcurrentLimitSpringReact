package com.kamleshgokal.springreact.consumer;

import com.kamleshgokal.springreact.domain.NotificationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.Consumer;

import java.util.Collections;

@Service
public class Consumer2 implements Consumer<Event<NotificationData>> {

    @Autowired
    private EventBus eventBus;

    @Override
    public void accept(Event<NotificationData> notificationDataEvent) {

        final String buffer = "\t";
        NotificationData notificationData = notificationDataEvent.getData();
        try {
            int i = (int) notificationData.getId();
            System.out.println(String.join("", Collections.nCopies(i, buffer)) + "<" + i);
            long start = System.currentTimeMillis();
            Thread.sleep(5000);
            long duration = System.currentTimeMillis() - start;
            System.out.println(String.join("", Collections.nCopies(i, buffer)) + i + " took " + duration);

        } catch (Exception e) {
            System.out.println("EXCEPTION!");
        }
    }

}