package com.hoangvo.chatappsocial.model.membership;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class MembershipKey implements Serializable {
    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "user_id")
    private String userId;
}
