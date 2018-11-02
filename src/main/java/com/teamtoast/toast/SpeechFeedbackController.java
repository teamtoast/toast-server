package com.teamtoast.toast;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

@RestController
public class SpeechFeedbackController {

    private String filePath = "C:/my upload/";

    @ApiOperation(value = "영어 음성 피드백", notes = "(프로토 타입) 타겟이 되는 영어 문장과 음성 파일을 넣으면 피드백 내용이 반환됩니다. 현재 소스코드상에서는 SpeechFeedbackController.java에서 파일 업로드 경로 설정이 필요합니다.")
    @RequestMapping(value = "/feedback/speechFeedback", produces = {"application/json"}, method = RequestMethod.POST)
    public String feedback(@RequestParam String text, @RequestBody MultipartFile file) {
        return getSpeechFeedback(text, file);
    }
    public String getSpeechFeedback(String text, MultipartFile file) {
        String ret = "";
        try {
            String fileName = file.getOriginalFilename();
            File convFile = new File(filePath + fileName);
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
            ret = getAPIResult(text, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public String getAPIResult(String text, String fileName) {
        String res = "";
        try {
            String url = "http://www.speechace.co/em/indexv.php"; // 파일을 전송할 URL
            String charset = "UTF-8";
            File binaryFile = new File(filePath + fileName);
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

}
