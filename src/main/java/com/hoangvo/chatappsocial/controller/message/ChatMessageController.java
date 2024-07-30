package com.hoangvo.chatappsocial.controller.message;

import com.hoangvo.chatappsocial.model.dto.ChatMessageDto;
import com.hoangvo.chatappsocial.service.ChatMessageService;
import com.hoangvo.chatappsocial.model.response.chat_message.ChatMessageResponse;
import com.hoangvo.chatappsocial.utils.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping(path = "/api/v1/chat/messages")
public class ChatMessageController {

    private final ChatMessageService messageService;
    private static final Logger LOG = LoggerFactory.getLogger(ChatMessageController.class);

    // endpoint app/chat/{channelId}
    // send to queue/{channelId}



    @GetMapping("/{channelId}")
    public ResponseEntity<BaseResponse<List<ChatMessageDto>>> getChatMessages(@PathVariable String channelId) {
        final BaseResponse<List<ChatMessageDto>> response = messageService.getChatMessages(channelId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }



    @GetMapping(path = "/{messageId}")
    public ResponseEntity<ChatMessageResponse> getChatMessage(@PathVariable String messageId){
        final ChatMessageResponse response = messageService.getChatMessage(messageId);
        return ResponseEntity.ok().body(response);
    }



}
