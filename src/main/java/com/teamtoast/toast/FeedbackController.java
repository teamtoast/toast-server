package com.teamtoast.toast;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class FeedbackController {
    @ApiOperation(value = "영어 문법 피드백", notes = "영어 문장을 넣으면 문법 점수와 바뀌어야 할 단어 부분이 반환됩니다. (추후에 삭제될 수 있는 API)")
    @RequestMapping(value = "/feedback/grammarFeedback", produces = {"application/json"}, method = RequestMethod.POST)
    public String feedback(@RequestBody String text) {
        return getGrammerFeedback(text);
    }

    public String getGrammerFeedback(String text) {
        String key = "RKhYv19FfAsM0oZN"; // 테스트용 무료 API Key
        String result = "";
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.textgears.com/check.php?text=" + text + "&key=" + key)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
}
