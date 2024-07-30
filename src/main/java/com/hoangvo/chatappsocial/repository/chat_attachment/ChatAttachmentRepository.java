package com.hoangvo.chatappsocial.repository.chat_attachment;

import com.hoangvo.chatappsocial.model.attachment.ChatAttachment;
import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ChatAttachmentRepository extends PagingAndSortingRepository<ChatAttachment, String>,
        JpaRepository<ChatAttachment, String> {

    List<ChatAttachment> findAllByMessageId(String messageId);

}
