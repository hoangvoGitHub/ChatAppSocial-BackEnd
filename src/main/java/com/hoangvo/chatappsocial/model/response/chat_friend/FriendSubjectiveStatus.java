package com.hoangvo.chatappsocial.model.response.chat_friend;

import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.model.user_friend.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public enum FriendSubjectiveStatus {
    REQUEST_FROM_ME,
    REQUEST_FROM_OTHER,
    FRIEND,
    NONE
}


