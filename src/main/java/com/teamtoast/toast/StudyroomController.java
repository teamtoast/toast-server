package com.teamtoast.toast;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
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
                result.getInt("categoryID"),
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

    //default value 수정해야함!
    @RequestMapping(value = "/studyroom", method = RequestMethod.POST)
    @ApiOperation(value = "스터디룸 생성")
    public void makeStudyroom(@RequestParam(value = "categoryID",defaultValue = "6") int categoryID,
                              @RequestParam(value = "studyroomTitle", defaultValue = "여행얘기 해요!") String studyroomTitle,
                              @RequestParam(value = "studyroomMinLevel",defaultValue = "1") int studyroomMinLevel,
                              @RequestParam(value = "studyroomTime", defaultValue = "45") int studyroomTime,
                              @RequestParam(value = "studyroomMaxUser",defaultValue = "4") int studyroomMaxUser
    ) {
        Connection connection = null;
        try {
            connection = Database.newConnection();
            try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO STUDYROOM( `categoryID`,`studyroomTitle`,`studyroomDate`,`studyroomMinLevel`,`studyroomTime`,`studyroomMaxUser`,`studyroomState`)" +
                    " VALUES(?,?,now(),?,?,?,'pending')")) {
                stmt.setInt(1, categoryID);
                stmt.setString(2, studyroomTitle);
                stmt.setInt(3, studyroomMinLevel);
                stmt.setInt(4, studyroomTime);
                stmt.setInt(5, studyroomMaxUser);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/studyroom/{studyroomID}", method = RequestMethod.GET)
    @ApiOperation(value = "스터디룸 정보")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "studyroomID", value = "스터디룸 기본키", required = true, dataType = "string", paramType = "path", defaultValue = "")
    })
    public Studyroom getStudyroom(@PathVariable String studyroomID) {
        Studyroom studyroom= null;
        Connection connection = null;
        try {
            connection = Database.newConnection();
            ResultSet result = connection.prepareStatement(
                    "SELECT * FROM STUDYROOM WHERE studyroomID = " + studyroomID).executeQuery();
            while (result.next()) {
                studyroom = load(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return studyroom;
    }


    @RequestMapping(value = "/todaystudyroom", method = RequestMethod.GET)
    @ApiOperation(value = "실시간 참여가능 스터디룸", notes = "홈화면의 실시간 참여가능 스터디룸")
    public Studyroom[] todaystudyroom() {
//        state가 'pending'인 스터디룸을 랜덤으로 3개뽑아 리턴
        return null;
    }

}