package com.hoangvo.chatappsocial.controller.socket;

import com.hoangvo.chatappsocial.model.event.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public interface ChatEventHandler<T extends UpChatEvent> {
    void handleEvent(String channelId, T event, SimpMessageHeaderAccessor accessor);
}

