package com.hoangvo.chatappsocial.model.dto;

import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import jakarta.annotation.Nonnull;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberReadDto {
    @Nonnull
    private ChatUserDto user;
    private String lastReadMessageId;
    private Date lastReadAt;
    private int unreadCount;
}
