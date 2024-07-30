package com.hoangvo.chatappsocial.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class UpTypingStartEvent extends UpChatEvent {
    private String cid;

    @Override
    public ChatEventType getType() {
        return ChatEventType.TypingStart;
    }

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    public Date getCreatedAt() {
        return super.getCreatedAt();
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    @Override
    public String toString() {
        return "UpTypingStartEvent{" +
               "cid='" + cid + '\'' +
               ", type=" + getType() +
               ", createdAt=" + getCreatedAt() +
               '}';
    }
}
