package com.hoangvo.chatappsocial.service;

import com.hoangvo.chatappsocial.model.attachment.ChatAttachment;
import com.hoangvo.chatappsocial.model.dto.ChatAttachmentDto;
import com.hoangvo.chatappsocial.repository.chat_attachment.ChatAttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AttachmentService {

    private final ChatAttachmentRepository attachmentRepository;


    public void handleAttachments(List<ChatAttachmentDto> attachments, String messageId) {
        final Date createdAt = new Date();
        attachments.forEach(attachment -> {
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
                        .originalHeight(attachment.getOriginalHeight())
                        .originalHeight(attachment.getOriginalWidth())
                        .messageId(messageId)
                        .createdAt(createdAt)
                        .build();
            } else {
                chatAttachment.setMessageId(messageId);
                chatAttachment.setExtraData(attachment.getExtraData());
                chatAttachment.setType(attachment.getType());
                chatAttachment.setType(attachment.getType());
            }

            attachmentRepository.save(chatAttachment);
        });
    }

}
