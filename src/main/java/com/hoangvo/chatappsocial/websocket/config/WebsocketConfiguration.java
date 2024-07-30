package com.hoangvo.chatappsocial.websocket.config;

import com.hoangvo.chatappsocial.controller.socket.MessageReadEventHandler;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@EnableWebSocket
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private MessageChannel outChannel;
    private static final Logger LOG = LoggerFactory.getLogger(WebsocketConfiguration.class);

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app")
                .enableSimpleBroker(DESTINATION_PREFIX);
    }

    public static final String DESTINATION_PREFIX = "/topic";


    @Override
    public void configureClientInboundChannel(@Nonnull ChannelRegistration registration) {
        WebSocketMessageBrokerConfigurer.super.configureClientInboundChannel(registration);
        registration.interceptors(new ExecutorChannelInterceptor() {

            @Override
            public void afterMessageHandled(@Nonnull Message<?> inMessage,
                                            @Nonnull MessageChannel inChannel,
                                            @Nonnull MessageHandler handler, Exception ex) {

                StompHeaderAccessor inAccessor = StompHeaderAccessor.wrap(inMessage);
                String receipt = inAccessor.getReceipt();
                if (receipt == null || receipt.isEmpty()) {
                    return;
                }

                StompHeaderAccessor outAccessor = StompHeaderAccessor.create(StompCommand.RECEIPT);
                outAccessor.setSessionId(inAccessor.getSessionId());
                outAccessor.setReceiptId(receipt);
                outAccessor.setLeaveMutable(true);

                Message<byte[]> outMessage =
                        MessageBuilder.createMessage(new byte[0], outAccessor.getMessageHeaders());
                outChannel.send(outMessage);

            }
        });
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ExecutorChannelInterceptor() {
            @Override
            public void afterMessageHandled(@Nonnull Message<?> message,
                                            @Nonnull MessageChannel channel,
                                            @Nonnull MessageHandler handler,
                                            @Nullable Exception ex) {
                outChannel = channel;
            }
        });
    }


}
