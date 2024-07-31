package com.hoangvo.chatappsocial.model.response.chat_channel;

import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import com.hoangvo.chatappsocial.model.chat_message.ChatMessage;
import com.hoangvo.chatappsocial.model.dto.ChatMemberReadDto;
import com.hoangvo.chatappsocial.model.dto.ChatMessageDto;
import com.hoangvo.chatappsocial.model.membership.Membership;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ChatChannelResponse {
    private ChatChannel channel;
    private List<ChatMessageDto> messages;
    private List<Membership> members;
    private List<ChatMemberReadDto> reads;
    private Membership membership;
    private int unreadCount;

}
