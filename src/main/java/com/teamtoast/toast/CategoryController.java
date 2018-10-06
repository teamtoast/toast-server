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
            ResultSet result = connection.prepareStatement(
                    "SELECT STUDY_CATEGORY.*, PARENT_CATEGORY.categoryName as parentName FROM STUDY_CATEGORY  JOIN STUDY_CATEGORY AS PARENT_CATEGORY  ON PARENT_CATEGORY.categoryID = STUDY_CATEGORY.categoryParent   WHERE STUDY_CATEGORY.categoryParent is not null").executeQuery();
            arr = loadArray(result);
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

        return arr;
    }

    public Category load(ResultSet result) throws SQLException {
        return new Category(
                result.getInt("categoryID"),
                result.getString("categoryName"),
                result.getObject("categoryParent", Integer.class),
                result.getString("parentName")
        );
    }

    public Category[] loadArray(ResultSet result) throws SQLException {
        LinkedList<Category> categories = new LinkedList<>();
        while (result.next()) {
            categories.add(load(result));
        }
        return categories.toArray(new Category[0]);
    }

    @RequestMapping(value = "/category/{categoryID}", method = RequestMethod.GET)
    @ApiOperation(value = "카테고리 정보", notes = "categoryID에 해당하는 카테고리 정보")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryID", value = "카테고리 기본키", required = true, dataType = "string", paramType = "path", defaultValue = "")
    })
    public Category category(@PathVariable String categoryID) {
        Category category= null;
        Connection connection = null;
        try {
            connection = Database.newConnection();
            ResultSet result = connection.prepareStatement(
                    "SELECT STUDY_CATEGORY.*, PARENT_CATEGORY.categoryName as parentName FROM STUDY_CATEGORY JOIN STUDY_CATEGORY AS PARENT_CATEGORY ON PARENT_CATEGORY.categoryID = STUDY_CATEGORY.categoryParent WHERE STUDY_CATEGORY.categoryID = " + categoryID).executeQuery();
            while (result.next()) {
                category = load(result);
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

        return category;
    }

    @RequestMapping(value = "/keyword", method = RequestMethod.GET)
    @ApiOperation(value = "카테고리 관련 키워드", notes = "categoryID에 해당하는 카테고리 관련 키워드리스트")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryID", value = "카테고리 기본키", required = true, dataType = "string", paramType = "path", defaultValue = "")
    })
    public void getKeywords() {
//        study_category_keyword 테이블에서 가져오기 랜덤 5개
    }

    @RequestMapping(value = "/todaycategory", method = RequestMethod.GET)
    @ApiOperation(value = "오늘의 인기 카테고리", notes = "홈화면의 오늘의 인기 카테고리 3개")
    public void todayCategory() {
//        스터디룸 수가 많은 상위 3개 카테고리 리턴
    }

}
