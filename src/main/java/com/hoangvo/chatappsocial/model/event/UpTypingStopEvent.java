package com.hoangvo.chatappsocial.model.event;

import java.util.Date;

public class UpTypingStopEvent extends UpChatEvent {
    private String cid;

    @Override
    public ChatEventType getType() {
        return ChatEventType.TypingStop;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    @Override
    public String toString() {
        return "UpTypingStopEvent{" +
               "cid='" + cid + '\'' +
               ", type=" + getType() +
               ", createdAt=" + getCreatedAt() +
               '}';
    }
}
