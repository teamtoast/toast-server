package com.teamtoast.toast.study;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SignalController {

    @MessageMapping("/offer")
    @SendTo("/study/remote")
    public String offer(String msg) {
        return msg;
    }

    @MessageMapping("/answer")
    @SendTo("/study/host")
    public String answer(String msg) {
        return msg;
    }

    @MessageMapping("/send")
    @SendTo("/study/member")
    public String send(String msg) {
        System.out.println(msg);
        return msg;
    }

    @MessageMapping("/candidate")
    @SendTo("/study/remote")
    public String candidate(String msg) {
        return msg;
    }

}
