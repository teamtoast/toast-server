package com.teamtoast.toast.auth.bodys;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.teamtoast.toast.auth.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserBySNSRequest {

    public User.AccountType type;
    public String token;
    public String nickname;
    public String contact;
    public User.Gender gender;
    public int age;

}
