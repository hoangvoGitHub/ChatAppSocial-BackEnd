package com.hoangvo.chatappsocial.model.aggregation;

import com.hoangvo.chatappsocial.model.response.chat_friend.FriendSubjectiveStatus;

import java.util.Date;

public interface ChatUserWithFriendStatus {
    String getId();
    String getImageUrl();
    Boolean getIsOnline();
    Boolean getIsInvisible();
    Date getLastActiveAt();
    Date getCreatedAt();
    Date getUpdatedAt();
    Date getDeletedAt();
    String getName(); // This will call the getName() method from ChatUser
    FriendSubjectiveStatus getFriendStatus(); // Custom property for friend status
}