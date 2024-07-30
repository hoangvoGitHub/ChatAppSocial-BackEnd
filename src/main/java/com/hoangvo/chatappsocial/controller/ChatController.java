package com.hoangvo.chatappsocial.controller;

import com.hoangvo.chatappsocial.service.ChatChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatChannelService chatChannelService;

//    @PostMapping("/api/v1/chat/{channelId}/send-message")
//    public ResponseEntity<ChatChannelResponse> sendMessage(
//            @PathVariable String channelId,
//            @RequestBody SendChatMessageRequest request
//    ){
//        final ChatChannel channel = chatChannelService.findChatChannelById(channelId).orElse(null);
//        if (channel == null){
//            channel = chatChannelService.createChatChannel()
//        }
//
//    }
}
