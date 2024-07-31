package com.hoangvo.chatappsocial.controller.channel;

import com.hoangvo.chatappsocial.model.request.MarkReadRequest;
import com.hoangvo.chatappsocial.model.request.chat_channel.CreateChatChannelRequest;
import com.hoangvo.chatappsocial.model.request.chat_channel.QueryChannelRequest;
import com.hoangvo.chatappsocial.model.request.chat_channel.QueryChannelsRequest;
import com.hoangvo.chatappsocial.model.request.chat_message.SendChatMessageRequest;
import com.hoangvo.chatappsocial.model.response.chat_channel.ChatChannelResponse;
import com.hoangvo.chatappsocial.model.response.chat_message.ChatMessageResponse;
import com.hoangvo.chatappsocial.model.response.file.FileUploadResponse;
import com.hoangvo.chatappsocial.service.ChatChannelService;
import com.hoangvo.chatappsocial.service.SendMessageService;
import com.hoangvo.chatappsocial.utils.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/chat/channels")
@RequiredArgsConstructor
public class ChatChannelController {
    private final ChatChannelService chatChannelService;
    private final SendMessageService sendMessageService;

    @PostMapping("/{channelId}/messages")
    ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable String channelId,
            @RequestBody SendChatMessageRequest request
    ) {
        final ChatMessageResponse response = sendMessageService.sendMessage(
                channelId, request
        );
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/{channelId}/image/{uploadId}")
    ResponseEntity<FileUploadResponse> sendImage(
            @PathVariable String channelId,
            @PathVariable String uploadId,
            @RequestParam(name = "file") MultipartFile file
    ) {
        final FileUploadResponse response = chatChannelService.sendImage(
                uploadId, file
        );
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/{channelId}/file")
    ResponseEntity<ChatMessageResponse> sendFile(
            @PathVariable String channelId,
            @RequestBody SendChatMessageRequest request
    ) {
        final ChatMessageResponse response = chatChannelService.sendMessage(
                channelId, request
        );
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/{channelId}/read")
    ResponseEntity<BaseResponse<String>> markRead(
            @PathVariable String channelId,
            @RequestBody MarkReadRequest request
    ) {
        final BaseResponse<String> response = chatChannelService.markRead(
                channelId, request
        );
        return ResponseEntity.ok().body(response);
    }


    @PostMapping(path = "/all")
    ResponseEntity<List<ChatChannelResponse>> queryChannels(@RequestBody QueryChannelsRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(chatChannelService.queryChannels(request));
    }

    @PostMapping(path = "/{channelId}")
    ResponseEntity<ChatChannelResponse> queryChannel(
            @PathVariable String channelId,
            @RequestBody QueryChannelRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(chatChannelService.queryChannel(channelId, request));
    }

    @DeleteMapping(path = "/{channelId}")
    ResponseEntity<ChatChannelResponse> deleteChannel(
            @PathVariable String channelId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(chatChannelService.deleteChannel(channelId));
    }

    @PostMapping(path = "/create")
    ResponseEntity<ChatChannelResponse> createChatChannel(
            @RequestBody CreateChatChannelRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(chatChannelService.createChatChannel(request));
    }

    @DeleteMapping(path = "/{channelId}/conversation")
    ResponseEntity<BaseResponse<String>> deleteConversationHistory(
            @PathVariable String channelId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(chatChannelService.deleteConversationHistory(channelId));
    }

}
