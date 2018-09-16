package com.teamtoast.toast;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            ResultSet result = connection.prepareStatement("SELECT * FROM categories").executeQuery();
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
                result.getInt("id"),
                result.getObject("parent", Integer.class),
                result.getString("name"),
                result.getString("imagePath")
        );
    }

    public Category[] loadArray(ResultSet result) throws SQLException {
        LinkedList<Category> categories = new LinkedList<>();
        while(result.next()) {
            categories.add(load(result));
        }
        return categories.toArray(new Category[0]);
    }

}
