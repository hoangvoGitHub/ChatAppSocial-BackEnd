package com.hoangvo.chatappsocial.websocket.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    @EventListener
    public void handleReceiptEvent(SessionSubscribeEvent event) {
        log.info("handleReceiptEvent: " + event);
        log.info("handleReceiptEvent: " + event);
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String receiptId = accessor.getReceiptId();
        if (receiptId != null) {
            log.info("ReceiptID: " + receiptId);
            // Mark the message as delivered in the database
            // messageService.markMessageAsDelivered(receiptId);
        }
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        String sessionId = Objects.requireNonNull(event.getMessage().getHeaders().get("simpSessionId")).toString();
        log.info("Received a new web socket connection: session :" + sessionId + ", " + event.getMessage());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        log.info("Disconnect Event: " + event.getMessage());
    }


}