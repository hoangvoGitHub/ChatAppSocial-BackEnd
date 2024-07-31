package com.hoangvo.chatappsocial.controller.search;

import com.hoangvo.chatappsocial.model.dto.SearchResultDto;
import com.hoangvo.chatappsocial.model.request.resource.QueryResourceRequest;
import com.hoangvo.chatappsocial.service.SearchService;
import com.hoangvo.chatappsocial.utils.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/chat/search")
public class SearchController {
    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<BaseResponse<SearchResultDto>> search(
            @RequestParam("query") String query,
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit
    ) {
        BaseResponse<SearchResultDto> response = searchService.search(query, offset, limit);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping
    public ResponseEntity<BaseResponse<SearchResultDto>> search(
            @RequestBody QueryResourceRequest request
    ){
        BaseResponse<SearchResultDto> response = searchService.search(request);
        return ResponseEntity.status(response.getStatus()).body(response);

    }
}
