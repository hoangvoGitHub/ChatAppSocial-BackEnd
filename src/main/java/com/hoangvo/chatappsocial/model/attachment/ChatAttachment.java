package com.hoangvo.chatappsocial.model.attachment;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.hoangvo.chatappsocial.utils.converter.HashMapConverter;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Table(name = "_chat_attachment")
public class ChatAttachment {
    @Id
    private String id;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(columnDefinition = "TEXT")
    private String name;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "video_length")
    private Integer videoLength;

    @Nullable
    private Integer originalHeight;

    @Nullable
    private Integer originalWidth;

    private String type;

    private int fileSize;

    @Nonnull
    @Builder.Default
    private Date createdAt = new Date();

    //    @Transient
    @Convert(converter = HashMapConverter.class)
    @Column(name = "extra_data", columnDefinition = "TEXT",
            nullable = false)
    private Map<String, String> extraData;
}
