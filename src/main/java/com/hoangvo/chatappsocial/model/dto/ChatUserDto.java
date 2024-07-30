package com.hoangvo.chatappsocial.model.dto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ChatUserDto {

    @Nonnull
    private String id;

    @Nullable
    private String imageUrl;

    @Nonnull
    private String name;

    @Nullable
    private Boolean isOnline;

    @Nullable
    private Boolean isInvisible;

    @Nonnull
    private Date lastActiveAt;

    @Nonnull
    private Date createdAt;

    @Nullable
    private Date updatedAt;

}
