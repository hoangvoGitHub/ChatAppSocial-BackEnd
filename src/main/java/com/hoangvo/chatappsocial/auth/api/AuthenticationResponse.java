package com.hoangvo.chatappsocial.auth.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private int status;

    private String message;

    private String id;

    private String username;

    private String token;
}
