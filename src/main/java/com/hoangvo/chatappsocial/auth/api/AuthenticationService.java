package com.hoangvo.chatappsocial.auth.api;


import com.google.api.gax.rpc.StatusCode;
import com.google.api.gax.rpc.UnauthenticatedException;
import com.hoangvo.chatappsocial.auth.config.JwtService;
import com.hoangvo.chatappsocial.auth.data.model.Role;
import com.hoangvo.chatappsocial.auth.data.model.User;
import com.hoangvo.chatappsocial.auth.data.UserRepository;
import com.hoangvo.chatappsocial.repository.chat_user.ChatUserRepository;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.utils.UserAccessHandler;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final ChatUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        String username = request.getUsername();
        if (repository.findByUsername(username).isPresent()) {
            return AuthenticationResponse.builder()
                    .status(HttpStatus.NOT_IMPLEMENTED.value())
                    .message("Choose other username!")
                    .username(null)
                    .token(null)
                    .build();
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .createdAt(new Date())
                .role(Role.USER)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        final ChatUser chatUser = createChatUserFromAuthUser(repository.save(user));
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .status(HttpStatus.OK.value())
                .id(chatUser.getId())
                .message("Your account is successfully registered!")
                .username(request.getUsername())
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    ));
            var user = repository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .username(request.getUsername())
                    .token(jwtToken)
                    .status(HttpStatus.OK.value())
                    .message("Successful authentication!")
                    .build();
        } catch (UsernameNotFoundException e) {
            return AuthenticationResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message(e.getMessage())
                    .build();
        }
    }

    @Nullable
    public ChatUser getCurrentChatUser() {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth != null && currentAuth.isAuthenticated()) {
            final Object principal = currentAuth.getPrincipal();
            final User currentUser = (User) principal;
            return userRepository.findChatUserByOriginalUser(currentUser);
        } else {
            return null;
        }
    }

    private ChatUser createChatUserFromAuthUser(User user) {
        var chatUser = ChatUser.builder()
                .originalUser(user)
                .lastActiveAt(new Date())
                .createdAt(new Date())
                .build();
        return userRepository.save(chatUser);
    }


}
