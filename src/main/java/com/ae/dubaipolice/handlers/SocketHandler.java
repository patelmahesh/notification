/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ae.dubaipolice.handlers;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 *
 * @author user
 */
//@Component
public class SocketHandler extends TextWebSocketHandler {

    private static Map< String, WebSocketSession> sessions = new HashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {

        for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
            String userId = entry.getKey();
            WebSocketSession socSec = entry.getValue();
            
            Map value = new Gson().fromJson(message.getPayload(), Map.class);
            
            socSec.sendMessage(new TextMessage("Hello " + value.get("name") + " !"));
        }
    }

    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getUri().getQuery(), session);
        session.sendMessage(new TextMessage("Added Succesfully"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.values().remove(session);
        super.afterConnectionClosed(session, status);
    }
}
