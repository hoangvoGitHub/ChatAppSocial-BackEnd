package com.hoangvo.chatappsocial.repository.membership;

import com.hoangvo.chatappsocial.model.membership.Membership;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends PagingAndSortingRepository<Membership, String>,
        JpaRepository<Membership, String> {

    Optional<Membership> findByChatUserIdAndChatChannelId(
            String uid, String cid
    );


    List<Membership> findAllByChatChannelId(String cid, Pageable pageable);
}
