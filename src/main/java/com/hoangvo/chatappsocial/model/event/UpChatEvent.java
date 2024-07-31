package com.hoangvo.chatappsocial.model.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UpMessageReadEvent.class, name = "Read"),
        @JsonSubTypes.Type(value = UpNewMessageEvent.class, name = "NewMessage"),
        @JsonSubTypes.Type(value = UpTypingStartEvent.class, name = "TypingStart"),
        @JsonSubTypes.Type(value = UpTypingStopEvent.class, name = "TypingStop")
})
public abstract class UpChatEvent {
    private ChatEventType type;
    private Date createdAt;
}

