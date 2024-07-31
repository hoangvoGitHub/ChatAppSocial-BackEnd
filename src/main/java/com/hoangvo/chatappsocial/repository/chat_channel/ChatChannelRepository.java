package com.hoangvo.chatappsocial.repository.chat_channel;

import com.hoangvo.chatappsocial.model.chat_channel.ChannelType;
import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import com.hoangvo.chatappsocial.model.membership.Membership;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatChannelRepository extends PagingAndSortingRepository<ChatChannel, String>,
        JpaRepository<ChatChannel, String> {

    @Query(value = "SELECT DISTINCT _chat_channel.* FROM _chat_channel " +
            "JOIN _membership ON _membership.channel_id LIKE _chat_channel.id " +
            "WHERE _membership.user_id IN :uids AND _chat_channel.type LIKE  concat('', :type,'')" +
            "GROUP BY _chat_channel.id " +
            "HAVING (_chat_channel.type LIKE 'MUTUAL' AND COUNT(*) LIKE 2) OR _chat_channel.`type` NOT LIKE 'MUTUAL'",
            nativeQuery = true
    )
    Optional<ChatChannel> findChannelsByMemberships(@Param("uids") List<String> uids, @Param("type") String type);

    @Query(value = "SELECT DISTINCT _chat_user.id FROM _membership LEFT JOIN _chat_user " +
                   "ON _membership.user_id LIKE  _chat_user.id WHERE _membership.channel_id LIKE :channelId",
            nativeQuery = true
    )
    List<String> findAllChatMemberIdsByChannelId(String channelId);

    @Query(value = "SELECT DISTINCT _chat_channel.*, temp.created_at_max FROM _chat_channel " +
            "JOIN _membership ON _membership.channel_id LIKE _chat_channel.id " +
            "LEFT JOIN (" +
            "SELECT _chat_message.channel_id AS cid, MAX(created_at) AS created_at_max FROM _chat_message " +
            "GROUP BY _chat_message.channel_id" +
            ") AS temp " +
            "ON _chat_channel.id LIKE temp.cid " +
            "WHERE _membership.user_id LIKE :uid " +
            "ORDER BY temp.created_at_max DESC;",
            nativeQuery = true
    )
        // FIXME: 6/25/2024 query returns wrong data
    List<ChatChannel> findChatChannelsByMembershipsOrderByLatestMessage(String uid, Pageable pageable);

    @Query(value = "SELECT _chat_channel.* FROM _chat_channel " +
            "JOIN _membership ON _membership.channel_id = _chat_channel.id " +
            "WHERE _chat_channel.id =?1 AND _membership.user_id =?2",
            nativeQuery = true
    )
    Optional<ChatChannel> findChatChannelByIdAndMembership(String cid, String uid);
}
