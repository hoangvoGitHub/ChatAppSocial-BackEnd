package com.hoangvo.chatappsocial.controller.user;

import com.hoangvo.chatappsocial.model.dto.ChatUserDto;
import com.hoangvo.chatappsocial.model.response.file.FileUploadResponse;
import com.hoangvo.chatappsocial.service.ChatUserService;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/chat/users")
@RequiredArgsConstructor
public class ChatUserController {

    private final ChatUserService chatUserService;

    @GetMapping(path = "/all")
    List<ChatUserDto> queryChatUsers(
            @RequestParam(value = "offset", required = false) int offset,
            @RequestParam(value = "limit", required = false) int limit
    ) {
        return chatUserService.queryChatUsers("", offset, limit);
    }

    @GetMapping(path = "/{uid}")
    ChatUserDto queryChatUser(@PathVariable String uid) {
        return chatUserService.getChatUser(uid);

    }

    @GetMapping(path = "/profile")
    ChatUserDto queryChatUser() {
        return chatUserService.getChatUser();
    }

    @PostMapping("/profile/image")
    public ResponseEntity<FileUploadResponse> updateProfileImage(
            @RequestParam(name = "file") MultipartFile file
    ) {
        try {
            final ChatUser chatUser = chatUserService.updateProfileImage(file);
            var fileUploadResponse = FileUploadResponse.builder().file(chatUser.getImageUrl()).build();
            return ResponseEntity.ok(fileUploadResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/username")
    ChatUserDto queryChatUserByUsername(
            @Param("username") String username
    ) {
        return chatUserService.getChatUserByUsername(username);
    }

    @GetMapping
    List<ChatUserDto> queryChatUsers(
            @RequestParam("query") String query,
            @RequestParam(value = "offset", required = false) int offset,
            @RequestParam(value = "limit", required = false) int limit
    ) {
        return chatUserService.queryChatUsers(query, offset, limit);
    }
}
