package com.hoangvo.chatappsocial.repository.chat_message;

import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import com.hoangvo.chatappsocial.model.chat_message.ChatMessage;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends PagingAndSortingRepository<ChatMessage, String>,
        JpaRepository<ChatMessage, String> {
    List<ChatMessage> findAllByChatChannel(ChatChannel channel, Pageable pageable);

    @Query(
            value = "SELECT _chat_message.* FROM _chat_message " +
                    "WHERE _chat_message.channel_id LIKE :channelId AND " +
                    "_chat_message.created_at > " +
                    "(SELECT _membership.display_messages_after FROM _membership WHERE _membership.user_id LIKE :userId " +
                    "AND _membership.channel_id LIKE :channelId) " +
                    "ORDER BY _chat_message.created_at DESC LIMIT :messageLimit",
            nativeQuery = true
    )
    List<ChatMessage> findAllMessageByChannelId(
            String channelId,
            int messageLimit,
            String userId
    );

    @Query(
            value = "SELECT _chat_message.* FROM _chat_message " +
                    "WHERE _chat_message.channel_id LIKE :channelId AND " +
                    "_chat_message.created_at < " +
                    "(SELECT _chat_message.created_at FROM _chat_message WHERE _chat_message.id LIKE :baseMessageId)" +
                    "AND _chat_message.created_at > " +
                    "(SELECT _membership.display_messages_after FROM _membership WHERE _membership.user_id LIKE :userId " +
                    "AND _membership.channel_id LIKE :channelId) " +
                    "ORDER BY _chat_message.created_at DESC LIMIT :messageLimit",
            nativeQuery = true
    )
    List<ChatMessage> findChatMessageOlderThan(@Nonnull String channelId,
                                               int messageLimit,
                                               @Nonnull String baseMessageId,
                                               String userId
    );

    @Query(
            value = "SELECT _chat_message.* FROM _chat_message " +
                    "WHERE _chat_message.channel_id LIKE :channelId AND " +
                    "_chat_message.created_at <= " +
                    "(SELECT _chat_message.created_at FROM _chat_message WHERE _chat_message.id LIKE :baseMessageId)" +
                    "AND _chat_message.created_at > " +
                    "(SELECT _membership.display_messages_after FROM _membership WHERE _membership.user_id LIKE :userId " +
                    "AND _membership.channel_id LIKE :channelId) " +
                    "ORDER BY _chat_message.created_at DESC LIMIT :messageLimit",
            nativeQuery = true
    )
    List<ChatMessage> findChatMessageEqualOrOlderThan(@Nonnull String channelId,
                                                      int messageLimit,
                                                      @Nonnull String baseMessageId,
                                                      String userId
    );

    @Query(
            value = "SELECT _chat_message.* FROM _chat_message " +
                    "WHERE _chat_message.channel_id LIKE :channelId AND " +
                    "_chat_message.created_at > " +
                    "(SELECT _chat_message.created_at FROM _chat_message WHERE _chat_message.id LIKE :baseMessageId) " +
                    "AND _chat_message.created_at > " +
                    "(SELECT _membership.display_messages_after FROM _membership WHERE _membership.user_id LIKE :userId " +
                    "AND _membership.channel_id LIKE :channelId) " +
                    "ORDER BY _chat_message.created_at DESC LIMIT :messageLimit",
            nativeQuery = true
    )
    List<ChatMessage> findChatMessageNewerThan(@Nonnull String channelId,
                                               int messageLimit,
                                               @Nonnull String baseMessageId,
                                               String userId
    );

    @Query(
            value = "SELECT _chat_message.* FROM _chat_message " +
                    "WHERE _chat_message.channel_id LIKE :channelId AND " +
                    "_chat_message.created_at >= " +
                    "(SELECT _chat_message.created_at FROM _chat_message WHERE _chat_message.id LIKE :baseMessageId)" +
                    "AND _chat_message.created_at > " +
                    "(SELECT _membership.display_messages_after FROM _membership WHERE _membership.user_id LIKE :userId " +
                    "AND _membership.channel_id LIKE :channelId) " +
                    "ORDER BY _chat_message.created_at DESC LIMIT :messageLimit",
            nativeQuery = true
    )
    List<ChatMessage> findChatMessageEqualOrNewerThan(@Nonnull String channelId,
                                                      int messageLimit,
                                                      @Nonnull String baseMessageId,
                                                      String userId
    );


//
//    List<ChatMessage> findChatMessageOlderThan()

    @Nonnull
    Optional<ChatMessage> findById(@Nonnull String messageId);

    Optional<ChatMessage> findByIdAndChatChannelId(String messageId, String channelId);

    @Query(
            value = "SELECT count(*) FROM _chat_message " +
                    "WHERE _chat_message.channel_id LIKE :cid AND " +
                    "_chat_message.sent_by NOT LIKE :uid AND " +
                    "_chat_message.created_at > " +
                    "( SELECT _chat_message.created_at " +
                    " FROM _chat_message" +
                    " WHERE _chat_message.id LIKE :id" +
                    ")",
            nativeQuery = true
    )
    int countUnreadMessages(@Param("id") String lastReadMessageId,
                            @Param("cid") String channelId,
                            @Param("uid") String currentUserId
    );

    int countChatMessagesByChatChannelAndSentByNot(ChatChannel channel, ChatUser user);
}
