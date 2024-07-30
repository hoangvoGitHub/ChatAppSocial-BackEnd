package com.hoangvo.chatappsocial.service;

import com.hoangvo.chatappsocial.model.aggregation.ChatUserWithFriendStatus;
import com.hoangvo.chatappsocial.model.dto.SearchGroupChannelDto;
import com.hoangvo.chatappsocial.model.request.resource.PageableRequest;
import com.hoangvo.chatappsocial.model.request.resource.QueryResourceRequest;
import com.hoangvo.chatappsocial.repository.chat_channel.ChatChannelRepository;
import com.hoangvo.chatappsocial.model.chat_channel.ChannelType;
import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import com.hoangvo.chatappsocial.repository.chat_user.ChatUserRepository;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.model.dto.SearchResultDto;
import com.hoangvo.chatappsocial.model.dto.SearchUserDto;
import com.hoangvo.chatappsocial.utils.UserAccessHandler;
import com.hoangvo.chatappsocial.utils.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ChatUserRepository chatUserRepository;
    private final ChatChannelRepository chatChannelRepository;

    public BaseResponse<SearchResultDto> search(
            String query, int offset, int limit
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentUser = UserAccessHandler.getCurrentChatUser(authentication, chatUserRepository).orElseThrow();
        // query chat user with name
        final SearchResultDto.SearchResultDtoBuilder resultBuilder = SearchResultDto.builder();
        final List<ChatUser> chatUsers = chatUserRepository
                .queryChatUserByNameContainsIgnoreCase(query, PageRequest.of(offset, limit));

        final Set<SearchUserDto> searchUserDtos = new HashSet<>();

        // query p2p channel between current user and this user

        chatUsers.forEach(
                chatUser -> {
                    String peerToPeerChannel = null;
                    final ChatChannel chatChannel = chatChannelRepository
                            .findChannelsByMemberships(
                                    List.of(chatUser.getId(), currentUser.getId()),
                                    ChannelType.MUTUAL.name()
                            ).orElse(null);

                    if (chatChannel != null) {
                        peerToPeerChannel = chatChannel.getId();
                    }
                    var searchUser = SearchUserDto.builder().id(chatUser.getId())
                            .image(chatUser.getImageUrl())
                            .name(chatUser.getName())
                            .channelId(peerToPeerChannel)
                            .build();

                    searchUserDtos.add(searchUser);


                }
        );
        resultBuilder.searchUsers(searchUserDtos.stream().toList())
                .searchGroups(List.of())
                .nextOffset(offset + 1);

        return BaseResponse.<SearchResultDto>builder()
                .status(HttpStatus.OK)
                .message("OK")
                .data(resultBuilder.build())
                .build();

        // query group with name or member's name

    }

    public BaseResponse<SearchResultDto> search(QueryResourceRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentUser = UserAccessHandler.getCurrentChatUser(authentication, chatUserRepository).orElseThrow();
        final String query = request.getSearchQuery();

        final SearchResultDto.SearchResultDtoBuilder resultBuilder = SearchResultDto.builder();

        request.getResource().forEach((searchResource, pageableRequest) -> {
            switch (searchResource) {
                case Users -> {
                    List<SearchUserDto> searchUserDtos =
                            retrieveSearchUserDtos(
                                    query,
                                    currentUser.getId(),
                                    pageableRequest
                            );
                    resultBuilder.searchUsers(searchUserDtos);
                }
                case Channels -> {
                    resultBuilder.searchGroups(
                            retrieveSearchGroupChannelDtos(
                                    query,
                                    currentUser.getId(),
                                    pageableRequest
                            )
                    );
                }

                case Messages -> {

                }
            }
        });

        return BaseResponse.<SearchResultDto>builder()
                .status(HttpStatus.OK)
                .message("OK")
                .data(resultBuilder
                        .nextOffset(1)
                        .build())
                .build();


    }


    private List<SearchUserDto> retrieveSearchUserDtos(
            String query,
            String currentUserId,
            PageableRequest pageableRequest

    ) {
        final List<ChatUserWithFriendStatus> chatUsers = chatUserRepository
                .findChatUsersWithFriendStatus(query,
                        currentUserId,
                        PageRequest.of(pageableRequest.getOffset(), pageableRequest.getLimit()));

        return chatUsers.stream().map(chatUser ->
                {
                    String peerToPeerCid
                            = chatChannelRepository.findChannelsByMemberships(
                            List.of(chatUser.getId(), currentUserId),
                            ChannelType.MUTUAL.name()
                    ).map(ChatChannel::getId).orElse(null);

                    return SearchUserDto.builder()
                            .id(chatUser.getId())
                            .image(chatUser.getImageUrl())
                            .name(chatUser.getName())
                            .friendStatus(chatUser.getFriendStatus())
                            .channelId(peerToPeerCid)
                            .build();
                }
        ).toList();
    }

    private List<SearchGroupChannelDto> retrieveSearchGroupChannelDtos(
            String query,
            String currentUserId,
            PageableRequest pageableRequest
    ) {
        return List.of();
    }
}

