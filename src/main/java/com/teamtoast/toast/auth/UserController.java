package com.teamtoast.toast.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamtoast.toast.auth.bodys.*;
import com.teamtoast.toast.auth.exceptions.AuthenticationException;
import com.teamtoast.toast.auth.exceptions.ConflictException;
import com.teamtoast.toast.auth.exceptions.PlatformException;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.sql.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;

    @PostMapping(value = "/users", produces = "application/json")
    public @ResponseBody
    TokenResponse createToastUser(@RequestBody CreateUserRequest info)
            throws SQLException, AuthenticationException, PlatformException, ConflictException {

        long id = userService.createUser(info.email, info.password,
                info.nickname,
                info.contact,
                info.gender,
                info.age);
        return new TokenResponse(tokenService.newToken(id, User.AccountType.TOAST));
    }

    @PostMapping(value = "/users/sns", produces = "application/json")
    public @ResponseBody
    TokenResponse createSNSUser(@RequestBody CreateUserBySNSRequest info)
            throws SQLException, AuthenticationException, PlatformException, ConflictException {

        long id = userService.createUser(info.type,
                getPlatformId(info.token, info.type),
                info.nickname,
                info.contact,
                info.gender,
                info.age);
        return new TokenResponse(tokenService.newToken(id, info.type));
    }

    @GetMapping(value = "/users/{id}", produces = "application/json")
    public @ResponseBody User getMe(@PathVariable("id") long id) {
        return userService.getUser(id);
    }

    @GetMapping(value = "/me", produces = "application/json")
    public @ResponseBody User getMe(@RequestHeader("Authorization") String authorization) throws AuthenticationException {
        if(authorization != null) {
            String[] splited = authorization.split("Bearer ");
            if(splited.length > 1) {
                return tokenService.verifyToken(splited[1]);
            }
        }

        throw new AuthenticationException();
    }

    @PostMapping(value = "/token", produces = "application/json")
    public @ResponseBody TokenResponse createToken(@RequestBody LoginRequest params) throws AuthenticationException {
        return new TokenResponse(tokenService.newToken(
                userService.getUserByIdAndPassword(
                        params.getEmail(),
                        params.getPassword()),
                User.AccountType.TOAST));
    }

    @PostMapping(value = "/token/sns", produces = "application/json")
    public @ResponseBody TokenResponse createToken(@RequestBody SNSLoginRequest params) throws AuthenticationException, PlatformException {

        String id = getFacebookId(params.getToken());

        return new TokenResponse(tokenService.newToken(
                userService.getUserBySNSIdAndSNSType(id, params.getType()).getId(),
                User.AccountType.TOAST));
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
            return root.get("email").asText();
        } catch (IOException e) {
            e.printStackTrace();
            throw new PlatformException();
        }
    }

    public String getFacebookId(String token) throws PlatformException {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://graph.facebook.com/me?fields=id&access_token=" + token)
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
