package com.hoangvo.chatappsocial.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SearchResultDto {
    private List<SearchUserDto> searchUsers;
    private List<SearchGroupChannelDto> searchGroups;
    private int nextOffset;
}
