package com.hoangvo.chatappsocial.model.dto;

import com.hoangvo.chatappsocial.model.response.chat_friend.FriendSubjectiveStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Builder
@Setter
public class SearchUserDto {
    private String id;
    private String image;
    private String name;
    @Nullable
    private String channelId;
    private FriendSubjectiveStatus friendStatus;
}
