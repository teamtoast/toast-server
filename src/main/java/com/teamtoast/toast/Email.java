package com.teamtoast.toast;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.teamtoast.toast.auth.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Email {

    public String to;
    public String subject;
    public String text;

}