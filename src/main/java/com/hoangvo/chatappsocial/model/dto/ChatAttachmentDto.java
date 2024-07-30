package com.hoangvo.chatappsocial.model.dto;

import lombok.*;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatAttachmentDto {
    private String name;
    private String url;
    private String mimeType;
    private String imageUrl;
    private String thumbnailUrl;
    private Integer videoLength;
    private Integer originalHeight;
    private Integer originalWidth;
    private String type;
    private int fileSize;
    private Date createdAt;
    private Map<String, String> extraData;

    public String getUploadId() {
        if (extraData.containsKey(EXTRA_DATA_UPLOAD_ID)) {
            return (extraData.get(EXTRA_DATA_UPLOAD_ID));
        }
        String generatedId = UUID.randomUUID().toString();
        extraData.put(EXTRA_DATA_UPLOAD_ID, generatedId);
        return generatedId;
    }

    private static final String EXTRA_DATA_UPLOAD_ID = "uploadId";
}

