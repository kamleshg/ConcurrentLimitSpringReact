package com.kamleshgokal.springreact.domain;

public class NotificationData {

    private long id;
    private String requestId;
    private String dcs;
    private String skus;
    private long wait = 1000;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getDcs() {
        return dcs;
    }

    public void setDcs(String dcs) {
        this.dcs = dcs;
    }

    public String getSkus() {
        return skus;
    }

    public void setSkus(String skus) {
        this.skus = skus;
    }

    public long getWait() {
        return wait;
    }

    public void setWait(long wait) {
        this.wait = wait;
    }
}
