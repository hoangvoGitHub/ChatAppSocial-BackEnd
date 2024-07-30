package com.hoangvo.chatappsocial.model.chat_channel;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.hoangvo.chatappsocial.model.chat_message.ChatMessage;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.model.membership.Membership;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
//@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Table(name = "_chat_channel")
public class ChatChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private ChannelType type;

    private String name;

    @Column(name  = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private ChatUser createdByUser;

    @JsonIgnore
    @OneToMany(mappedBy = "chatChannel")
    @Nonnull
    private Set<Membership> memberships;

    @JsonIgnore
    @OneToMany(mappedBy = "chatChannel")
    private Set<ChatMessage> chatMessages;

    @Temporal(TemporalType.TIMESTAMP)
    @Nonnull
    @Builder.Default
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Nullable
    private Date updatedAt;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Nullable
    private Date deletedAt;


    @Override
    public String toString() {
        return "ChatChannel{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdByUser=" + createdByUser +
                ", memberships=" + memberships +
                ", chatMessages=" + chatMessages +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
