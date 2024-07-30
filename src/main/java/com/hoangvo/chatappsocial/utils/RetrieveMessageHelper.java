package com.hoangvo.chatappsocial.utils;

import com.hoangvo.chatappsocial.model.attachment.ChatAttachment;
import com.hoangvo.chatappsocial.model.chat_message.ChatMessage;
import com.hoangvo.chatappsocial.model.dto.ChatMessageDto;
import com.hoangvo.chatappsocial.repository.chat_attachment.ChatAttachmentRepository;
import com.hoangvo.chatappsocial.repository.chat_message.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class RetrieveMessageHelper {

    private final ChatAttachmentRepository chatAttachmentRepository;

    public ChatMessageDto enrichWithAttachments(ChatMessage chatMessage) {
        List<ChatAttachment> attachments = chatAttachmentRepository.findAllByMessageId(chatMessage.getId());
        return ChatMessageMapper.toDto(chatMessage, attachments);
    }

}
