package com.hoangvo.chatappsocial.model.request.chat_message;

import com.hoangvo.chatappsocial.model.dto.UpChatMessageDto;
import lombok.Getter;

@Getter
public class SendChatMessageRequest {
    private UpChatMessageDto message;
}
