package com.ae.dubaipolice.configuration;

import com.ae.dubaipolice.model.ChatMessage;
import com.ae.dubaipolice.util.ParticipantRepository;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");

        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        
        GenericMessage connectHeader = (GenericMessage) headers.getHeader(SimpMessageHeaderAccessor.CONNECT_MESSAGE_HEADER);
        Map<String, List<String>> nativeHeaders = (Map<String, List<String>>) connectHeader.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
        
        String username = nativeHeaders.get("user").get(0);

        // We store the session as we need to be idempotent in the disconnect event processing
        participantRepository.add(headers.getSessionId(), username);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            logger.info("User Disconnected : " + username);
            
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);

            messagingTemplate.convertAndSend("/topic/public", chatMessage);
            //Remove Particiapant
            participantRepository.removeParticipant(event.getSessionId());
        }
    }
}
