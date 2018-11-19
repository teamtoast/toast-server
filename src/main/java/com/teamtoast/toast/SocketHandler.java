package com.teamtoast.toast;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamtoast.toast.auth.TokenService;
import com.teamtoast.toast.auth.User;
import com.teamtoast.toast.auth.UserService;
import com.teamtoast.toast.study.Room;
import com.teamtoast.toast.study.Member;
import com.teamtoast.toast.study.RoomRepository;
import com.teamtoast.toast.study.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RoomRepository roomRepository;

    private ArrayList<WebSocketSession> sessions = new ArrayList<>();
    private HashMap<Long, Room> rooms = new HashMap<>();
    private HashMap<String, Session> sessionMap = new HashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode root = new ObjectMapper().readTree(message.getPayload());
        String cmd = root.get("cmd").asText();
        if(cmd.equals("connect")) {
            String[] splited = root.get("data").asText().split("Bearer ");
            if(splited.length > 1) {
                try {
                    User user = tokenService.verifyToken(splited[1]);
                    sessionMap.put(session.getId(), new Session(this, user.getId(), session));
                }
                catch (SignatureVerificationException e) {
                    session.close();
                }
            }
            else
                session.close();
        }
        else {
            sessionMap.get(session.getId()).onMessage(cmd, root.get("data"));
        }
        /*else if(cmd.equals("join")) {
            int id = root.get("data").asInt();
            if(rooms.get(id) == null)
                createRoom(id);

            sessionMap.put(session.getId(), rooms.get(id).join(session));
        }
        else {
            sessionMap.get(session.getId()).onMessage(cmd, root.get("data"));
        }*/
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        synchronized (this) {
            sessions.add(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        synchronized (this) {
            sessionMap.get(session.getId()).onClose();
            /*Member roomMember = sessionMap.get(session.getId());
            if(roomMember != null) {
                Room room = roomMember.getRoom();
                if(room != null) room.leave(roomMember);
            }*/

            sessions.remove(session);
        }
    }

    public Room createRoom(int category, String title, int maxUsers, int studyMinutes, int minLevel) {
        synchronized (this) {
            Room.Data data = new Room.Data();
            data.setCategory(category);
            data.setTitle(title);
            data.setMaxUsers(maxUsers);
            data.setStudyMinutes(studyMinutes);
            data.setMinLevel(minLevel);
            roomRepository.save(data);

            Room room = new Room(this, data.getId());
            rooms.put(data.getId(), room);

            return room;
        }
    }

    public Room getRoom(long id) {
        return rooms.get(id);
    }

    public Room.Info[] getRoomInfos() {
        ArrayList<Room.Info> infos = new ArrayList<>();
        synchronized (this) {
            for (Room room : rooms.values()) {
                infos.add(room.createInfo());
            }
        }

        return infos.toArray(new Room.Info[0]);
    }

    public Room.Info[] getRoomInfos(int category) {
        ArrayList<Room.Info> infos = new ArrayList<>();
        synchronized (this) {
            for (Room room : rooms.values()) {
                infos.add(room.createInfo());
            }
        }

        return infos.toArray(new Room.Info[0]);
    }

    public Optional<Room.Data> getRoomData(long id) {
        return roomRepository.findById(id);
    }
}
