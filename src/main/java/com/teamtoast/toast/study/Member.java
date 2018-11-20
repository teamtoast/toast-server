package com.teamtoast.toast.study;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.dialogflow.v2.*;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class Member {

    private Room room;
    private Session session;
    private boolean ready = false;
    private boolean streamLoaded = false;

    public Member(Session session) {
        this.session = session;
    }

    public long getUserId() {
        return session.getUserId();
    }

    public void offer(Member target, JsonNode data) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("from", session.getUserId());
        node.set("data", data);
        target.session.send("offer", node);
    }

    public void answer(Member target, JsonNode data) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("from", session.getUserId());
        node.set("data", data);
        target.session.send("answer", node);
    }

    public void sendCandidate(Member target, JsonNode data) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("from", session.getUserId());
        node.set("data", data);
        target.session.send("candidate", node);
    }

    public void start() {
        if(room.getHost() == session.getUserId()) {
            room.start();
        }
    }

    public void welcome(Member target) {
        session.send("join", target.session.getUserId());
    }

    public void noticeLeave(Member target) {
        ObjectNode node = new ObjectMapper().getNodeFactory().objectNode();
        node.put("id", target.session.getUserId());
        session.send("leave", node);
    }

    public void sendReadyStates(JsonNode node) {
        session.send("ready", node);
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void sendInfo(Room.Info info) {
        session.send("info", info);
    }

    public void noticeStart() {
        session.send("start", null);
    }

    public void onMessage(String cmd, JsonNode data) {
        switch (cmd) {
            case "ready":
                toggleReady();
                room.sendReadyStates();
                break;

            case "start":
                if(room.getHost() == getUserId())
                    start();
                break;

            case "stream":
                synchronized (room) {
                    for(Member member : room.getMembers()) {
                        if(member.streamLoaded) {
                            member.session.send("stream", getUserId());
                        }
                    }
                    streamLoaded = true;
                }
                break;

            case "offer":
                Member offerTarget = room.getSessionByUserId(data.get("target").asLong());
                offer(offerTarget, data.get("data"));
                break;

            case "answer":
                Member answerTarget = room.getSessionByUserId(data.get("target").asLong());
                answer(answerTarget, data.get("data"));
                break;

            case "candidate":
                Member candidateTarget = room.getSessionByUserId(data.get("target").asLong());
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

    private int fileCount = 1;
    private String recognizeVoice(String data) {
        try (SpeechClient speechClient = SpeechClient.create()) {
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(44100)
                    .setLanguageCode("en-US")
                    .build();

            byte[] raw = Base64.getDecoder().decode(data);
            ByteString audioBytes = ByteString.copyFrom(raw);
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            // 신뢰도(Confidence)를 측정합니다.
            float totalConfidence = 0.0f;
            int confidenceCount = 0;
            StringBuilder script = new StringBuilder();
            for (SpeechRecognitionResult result : results) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);

                totalConfidence += alternative.getConfidence();
                confidenceCount++;

                if (script.length() > 0)
                    script.append(' ');
                script.append(alternative.getTranscript());
            }

            // 평균 신뢰도와 측정된 텍스트를 JSON 형태로 기록합니다.
            float avgConfidence = 0;
            if(confidenceCount > 0)
                avgConfidence = totalConfidence / (float) confidenceCount;
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode obj = mapper.createObjectNode();
            obj.put("confidence", avgConfidence);
            obj.put("text", script.toString());

            String fileName = session.getSocketHandler().getDataPath() + "/" + room.getId() + "/" + getUserId() + "/" + fileCount;
            File info = new File(fileName + ".json");
            info.getParentFile().mkdirs();
            info.createNewFile();
            File wav = new File(fileName + ".wav");
            wav.getParentFile().mkdirs();
            info.createNewFile();
            mapper.writeValue(new FileOutputStream(info), obj);
            FileOutputStream os = new FileOutputStream(wav);
            os.write(raw);
            os.close();

            // JSON 파일을 경로로 내보내기 합니다.
            fileCount++;

            return script.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void sendScript(String script) {
        ObjectNode data = new ObjectMapper().createObjectNode();
        data.put("speaker", session.getUserId());
        data.put("script", script);

        for(Member member : room.getMembers()) {
            member.session.send("say", data);
        }
    }

    private void sendRecommend(String script, String recommend) {
        ObjectNode data = new ObjectMapper().createObjectNode();
        data.put("speaker", session.getUserId());
        data.put("script", script);
        data.put("recommend", recommend);

        for(Member member : room.getMembers()) {
            member.session.send("recommend", data);
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

    public void toggleReady() {
        ready = !ready;
    }

    public boolean isReady() {
        return ready;
    }
}
