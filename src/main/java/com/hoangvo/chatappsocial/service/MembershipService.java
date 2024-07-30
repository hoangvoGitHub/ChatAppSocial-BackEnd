package com.hoangvo.chatappsocial.service;


import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import com.hoangvo.chatappsocial.model.membership.Membership;
import com.hoangvo.chatappsocial.repository.chat_channel.ChatChannelRepository;
import com.hoangvo.chatappsocial.repository.chat_message.ChatMessageRepository;
import com.hoangvo.chatappsocial.repository.membership.MembershipRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;
    private final ChatChannelRepository channelRepository;
    private final ChatMessageRepository messageRepository;

    @Nullable
    public Membership getMembership(String userId, String channelId) {
        return membershipRepository.findByChatUserIdAndChatChannelId(userId, channelId).orElse(null);
    }

    @Nullable
    public ChatChannel getChatChannel(String userId, String channelId) {
        return channelRepository.findChatChannelByIdAndMembership(channelId, userId).orElse(null);
    }

    public int getMessageUnreadCount(ChatChannel channel, Membership member) {
         if (member.getLastReadMessage() != null) {
           return messageRepository.countUnreadMessages(
                    member.getLastReadMessage().getId(),
                    channel.getId(),
                    member.getChatUser().getId()
            );
        } else {
             return  messageRepository.countChatMessagesByChatChannelAndSentByNot(
                    channel,
                    member.getChatUser()
            );
        }
    }
}