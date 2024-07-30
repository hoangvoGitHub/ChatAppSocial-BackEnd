package com.hoangvo.chatappsocial.service;

import com.hoangvo.chatappsocial.auth.api.AuthenticationService;
import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import com.hoangvo.chatappsocial.model.chat_message.ChatMessage;
import com.hoangvo.chatappsocial.model.chat_message.MessageType;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.model.event.NewMessageEvent;
import com.hoangvo.chatappsocial.model.membership.Membership;
import com.hoangvo.chatappsocial.model.request.chat_message.SendChatMessageRequest;
import com.hoangvo.chatappsocial.model.response.chat_message.ChatMessageResponse;
import com.hoangvo.chatappsocial.repository.chat_message.ChatMessageRepository;
import com.hoangvo.chatappsocial.utils.ChatUserMapper;
import com.hoangvo.chatappsocial.utils.RetrieveMessageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class SendMessageService {
    private final AuthenticationService authenticationService;
    private final ChatMessageRepository messageRepository;
    private final AttachmentService attachmentService;
    private final SocketMessagingService messagingService;
    private final ChatMessageService messageService;
    private final RetrieveMessageHelper retrieveMessageHelper;
    private final MembershipService membershipService;

    public ChatMessageResponse sendMessage(String channelId, SendChatMessageRequest request) {
        final ChatUser currentChatUser = authenticationService.getCurrentChatUser();

        final Membership membership = membershipService.getMembership(
                currentChatUser.getId(),
                channelId
        );

        if (membership == null) {
            return null;
        }

        final ChatChannel chatChannel = membershipService
                .getChatChannel(
                        currentChatUser.getId(),
                        channelId
                );

        ChatMessage replyTo = messageService.getReplyMessage(request.getMessage().getReplyTo());

        var chatMessage = ChatMessage.ChatMessageBuilder.builder()
                .id(request.getMessage().getId())
                .sentBy(currentChatUser)
                .chatChannel(chatChannel)
                .text(request.getMessage().getText())
                .createdAt(new Date())
                .type(MessageType.CHAT)
                .replyTo(replyTo)
                .build();

        final ChatMessage savedChatMessage = messageRepository.save(chatMessage);

        attachmentService.handleAttachments(request.getMessage().getAttachments(), savedChatMessage.getId());

        chatChannel.getMemberships().forEach(member -> {
            final int unreadCount;
            if (member.getLastReadMessage() != null) {
                unreadCount = messageRepository.countUnreadMessages(
                        member.getLastReadMessage().getId(),
                        channelId,
                        member.getChatUser().getId()
                );
            } else {
                unreadCount = messageRepository.countChatMessagesByChatChannelAndSentByNot(
                        chatChannel,
                        currentChatUser
                );
            }

            var event = NewMessageEvent.builder()
                    .unreadCount(unreadCount)
                    .createdAt(new Date())
                    .message(retrieveMessageHelper.enrichWithAttachments(savedChatMessage))
                    .cid(channelId)
                    .user(ChatUserMapper.toDto(currentChatUser))
                    .build();

            messagingService.sendMessage("/topic/" + member.getChatUser().getId(), event);
        });


        return ChatMessageResponse.builder()
                .chatMessage(retrieveMessageHelper.enrichWithAttachments(savedChatMessage))
                .build();
    }
}
