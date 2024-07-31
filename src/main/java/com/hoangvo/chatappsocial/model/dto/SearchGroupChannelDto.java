package com.hoangvo.chatappsocial.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SearchGroupChannelDto {
    private String id;
    private String image;
    private List<SearchUserDto> members;
}
