package com.hoangvo.chatappsocial.model.chat_user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoangvo.chatappsocial.auth.data.model.User;
import com.hoangvo.chatappsocial.model.membership.Membership;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_chat_user")
//@JsonIdentityInfo(
//        generator = ObjectIdGenerators.PropertyGenerator.class,
//        property = "id")
public class ChatUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "original_user_id", referencedColumnName = "id")
    private User originalUser;

    @Column(name  = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    private Boolean isOnline;

    private Boolean isInvisible;

    @JsonIgnore
    @OneToMany(mappedBy = "chatUser")
    private Set<Membership> memberships;

    @Temporal(TemporalType.TIMESTAMP)
    @Nonnull
    private Date lastActiveAt;

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

    public String getName(){
        return  originalUser.getFirstName() + " " + originalUser.getLastName();
    }
}
