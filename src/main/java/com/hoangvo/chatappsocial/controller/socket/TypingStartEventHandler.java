package com.hoangvo.chatappsocial.controller.socket;

import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.model.event.ChatEvent;
import com.hoangvo.chatappsocial.model.event.UpTypingStartEvent;
import com.hoangvo.chatappsocial.repository.chat_channel.ChatChannelRepository;
import com.hoangvo.chatappsocial.repository.chat_user.ChatUserRepository;
import com.hoangvo.chatappsocial.service.SocketMessagingService;
import com.hoangvo.chatappsocial.utils.ChatEventMapper;
import com.hoangvo.chatappsocial.websocket.config.WebsocketConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class TypingStartEventHandler implements ChatEventHandler<UpTypingStartEvent> {

    private final SocketMessagingService messagingService;
    private final ChatUserRepository chatUserRepository;
    private final ChatChannelRepository chatChannelRepository;
    private final ChatEventMapper chatEventMapper;

    @Override
    public void handleEvent(String channelId, UpTypingStartEvent event, SimpMessageHeaderAccessor accessor) {
        final String email = Objects.requireNonNull(accessor.getUser()).getName();
        final ChatChannel chatChannel = chatChannelRepository.findById(channelId).orElse(null);
        if (chatChannel == null) return;
        final ChatUser currentChatUser = chatUserRepository.findChatUserByOriginalUserUsername(email);
        Optional<ChatEvent> eventToSend = chatEventMapper.buildChatEvent(chatChannel, event, currentChatUser);
        eventToSend.ifPresent(chatEvent -> messagingService.sendMessage(
                WebsocketConfiguration.DESTINATION_PREFIX + "/" + channelId, chatEvent
        ));

    }
}
