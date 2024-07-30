package com.hoangvo.chatappsocial.service;

import com.hoangvo.chatappsocial.auth.data.model.User;
import com.hoangvo.chatappsocial.model.attachment.ChatAttachment;
import com.hoangvo.chatappsocial.model.chat_channel.ChannelType;
import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import com.hoangvo.chatappsocial.model.chat_message.ChatMessage;
import com.hoangvo.chatappsocial.model.chat_message.MessageType;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.model.dto.ChatMessageDto;
import com.hoangvo.chatappsocial.model.event.MessageReadEvent;
import com.hoangvo.chatappsocial.model.event.NewMessageEvent;
import com.hoangvo.chatappsocial.model.membership.ChannelRole;
import com.hoangvo.chatappsocial.model.membership.Membership;
import com.hoangvo.chatappsocial.model.request.MarkReadRequest;
import com.hoangvo.chatappsocial.model.request.chat_channel.CreateChatChannelRequest;
import com.hoangvo.chatappsocial.model.request.chat_channel.QueryChannelRequest;
import com.hoangvo.chatappsocial.model.request.chat_channel.QueryChannelsRequest;
import com.hoangvo.chatappsocial.model.request.chat_message.SendChatMessageRequest;
import com.hoangvo.chatappsocial.model.response.chat_channel.ChatChannelResponse;
import com.hoangvo.chatappsocial.model.response.chat_message.ChatMessageResponse;
import com.hoangvo.chatappsocial.model.response.file.FileUploadResponse;
import com.hoangvo.chatappsocial.repository.chat_attachment.ChatAttachmentRepository;
import com.hoangvo.chatappsocial.repository.chat_channel.ChatChannelRepository;
import com.hoangvo.chatappsocial.repository.chat_message.ChatMessageRepository;
import com.hoangvo.chatappsocial.repository.chat_user.ChatUserRepository;
import com.hoangvo.chatappsocial.repository.membership.MembershipRepository;
import com.hoangvo.chatappsocial.service.upload.FileUploadService;
import com.hoangvo.chatappsocial.utils.*;
import com.hoangvo.chatappsocial.utils.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatChannelService {

    private final ChatChannelRepository channelRepository;
    private final ChatUserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final ChatMessageRepository messageRepository;
    private final ChatAttachmentRepository attachmentRepository;
    private final FileUploadService fileUploadService;
    private final RetrieveMessageHelper retrieveMessageHelper;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(ChatChannelService.class);

    public ChatChannelResponse createChatChannel(CreateChatChannelRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final User currentUser = (User) authentication.getPrincipal();
        final ChatUser currentChatUser = userRepository.findChatUserByOriginalUser(currentUser);


        if (request.getType() != null && request.getType() == ChannelType.MUTUAL) {
            // Check whether the mutual channel exists or not
            final ChatChannel chatChannel = channelRepository
                    .findChannelsByMemberships(
                            request.getMembers(),
                            ChannelType.MUTUAL.name()
                    ).orElse(null);
            if (chatChannel != null) {

                List<ChatMessageDto> messagesWithAttachments =
                        messageRepository.findAllByChatChannel(chatChannel, null).stream()
                                .map(retrieveMessageHelper::enrichWithAttachments).toList();

                List<Membership> members =
                        membershipRepository.findAllByChatChannelId(chatChannel.getId(), null);

                Membership myMembership = membershipRepository
                        .findByChatUserIdAndChatChannelId(
                                currentChatUser.getId(),
                                chatChannel.getId()).orElse(null);

                return ChatChannelResponse.builder()
                        .unreadCount(0)
                        .channel(chatChannel)
                        .reads(List.of())
                        .messages(messagesWithAttachments)
                        .members(members)
                        .membership(myMembership)
                        .build();
            }
        }

        List<ChatUser> chatUsers = userRepository.findAllById(request.getMembers());

        var newChannel = ChatChannel.builder()
                .createdAt(new Date())
                .memberships(Set.of())
                .createdByUser(currentChatUser)
                .name(request.getName())
                .type(request.getType())
                .build();
        //Create channel and save to database first
        final ChatChannel savedChannel = channelRepository.save(newChannel);

        // Saved the creator as the admin
        final Date createdAt = new Date();
        membershipRepository
                .save(
                        Membership.builder()
                                .chatChannel(savedChannel)
                                .chatUser(currentChatUser)
                                .channelRole(ChannelRole.CHANNEL_ADMIN)
                                .createdAt(createdAt)
                                .displayMessagesAfter(createdAt)
                                .build()
                );
        // Save other member
        chatUsers.forEach(
                chatUser -> {
                    if (!Objects.equals(chatUser.getId(), currentChatUser.getId())) {
                        membershipRepository.save(
                                Membership.builder()
                                        .chatChannel(savedChannel)
                                        .chatUser(chatUser)
                                        .createdAt(createdAt)
                                        .displayMessagesAfter(createdAt)
                                        .channelRole(ChannelRole.CHANNEL_MEMBER)
                                        .build()
                        );
                    }
                }
        );
        if (request.getMessage() != null) {
            // Save first message
            ChatMessage savedChatMessage = messageRepository.save(
                    ChatMessage.ChatMessageBuilder.builder()
                            .createdAt(new Date())
                            .chatChannel(savedChannel)
                            .sentBy(currentChatUser)
                            .text(request.getMessage())
                            .type(MessageType.CHAT)
                            .build());

            // send message to other users
            var event = NewMessageEvent.builder()
                    .createdAt(new Date())
                    .unreadCount(0)
                    .message(retrieveMessageHelper.enrichWithAttachments(savedChatMessage))
                    .cid(savedChannel.getId())
                    .user(ChatUserMapper.toDto(currentChatUser))
                    .build();


            savedChannel.getMemberships().forEach(membership -> {
                LOG.info("member id: " + membership.getId());
                simpMessagingTemplate.convertAndSend("/topic/" + membership.getChatUser().getId(), event);
            });
        }


        final ChatChannel finalChannel = channelRepository.findById(savedChannel.getId()).orElse(null);
        List<ChatMessageDto> messagesWithAttachments =
                messageRepository.findAllByChatChannel(finalChannel, null).stream()
                        .map(retrieveMessageHelper::enrichWithAttachments).toList();

        return ChatChannelResponse.builder()
                .unreadCount(0)
                .reads(List.of())
                .channel(finalChannel)
                .messages(messagesWithAttachments)
                .members(membershipRepository.findAllByChatChannelId(finalChannel.getId(), null))
                .membership(membershipRepository.findByChatUserIdAndChatChannelId(currentChatUser.getId(),
                        finalChannel.getId()).orElse(null))
                .build();


    }

    public List<ChatChannelResponse> queryChannels(QueryChannelsRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentChatUser = UserAccessHandler.getCurrentChatUser(authentication, userRepository).orElseThrow();
        final List<ChatChannel> chatChannels = channelRepository.findChatChannelsByMembershipsOrderByLatestMessage(
                currentChatUser.getId(),
                PageRequest.of(
                        request.getOffset(),
                        request.getLimit()
                )
        );

        final List<ChatChannelResponse> responses = new ArrayList<>();

        for (ChatChannel chatChannel : chatChannels) {
            List<ChatMessageDto> messagesWithAttachments =
                    messageRepository.findAllMessageByChannelId(
                                    chatChannel.getId(),
                                    request.getMessageLimit(),
                                    currentChatUser.getId()
                            )
                            .stream()
                            .map(retrieveMessageHelper::enrichWithAttachments).toList();

            if (messagesWithAttachments.isEmpty()) {
                continue;
            }

            final List<Membership> memberships = membershipRepository.findAllByChatChannelId(chatChannel.getId(),
                    PageRequest.of(0, request.getMemberLimit()));

            final Membership membershipOfCurrentUser = membershipRepository
                    .findByChatUserIdAndChatChannelId(
                            currentChatUser.getId(),
                            chatChannel.getId()
                    ).orElseThrow();

            final int unreadCount;
            if (membershipOfCurrentUser.getLastReadMessage() != null
            ) {
                unreadCount = messageRepository.countUnreadMessages(
                        membershipOfCurrentUser.getLastReadMessage().getId(),
                        chatChannel.getId(),
                        currentChatUser.getId()
                );
            } else {
                unreadCount = messageRepository.countChatMessagesByChatChannelAndSentByNot(
                        chatChannel,
                        currentChatUser
                );
            }


            var channelResponse = ChatChannelResponse.builder()
                    .channel(chatChannel)
                    .messages(messagesWithAttachments)
                    .members(memberships)
                    .membership(membershipOfCurrentUser)
                    .unreadCount(unreadCount)
                    .build();

            responses.add(channelResponse);
        }

        return responses;
    }

    public ChatChannelResponse queryChannel(String channelId, QueryChannelRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentChatUser = UserAccessHandler.getCurrentChatUser(authentication, userRepository).orElseThrow();
        final ChatChannel channel = channelRepository
                .findChatChannelByIdAndMembership(
                        channelId,
                        currentChatUser.getId()
                ).orElseThrow();

        final List<ChatMessage> messages;
        if (request.getBaseMessageId().isEmpty() || request.getBaseMessageId().isBlank()) {
            messages = messageRepository.findAllMessageByChannelId(
                    channel.getId(),
                    request.getMessageLimit(),
                    currentChatUser.getId()
            );
        } else {
            messages = switch (request.getDirection()) {

                case NEWER_THAN -> messageRepository.findChatMessageNewerThan(
                        channelId,
                        request.getMessageLimit(),
                        request.getBaseMessageId(),
                        currentChatUser.getId()
                );
                case NEWER_THAN_OR_EQUAL -> messageRepository.findChatMessageEqualOrNewerThan(
                        channelId,
                        request.getMessageLimit(),
                        request.getBaseMessageId(),
                        currentChatUser.getId()
                );
                case OLDER_THAN -> messageRepository.findChatMessageOlderThan(channelId,
                        request.getMessageLimit(),
                        request.getBaseMessageId(),
                        currentChatUser.getId());
                case OLDER_THAN_OR_EQUAL -> messageRepository.findChatMessageEqualOrOlderThan(channelId,
                        request.getMessageLimit(),
                        request.getBaseMessageId(),
                        currentChatUser.getId());
            };
        }

        List<ChatMessageDto> messagesWithAttachments = messages.stream()
                .map(retrieveMessageHelper::enrichWithAttachments).toList();

        return ChatChannelResponse.builder()
                .unreadCount(0)
                .channel(channel)
                .messages(messagesWithAttachments)
                .members(membershipRepository.findAllByChatChannelId(channelId,
                                PageRequest.of(
                                        0,
                                        request.getMemberLimit(),
                                        Sort.by("createdAt")
                                )
                        )
                )
                .membership(membershipRepository.findByChatUserIdAndChatChannelId(
                        currentChatUser.getId(),
                        channelId
                ).orElse(null))
                .build();
    }

    public ChatChannelResponse deleteChannel(String channelId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentChatUser = UserAccessHandler.getCurrentChatUser(authentication, userRepository).orElseThrow();
        final Membership membership = membershipRepository.findByChatUserIdAndChatChannelId(
                currentChatUser.getId(),
                channelId
        ).orElse(null);

        if (membership != null && membership.getChannelRole() == ChannelRole.CHANNEL_ADMIN) {
            final ChatChannel channel = channelRepository
                    .findChatChannelByIdAndMembership(
                            channelId,
                            currentChatUser.getId()
                    ).orElseThrow();
            channelRepository.delete(channel);
            return ChatChannelResponse.builder().channel(channel).build();
        }

        return null;
    }

    public ChatMessageResponse sendMessage(String channelId, SendChatMessageRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentChatUser = UserAccessHandler.getCurrentChatUser(authentication, userRepository).orElseThrow();

        final Membership membership = membershipRepository.findByChatUserIdAndChatChannelId(
                currentChatUser.getId(),
                channelId
        ).orElse(null);

        if (membership == null) {
            return null;
        }

        final ChatChannel chatChannel = channelRepository
                .findChatChannelByIdAndMembership(
                        channelId,
                        currentChatUser.getId()
                ).orElseThrow();

        ChatMessage replyTo = null;

        String replyToId = request.getMessage().getReplyTo();
        if (replyToId != null) {
            replyTo = messageRepository.findById(replyToId).orElse(null);
        }

        var chatMessage = ChatMessage.ChatMessageBuilder.builder()
                .id(request.getMessage().getId())
                .sentBy(currentChatUser)
                .chatChannel(chatChannel)
                .text(request.getMessage().getText())
                .createdAt(new Date())
                .type(MessageType.CHAT)
                .replyTo(replyTo)
                .build();


        final ChatMessage savedChatMessage = messageRepository.save(chatMessage);

        request.getMessage().getAttachments().forEach(attachment -> {
            String uploadId = attachment.getUploadId();
            ChatAttachment chatAttachment = attachmentRepository.findById(uploadId).orElse(null);

            if (chatAttachment == null) {
                chatAttachment = ChatAttachment.builder()
                        .id(uploadId)
                        .url(attachment.getUrl())
                        .mimeType(attachment.getMimeType())
                        .imageUrl(attachment.getImageUrl())
                        .thumbnailUrl(attachment.getThumbnailUrl())
                        .videoLength(attachment.getVideoLength())
                        .type(attachment.getType())
                        .fileSize(attachment.getFileSize())
                        .extraData(attachment.getExtraData())
                        .messageId(savedChatMessage.getId())
                        .build();
            } else {
                chatAttachment.setMessageId(savedChatMessage.getId());
                chatAttachment.setExtraData(attachment.getExtraData());
                chatAttachment.setType(attachment.getType());
                chatAttachment.setType(attachment.getType());
            }

            attachmentRepository.save(chatAttachment);
        });


        chatChannel.getMemberships().forEach(member -> {
            final int unreadCount;
            if (member.getLastReadMessage() != null) {
                unreadCount = messageRepository.countUnreadMessages(
                        member.getLastReadMessage().getId(),
                        channelId,
                        member.getChatUser().getId()
                );
            } else {
                unreadCount = messageRepository.countChatMessagesByChatChannelAndSentByNot(
                        chatChannel,
                        currentChatUser
                );
            }

            var event = NewMessageEvent.builder()
                    .unreadCount(unreadCount)
                    .createdAt(new Date())
                    .message(retrieveMessageHelper.enrichWithAttachments(savedChatMessage))
                    .cid(channelId)
                    .user(ChatUserMapper.toDto(currentChatUser))
                    .build();

            simpMessagingTemplate.convertAndSend("/topic/" + member.getChatUser().getId(), event);
        });


        return ChatMessageResponse.builder()
                .chatMessage(retrieveMessageHelper.enrichWithAttachments(savedChatMessage))
                .build();
    }

    public BaseResponse<String> markRead(String channelId, MarkReadRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentChatUser = UserAccessHandler.getCurrentChatUser(authentication, userRepository).orElseThrow();

        final Membership membership = membershipRepository.findByChatUserIdAndChatChannelId(
                currentChatUser.getId(),
                channelId
        ).orElse(null);

        if (membership == null) {
            return BaseResponse.<String>builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message("Membership not found")
                    .data("Membership not found")
                    .build();
        }
        // if mark read the message that before member last read then not update the read
        if (membership.getLastReadAt().after(new Date())) {
            return BaseResponse.<String>builder()
                    .status(HttpStatus.CONFLICT)
                    .message("New read must after member's last read")
                    .data(null)
                    .build();
        }
        final ChatMessage chatMessage = messageRepository.findById(request.getMessageId()).orElse(null);
        if (chatMessage == null) {
            return BaseResponse.<String>builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message("Message not found")
                    .data(null)
                    .build();
        }
        final Date current = new Date();
        var event = MessageReadEvent.builder()
                .createdAt(current)
                .cid(channelId)
                .message(ChatMessageMapper.toDto(chatMessage, List.of()))
                .user(ChatUserMapper.toDto(currentChatUser))
                .build();

        chatMessage.getChatChannel().getMemberships().forEach(member -> {
            simpMessagingTemplate.convertAndSend("/topic/" + member.getChatUser().getId(), event);
        });

        membership.setLastReadMessage(chatMessage, current);
        membershipRepository.save(membership);

        return BaseResponse.<String>builder()
                .status(HttpStatus.OK)
                .message("Successful")
                .data(chatMessage.getId())
                .build();
    }

    public FileUploadResponse sendImage(String uploadId, MultipartFile file) {
        final Date createdAt = new Date();
        var fileUploadResponseBuilder = FileUploadResponse.builder();
        try {
            FileUploadService.UploadInfo uploadInfo = fileUploadService.save(file);
            fileUploadResponseBuilder
                    .file(uploadInfo.url)
                    .created(createdAt);
            ChatAttachment attachment = attachmentRepository.findById(uploadId).orElse(null);
            if (attachment != null) {
                attachment.setUrl(uploadInfo.url);
                attachment.setImageUrl(uploadInfo.url);
            } else {
                attachment = ChatAttachment.builder()
                        .id(uploadId)
                        .imageUrl(uploadInfo.url)
                        .url(uploadInfo.url)
                        .type("image")
                        .fileSize(uploadInfo.fileSize.intValue())
                        .mimeType(file.getContentType())
                        .name(uploadInfo.fileName)
                        .createdAt(createdAt)
                        .build();
            }
            attachmentRepository.save(attachment);
        } catch (IOException e) {
            return fileUploadResponseBuilder.build();
        }
        return fileUploadResponseBuilder.build();
    }

    public BaseResponse<String> deleteConversationHistory(String channelId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentChatUser = UserAccessHandler.getCurrentChatUser(authentication, userRepository).orElseThrow();
        final Membership membership = membershipRepository.findByChatUserIdAndChatChannelId(
                currentChatUser.getId(),
                channelId
        ).orElse(null);

        if (membership == null) {
            return BaseResponse.<String>builder()
                    .status(HttpStatus.CONFLICT)
                    .message("You don't have permission to view this channel")
                    .data(channelId)
                    .build();
        }

        membership.setDisplayMessagesAfter(new Date());
        membershipRepository.save(membership);
        return BaseResponse.<String>builder()
                .status(HttpStatus.OK)
                .message("Delete conversation history successfully")
                .data(channelId)
                .build();
    }
}




