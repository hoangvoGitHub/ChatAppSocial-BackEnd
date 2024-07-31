package com.hoangvo.chatappsocial.controller.socket;

import com.hoangvo.chatappsocial.model.event.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketEventHandlersProvider {

    private final MessageReadEventHandler messageReadEventHandler;
    private final NewMessageEventHandler newMessageEventHandler;
    private final TypingStartEventHandler typingStartEventHandler;
    private final TypingStopEventHandler typingStopEventHandler;

    public WebSocketEventHandlersProvider(
            MessageReadEventHandler messageReadEventHandler,
            NewMessageEventHandler newMessageEventHandler,
            TypingStartEventHandler typingStartEventHandler,
            TypingStopEventHandler typingStopEventHandler
    ) {
        this.messageReadEventHandler = messageReadEventHandler;
        this.newMessageEventHandler = newMessageEventHandler;
        this.typingStartEventHandler = typingStartEventHandler;
        this.typingStopEventHandler = typingStopEventHandler;
    }

    @Bean
    public Map<Class<? extends UpChatEvent>, ChatEventHandler> eventHandlers() {
        Map<Class<? extends UpChatEvent>, ChatEventHandler> eventHandlers = new HashMap<>();
        eventHandlers.put(UpMessageReadEvent.class, messageReadEventHandler);
        eventHandlers.put(UpNewMessageEvent.class, newMessageEventHandler);
        eventHandlers.put(UpTypingStartEvent.class, typingStartEventHandler);
        eventHandlers.put(UpTypingStopEvent.class, typingStopEventHandler);
        return eventHandlers;
    }
}