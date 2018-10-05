package com.teamtoast.toast;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

@RestController
public class CategoryController {

    @GetMapping("/categories")
    @ApiOperation("카테고리 목록")
    public Category[] categories() {
        Category[] arr = new Category[]{};
        Connection connection = null;
        try {
            connection = Database.newConnection();
            ResultSet result = connection.prepareStatement("SELECT * FROM STUDY_CATEGORY WHERE categoryParent is not null ").executeQuery();
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

    public Category load(ResultSet result) throws SQLException {
        return new Category(
                result.getInt("categoryID"),
                result.getObject("categoryParent", Integer.class),
                result.getString("categoryName")
        );
    }

    public Category[] loadArray(ResultSet result) throws SQLException {
        LinkedList<Category> categories = new LinkedList<>();
        while(result.next()) {
            categories.add(load(result));
        }
        return categories.toArray(new Category[0]);
    }

    @RequestMapping(value = "/category", method = RequestMethod.GET)
    @ApiOperation(value = "카테고리 정보", notes = "categoryID에 해당하는 카테고리 정보를 리턴")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryID", value = "카테고리 기본키", required = true, dataType = "string", paramType = "path", defaultValue = "")
    })
    public Category category() {
//        {
//            categroyID: ,
//            categoryName: ,
//            categoryParent 가 null이 아닐경우 부모 categoryName 까지 함께 리턴할것
//        }
        return null;
    }

    @RequestMapping(value = "/keyword", method = RequestMethod.GET)
    @ApiOperation(value = "카테고리 관련 키워드", notes = "categoryID에 해당하는 카테고리관련 키워드리스트를 리턴")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryID", value = "카테고리 기본키", required = true, dataType = "string", paramType = "path", defaultValue = "")
    })
    public void getKeywords() {
//        study_category_keyword 테이블에서 가져오기 랜덤 5개
    }

}
