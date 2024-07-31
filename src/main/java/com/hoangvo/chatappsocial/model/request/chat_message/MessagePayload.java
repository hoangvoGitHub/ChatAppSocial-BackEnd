package com.hoangvo.chatappsocial.model.request.chat_message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
@AllArgsConstructor
public class MessagePayload {
    @NonNull
    private String cid;

    private String text;

    private String replyTo;

    // make sure that a message in local is only synced once
    private String id;

}
