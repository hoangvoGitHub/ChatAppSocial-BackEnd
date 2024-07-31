package com.hoangvo.chatappsocial.model.event;

import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.model.dto.ChatMessageDto;
import com.hoangvo.chatappsocial.model.dto.ChatUserDto;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class MessageReadEvent extends ChatEvent {
    @Nonnull
    private String cid;
    @Nonnull
    private ChatUserDto user;

    @Nonnull
    ChatMessageDto message;

    @Builder
    public MessageReadEvent(Date createdAt,
                            @Nonnull String cid,
                            @Nonnull ChatUserDto user,
                            @Nonnull ChatMessageDto message) {
        super(ChatEventType.Read, createdAt);
        this.cid = cid;
        this.user = user;
        this.message = message;
    }


}
