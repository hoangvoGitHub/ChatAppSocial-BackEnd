package com.hoangvo.chatappsocial.model.chat_message;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.model.membership.MembershipKey;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_chat_message")
public class ChatMessage {
    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Nonnull
    @ManyToOne
    @JoinColumn(name = "sent_by", referencedColumnName = "id")
    private ChatUser sentBy;

    @Nonnull
    @ManyToOne
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private ChatChannel chatChannel;

    @Nonnull
    private String text;

    @ManyToOne
    @Builder.Default
    @JoinColumn(name = "reply_to_id", referencedColumnName = "id")
    private ChatMessage replyTo = null;

    @Nonnull
    @Enumerated(EnumType.STRING)
    private MessageType type;


    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    @Nonnull
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Nullable
    private Date updatedAt;

    @Nullable
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", sentBy=" + sentBy +
                ", chatChannel=" + chatChannel +
                ", text='" + text + '\'' +
                ", replyTo=" + replyTo +
                ", type=" + type +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }

    public ChatMessage(ChatMessageBuilder builder) {
        this.id = builder.id;
        this.sentBy = builder.sentBy;
        this.chatChannel = builder.chatChannel;
        this.text = builder.text;
        this.replyTo = builder.replyTo;
        this.type = builder.type;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.deletedAt = builder.deletedAt;
    }

    // Getters for all fields (omitted for brevity)

    public static final class ChatMessageBuilder {
        private String id;
        private ChatUser sentBy;
        private ChatChannel chatChannel;
        private String text;
        private ChatMessage replyTo = null;
        private MessageType type;
        private Date createdAt = new Date();
        private Date updatedAt;
        private Date deletedAt;

        private ChatMessageBuilder() {}

        public static ChatMessageBuilder builder() {
            return new ChatMessageBuilder();
        }

        public ChatMessageBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ChatMessageBuilder sentBy(ChatUser sentBy) {
            this.sentBy = sentBy;
            return this;
        }

        public ChatMessageBuilder chatChannel(ChatChannel chatChannel) {
            this.chatChannel = chatChannel;
            return this;
        }

        public ChatMessageBuilder text(String text) {
            this.text = text;
            return this;
        }

        public ChatMessageBuilder replyTo(ChatMessage replyTo) {
            this.replyTo = replyTo;
            return this;
        }

        public ChatMessageBuilder type(MessageType type) {
            this.type = type;
            return this;
        }

        public ChatMessageBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ChatMessageBuilder updatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ChatMessageBuilder deletedAt(Date deletedAt) {
            this.deletedAt = deletedAt;
            return this;
        }

        public ChatMessage build() {
            return new ChatMessage(this);
        }
    }

    @PrePersist
    public void prePersist() {
        if (getId() == null) {
            this.id = UUID.randomUUID().toString();
        }

    }

}
