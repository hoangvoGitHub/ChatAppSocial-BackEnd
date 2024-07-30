package com.hoangvo.chatappsocial.model.user_friend;

import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "_chat_friend")
public class ChatFriend {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Nonnull
    @ManyToOne
    @JoinColumn(name = "first_user_id", referencedColumnName = "id")
    private ChatUser firstUser;

    @Nonnull
    @ManyToOne
    @JoinColumn(name = "second_user_id", referencedColumnName = "id")
    private ChatUser secondUser;

    @Nonnull
    private Date createdAt;

    @Nonnull
    @Enumerated(EnumType.STRING)
    private FriendStatus status;

    @PrePersist
    public void prePersist() {
        // Ensure that the first user's ID is smaller than the second user's ID
        if (!Objects.equals(firstUser.getId(), secondUser.getId()) && firstUser.getId().compareTo(secondUser.getId()) > 0) {
            throw new IllegalStateException("The ID of the first user must be smaller than the ID of the second user.");
        }
    }
}

