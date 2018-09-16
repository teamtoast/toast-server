package com.teamtoast.toast.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    public long id;
    public String nickname;
    public String contact;
    public Gender gender;
    public int age;
    public int level;
    public String picture;
    public Date createdAt;

    public enum AccountType {
        @JsonProperty("kakao")
        KAKAO,
        @JsonProperty("facebook")
        FACEBOOK,
        @JsonProperty("google")
        GOOGLE,
        @JsonProperty("github")
        GITHUB
    }

    public enum Gender {
        @JsonProperty("male")
        MALE,
        @JsonProperty("female")
        FEMALE
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreateRequest {

        public AccountType type;
        public String token;
        public String nickname;
        public String contact;
        public Gender gender;
        public int age;

    }

    public static class CreateResponse {

        public String token;

        public CreateResponse(String token) {
            this.token = token;
        }
    }

}
