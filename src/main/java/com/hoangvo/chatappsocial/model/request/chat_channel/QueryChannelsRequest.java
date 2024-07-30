package com.hoangvo.chatappsocial.model.request.chat_channel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter

public class QueryChannelsRequest {
    private Map<String,String> filterCondition;
    private List<Map<String, String>> sort;
    private int messageLimit;
    private int memberLimit;
    private Boolean watch;
    private int offset;
    private int limit;

    public QueryChannelsRequest() {
        this.messageLimit = 20;
        this.memberLimit = 20;
        this.offset = 0;
        this.limit = 10;
    }
}
