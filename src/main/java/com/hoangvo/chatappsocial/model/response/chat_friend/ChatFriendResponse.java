package com.hoangvo.chatappsocial.model.response.chat_friend;

import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ChatFriendResponse {
    private String id;
    private ChatUser user;
    private FriendSubjectiveStatus status;
    private Date createdAt;
    @Nullable
    private String channelId;
}

