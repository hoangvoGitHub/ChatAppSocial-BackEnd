package com.hoangvo.chatappsocial.repository.chat_user;

import com.hoangvo.chatappsocial.auth.data.model.User;
import com.hoangvo.chatappsocial.model.aggregation.ChatUserWithFriendStatus;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatUserRepository extends PagingAndSortingRepository<ChatUser, String>,
        JpaRepository<ChatUser, String> {
    ChatUser findChatUserByOriginalUser(User user);
    ChatUser findChatUserByOriginalUserUsername(String email);

    /*user: aca86dfc-2916-4727-950d-476e1d13f4df - Hoang 0
     * 87c7114b-000f-4aff-a1f4-caf6a6dd6fde - with Hoang 1
     * f011d956-4df0-444c-8cee-d90d972135eb - with Hoang 4
     * f0ceddc3-a35e-4fcf-80f1-aa8985caee12  - with Hoang 3
     * */
    @Query(value = "SELECT _chat_user.* FROM _chat_user " +
            "JOIN _user ON _chat_user.original_user_id = _user.id " +
            "WHERE lower(_user.first_name) like lower(concat('%', :nameToFind,'%'))" +
            "OR lower(_user.last_name) like lower(concat('%', :nameToFind,'%'))",
            nativeQuery = true
    )
    List<ChatUser> queryChatUserByNameContainsIgnoreCase(@Param("nameToFind") String name, Pageable pageable);

    @Query(value = """
            SELECT 
            DISTINCT 
                cu.id,
                cu.image_url AS imageUrl,
                cu.is_online AS isOnline,
                cu.is_invisible AS isInvisible,
                cu.last_active_at AS lastActiveAt,
                cu.created_at AS createdAt,
                cu.updated_at AS updatedAt,
                cu.deleted_at AS deletedAt,
                CONCAT(u.first_name, ' ', u.last_name) AS name,
                CASE 
                    WHEN cf.status = 'FRIEND' THEN 'FRIEND'
                    WHEN cf.status = 'REQUEST_FROM_UID1' AND cf.first_user_id = :currentUserId THEN 'REQUEST_FROM_ME'
                    WHEN cf.status = 'REQUEST_FROM_UID1' AND cf.first_user_id != :currentUserId THEN 'REQUEST_FROM_OTHER'
                    WHEN cf.status = 'REQUEST_FROM_UID2' AND cf.first_user_id = :currentUserId THEN 'REQUEST_FROM_OTHER'
                    WHEN cf.status = 'REQUEST_FROM_UID2' AND cf.first_user_id != :currentUserId THEN 'REQUEST_FROM_ME'
                    ELSE 'NONE'
                END AS friendStatus
            FROM 
                _chat_user cu 
            JOIN 
                _user u ON cu.original_user_id = u.id 
            LEFT JOIN 
                _chat_friend cf 
                ON (cf.first_user_id = cu.id AND cf.second_user_id = :currentUserId)
                OR (cf.second_user_id = cu.id AND cf.first_user_id = :currentUserId)
            WHERE lower(CONCAT(u.first_name, ' ', u.last_name)) like lower(concat('%', :nameToFind,'%'))
            """, nativeQuery = true)
    List<ChatUserWithFriendStatus> findChatUsersWithFriendStatus(
            @Param("nameToFind") String name,
            @Param("currentUserId") String currentUserId,
            Pageable pageable);


    Optional<ChatUser> findByOriginalUserUsername(String username);

}
