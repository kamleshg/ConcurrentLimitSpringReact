package com.kamleshgokal.springreact.service;

import com.kamleshgokal.springreact.domain.NotificationData;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class NotificationService {

    private ConcurrentRequestLimiter concurrentRequestLimiter = new ConcurrentRequestLimiter(1, 30, TimeUnit.SECONDS);

    public void initiateNotification(NotificationData notificationData) {
        String requestId = notificationData.getRequestId();
        String dcs = notificationData.getDcs();
        String key = requestId + ":" + dcs;

        try {
            System.out.println(key + " is asking for lock");
            concurrentRequestLimiter.acquire(requestId);
            System.out.println(key + " got the lock");
            Thread.sleep(notificationData.getWait());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Failed to get lock within timeout!");
        } finally {
            concurrentRequestLimiter.release(requestId);
            System.out.println(key + " released the lock");
        }
    }
}
