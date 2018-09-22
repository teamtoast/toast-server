package com.teamtoast.toast.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamtoast.toast.Application;
import com.teamtoast.toast.Database;
import com.teamtoast.toast.auth.exceptions.AuthenticationException;
import com.teamtoast.toast.auth.exceptions.ConflictException;
import com.teamtoast.toast.auth.exceptions.PlatformException;
import okhttp3.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.*;
import java.sql.Connection;

@RestController
public class UserController {

    private static Algorithm algorithm;

    @PostMapping(value = "/users", produces = "application/json")
    public @ResponseBody
    User.CreateResponse createUser(@RequestBody User.CreateRequest info) throws SQLException, AuthenticationException, PlatformException, ConflictException {
        long id = 0;
        Connection connection = null;
        try {
            String platformId = getPlatformId(info.token, info.type);
            connection = Database.newConnection();
            connection.setAutoCommit(false);

            PreparedStatement stmt = connection.prepareStatement("INSERT INTO `users` (`nickname`, `contact`, `gender`, `age`) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, info.nickname);
            stmt.setString(2, info.contact);
            stmt.setString(3, info.gender == User.Gender.MALE ? "male" : "female");
            stmt.setInt(4, info.age);

            stmt.execute();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.first();
            id = rs.getLong(1);

            createAccountInfo(connection, id, platformId, info.type);
            connection.commit();
        } catch (SQLException e) {
            if(e.getErrorCode() == 1062)
                throw new ConflictException("Already registered.");

            if(connection != null)
                connection.rollback();
            throw e;
        } finally {
            if(connection != null)
                connection.close();
        }

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
            System.out.println(body);
            JsonNode root = new ObjectMapper().readTree(body);
            return root.get("id").asText();
        } catch (IOException e) {
            e.printStackTrace();
            throw new PlatformException();
        }
    }

    public String getFacebookId(String token) throws AuthenticationException, PlatformException {
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

    public void createAccountInfo(Connection connection, long id, String platformId, User.AccountType type) throws SQLException {
        PreparedStatement stmt = null;
        String typeString = getType(type);
        stmt = connection.prepareStatement("INSERT INTO `sns_accounts` (`user`, `sns_id`, `sns_type`) VALUES (?, ?, ?)");
        stmt.setLong(1, id);
        stmt.setString(2, platformId);
        stmt.setString(3, typeString);
        stmt.execute();
    }

    public static void initAlgorithm() {
        algorithm = Algorithm.HMAC256(Application.config.tokenSecret);
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

}
