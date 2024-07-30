package com.hoangvo.chatappsocial.utils.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class BaseResponse<T> {
    private HttpStatus status;

    private String message;

    private T data;
}
