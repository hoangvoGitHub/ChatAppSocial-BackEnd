package com.hoangvo.chatappsocial.controller.socket;

import com.hoangvo.chatappsocial.model.event.UpChatEvent;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class WebsocketController {

    private final Map<Class<? extends UpChatEvent>, ChatEventHandler> eventHandlers;

    public WebsocketController(Map<Class<? extends UpChatEvent>, ChatEventHandler> eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    @MessageMapping("/chat/{channelId}")
    public void handleChatEvent(
            @DestinationVariable String channelId,
            @Payload UpChatEvent chatEvent,
            SimpMessageHeaderAccessor accessor
    ) {
        StompHeaderAccessor accessor1 = StompHeaderAccessor.create(StompCommand.SEND);

        ChatEventHandler eventHandler = eventHandlers.get(chatEvent.getClass());
        if (eventHandler != null) {
            eventHandler.handleEvent(channelId, chatEvent, accessor);
        }
    }
}
