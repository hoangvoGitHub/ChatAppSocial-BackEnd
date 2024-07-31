package com.hoangvo.chatappsocial.model.response.chat_message;

import com.hoangvo.chatappsocial.model.chat_message.ChatMessage;
import com.hoangvo.chatappsocial.model.dto.ChatMessageDto;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {
    private ChatMessageDto chatMessage;
}
