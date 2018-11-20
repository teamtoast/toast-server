package com.teamtoast.toast.study;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamtoast.toast.SocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.FileOutputStream;
import java.io.IOException;

public class Session {

    private long userId;
    private WebSocketSession session;
    private SocketHandler socketHandler;
    private Member member;

    public Session(SocketHandler socketHandler, long userId, WebSocketSession session) {
        this.socketHandler = socketHandler;
        this.userId = userId;
        this.session = session;

        send("connect", userId);
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void send(String cmd, String data) {
        synchronized (this) {
            try {
                session.sendMessage(new TextMessage("{\"cmd\":\"" + cmd + "\", \"data\": \"" + data + "\"}"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String cmd, Object object) {
        synchronized (this) {
            try {
                String data = new ObjectMapper().writeValueAsString(object);

                session.sendMessage(new TextMessage("{\"cmd\":\"" + cmd + "\", \"data\": " + data + "}"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onMessage(String cmd, JsonNode data) {
        switch (cmd) {
            case "create":
                member = socketHandler.createRoom(data.get("category").asInt(), data.get("title").asText(),
                        data.get("maxUsers").asInt(), data.get("studyMinutes").asInt(),
                        data.get("minLevel").asInt()).join(this);
                member.getRoom().setHost(userId);
                break;

            case "join":
                member = socketHandler.getRoom(data.asLong()).join(this);
                break;

            case "leave":
                try {
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            default:
                if(member != null)
                    member.onMessage(cmd, data);
        }
    }

    public void onClose() {
        if(member != null) {
            member.getRoom().leave(member);
        }
        member = null;
    }

    public void close() {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SocketHandler getSocketHandler() {
        return socketHandler;
    }
}
