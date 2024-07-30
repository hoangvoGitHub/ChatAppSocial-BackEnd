package com.hoangvo.chatappsocial.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class UpMessageReadEvent extends UpChatEvent {
    private String cid;
    private String messageId;

    @Override
    public ChatEventType getType() {
        return ChatEventType.Read;
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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "UpMessageReadEvent{" +
               "cid='" + cid + '\'' +
               ", messageId='" + messageId + '\'' +
               ", type=" + getType() +
               ", createdAt=" + getCreatedAt() +
               '}';
    }
}
