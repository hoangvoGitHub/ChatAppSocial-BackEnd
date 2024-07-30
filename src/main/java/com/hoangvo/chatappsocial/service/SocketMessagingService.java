package com.hoangvo.chatappsocial.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class SocketMessagingService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendMessage(String destination, Object message) {
        simpMessagingTemplate.convertAndSend(destination, message);
    }

    public void sendMessage(String destination, Object message,
                            Map<String, Object> headers) {
        simpMessagingTemplate.convertAndSend(destination, message, headers);
    }


}
