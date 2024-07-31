package com.hoangvo.chatappsocial.model.request.chat_channel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
public class QueryChannelRequest {
    private MessagesDirection direction;
    private String baseMessageId;
    private int messageLimit;
    private int memberLimit;
    private Boolean watch;
}
