package com.hoangvo.chatappsocial.repository.user_friend;

import com.hoangvo.chatappsocial.model.response.chat_friend.FriendSubjectiveStatus;
import com.hoangvo.chatappsocial.model.user_friend.ChatFriend;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatFriendRepository extends PagingAndSortingRepository<ChatFriend, String>,
        JpaRepository<ChatFriend, String> {

    @Query(value = "SELECT _chat_friend.* FROM _chat_friend " +
            "WHERE (_chat_friend.first_user_id LIKE :first_user_id OR  _chat_friend.first_user_id LIKE :second_user_id) AND " +
            "(_chat_friend.second_user_id LIKE :first_user_id OR  _chat_friend.second_user_id LIKE :second_user_id)",
            nativeQuery = true
    )
    Optional<ChatFriend> findChatFriendByChatUsers(
            @Param("first_user_id") String firstUserId,
            @Param("second_user_id") String secondUserId
    );

    @Query(value = "SELECT DISTINCT _chat_friend.* FROM _chat_friend JOIN _chat_user ON " +
            "_chat_friend.first_user_id LIKE _chat_user.id OR _chat_friend.second_user_id LIKE _chat_user.id " +
            "JOIN _user ON _chat_user.original_user_id LIKE _user.id " +
            "WHERE (lower(_user.first_name) LIKE lower(concat('%', :name,'%')) OR " +
            "lower(_user.last_name) LIKE lower(concat('%', :name,'%')) )" +
            "AND ( (_chat_friend.first_user_id LIKE :currentUserId AND _chat_friend.`status` LIKE 'FRIEND' ) OR " +
            "( _chat_friend.second_user_id LIKE :currentUserId AND _chat_friend.`status` LIKE 'FRIEND') )",
            nativeQuery = true
    )
    List<ChatFriend> queryFriendList(
            @Param("name") String name,
            String currentUserId,
            Pageable pageable
    );

    @Query(value = "SELECT DISTINCT _chat_friend.* FROM _chat_friend JOIN _chat_user ON " +
            "_chat_friend.first_user_id LIKE _chat_user.id OR _chat_friend.second_user_id LIKE _chat_user.id " +
            "JOIN _user ON _chat_user.original_user_id LIKE _user.id " +
            "WHERE (lower(_user.first_name) LIKE lower(concat('%', :name,'%')) OR " +
            "lower(_user.last_name) LIKE lower(concat('%', :name,'%')) )" +
            "AND _chat_user.id NOT LIKE :currentUserId " +
            "AND ( (_chat_friend.first_user_id LIKE :currentUserId AND _chat_friend.`status` LIKE 'REQUEST_FROM_UID2') " +
            "OR (_chat_friend.second_user_id LIKE :currentUserId AND _chat_friend.`status` LIKE 'REQUEST_FROM_UID1'))",
            nativeQuery = true
    )
    List<ChatFriend> queryFriendRequests(
            @Param("name") String name,
            @Param("currentUserId") String currentUserId,
            Pageable pageable
    );

//    List<Tuple> queryFriend()

}
