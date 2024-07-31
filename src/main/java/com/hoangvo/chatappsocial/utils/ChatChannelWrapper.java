package com.hoangvo.chatappsocial.utils;

import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class ChatChannelWrapper {
    private ChatChannel chatChannel;
    @NonNull
    private HttpStatus status;

}
