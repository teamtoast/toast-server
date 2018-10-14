package com.teamtoast.toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamtoast.toast.study.Room;
import com.teamtoast.toast.study.Session;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {

    private CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<Integer, Room> rooms = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode root = new ObjectMapper().readTree(message.getPayload());
        String cmd = root.get("cmd").asText();
        if(cmd.equals("join")) {
            int id = root.get("data").asInt();
            if(rooms.get(id) == null)
                createRoom(id);

            sessionMap.put(session.getId(), rooms.get(id).join(session));
        }
        else {
            sessionMap.get(session.getId()).onMessage(cmd, root.get("data"));
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Session roomSession = sessionMap.get(session.getId());
        if(roomSession != null) {
            Room room = roomSession.getRoom();
            if(room != null) room.leave(roomSession);
        }

        sessions.remove(session);
    }

    private void createRoom(int id) {
        rooms.putIfAbsent(id, new Room(id));
    }
}
