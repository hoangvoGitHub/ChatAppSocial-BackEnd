package com.hoangvo.chatappsocial.service;

import com.hoangvo.chatappsocial.model.chat_channel.ChannelType;
import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.model.membership.ChannelRole;
import com.hoangvo.chatappsocial.model.membership.Membership;
import com.hoangvo.chatappsocial.model.membership.MembershipKey;
import com.hoangvo.chatappsocial.model.request.chat_friend.ChatFriendRequest;
import com.hoangvo.chatappsocial.model.response.chat_friend.ChatFriendResponse;
import com.hoangvo.chatappsocial.model.response.chat_friend.FriendSubjectiveStatus;
import com.hoangvo.chatappsocial.model.user_friend.ChatFriend;
import com.hoangvo.chatappsocial.model.user_friend.FriendStatus;
import com.hoangvo.chatappsocial.repository.chat_channel.ChatChannelRepository;
import com.hoangvo.chatappsocial.repository.chat_user.ChatUserRepository;
import com.hoangvo.chatappsocial.repository.membership.MembershipRepository;
import com.hoangvo.chatappsocial.repository.user_friend.ChatFriendRepository;
import com.hoangvo.chatappsocial.utils.UserAccessHandler;
import com.hoangvo.chatappsocial.utils.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatFriendService {

    private final ChatFriendRepository repository;
    private final ChatUserRepository userRepository;
    private final ChatChannelRepository channelRepository;
    private final MembershipRepository membershipRepository;

    public BaseResponse<ChatFriendResponse> addFriend(ChatFriendRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentChatUser = UserAccessHandler.getCurrentChatUser(authentication, userRepository).orElseThrow();
        final ChatUser secondUser = userRepository.findById(request.getRecipientId()).orElseThrow();

        if (Objects.equals(currentChatUser.getId(), request.getRecipientId())) {
            return BaseResponse.<ChatFriendResponse>builder()
                    .status(HttpStatus.CONFLICT)
                    .data(null)
                    .message("You cannot send request to yourself")
                    .build();
        }

        final ChatFriend existChatFriend = repository.findChatFriendByChatUsers(currentChatUser.getId(), secondUser.getId()).orElse(null);
        if (existChatFriend != null) {
            return BaseResponse.<ChatFriendResponse>builder()
                    .status(HttpStatus.CONFLICT)
                    .data(null)
                    .message("Already exist")
                    .build();
        }
        ChatFriend.ChatFriendBuilder chatFriendBuilder;
        FriendStatus status;

        if (currentChatUser.getId().compareTo(request.getRecipientId()) < 0) {
            chatFriendBuilder = ChatFriend.builder()
                    .firstUser(currentChatUser)
                    .secondUser(secondUser);
            status = FriendStatus.REQUEST_FROM_UID1;
        } else {
            chatFriendBuilder = ChatFriend.builder()
                    .firstUser(secondUser)
                    .secondUser(currentChatUser);
            status = FriendStatus.REQUEST_FROM_UID2;
        }

        final Date createdAt = new Date();

        chatFriendBuilder.createdAt(createdAt);
        chatFriendBuilder.status(status);

        final ChatFriend savedChatFriend = repository.save(chatFriendBuilder.build());
        var chatFriendResponse = ChatFriendResponse.builder()
                .id(savedChatFriend.getId())
                .user(secondUser)
                .createdAt(savedChatFriend.getCreatedAt())
                .status(FriendSubjectiveStatus.REQUEST_FROM_ME)
                .build();

        return BaseResponse.<ChatFriendResponse>builder()
                .status(HttpStatus.OK)
                .message("OK")
                .data(chatFriendResponse)
                .build();
    }

    public BaseResponse<ChatFriendResponse> acceptFriend(ChatFriendRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentChatUser = UserAccessHandler.getCurrentChatUser(authentication, userRepository).orElseThrow();
        final ChatUser secondUser = userRepository.findById(request.getRecipientId()).orElseThrow();
        final ChatFriend existChatFriend = repository.findChatFriendByChatUsers(currentChatUser.getId(), secondUser.getId()).orElse(null);
        if (existChatFriend == null) {
            return BaseResponse.<ChatFriendResponse>builder()
                    .status(HttpStatus.NOT_FOUND)
                    .data(null)
                    .message("Friend not found")
                    .build();
        }

        FriendStatus requiredStatus = (currentChatUser.getId().compareTo(request.getRecipientId()) < 0)
                ? FriendStatus.REQUEST_FROM_UID2
                : FriendStatus.REQUEST_FROM_UID1;

        if (existChatFriend.getStatus() != requiredStatus) {
            return BaseResponse.<ChatFriendResponse>builder()
                    .status(HttpStatus.CONFLICT)
                    .data(null)
                    .message("Unknown Error")
                    .build();
        }

        existChatFriend.setStatus(FriendStatus.FRIEND);

        var chatFriendResponseBuilder = ChatFriendResponse.builder()
                .user(secondUser)
                .createdAt(existChatFriend.getCreatedAt())
                .status(FriendSubjectiveStatus.FRIEND);


        // Query the peer to peer channel between two user
        final ChatChannel existChannel =
                channelRepository.findChannelsByMemberships(
                        List.of(currentChatUser.getId(),
                                secondUser.getId()),
                        ChannelType.MUTUAL.name()
                ).orElse(null);

        if (existChannel != null) {
            // if present, return the response
            chatFriendResponseBuilder.channelId(existChannel.getId())
                    .id(repository.save(existChatFriend).getId());
            return BaseResponse.<ChatFriendResponse>builder()
                    .status(HttpStatus.OK)
                    .message("OK")
                    .data(chatFriendResponseBuilder.build())
                    .build();
        }
        //otherwise, create a new one
        // if there is no channel, create new
        final Date createdAt = new Date();
        final ChatChannel newChannel = channelRepository.save(
                ChatChannel.builder()
                        .createdAt(createdAt)
                        .memberships(Set.of())
                        .createdByUser(currentChatUser)
                        .type(ChannelType.MUTUAL)
                        .build()
        );

        // Saved the second user as the admin

        membershipRepository
                .save(
                        Membership.builder()
                                .id(new MembershipKey())
                                .chatChannel(newChannel)
                                .chatUser(secondUser)
                                .channelRole(ChannelRole.CHANNEL_ADMIN)
                                .createdAt(createdAt)
                                .displayMessagesAfter(createdAt)
                                .build()
                );

        // Saved the current user as the member
        membershipRepository
                .save(
                        Membership.builder()
                                .id(new MembershipKey())
                                .chatChannel(newChannel)
                                .chatUser(currentChatUser)
                                .channelRole(ChannelRole.CHANNEL_MEMBER)
                                .createdAt(createdAt)
                                .displayMessagesAfter(createdAt)
                                .build()
                );

        chatFriendResponseBuilder.channelId(newChannel.getId())
                .id(repository.save(existChatFriend).getId());
        return BaseResponse.<ChatFriendResponse>builder()
                .status(HttpStatus.OK)
                .message("OK")
                .data(chatFriendResponseBuilder.build())
                .build();
    }

    public BaseResponse<ChatFriendResponse> rejectFriend(ChatFriendRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentChatUser = UserAccessHandler.getCurrentChatUser(authentication, userRepository).orElseThrow();
        final ChatUser secondUser = userRepository.findById(request.getRecipientId()).orElseThrow();
        final ChatFriend existChatFriend = repository.findChatFriendByChatUsers(currentChatUser.getId(), secondUser.getId()).orElse(null);
        if (existChatFriend == null) {
            return BaseResponse.<ChatFriendResponse>builder()
                    .status(HttpStatus.NOT_FOUND)
                    .data(null)
                    .message("Friend not found")
                    .build();
        }
        if (existChatFriend.getStatus() == FriendStatus.FRIEND) {
            return BaseResponse.<ChatFriendResponse>builder()
                    .status(HttpStatus.OK)
                    .message("You both were friends, you can only remove this friend from friend list")
                    .data(null)
                    .build();
        }
        repository.deleteById(existChatFriend.getId());
        return BaseResponse.<ChatFriendResponse>builder()
                .status(HttpStatus.OK)
                .message("OK")
                .data(null)
                .build();


    }

    public BaseResponse<ChatFriendResponse> removeFriend(ChatFriendRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentChatUser = UserAccessHandler.getCurrentChatUser(authentication, userRepository).orElseThrow();
        final ChatUser secondUser = userRepository.findById(request.getRecipientId()).orElseThrow();
        final ChatFriend existChatFriend = repository.findChatFriendByChatUsers(currentChatUser.getId(), secondUser.getId()).orElse(null);
        if (existChatFriend == null) {
            return BaseResponse.<ChatFriendResponse>builder()
                    .status(HttpStatus.NOT_FOUND)
                    .data(null)
                    .message("Friend not found")
                    .build();
        }
        if (existChatFriend.getStatus() != FriendStatus.FRIEND) {
            return BaseResponse.<ChatFriendResponse>builder()
                    .status(HttpStatus.OK)
                    .message("You can only remove friend")
                    .data(null)
                    .build();
        }
        repository.deleteById(existChatFriend.getId());
        return BaseResponse.<ChatFriendResponse>builder()
                .status(HttpStatus.OK)
                .message("OK")
                .data(null)
                .build();

    }

    public BaseResponse<List<ChatFriendResponse>> queryFriends(
            String name,
            FriendSubjectiveStatus status,
            int limit,
            int offset,
            Optional<String> sortBy
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentChatUser = UserAccessHandler.getCurrentChatUser(authentication, userRepository).orElseThrow();
        final List<ChatFriend> chatFriends;
        final List<ChatFriendResponse> chatFriendResponses = new ArrayList<>();
        if (status == FriendSubjectiveStatus.FRIEND) {
            chatFriends = repository.queryFriendList(
                    name,
                    currentChatUser.getId(),
                    PageRequest.of(
                            offset,
                            limit
                    )
            );

        } else {
            chatFriends = repository.queryFriendRequests(
                    name,
                    currentChatUser.getId(),
                    PageRequest.of(
                            offset,
                            limit
                    )
            );
        }


        for (ChatFriend chatFriend : chatFriends) {
            final ChatUser otherUser = (Objects.equals(chatFriend.getFirstUser().getId(), currentChatUser.getId()))
                    ? chatFriend.getSecondUser()
                    : chatFriend.getFirstUser();

            String peerToPeerChannel = null;
            final ChatChannel chatChannel = channelRepository
                    .findChannelsByMemberships(
                            List.of(chatFriend.getFirstUser().getId(),
                                    chatFriend.getSecondUser().getId()),
                            ChannelType.MUTUAL.name()
                    ).orElse(null);

            if (chatChannel != null) {
                peerToPeerChannel = chatChannel.getId();
            }


            FriendSubjectiveStatus subjectiveStatus = extractSubjectiveStatus(
                    chatFriend.getStatus(),
                    chatFriend.getFirstUser().getId(),
                    currentChatUser.getId()
            );

            final ChatFriendResponse response = ChatFriendResponse.builder()
                    .id(chatFriend.getId())
                    .user(otherUser)
                    .createdAt(chatFriend.getCreatedAt())
                    .channelId(peerToPeerChannel)
                    .status(subjectiveStatus)
                    .build();
            chatFriendResponses.add(response);
        }

        return BaseResponse.<List<ChatFriendResponse>>builder()
                .data(chatFriendResponses)
                .message("OK")
                .status(HttpStatus.OK)
                .build();


    }

    private FriendSubjectiveStatus extractSubjectiveStatus(@Nonnull FriendStatus status,
                                                           @Nonnull String firstUserId,
                                                           @Nonnull String currentUserId
    ) {
        return switch (status) {
            case REQUEST_FROM_UID1 -> (firstUserId.equals(currentUserId))
                    ? FriendSubjectiveStatus.REQUEST_FROM_ME
                    : FriendSubjectiveStatus.REQUEST_FROM_OTHER;


            case REQUEST_FROM_UID2 -> (firstUserId.equals(currentUserId))
                    ? FriendSubjectiveStatus.REQUEST_FROM_OTHER
                    : FriendSubjectiveStatus.REQUEST_FROM_ME;

            case FRIEND -> FriendSubjectiveStatus.FRIEND;
        };
    }

}
