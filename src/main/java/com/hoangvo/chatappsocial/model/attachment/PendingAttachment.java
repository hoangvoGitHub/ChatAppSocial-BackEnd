package com.hoangvo.chatappsocial.model.attachment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * The PendingAttachment entity is used to temporarily store attachment information until the corresponding message is finalized.
 * This entity acts as a staging area for attachments before they are linked to a finalized message.
 */
@Getter
//@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_pending_attachment")
public class PendingAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String url;
    @Column(unique = true, nullable = false, name  = "upload_id")
    private String uploadId;
}
