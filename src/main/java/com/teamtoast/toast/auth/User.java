package com.teamtoast.toast.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", updatable = false, insertable = false)
    @JsonProperty("userId")
    private long id;
    @Column(name = "userNickname")
    @JsonProperty("userNickname")
    private String nickname;
    @Column(name = "userContact")
    @JsonProperty("userContact")
    private String contact;
    @Column(name = "userGender")
    @Enumerated(EnumType.STRING)
    @JsonProperty("userGender")
    private Gender gender;
    @Column(name = "userAge")
    @JsonProperty("userAge")
    private int age;
    @Column(name = "userLevel")
    @JsonProperty("userLevel")
    private int level = 0;
    @Column(name = "userPicture")
    @JsonProperty("userPicture")
    private String picture;
    @Column(name = "userCreatedAt", insertable = false, updatable = false)
    @JsonProperty("userCreatedAt")
    private Date createdAt;

    public User() {
    }

    public User(String nickname, String contact, Gender gender, int age) {
        this.nickname = nickname;
        this.contact = contact;
        this.gender = gender;
        this.age = age;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

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
