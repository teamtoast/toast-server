package com.teamtoast.toast;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EmailController {

    @Autowired
    public JavaMailSender emailSender;

    @ApiOperation(value = "이메일 전송", notes = "받는 사람, 제목, 내용을 넣으면 이메일을 송부할 수 있습니다. (추후에 삭제될 API)")
    @RequestMapping(value = "/email/sendEmail", produces = {"application/json"}, method = RequestMethod.POST)
    public Map sendEmail(@RequestBody Email email) {
        return sendSimpleEmail(email);
    }

    @Autowired
    Environment env;

    public Map sendSimpleEmail(Email email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(env.getProperty("spring.mail.username"));
        message.setTo(email.to);
        message.setSubject(email.subject);
        message.setText(email.text);
        emailSender.send(message);
        Map<String, String> map = new HashMap();
        map.put("result", "success");
        return map;
    }

}
