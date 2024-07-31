package com.hoangvo.chatappsocial.model.request.chat_message;

import lombok.Getter;

import java.util.Date;

@Getter
public class ChatMessageRequest {
    private String id;
    private String cid;
    private String text;
    private Date locallyCreatedAt;
    private String replyTo;
}
