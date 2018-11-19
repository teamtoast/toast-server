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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class Session {

    @Value("speech-data-path")
    private String savePath; // 음성 파일 및 .json 파일 저장 위치: 나동빈 수정(2018-11-18)

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
        node.put("email", target.id);
        send("join", node);
    }

    public void noticeLeave(Session target) {
        ObjectNode node = new ObjectMapper().getNodeFactory().objectNode();
        node.put("email", target.id);
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
        node.put("email", id);
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
                // recognizeVoice() 함수 내에서 음성 파일 및 .json 파일 저장하는 기능 추가 : 나동빈 수정(2018-11-18)
                String script = recognizeVoice(data.asText());
                sendScript(script);
                String recommend = getRecommend(script);
                sendRecommend(script, recommend);
                break;
        }
    }

    /*
        -- 나동빈 수정(2018-11-18) --
        디버깅을 못 해서 정상 작동하는지는 확인 못 함.
     */
    private int fileCount = 1;
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

            // 신뢰도(Confidence)를 측정합니다.
            float totalConfidence = 0.0f;
            int confidenceCount = 0;
            StringBuilder script = new StringBuilder();
            for(SpeechRecognitionResult result : results) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);

                totalConfidence += alternative.getConfidence();
                confidenceCount++;

                if(script.length() > 0)
                    script.append(' ');
                script.append(alternative.getTranscript());
            }

            // 평균 신뢰도와 측정된 텍스트를 JSON 형태로 기록합니다.
            float avgConfidence = totalConfidence / (float) confidenceCount;
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode obj = mapper.createObjectNode();
            obj.put("confidence", avgConfidence);
            obj.put("text", script.toString());
            mapper.writeValue(new FileOutputStream(savePath + "/" + room.getId() + "/" + getId() + "/" + fileCount + ".json"), obj);

            FileOutputStream os = new FileOutputStream(savePath + "/" + room.getId() + "/" + getId() + "/" + fileCount + ".wav");
            os.write(data.getBytes());
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
