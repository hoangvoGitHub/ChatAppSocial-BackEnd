package com.hoangvo.chatappsocial.utils;

import com.hoangvo.chatappsocial.auth.data.model.User;
import com.hoangvo.chatappsocial.repository.chat_channel.ChatChannelRepository;
import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import com.hoangvo.chatappsocial.model.request.chat_message.ChatMessageRequest;
import com.hoangvo.chatappsocial.model.request.chat_message.MessagePayload;
import com.hoangvo.chatappsocial.repository.chat_message.ChatMessageRepository;
import com.hoangvo.chatappsocial.model.chat_message.ChatMessage;
import com.hoangvo.chatappsocial.repository.chat_user.ChatUserRepository;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@RequiredArgsConstructor
@Component
public class MessageInterceptor {
    public ChatMessageWrapper intercept(Authentication currentAuth,
                                        String channelId,
                                        ChatMessageRequest chatMessageRequest,
                                        ChatUserRepository chatUserRepository,
                                        ChatChannelRepository chatChannelRepository,
                                        ChatMessageRepository chatMessageRepository
    ) {
        if (currentAuth == null) {
            return null;
        }
        if (!currentAuth.isAuthenticated()) {
            return ChatMessageWrapper.builder()
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
        final Object principal = currentAuth.getPrincipal();
        final User currentUser = (User) principal;
        final ChatUser chatUser = chatUserRepository.findChatUserByOriginalUser(currentUser);
        final ChatChannel currentChannel = chatChannelRepository.findChatChannelByIdAndMembership(channelId, chatUser.getId())
                .orElse(null);
        if (currentChannel == null) {
            return ChatMessageWrapper.builder()
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .build();
        }

        String replyToId = chatMessageRequest.getReplyTo();
        ChatMessage replyTo;
        if (replyToId != null) {
            replyTo = chatMessageRepository.findById(replyToId).orElse(null);
        } else {
            replyTo = null;
        }
        var newMessage = ChatMessage.ChatMessageBuilder.builder()
                .text(chatMessageRequest.getText())
                .chatChannel(currentChannel)
                .sentBy(chatUser)
                .text(chatMessageRequest.getText())
                .createdAt(new Date())
                .replyTo(replyTo)
                .build();

        return ChatMessageWrapper.builder()
                .status(HttpStatus.OK)
                .chatMessage(chatMessageRepository.save(newMessage))
                .build();
    }

    public ChatMessageWrapper intercept(Authentication currentAuth, String channelId, MessagePayload messagePayload,
                                        ChatUserRepository chatUserRepository,
                                        ChatChannelRepository chatChannelRepository,
                                        ChatMessageRepository chatMessageRepository) {
        if (currentAuth == null) {
            return null;
        }
        if (!currentAuth.isAuthenticated()) {
            return ChatMessageWrapper.builder()
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
        final Object principal = currentAuth.getPrincipal();
        final User currentUser = (User) principal;
        final ChatUser chatUser = chatUserRepository.findChatUserByOriginalUser(currentUser);
        final ChatChannel currentChannel = chatChannelRepository.findChatChannelByIdAndMembership(channelId, chatUser.getId())
                .orElse(null);
        if (currentChannel == null) {
            return ChatMessageWrapper.builder()
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .build();
        }

        String replyToId = messagePayload.getReplyTo();
        ChatMessage replyTo;
        if (replyToId != null) {
            replyTo = chatMessageRepository.findById(replyToId).orElse(null);
        } else {
            replyTo = null;
        }
        var newMessage = ChatMessage.ChatMessageBuilder.builder()
                .text(messagePayload.getText())
                .chatChannel(currentChannel)
                .sentBy(chatUser)
                .text(messagePayload.getText())
                .createdAt(new Date())
                .replyTo(replyTo)
                .build();

        return ChatMessageWrapper.builder()
                .status(HttpStatus.OK)
                .chatMessage(chatMessageRepository.save(newMessage))
                .build();
    }

    public ChatChannelWrapper retrieveChatChannel(Authentication currentAuth, String channelId,
                                                  ChatUserRepository chatUserRepository,
                                                  ChatChannelRepository chatChannelRepository) {
        if (currentAuth == null) {
            return null;
        }
        if (!currentAuth.isAuthenticated()) {
            return ChatChannelWrapper.builder()
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
        final Object principal = currentAuth.getPrincipal();
        final User currentUser = (User) principal;
        final ChatUser chatUser = chatUserRepository.findChatUserByOriginalUser(currentUser);
        final ChatChannel currentChannel = chatChannelRepository.findChatChannelByIdAndMembership(channelId, chatUser.getId())
                .orElse(null);
        if (currentChannel == null) {
            return ChatChannelWrapper.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
        return ChatChannelWrapper.builder()
                .status(HttpStatus.OK)
                .chatChannel(currentChannel)
                .build();
    }
}
