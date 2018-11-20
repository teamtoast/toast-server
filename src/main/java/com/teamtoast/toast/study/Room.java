package com.teamtoast.toast.study;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.teamtoast.toast.SocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;

import javax.persistence.*;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class Room {

    private SocketHandler handler;
    private long id;
    private long host;
    private CopyOnWriteArrayList<Member> members = new CopyOnWriteArrayList<>();

    public Room(SocketHandler handler, long id) {
        this.handler = handler;
        this.id = id;
    }

    public Member join(Session session) {
        Member member = new Member(session);
        member.sendInfo(createInfo());
        member.setRoom(this);
        members.add(member);

        for(Member sess : members) {
            sess.welcome(member);
        }

        return member;
    }

    public void leave(Member member) {
        synchronized (this) {
            members.remove(member);

            for (Member sess : members) {
                sess.noticeLeave(member);
            }

            if(members.size() == 0) {
                handler.destroyRoom(this);
            }
        }
    }

    public void sendReadyStates() {
        synchronized (this) {
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode root = mapper.createArrayNode();

            for(Member m : members) {
                ObjectNode node = mapper.createObjectNode();
                node.put("id", m.getUserId());
                node.put("ready", m.isReady());
                root.add(node);
            }

            for(Member m : members) {
                m.sendReadyStates(root);
            }
        }
    }

    public void start() {
        synchronized (this) {
            boolean ready = true;
            for (Member member : members) {
                ready = ready && member.isReady();
            }

            if(ready) {
                for (Member member : members) {
                    member.noticeStart();
                }
            }
        }
    }

    public long getId() {
        return id;
    }

    public Member getSessionByUserId(long id) {
        for (Member sess : members) {
            if (sess.getUserId() == id)
                return sess;
        }
        return null;
    }

    public Member[] getMembers() {
        return members.toArray(new Member[0]);
    }

    public long getHost() {
        return host;
    }

    public void setHost(long host) {
        this.host = host;
    }

    public Info createInfo() {
        Data data = handler.getRoomData(id).get();
        Info info = new Info();
        info.setId(id);
        info.setCategory(data.getCategory());
        info.setTitle(data.getTitle());

        long[] memberIds = new long[members.size()];
        for(int i = 0; i < memberIds.length; i++) {
            memberIds[i] = members.get(i).getUserId();
        }
        info.setUsers(memberIds);
        info.setMaxUsers(data.maxUsers);
        info.setStudyMinutes(data.studyMinutes);
        info.setMinLevel(data.minLevel);

        return info;
    }

    @Entity
    @Table(name = "studyrooms")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(updatable = false, insertable = false)
        private long id;
        private int category;
        private String title;
        private int maxUsers;
        private int studyMinutes;
        private int minLevel;
        @Column(insertable = false, updatable = false)
        private Date createdAt;
        private Date startedAt;
        private Date endedAt;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getCategory() {
            return category;
        }

        public void setCategory(int category) {
            this.category = category;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getMaxUsers() {
            return maxUsers;
        }

        public void setMaxUsers(int maxUsers) {
            this.maxUsers = maxUsers;
        }

        public int getStudyMinutes() {
            return studyMinutes;
        }

        public void setStudyMinutes(int studyMinutes) {
            this.studyMinutes = studyMinutes;
        }

        public int getMinLevel() {
            return minLevel;
        }

        public void setMinLevel(int minLevel) {
            this.minLevel = minLevel;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public Date getStartedAt() {
            return startedAt;
        }

        public void setStartedAt(Date startedAt) {
            this.startedAt = startedAt;
        }

        public Date getEndedAt() {
            return endedAt;
        }

        public void setEndedAt(Date endedAt) {
            this.endedAt = endedAt;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Info {

        private long id;
        private int category;
        private String title;
        private long[] users;
        private int maxUsers;
        private int studyMinutes;
        private int minLevel;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getCategory() {
            return category;
        }

        public void setCategory(int category) {
            this.category = category;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long[] getUsers() {
            return users;
        }

        public void setUsers(long[] users) {
            this.users = users;
        }

        public int getMaxUsers() {
            return maxUsers;
        }

        public void setMaxUsers(int maxUsers) {
            this.maxUsers = maxUsers;
        }

        public int getStudyMinutes() {
            return studyMinutes;
        }

        public void setStudyMinutes(int studyMinutes) {
            this.studyMinutes = studyMinutes;
        }

        public int getMinLevel() {
            return minLevel;
        }

        public void setMinLevel(int minLevel) {
            this.minLevel = minLevel;
        }
    }
}
