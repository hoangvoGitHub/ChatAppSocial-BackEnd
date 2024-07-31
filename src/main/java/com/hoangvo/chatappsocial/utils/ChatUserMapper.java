package com.hoangvo.chatappsocial.utils;

import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.model.dto.ChatUserDto;

public class ChatUserMapper {
    public static ChatUserDto toDto(ChatUser chatUser) {
        final ChatUserDto chatUserDto = new ChatUserDto();

        chatUserDto.setId(chatUser.getId());
        chatUserDto.setImageUrl(chatUser.getImageUrl());
        chatUserDto.setName(chatUser.getName());
        chatUserDto.setIsOnline(chatUser.getIsOnline());
        chatUserDto.setIsInvisible(chatUser.getIsInvisible());
        chatUserDto.setLastActiveAt(chatUser.getLastActiveAt());
        chatUserDto.setCreatedAt(chatUser.getCreatedAt());
        chatUserDto.setUpdatedAt(chatUser.getUpdatedAt());

        return chatUserDto;
    }
}
