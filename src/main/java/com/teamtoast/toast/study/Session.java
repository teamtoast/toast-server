package com.teamtoast.toast.study;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class Session {

    private int id;
    private Room room;
    private WebSocketSession session;

    public Session(int id, WebSocketSession session) {
        this.id = id;
        this.session = session;
    }

    public void offer(Session target, JsonNode data) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("from", id);
        node.set("data", data);
        target.send("offer", node);
    }

    public void answer(Session target, JsonNode data) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("from", id);
        node.set("data", data);
        target.send("answer", node);
    }

    public void sendCandidate(Session target, JsonNode data) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("from", id);
        node.set("data", data);
        target.send("candidate", node);
    }

    public void welcome(Session target) {
        ObjectNode node = new ObjectMapper().getNodeFactory().objectNode();
        node.put("id", target.id);
        send("join", node);
    }

    public void noticeLeave(Session target) {
        ObjectNode node = new ObjectMapper().getNodeFactory().objectNode();
        node.put("id", target.id);
        send("leave", node);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void sendInfo() {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("id", id);
        send("info", node);
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
            case "offer":
                Session offerTarget = room.getSessionById(data.get("target").asInt());
                offer(offerTarget, data.get("data"));
                break;

            case "answer":
                Session answerTarget = room.getSessionById(data.get("target").asInt());
                answer(answerTarget, data.get("data"));
                break;

            case "candidate":
                Session candidateTarget = room.getSessionById(data.get("target").asInt());
                if(candidateTarget == null)
                    System.out.println("ana");
                sendCandidate(candidateTarget, data.get("data"));
                break;
        }
    }
}
