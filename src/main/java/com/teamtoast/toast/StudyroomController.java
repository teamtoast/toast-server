package com.teamtoast.toast;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.LinkedList;


@RestController
public class StudyroomController {


    @ApiOperation(value = "스터디룸 목록", notes = "categoryID에 해당하는 스터디룸 리스트를 리턴")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryID", value = "카테고리 기본키", required = true, dataType = "string", paramType = "path", defaultValue = ""),
    })
    @RequestMapping(value = "/studyrooms/{categoryID}", produces = {"application/json"}, method = RequestMethod.GET)
    public Studyroom[] studyrooms(@PathVariable String categoryID) {
        Studyroom[] arr = new Studyroom[]{};
        Connection connection = null;
        try {
            connection = Database.newConnection();
            ResultSet result = connection.prepareStatement("SELECT * FROM STUDYROOM WHERE categoryID = " + categoryID).executeQuery();
            arr = loadArray(result);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return arr;
    }


    public Studyroom load(ResultSet result) throws SQLException {
        return new Studyroom(
                result.getInt("studyroomID"),
                result.getString("studyroomTitle"),
                result.getDate("studyroomDate"),
                result.getInt("studyroomMinLevel"),
                result.getInt("studyroomTime"),
                result.getInt("studyroomMaxUser"),
                result.getString("studyroomState")
                );
    }

    public Studyroom[] loadArray(ResultSet result) throws SQLException {
        LinkedList<Studyroom> studyrooms = new LinkedList<>();
        while(result.next()) {
            studyrooms.add(load(result));
        }
        return studyrooms.toArray(new Studyroom[0]);
    }

    @RequestMapping(value = "/studyroom", method = RequestMethod.POST)
    @ApiOperation(value = "스터디룸 생성")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "studyroomTitle", value = "방이름", required = true, dataType = "string", paramType = "path", defaultValue = ""),
            @ApiImplicitParam(name = "studyroomMinLevel", value = "입장제한 레벨", required = true, dataType = "string", paramType = "path", defaultValue = "1"),
            @ApiImplicitParam(name = "studyroomTime", value = "최대시간", required = true, dataType = "string", paramType = "path", defaultValue = "30"),
            @ApiImplicitParam(name = "studyroomMaxUser", value = "인원제한", required = true, dataType = "string", paramType = "path", defaultValue = "4"),
    })
    public void makeStudyroom() {
        //studyroomDate 넣기
        //studyroomState: defaultValue = "pending"
    }

    @RequestMapping(value = "/studyroom", method = RequestMethod.GET)
    @ApiOperation(value = "스터디룸 정보")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "studyroomID", value = "스터디룸 기본키", required = true, dataType = "string", paramType = "path", defaultValue = "")
    })
    public void getStudyroom() {
//        const studyRoomInfo = {
//                studyroomID: 1,
//                studyroomTitle: "취준생들 모여서 즐겁게 얘기해요!",
//                studyroomDate: "",
//                studyroomMinLevel: 1,
//                studyroomTime: 1,
//                studyroomMaxUser: 4,
//                categoryID: 1,
//                studyroomState: "pending"
//        };
    }

    @RequestMapping(value = "/studyuser", method = RequestMethod.GET)
    @ApiOperation(value = "스터디룸 유저 정보", notes = "studyroomID에 해당하는 스터디룸의 유저리스트를 리턴")
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


}


