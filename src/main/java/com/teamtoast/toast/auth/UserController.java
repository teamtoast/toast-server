package com.teamtoast.toast.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamtoast.toast.Application;
import com.teamtoast.toast.auth.exceptions.AuthenticationException;
import com.teamtoast.toast.auth.exceptions.ConflictException;
import com.teamtoast.toast.auth.exceptions.PlatformException;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Value("${token-secret}")
    private String tokenSecret;
    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(tokenSecret);
    }

    @PostMapping(value = "/users", produces = "application/json")
    public @ResponseBody
    User.CreateResponse createUser(@RequestBody User.CreateRequest info)
            throws SQLException, AuthenticationException, PlatformException, ConflictException {

        long id = userService.createUser(info.type,
                getPlatformId(info.token, info.type),
                info.nickname,
                info.contact,
                info.gender,
                info.age);
        return new User.CreateResponse(newToken(id, info.type));
    }

    public String getPlatformId(String token, User.AccountType type) throws AuthenticationException, PlatformException {
        switch (type) {
            case KAKAO:
                return getKakaoId(token);
            case FACEBOOK:
                return getFacebookId(token);
        }

        return "";
    }

    public String getKakaoId(String token) throws AuthenticationException, PlatformException {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://kapi.kakao.com/v2/user/me")
                    .header("Authorization", "Bearer " + token)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            JsonNode root = new ObjectMapper().readTree(body);
            return root.get("id").asText();
        } catch (IOException e) {
            e.printStackTrace();
            throw new PlatformException();
        }
    }

    public String getFacebookId(String token) throws PlatformException {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://graph.facebook.com/me?fields=id,email,gender,birthday,age_range&access_token=" + token)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            System.out.println(body);
            JsonNode root = new ObjectMapper().readTree(body);
            return root.get("id").asText();
        } catch (IOException e) {
            e.printStackTrace();
            throw new PlatformException();
        }
    }

    public String newToken(long id, User.AccountType type) {
        String typeString = getType(type);
        return JWT.create()
                .withClaim("id", id)
                .withClaim("type", typeString)
                .sign(algorithm);
    }

    public String getType(User.AccountType type) {
        String typeString = "";
        switch (type) {
            case KAKAO:
                typeString = "kakao";
                break;
            case FACEBOOK:
                typeString = "facebook";
                break;
            case GOOGLE:
                typeString = "google";
                break;
            case GITHUB:
                typeString = "github";
                break;
        }
        return typeString;
    }

    @RequestMapping(value = "/studyuser", method = RequestMethod.GET)
    @ApiOperation(value = "스터디룸 유저 정보", notes = "studyroomID에 해당하는 스터디룸의 유저리스트")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "studyroomID", value = "스터디룸 기본키", required = true, dataType = "string", paramType = "path", defaultValue = "")
    })
    public void getStudyroomUser() {
//        studyRoomUserList = [{
//            userID: "asdf@naver.com",
//                    userNickname: "user1",
//                    userProfilePath: " ",
//                    userLevel: 15,
//                    userState: 'wait'
//        }, {
//            userID: "asdf@naver.com",
//                    userNickname: "user2",
//                    userProfilePath: " ",
//                    userLevel: 12,
//                    userState: 'ready'
//        }]
    }

    @RequestMapping(value = "/userrank", method = RequestMethod.GET)
    @ApiOperation(value = "유저 랭킹", notes = "유저 랭킹 리스트")
    public void getUserRank() {
//        [{
//            profileImage: "",
//            userNickname: "Anna",
//            userLevel: 1
//        },
//        {
//            profileImage: "",
//            userNickname: "Anna",
//            userLevel: 1
//        }]
    }


}
