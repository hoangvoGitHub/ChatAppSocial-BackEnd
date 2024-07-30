package com.hoangvo.chatappsocial.utils;

import com.hoangvo.chatappsocial.model.attachment.ChatAttachment;
import com.hoangvo.chatappsocial.model.dto.ChatAttachmentDto;

public class ChatAttachmentMapper {
    public static ChatAttachmentDto toDto(ChatAttachment chatAttachment) {
        final ChatAttachmentDto chatAttachmentDto = new ChatAttachmentDto();

        chatAttachmentDto.setName(chatAttachment.getName());
        chatAttachmentDto.setUrl(chatAttachment.getUrl());
        chatAttachmentDto.setImageUrl(chatAttachment.getImageUrl());
        chatAttachmentDto.setMimeType(chatAttachment.getMimeType());
        chatAttachmentDto.setThumbnailUrl(chatAttachment.getThumbnailUrl());
        chatAttachmentDto.setVideoLength(chatAttachment.getVideoLength());
        chatAttachmentDto.setOriginalHeight(chatAttachment.getOriginalHeight());
        chatAttachmentDto.setOriginalWidth(chatAttachment.getOriginalWidth());
        chatAttachmentDto.setType(chatAttachment.getType());
        chatAttachmentDto.setFileSize(chatAttachment.getFileSize());
        chatAttachmentDto.setExtraData(chatAttachment.getExtraData());
        chatAttachmentDto.setCreatedAt(chatAttachment.getCreatedAt());


        return chatAttachmentDto;

    }
}
