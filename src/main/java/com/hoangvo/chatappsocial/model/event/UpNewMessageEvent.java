package com.hoangvo.chatappsocial.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hoangvo.chatappsocial.model.dto.UpChatMessageDto;

import java.util.Date;

public class UpNewMessageEvent extends UpChatEvent {
    private String cid;
    private UpChatMessageDto message;

    @Override
    public ChatEventType getType() {
        return ChatEventType.NewMessage;
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

    public UpChatMessageDto getMessage() {
        return message;
    }

    public void setMessage(UpChatMessageDto message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "UpNewMessageEvent{" +
               "cid='" + cid + '\'' +
               ", message=" + message +
               ", type=" + getType() +
               ", createdAt=" + getCreatedAt() +
               '}';
    }
}
