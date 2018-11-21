package com.teamtoast.toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.ApiOperation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;

@RestController
public class SpeechFeedbackController {

    @Value("${speech-data-path}")
    private String speechPath;

    @ApiOperation(value = "특정 대화방에서의 모든 피드백", notes = ".json과 오디오 파일만 존재하는 폴더를 지정하여 전체 피드백을 제시합니다. 예를 들어 'C:\\Toast Sample Data' 경로에 3개의 음성 파일과 3개의 음성 인식 json 파일이 있으면 알아서 이에 대한 피드백을 제공합니다.")
    @RequestMapping(value = "/feedback/getAllFeedback/{roomId}/{userId}", produces = {"application/json"}, method = RequestMethod.POST)
    public String getAllFeedback(@PathVariable long roomId, @PathVariable long userId) {
        String path = speechPath + "/" + roomId + "/" + userId;
        ArrayList<String> getAllPath = getAllFiles(path);
        for(int i = 0; i < getAllPath.size(); i++) {
            getAllPath.set(i, path + "/" + getAllPath.get(i)); // Full Path로 저장
        }
        ArrayList<String> results = new ArrayList<>();
        double totalConfidence = 0.0;
        int confidenceCount = 0;
        try {
            for(int i = 0; i < getAllPath.size(); i++) {
                String now = getAllPath.get(i);
                int index = now.lastIndexOf(".");
                now = now.substring(0, index) + ".json";
                // 음성 JSON 파일 읽기
                File file = new File(now);
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String s;
                String res = "";
                while((s = reader.readLine()) != null) {
                    res += s;
                }
                // 음성 JSON은 confidence와 text로 구성됩니다.
                JsonNode temp = new ObjectMapper().readTree(res);
                if(!temp.isObject()) continue;
                ObjectNode obj = (ObjectNode) temp;
                String text = obj.get("text").toString();
                text = text.substring(1, text.length() - 1);
                double confidence = Double.parseDouble(obj.get("confidence").toString());
                // 음성 인식 Confidence 값이 80% 이상인 경우만 허용 (80% 미만인 경우 음성 오류 있을 가능성이 높음.)
                // 아예 Path List에서 제거해버리기.
                totalConfidence += confidence;
                confidenceCount++;
                if(confidence < 0.8) {
                    getAllPath.remove(i);
                    i--;
                    continue;
                }
                results.add(text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 해당 문장의 문법 피드백 결과를 삽입합니다.
        FeedbackController feedbackController = new FeedbackController();
        // 최종 결과 반환 객체입니다.
        ObjectNode res = new ObjectMapper().createObjectNode();
        ArrayNode grammarFeedback = new ObjectMapper().createArrayNode();
        int grammarFaultCount = 0;
        int spellingFaultCount = 0;
        int totalWordCount = 0;
        // 먼저 문법 검사를 할 전체 문장을 삽입합니다.
        for(int i = 0; i < results.size(); i++) {
            ObjectNode text = new ObjectMapper().createObjectNode();
            text.put("expression", results.get(i));
            totalWordCount += (results.get(i).split(" ") == null)? 0 : results.get(i).split(" ").length;
            String errorCheck = feedbackController.getGrammerFeedback(results.get(i));
            try {
                JsonNode errorNode = new ObjectMapper().readTree(errorCheck);
                if(errorNode != null) {
                    if(errorNode.get("result") != null) {
                        String error = errorNode.get("errors").toString();
                        ArrayNode array = (ArrayNode) new ObjectMapper().readTree(error);
                        // 문법 오류가 있는 경우에만 Grammar Feedback 부분에 값이 들어갑니다.
                        if(array.get(0) != null) {
                            for(int j = 0; j < array.size(); j++)
                            {
                                ((ObjectNode) array.get(j)).remove("id"); // ID 부분 지우기 (필요 없음)
                                String type = array.get(j).get("type").toString();
                                if(type.equals("\"grammar\"")) {
                                    grammarFaultCount++;
                                }
                                else if(type.equals("\"spelling\"")) {
                                    spellingFaultCount++;
                                }
                            }
                            text.put("grammarFeedback", array);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            grammarFeedback.add(text);
        }
        res.put("grammarFeedbacks", grammarFeedback);
        ArrayList<String> poorWordList = new ArrayList<>();
        int totalAccuracy = 0;
        int wordCount = 0;
        double proScore = 0.0;
        int proScoreCount = 0;
        try {
            for(int i = 0; i < res.get("grammarFeedbacks").size(); i++) {
                // 문법적으로 오류가 없는 문장에 한해서 발음 피드백 진행
                if(res.get("grammarFeedbacks").get(i).get("grammarFeedback") == null) {
                    String text = res.get("grammarFeedbacks").get(i).get("expression").toString();
                    String mediaPath = getAllPath.get(i);
                    String pronunciationFeedback = getFeedbackFromFile(text, mediaPath);
                    ArrayNode nowFeedback = (ArrayNode) (new ObjectMapper().readTree(pronunciationFeedback).get("words"));
                    for(int j = 0; j < nowFeedback.size(); j++) {
                        String wordString = nowFeedback.get(j).get("word").toString();
                        String accuarcyString = nowFeedback.get(j).get("accuracy").toString();
                        int accuracy = Integer.parseInt(accuarcyString);
                        if(accuracy < 85) {
                            poorWordList.add(wordString);
                        }
                        totalAccuracy += accuracy;
                        wordCount += 1;
                    }
                    proScore += Double.parseDouble(new ObjectMapper().readTree(pronunciationFeedback).get("totalScore").toString());
                    proScoreCount++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayNode pronunciations = new ObjectMapper().createArrayNode();
        for(int i = 0; i < poorWordList.size(); i++) {
            pronunciations.add(poorWordList.get(i).substring(1, poorWordList.get(i).length() - 1));
        }
        ArrayNode recommendSentences = new ObjectMapper().createArrayNode();
        int target = 3;
        for(int i = 0; i < results.size() && i < target; i++) {
            ObjectNode obj =  new ObjectMapper().createObjectNode();
            obj.put("question", results.get(i));
            String answer = getAnswer(results.get(i));
            if(answer.equals("")) {
                target++;
                continue;
            }
            obj.put("answer", answer);
            recommendSentences.add(obj);
        }
        res.put("recommendSentences", recommendSentences);
        res.put("poorPronunciation", pronunciations);
        double pronunciationScoreDouble = proScore / proScoreCount;
        double grammarScoreDouble = ((double) totalWordCount - grammarFaultCount) * 100 / totalWordCount;
        double wordScoreDouble = ((double) totalWordCount - spellingFaultCount) * 100 / totalWordCount;
        double expressionScore = (totalConfidence * 100) / confidenceCount;
        res.put("pronunciationScore", getScore(pronunciationScoreDouble));
        res.put("grammarScore", getScore(grammarScoreDouble));
        res.put("wordScore", getScore(wordScoreDouble));
        res.put("expressionScore", getScore(expressionScore));
        return res.toString();
    }

    public String getScore(double input) {
        String res = "Bad";
        if(input >= 90) {
            res = "Excellent";
        }
        else if(input >= 82) {
            res = "Great";
        }
        else if(input >= 75) {
            res = "Good";
        }
        else if(input >= 70) {
            res = "intermediate";
        }
        return res;
    }

    public String getAnswer(String text) {
        String result = "";
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .header("Authorization", "Bearer e3dd27f78a324a58bce120462c51171b") // 액세스 토큰 하드코딩 (죄송합니다.)
                    .url("https://api.dialogflow.com/v1/query?query=" + text + "&sessionId=796&lang=ko")
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            ObjectNode root = (ObjectNode) new ObjectMapper().readTree(body);
            return root.get("result").get("speech").textValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getFeedbackFromFile(String text, String path) {
        String ret = "";
        try {
            ret = getAPIResult(text, path);
            // 최종 결과 반환 객체입니다.
            ObjectNode result = new ObjectMapper().createObjectNode();
            // 각 단어마다 정확도를 체크하므로 2차원 배열을 사용합니다.
            ArrayNode wordsArray = new ObjectMapper().createArrayNode();
            ArrayNode root = (ArrayNode) new ObjectMapper().readTree(ret);
            double sum = 0;
            for(int i = 0; i < root.size(); i++) {
                // 개별 단어에 접근하여 단어별 정확도를 뽑아 삽입합니다.
                ObjectNode obj = new ObjectMapper().createObjectNode();
                obj.put("word", root.get(i).get("word"));
                obj.put("accuracy", root.get(i).get("quality_score"));
                sum += Double.parseDouble(root.get(i).get("quality_score").toString());
                wordsArray.add(obj);
            }
            result.put("words", wordsArray);
            ObjectNode totalWord = new ObjectMapper().createObjectNode();
            result.put("totalWord", root.size());
            ObjectNode totalScore = new ObjectMapper().createObjectNode();
            result.put("totalScore", sum / root.size());
            String judgement = "Bad";
            if(sum / root.size() >= 85) {
                judgement = "Excellent";
            }
            else if(sum / root.size() >= 78) {
                judgement = "Great";
            }
            else if(sum / root.size() >= 70) {
                judgement = "Good";
            }
            else if(sum / root.size() >= 60) {
                judgement = "So So";
            }
            ObjectNode totalJudgement = new ObjectMapper().createObjectNode();
            result.put("totalJudgement", judgement);
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /*
    @ApiOperation(value = "영어 음성 피드백", notes = "(프로토 타입) 타겟이 되는 영어 문장과 음성 파일을 넣으면 피드백 내용이 반환됩니다. 현재 소스코드상에서는 SpeechFeedbackController.java에서 파일 업로드 경로 설정이 필요합니다.")
    @RequestMapping(value = "/feedback/speechFeedback", produces = {"application/json"}, method = RequestMethod.POST)
    public String feedback(@RequestParam String text, @RequestBody MultipartFile file) {
        return getSpeechFeedback(text, file);
    }
    */

    public String getAPIResult(String text, String path) {
        String res = "";
        try {
            String url = "http://www.speechace.co/em/indexv.php"; // 파일을 전송할 URL
            String charset = "UTF-8";
            // File binaryFile = new File(filePath + fileName);
            File binaryFile = new File(path);
            String boundary = Long.toHexString(System.currentTimeMillis()); // 랜덤 값을 생성합니다.
            String CRLF = "\r\n"; // multipart/form-data에 사용될 줄바꿈 텍스트입니다.

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.connect();

            OutputStream output = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);

            // 기본적인 형태의 파라미터를 전송합니다.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"texttoscore\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append(text).append(CRLF).flush();

            // 바이너리 파일을 전송합니다.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"fileToUpload\"; filename=\"" + binaryFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(binaryFile.toPath(), output);
            output.flush(); // 반드시 writer가 끝나기 전에 flush()를 수행합니다.
            writer.append(CRLF).flush(); // 경계의 마지막을 알리기 위해서 CRLF를 넣어야 합니다.

            // 기본적인 형태의 파라미터를 전송합니다.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"submit\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append("Check score").append(CRLF).flush();

            // multipart/form-data 형식의 끝을 알립니다.
            writer.append("--" + boundary + "--").append(CRLF).flush();

            InputStream input = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            String total = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                total += inputLine;
            }
            if (total.contains("Sorry could not score file:")) {
                res = "There is an error.";
            } else {
                res = total.split("</div>")[4].split("<div>")[1];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public ArrayList<String> getAllFiles(String path) {
        ArrayList<String> results = new ArrayList<>();
        File[] files = new File(path).listFiles();
        for (File file : files) {
            if(file.isFile()) {
                /*
                String basicFileName = file.getName();
                int index = basicFileName.lastIndexOf(".");
                String fileName = basicFileName.substring(0, index);
                boolean find = false;
                for(int i = 0; i < results.size(); i++) {
                    if(results.get(i).equals(fileName)) {
                        find = true;
                    }
                }
                if(!find) results.add(file.getName().substring(0, index));
                */
                String basicFileName = file.getName();
                if(basicFileName.contains("json")) continue;
                results.add(basicFileName);
            }
        }
        return results;
    }

}