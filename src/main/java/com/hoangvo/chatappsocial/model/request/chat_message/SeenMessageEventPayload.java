package com.hoangvo.chatappsocial.model.request.chat_message;

import lombok.Getter;

import java.util.Date;

@Getter
public class SeenMessageEventPayload {
    private String userId;
    private String messageId;
    private Date localSeenAt;
}
