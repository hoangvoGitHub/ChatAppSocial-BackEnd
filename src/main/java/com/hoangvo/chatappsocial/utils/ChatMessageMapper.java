package com.hoangvo.chatappsocial.utils;

import com.hoangvo.chatappsocial.model.attachment.ChatAttachment;
import com.hoangvo.chatappsocial.model.chat_message.ChatMessage;
import com.hoangvo.chatappsocial.model.dto.ChatMessageDto;
import jakarta.annotation.Nullable;

import java.util.List;

public class ChatMessageMapper {

    @Nullable
    public static ChatMessageDto toDto(@Nullable ChatMessage chatMessage,
                                       List<ChatAttachment> attachments) {
        if (chatMessage == null) {
            return null;
        }
        final ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setId(chatMessage.getId());
        chatMessageDto.setSentBy(ChatUserMapper.toDto(chatMessage.getSentBy()));
        chatMessageDto.setChatChannel(chatMessage.getChatChannel().getId());
        chatMessageDto.setText(chatMessage.getText());
        chatMessageDto.setReplyTo(toDto(chatMessage.getReplyTo(), List.of()));
        chatMessageDto.setType(chatMessage.getType());
        chatMessageDto.setCreatedAt(chatMessage.getCreatedAt());
        chatMessageDto.setUpdatedAt(chatMessage.getUpdatedAt());
        chatMessageDto.setAttachments(attachments.stream().map(ChatAttachmentMapper::toDto).toList());
        return chatMessageDto;
    }
}
