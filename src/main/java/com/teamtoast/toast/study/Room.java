package com.teamtoast.toast.study;

import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.CopyOnWriteArrayList;

public class Room {

    private int id;
    private int connectCount = 0;
    private CopyOnWriteArrayList<Session> sessions = new CopyOnWriteArrayList<>();

    public Room(int id) {
        this.id = id;
    }

    public Session join(WebSocketSession ws) {
        Session session = new Session(connectCount++, ws);
        session.sendInfo();
        session.setRoom(this);
        sessions.add(session);

        for(Session sess : sessions) {
            sess.welcome(session);
        }

        return session;
    }

    public void leave(Session session) {
        sessions.remove(session);

        for(Session sess : sessions) {
            sess.noticeLeave(session);
        }
    }

    public Session getSessionById(int id) {
        for(Session sess : sessions) {
            if(sess.getId() == id)
                return sess;
        }
        return null;
    }
}
