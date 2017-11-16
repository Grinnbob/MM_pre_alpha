package com.mycompany.grifon.mm_pre_alpha.data;

import java.util.Date;
import java.util.UUID;

public class Message {

    private String textMessage;
    private String autor;
    private long timeMessage;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    private String uuid;

    public Message(String textMessage, String autor) {
        this.textMessage = textMessage;
        this.autor = autor;
        this.uuid= UUID.randomUUID().toString();

        timeMessage = new Date().getTime();
    }

    public Message() {
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public long getTimeMessage() {
        return timeMessage;
    }

    public void setTimeMessage(long timeMessage) {
        this.timeMessage = timeMessage;
    }
}
