package com.hoangvo.chatappsocial.model.event;

import com.hoangvo.chatappsocial.model.dto.ChatMessageDto;
import com.hoangvo.chatappsocial.model.dto.ChatUserDto;
import jakarta.annotation.Nonnull;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class NewMessageEvent extends ChatEvent {
    @Nonnull
    private String cid;
    @Nonnull
    private ChatMessageDto message;
    @Nonnull
    private ChatUserDto user;
    @Nonnull
    private Integer unreadCount;

    @Builder
    public NewMessageEvent(Date createdAt, @Nonnull String cid, @Nonnull ChatMessageDto message, @Nonnull ChatUserDto user, @Nonnull Integer unreadCount) {
        super(ChatEventType.NewMessage, createdAt);
        this.cid = cid;
        this.message = message;
        this.user = user;
        this.unreadCount = unreadCount;
    }

}
