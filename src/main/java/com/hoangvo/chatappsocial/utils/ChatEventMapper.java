package com.hoangvo.chatappsocial.utils;

import com.hoangvo.chatappsocial.auth.api.AuthenticationService;
import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import com.hoangvo.chatappsocial.model.chat_message.ChatMessage;
import com.hoangvo.chatappsocial.model.chat_message.MessageType;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.model.event.*;
import com.hoangvo.chatappsocial.model.membership.Membership;
import com.hoangvo.chatappsocial.repository.chat_message.ChatMessageRepository;
import com.hoangvo.chatappsocial.repository.membership.MembershipRepository;
import com.hoangvo.chatappsocial.service.AttachmentService;
import com.hoangvo.chatappsocial.service.ChatMessageService;
import com.hoangvo.chatappsocial.service.MembershipService;
import com.hoangvo.chatappsocial.service.SocketMessagingService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Member;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ChatEventMapper {
    private final ChatMessageRepository messageRepository;
    private final MembershipRepository membershipRepository;
    private final AttachmentService attachmentService;
    private final ChatMessageService messageService;
    private final RetrieveMessageHelper retrieveMessageHelper;
    private final MembershipService membershipService;


    public Optional<ChatEvent> buildChatEvent(
            UpChatEvent chatEvent,
            ChatUser currentUser
    ) {
        return switch (chatEvent.getType()) {
            case NewMessage -> buildNewMessageEvent(
                    (UpNewMessageEvent) chatEvent,
                    currentUser);
            case Read -> buildMessageReadEvent(
                    (UpMessageReadEvent) chatEvent,
                    currentUser);
            case TypingStart -> buildTypingStartEvent(
                    (UpTypingStartEvent) chatEvent,
                    currentUser);
            case TypingStop -> buildTypingStopEvent(
                    (UpTypingStopEvent) chatEvent,
                    currentUser);
        };
    }

    private Optional<ChatEvent> buildTypingStopEvent(UpTypingStopEvent chatEvent, ChatUser currentUser) {
        return Optional.empty();
    }

    private Optional<ChatEvent> buildTypingStartEvent(UpTypingStartEvent chatEvent, ChatUser currentUser) {
        return Optional.empty();
    }

    private Optional<ChatEvent> buildNewMessageEvent(UpNewMessageEvent chatEvent, ChatUser currentUser) {
        try {
            final Membership membership = membershipService.getMembership(
                    currentUser.getId(),
                    chatEvent.getCid()
            );
            final ChatChannel chatChannel = membershipService
                    .getChatChannel(
                            currentUser.getId(),
                            chatEvent.getCid()
                    );

            ChatMessage replyTo = messageService.getReplyMessage(chatEvent.getMessage().getReplyTo());

            var chatMessage = ChatMessage.ChatMessageBuilder.builder()
                    .id(chatEvent.getMessage().getId())
                    .sentBy(currentUser)
                    .chatChannel(chatChannel)
                    .text(chatEvent.getMessage().getText())
                    .createdAt(new Date())
                    .type(MessageType.CHAT)
                    .replyTo(replyTo)
                    .build();

            final ChatMessage savedChatMessage = messageRepository.save(chatMessage);

            attachmentService.handleAttachments(chatEvent.getMessage().getAttachments(), savedChatMessage.getId());

            int unreadCount = membershipService.getMessageUnreadCount(
                    chatChannel, membership
            );
            var event = NewMessageEvent
                    .builder()
                    .unreadCount(unreadCount)
                    .createdAt(chatEvent.getCreatedAt())
                    .message(retrieveMessageHelper.enrichWithAttachments(savedChatMessage))
                    .cid(chatEvent.getCid())
                    .user(ChatUserMapper.toDto(currentUser))
                    .build();

            return Optional.of(event);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private Optional<ChatEvent> buildMessageReadEvent(UpMessageReadEvent chatEvent, ChatUser currentUser) {
        try {
            ChatMessage chatMessage = messageRepository.findById(chatEvent.getMessageId()).orElseThrow();
            final Membership membership = membershipRepository.findByChatUserIdAndChatChannelId(currentUser.getId(),
                    chatEvent.getCid()).orElseThrow();

            final Date createdAt = new Date();
            membership.setLastReadMessage(chatMessage, createdAt);
            membershipRepository.save(membership);
            var event = MessageReadEvent.builder()
                    .user(ChatUserMapper.toDto(currentUser))
                    .createdAt(createdAt)
                    .message(retrieveMessageHelper.enrichWithAttachments(chatMessage))
                    .cid(chatEvent.getCid())
                    .build();
            return Optional.of(event);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }
}
