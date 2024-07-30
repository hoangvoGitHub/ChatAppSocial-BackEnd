package com.hoangvo.chatappsocial.model.event;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class ChatEvent {
    protected ChatEventType type;
    protected Date createdAt;
}

