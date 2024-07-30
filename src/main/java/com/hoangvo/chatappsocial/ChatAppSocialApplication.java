package com.hoangvo.chatappsocial;

import com.hoangvo.chatappsocial.auth.data.UserRepository;
import com.hoangvo.chatappsocial.repository.chat_channel.ChatChannelRepository;
import com.hoangvo.chatappsocial.model.chat_channel.ChatChannel;
import com.hoangvo.chatappsocial.repository.chat_message.ChatMessageRepository;
import com.hoangvo.chatappsocial.repository.chat_user.ChatUserRepository;
import com.hoangvo.chatappsocial.model.chat_user.ChatUser;
import com.hoangvo.chatappsocial.repository.membership.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@SpringBootApplication
@RequiredArgsConstructor
public class ChatAppSocialApplication {

    private final ChatUserRepository chatUserRepository;
    private final UserRepository userRepository;
    private final ChatChannelRepository chatChannelRepository;
    private final PasswordEncoder passwordEncoder;
    private final ChatMessageRepository chatMessageRepository;
    private final MembershipRepository membershipRepository;
    private static final Logger LOG = LoggerFactory.getLogger(ChatAppSocialApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ChatAppSocialApplication.class, args);
    }

//    @Bean
//    CommandLineRunner populateDatabase() {
//        return args -> {
//            for (int i = 0; i < 10; i++) {
//                LOG.info("Loop number " + i);
//                var user = User.builder()
//                        .createdAt(new Date())
//                        .role(Role.USER)
//                        .firstName("Hoang " + i)
//                        .lastName("Vo " + i)
//                        .password(passwordEncoder.encode("0147852369Fa"))
//                        .username("hoang" + new Random().nextInt() + "@gmail.com")
//                        .build();
//
//                var savedUser = userRepository.save(user);
//
//                var chatUser = ChatUser.builder()
//                        .originalUser(savedUser)
//                        .createdAt(savedUser.getCreatedAt())
//                        .lastActiveAt(new Date())
//                        .build();
//                var savedChatUser = chatUserRepository.save(chatUser);
//
//                var chatUsers = chatUserRepository.findAll();
//                LOG.info("ChatUsers Size: " + chatUsers.size());
//                if (chatUsers.size() < 2) {
//                    continue;
//                }
//
//                var channel = ChatChannel.builder()
//                        .type(ChannelType.MUTUAL)
//                        .createdAt(new Date())
//                        .createdByUser(savedChatUser)
//                        .memberships(Set.of())
//                        .build();
//
//                var savedChannel = chatChannelRepository.save(channel);
//
//                var membership = Membership.builder()
//                        .createdAt(new Date())
//                        .id(new MembershipKey())
//                        .chatUser(savedChatUser)
//                        .chatChannel(savedChannel)
//                        .channelRole(ChannelRole.CHANNEL_ADMIN)
//                        .build();
//
//                LOG.info("First membership key: ChannelID: " + membership.getChatChannel().getId()
//                        + ", ChatUserID: " + membership.getChatUser().getId()
//                );
//                membershipRepository.save(membership);
//
//                chatUsers.removeIf(chatUser1 -> Objects.equals(chatUser1.getId(), savedChatUser.getId()));
//                Collections.shuffle(chatUsers);
//                var otherUser = chatUsers.get(0);
//                var membership2 = Membership.builder()
//                        .id(new MembershipKey())
//                        .createdAt(new Date())
//                        .channelRole(ChannelRole.CHANNEL_MEMBER)
//                        .chatUser(otherUser)
//                        .chatChannel(savedChannel)
//
//                        .build();
//
//                LOG.info("Second membership key: ChannelID: " + membership2.getChatChannel().getId()
//                        + ", ChatUserID: " + membership2.getChatUser().getId()
//                );
//                membershipRepository.save(membership2);
//
//                for (int j = 0; j < 5; j++) {
//                    var message = ChatMessage.builder()
//                            .text("Xin chao " + j * i)
//                            .chatChannel(savedChannel)
//                            .sentBy((new Random().nextBoolean()) ? savedChatUser : chatUsers.get(0))
//                            .createdAt(new Date())
//                            .build();
//                    chatMessageRepository.save(message);
//                }
//                LOG.info("----------------------------------------------------");
//
//            }
//
//            for (int i = 0; i < 5; i++) {
//                var user = User.builder()
//                        .createdAt(new Date())
//                        .role(Role.USER)
//                        .firstName("Dieu " + i)
//                        .lastName("Nguyen " + i)
//                        .password(passwordEncoder.encode("0147852369Fa"))
//                        .username("dieu" + new Random().nextInt() + "@gmail.com")
//                        .build();
//
//                var savedUser = userRepository.save(user);
//
//                var chatUser = ChatUser.builder()
//                        .originalUser(savedUser)
//                        .createdAt(savedUser.getCreatedAt())
//                        .lastActiveAt(new Date())
//                        .build();
//                chatUserRepository.save(chatUser);
//
//            }
//
//        };
//    }

    private ChatChannel createAndSaveChatChannel(ChatUser chatUser) {
        var channel = ChatChannel.builder()
                .createdByUser(chatUser)
                .createdAt(new Date())
                .name("Channel of" + chatUser.getOriginalUser().getFirstName())
                .build();
        return chatChannelRepository.save(channel);
    }
}
