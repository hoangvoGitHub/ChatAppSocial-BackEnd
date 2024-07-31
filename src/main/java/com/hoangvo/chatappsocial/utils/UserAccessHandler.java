package com.hoangvo.chatappsocial.utils;

import com.hoangvo.chatappsocial.auth.data.model.User;
import com.hoangvo.chatappsocial.repository.chat_user.ChatUserRepository;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserAccessHandler {
//    private final;

    public static Optional<ChatUser> getCurrentChatUser(Authentication currentAuth,
                                                        ChatUserRepository chatUserRepository) {
        if (currentAuth != null && currentAuth.isAuthenticated()) {
            final Object principal = currentAuth.getPrincipal();
            final User currentUser = (User) principal;
            return Optional.of(chatUserRepository.findChatUserByOriginalUser(currentUser));
        } else {
            return Optional.empty();
        }
    }
}