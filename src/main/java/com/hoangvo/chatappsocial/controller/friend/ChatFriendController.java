package com.hoangvo.chatappsocial.controller.friend;

import com.hoangvo.chatappsocial.model.dto.ChatFriendDto;
import com.hoangvo.chatappsocial.model.dto.SearchUserDto;
import com.hoangvo.chatappsocial.model.request.chat_friend.ChatFriendRequest;
import com.hoangvo.chatappsocial.model.response.chat_friend.ChatFriendResponse;
import com.hoangvo.chatappsocial.model.response.chat_friend.FriendSubjectiveStatus;
import com.hoangvo.chatappsocial.service.ChatFriendService;
import com.hoangvo.chatappsocial.utils.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/chat/friend")
public class ChatFriendController {

    private final ChatFriendService service;

    @PostMapping("/add")
    public ResponseEntity<BaseResponse<ChatFriendResponse>> addFriend(
            @RequestBody ChatFriendRequest request
    ) {
        BaseResponse<ChatFriendResponse> response = service.addFriend(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/accept")
    public ResponseEntity<BaseResponse<ChatFriendResponse>> acceptFriend(
            @RequestBody ChatFriendRequest request
    ) {
        BaseResponse<ChatFriendResponse> response = service.acceptFriend(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/reject")
    public ResponseEntity<BaseResponse<ChatFriendResponse>> rejectFriend(
            @RequestBody ChatFriendRequest request
    ) {
        BaseResponse<ChatFriendResponse> response = service.rejectFriend(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/remove")
    public ResponseEntity<BaseResponse<ChatFriendResponse>> removeFriend(
            @RequestBody ChatFriendRequest request
    ) {
        BaseResponse<ChatFriendResponse> response = service.removeFriend(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }



    @GetMapping()
    public ResponseEntity<BaseResponse<List<ChatFriendResponse>>> queryFriend(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "status") FriendSubjectiveStatus status,
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "sortBy", required = false) Optional<String> sortBy
    ) {
        BaseResponse<List<ChatFriendResponse>> response = service.queryFriends(name, status, limit, offset, sortBy);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

}
