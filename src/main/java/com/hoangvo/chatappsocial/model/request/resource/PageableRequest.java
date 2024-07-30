package com.hoangvo.chatappsocial.model.request.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageableRequest {
    private int limit;
    private int offset;
}
