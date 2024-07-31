package com.hoangvo.chatappsocial.controller.socket;

import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.model.event.ChatEvent;
import com.hoangvo.chatappsocial.model.event.NewMessageEvent;
import com.hoangvo.chatappsocial.model.event.UpNewMessageEvent;
import com.hoangvo.chatappsocial.repository.chat_channel.ChatChannelRepository;
import com.hoangvo.chatappsocial.repository.chat_user.ChatUserRepository;
import com.hoangvo.chatappsocial.service.MembershipService;
import com.hoangvo.chatappsocial.service.SocketMessagingService;
import com.hoangvo.chatappsocial.utils.ChatEventMapper;
import com.hoangvo.chatappsocial.websocket.config.WebsocketConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NewMessageEventHandler implements ChatEventHandler<UpNewMessageEvent> {

    private final SocketMessagingService messagingService;
    private final ChatEventMapper chatEventMapper;
    private final ChatUserRepository chatUserRepository;
    private final ChatChannelRepository chatChannelRepository;
    private final MembershipService membershipService;


    @Override
    public void handleEvent(String channelId, UpNewMessageEvent event, SimpMessageHeaderAccessor accessor) {
        final String email = Objects.requireNonNull(accessor.getUser()).getName();

        final ChatUser currentChatUser = chatUserRepository.findChatUserByOriginalUserUsername(email);

        final ChatChannel chatChannel = chatChannelRepository.findById(channelId).orElse(null);
        if (chatChannel == null) return;

        Optional<ChatEvent> eventToSend = chatEventMapper.buildChatEvent(chatChannel, event, currentChatUser);
        eventToSend.ifPresent(chatEvent -> sendMessage(chatChannel, (NewMessageEvent) chatEvent));
    }

    private void sendMessage(ChatChannel channel, NewMessageEvent event) {
        sendMessageToDestination(channel.getId(), event);
        channel.getMemberships().forEach(membership -> {
            int unreadCount =membershipService.getMessageUnreadCount(channel,membership);
            event.setUnreadCount(unreadCount);
            sendMessageToDestination(membership.getChatUser().getId(), event);
        });

    }

    private void sendMessageToDestination(String userId, ChatEvent event) {
        messagingService.sendMessage(
                WebsocketConfiguration.DESTINATION_PREFIX + "/" + userId, event
        );
    }
}
