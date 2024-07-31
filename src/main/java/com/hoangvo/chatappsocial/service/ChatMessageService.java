package com.hoangvo.chatappsocial.service;

import com.hoangvo.chatappsocial.model.chat_message.ChatMessage;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.model.dto.ChatMessageDto;
import com.hoangvo.chatappsocial.model.membership.Membership;
import com.hoangvo.chatappsocial.model.request.chat_message.SeenMessageEventPayload;
import com.hoangvo.chatappsocial.model.request.chat_message.TypingEventPayload;
import com.hoangvo.chatappsocial.model.response.chat_message.ChatMessageResponse;
import com.hoangvo.chatappsocial.repository.chat_channel.ChatChannelRepository;
import com.hoangvo.chatappsocial.repository.chat_message.ChatMessageRepository;
import com.hoangvo.chatappsocial.repository.chat_user.ChatUserRepository;
import com.hoangvo.chatappsocial.repository.membership.MembershipRepository;
import com.hoangvo.chatappsocial.utils.ChatChannelWrapper;
import com.hoangvo.chatappsocial.utils.MessageInterceptor;
import com.hoangvo.chatappsocial.utils.RetrieveMessageHelper;
import com.hoangvo.chatappsocial.utils.UserAccessHandler;
import com.hoangvo.chatappsocial.utils.response.BaseResponse;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatUserRepository chatUserRepository;
    private final ChatChannelRepository chatChannelRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MembershipRepository membershipRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageInterceptor messageInterceptor;
    private final RetrieveMessageHelper retrieveMessageHelper;


    public BaseResponse<List<ChatMessageDto>> getChatMessages(String channelId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatChannelWrapper channelWrapper = messageInterceptor
                .retrieveChatChannel(authentication, channelId, chatUserRepository, chatChannelRepository);
        if (channelWrapper.getStatus() == HttpStatus.OK) {
            List<ChatMessageDto> messagesWithAttachments =
                    chatMessageRepository.findAllByChatChannel(channelWrapper.getChatChannel(), null).stream()
                            .map(retrieveMessageHelper::enrichWithAttachments).toList();

            return new BaseResponse<>(
                    HttpStatus.OK,
                    "Successful",
                    messagesWithAttachments
            );
        }
        String message;
        if (channelWrapper.getStatus() == HttpStatus.UNAUTHORIZED) {
            message = "Please log in";
        } else if (channelWrapper.getStatus() == HttpStatus.NOT_FOUND) {
            message = "Channel Not Found";
        } else {
            message = "Unknown Error";
        }
        return new BaseResponse<>(channelWrapper.getStatus(), message, null);

    }

    public void handleTyping(String channelId, TypingEventPayload payload) {
        simpMessagingTemplate.convertAndSend("queue/typing/" + channelId, payload);
    }

    public void handleSeenEvent(String channelId, SeenMessageEventPayload payload) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentChatUser = UserAccessHandler.getCurrentChatUser(authentication, chatUserRepository).orElseThrow();
        final ChatMessage chatMessage = chatMessageRepository
                .findByIdAndChatChannelId(payload.getMessageId(), channelId)
                .orElse(null);
        if (chatMessage == null) {
            return;
        }
        final Membership membership = membershipRepository
                .findByChatUserIdAndChatChannelId(currentChatUser.getId(), channelId)
                .orElse(null);
        if (membership == null) {
            return;
        }
        final Date currentTime = new Date();
        final ChatMessage lastReadMessage
                = membership.getLastReadMessage();

        // check whether this seen event is new or not
        // if new update and send, otherwise send nothing

        if (lastReadMessage == null || membership.getLastReadAt().before(currentTime)) {
            simpMessagingTemplate.convertAndSend("queue/seen/" + channelId, payload);
            membership.setLastReadMessage(chatMessage, currentTime);
            membershipRepository.save(membership);
        }
    }

    public ChatMessageResponse getChatMessage(String messageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentChatUser = UserAccessHandler.getCurrentChatUser(authentication, chatUserRepository).orElseThrow();
        final ChatMessage message = chatMessageRepository.findById(messageId).orElse(null);
        if (message == null) {
            return null;
        }
        return ChatMessageResponse.builder().chatMessage(retrieveMessageHelper.enrichWithAttachments(message)).build();

    }

    @Nullable
    public ChatMessage getReplyMessage(@Nullable String replyTo) {
        if (replyTo == null) return null;
        return chatMessageRepository.findById(replyTo).orElse(null);
    }
}
