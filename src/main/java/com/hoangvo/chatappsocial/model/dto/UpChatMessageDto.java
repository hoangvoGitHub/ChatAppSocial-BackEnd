package com.hoangvo.chatappsocial.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpChatMessageDto {
    private String id;
    private String cid;
    private String text;
    private String replyTo;
    @Nullable
    private String receiptId;
    private List<ChatAttachmentDto> attachments;
    @Override
    public String toString() {
        return "UpChatMessageDto{" +
               "id='" + id + '\'' +
               ", cid='" + cid + '\'' +
               ", text='" + text + '\'' +
               ", replyTo='" + replyTo + '\'' +
               ", receiptId='" + receiptId + '\'' +
               ", attachments=" + attachments +
               '}';
    }
}
