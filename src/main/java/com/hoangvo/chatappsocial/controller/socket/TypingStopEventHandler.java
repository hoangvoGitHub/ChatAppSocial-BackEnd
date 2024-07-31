package com.hoangvo.chatappsocial.controller.socket;

import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.model.event.ChatEvent;
import com.hoangvo.chatappsocial.model.event.UpTypingStopEvent;
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
public class TypingStopEventHandler implements ChatEventHandler<UpTypingStopEvent> {

    private final SocketMessagingService messagingService;
    private final ChatUserRepository chatUserRepository;
    private final ChatEventMapper chatEventMapper;
    private final ChatChannelRepository chatChannelRepository;

    @Override
    public void handleEvent(String channelId, UpTypingStopEvent event, SimpMessageHeaderAccessor accessor) {
        final String email = Objects.requireNonNull(accessor.getUser()).getName();

        final ChatUser currentChatUser = chatUserRepository.findChatUserByOriginalUserUsername(email);

        final ChatChannel chatChannel = chatChannelRepository.findById(channelId).orElse(null);
        if (chatChannel == null) return;

        Optional<ChatEvent> eventToSend = chatEventMapper.buildChatEvent(chatChannel,event, currentChatUser);
        eventToSend.ifPresent(chatEvent -> messagingService.sendMessage(
                WebsocketConfiguration.DESTINATION_PREFIX + "/" + channelId, chatEvent
        ));
    }
}
