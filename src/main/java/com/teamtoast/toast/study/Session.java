package com.teamtoast.toast.study;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.dialogflow.v2.*;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

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

            case "say":
                String script = recognizeVoice(data.asText());
                sendScript(script);
                String recommend = getRecommend(script);
                sendRecommend(script, recommend);
                break;
        }
    }

    private String recognizeVoice(String data) {
        try(SpeechClient speechClient = SpeechClient.create()) {
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(44100)
                    .setLanguageCode("en-US")
                    .build();

            ByteString audioBytes = ByteString.copyFrom(Base64.getDecoder().decode(data));
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            StringBuilder script = new StringBuilder();
            for(SpeechRecognitionResult result : results) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);

                if(script.length() > 0)
                    script.append(' ');
                script.append(alternative.getTranscript());
            }

            return script.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void sendScript(String script) {
        ObjectNode data = new ObjectMapper().createObjectNode();
        data.put("speaker", id);
        data.put("script", script);

        for(Session sess : room.getSessions()) {
            sess.send("say", data);
        }
    }

    private void sendRecommend(String script, String recommend) {
        ObjectNode data = new ObjectMapper().createObjectNode();
        data.put("speaker", id);
        data.put("script", script);
        data.put("recommend", recommend);

        for(Session sess : room.getSessions()) {
            sess.send("recommend", data);
        }
    }

    private String getRecommend(String script) {
        if(script.isEmpty()) return "";

        try(SessionsClient sessionsClient = SessionsClient.create()) {
            SessionName sessionName = SessionName.of("voicetest-213510", "testsession");
            TextInput.Builder textInput = TextInput.newBuilder().setText(script).setLanguageCode("en-US");
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

            DetectIntentResponse response = sessionsClient.detectIntent(sessionName, queryInput);
            QueryResult queryResult = response.getQueryResult();

            return queryResult.getFulfillmentText();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
