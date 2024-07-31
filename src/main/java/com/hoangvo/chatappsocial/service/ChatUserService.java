package com.hoangvo.chatappsocial.service;


import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.model.dto.ChatUserDto;
import com.hoangvo.chatappsocial.repository.chat_user.ChatUserRepository;
import com.hoangvo.chatappsocial.service.upload.FileUploadService;
import com.hoangvo.chatappsocial.utils.ChatUserMapper;
import com.hoangvo.chatappsocial.utils.UserAccessHandler;
import com.hoangvo.chatappsocial.utils.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatUserService {

    private final ChatUserRepository repository;
    private final FileUploadService fileUploadService;

    public List<ChatUserDto> queryChatUsers(
            String query,
            int offset,
            int limit
    ) {
        return repository.queryChatUserByNameContainsIgnoreCase(query, PageRequest.of(offset, limit))
                .stream().map(ChatUserMapper::toDto).toList();
    }

    public ChatUserDto getChatUser(String id) {
        final ChatUser chatUser = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("customer with id " + id + " not found"));
        return ChatUserMapper.toDto(chatUser);
    }


    public ChatUserDto getChatUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentChatUser = UserAccessHandler.getCurrentChatUser(authentication, repository).orElseThrow();
        return ChatUserMapper.toDto(currentChatUser);
    }


    public ChatUserDto getChatUserByUsername(String username) {
        final ChatUser chatUser = repository.findByOriginalUserUsername(username)
                .orElseThrow(() -> new NotFoundException("customer with id " + username + " not found"));
        return ChatUserMapper.toDto(chatUser);
    }

    public ChatUser updateProfileImage(MultipartFile file) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final ChatUser currentChatUser = UserAccessHandler.getCurrentChatUser(authentication, repository).orElseThrow();

        String imageUrl = fileUploadService.save(file).url;
        currentChatUser.setImageUrl(imageUrl);
        return repository.save(currentChatUser);
    }
}
