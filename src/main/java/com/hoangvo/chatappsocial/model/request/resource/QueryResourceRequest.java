package com.hoangvo.chatappsocial.model.request.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QueryResourceRequest {
    private String searchQuery;
    private Map<SearchResource, PageableRequest> resource;
}

