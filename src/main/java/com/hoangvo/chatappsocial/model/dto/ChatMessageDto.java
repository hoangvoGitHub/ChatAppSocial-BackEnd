package com.hoangvo.chatappsocial.model.dto;

import com.hoangvo.chatappsocial.model.chat_message.MessageType;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatMessageDto {
    private String id;

    @Nonnull
    private ChatUserDto sentBy;

    @Nonnull
    private String chatChannel;

    @Nonnull
    private String text;

    @Nullable
    private ChatMessageDto replyTo = null;

    @Nonnull
    private MessageType type;

    @Nonnull
    private Date createdAt = new Date();

    @Nullable
    private Date updatedAt;

    @Nonnull
    private List<ChatAttachmentDto> attachments;
}
