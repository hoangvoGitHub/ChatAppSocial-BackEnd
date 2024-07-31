package com.hoangvo.chatappsocial.utils;

import com.hoangvo.chatappsocial.model.chat_message.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Builder
public class ChatMessageWrapper {
    private ChatMessage chatMessage;
    private HttpStatus status;
}
