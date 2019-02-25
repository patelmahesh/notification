package com.ae.dubaipolice.controller;

import com.ae.dubaipolice.model.ChatMessage;
import com.ae.dubaipolice.util.ParticipantRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;

@Controller
public class ChatController {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());

        return chatMessage;
    }

    @MessageMapping("/reply/{sessionId}")
    @SendToUser("/queue/reply")
    public void sendSpecific(@Payload ChatMessage chatMessage, @DestinationVariable("sessionId") String sessionId) {
        
        
        /*for (Map.Entry<String, String> entry : participantRepository.getActiveSessions().entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            for (int i = 0; i < 4; i++) {
                chatMessage.setContent(i + "" + value);
                messagingTemplate.convertAndSend("/queue/reply-user" + key, chatMessage);
            }
        }*/

        messagingTemplate.convertAndSend("/queue/reply-user" + sessionId, chatMessage);

    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

    @SubscribeMapping("/activeUsers")
    public Map<String, String> getConnectedUsers() {
        return participantRepository.getActiveSessions();
    }

}
