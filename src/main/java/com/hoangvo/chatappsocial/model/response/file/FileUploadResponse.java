package com.hoangvo.chatappsocial.model.response.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class FileUploadResponse {
    private String file;

    private Date created;

    @Nullable
    private String thumbnailUrl;
}
