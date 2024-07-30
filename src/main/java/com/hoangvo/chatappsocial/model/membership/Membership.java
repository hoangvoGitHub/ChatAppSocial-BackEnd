package com.hoangvo.chatappsocial.model.membership;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import com.hoangvo.chatappsocial.model.chat_message.ChatMessage;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "_membership")
public class Membership {
    @JsonIgnore
    @EmbeddedId
    @Builder.Default
    private MembershipKey id = new MembershipKey();

    @JsonIgnore
    @ManyToOne
    @MapsId("channel_id")
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    private ChatChannel chatChannel;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private ChatUser chatUser;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private ChannelRole channelRole;

    private Date lastReadAt;

    @ManyToOne
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JoinColumn(name = "last_read_message_id", referencedColumnName = "id")
    private ChatMessage lastReadMessage;

    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    @Nonnull
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Nullable
    private Date updatedAt;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Nullable
    private Date deletedAt;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "display_messages_after",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date displayMessagesAfter;

    public void setLastReadMessage(@Nonnull ChatMessage chatMessage, @Nonnull Date readAt) {
        this.lastReadMessage = chatMessage;
        this.lastReadAt = readAt;
    }

    public void setDisplayMessagesAfter(Date displayMessagesAfter) {
        this.displayMessagesAfter = displayMessagesAfter;
    }

    @PrePersist
    public void prePersist() {
        if (getId() == null) {
            this.id = new MembershipKey(
                    chatChannel.getId(),
                    chatUser.getId()
            );
        } else if (getId().getChannelId() == null || getId().getUserId() == null) {
            this.id.setChannelId(chatChannel.getId());
            this.id.setUserId(chatUser.getId());
        }
    }
}
