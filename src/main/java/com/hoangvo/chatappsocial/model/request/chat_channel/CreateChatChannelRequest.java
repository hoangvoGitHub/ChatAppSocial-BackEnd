package com.hoangvo.chatappsocial.model.request.chat_channel;

import com.hoangvo.chatappsocial.model.chat_channel.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class CreateChatChannelRequest {
    private String name;

    @NonNull
    private List<String> members;

    private String message;

    private ChannelType type;
}
