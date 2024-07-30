package com.hoangvo.chatappsocial.controller.socket;

import com.hoangvo.chatappsocial.auth.api.AuthenticationService;
import com.hoangvo.chatappsocial.auth.data.model.User;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.model.event.ChatEvent;
import com.hoangvo.chatappsocial.model.event.UpMessageReadEvent;
import com.hoangvo.chatappsocial.repository.chat_channel.ChatChannelRepository;
import com.hoangvo.chatappsocial.repository.chat_user.ChatUserRepository;
import com.hoangvo.chatappsocial.service.SocketMessagingService;
import com.hoangvo.chatappsocial.utils.ChatEventMapper;
import com.hoangvo.chatappsocial.utils.UserAccessHandler;
import com.hoangvo.chatappsocial.websocket.config.WebsocketConfiguration;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MessageReadEventHandler implements ChatEventHandler<UpMessageReadEvent> {

    private final SocketMessagingService messagingService;
    private final ChatUserRepository chatUserRepository;
    private final ChatChannelRepository chatChannelRepository;
    private final ChatEventMapper chatEventMapper;

    private static final Logger LOG = LoggerFactory.getLogger(MessageReadEventHandler.class);

    @Override
    public void handleEvent(String channelId, UpMessageReadEvent event, SimpMessageHeaderAccessor accessor) {
        LOG.info("Handle event at " + channelId + " : " + event);
        LOG.info("Handle event ChatEventMapper instance " + chatEventMapper.hashCode());
        final String email = Objects.requireNonNull(accessor.getUser()).getName();
        final ChatUser currentChatUser = chatUserRepository.findChatUserByOriginalUserUsername(email);
        Optional<ChatEvent> eventToSend = chatEventMapper.buildChatEvent(event, currentChatUser);
        eventToSend.ifPresent(chatEvent -> {
            String receiptId = "receipt-" + email; // Assuming chatEvent has a getId() method
            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
            headerAccessor.setHeader("receipt", receiptId);

            messagingService.sendMessage(
                    WebsocketConfiguration.DESTINATION_PREFIX + "/" + channelId, chatEvent,
                    headerAccessor.getMessageHeaders()
            );
        });

    }
}
