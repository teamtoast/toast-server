package com.teamtoast.toast.category;

import com.google.common.collect.Iterables;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
public class CategoryController {

    @Autowired
    private CategoryRepository repository;
    @Autowired
    private CategoryKeywordRepository keywordRepository;

    @Autowired
    private JdbcTemplate jdbc;

    // ModelAttribute 설정으로 생성자 없이 진행
    @ModelAttribute("categoryParent")
    public CategoryParent getCategoryParent() {
        return new CategoryParent();
    }

    // CategoryParent 객체 리스트로 반환 (일단 JPA 안 쓰고 Query 하드코딩)
    @GetMapping("/categories")
    @ApiOperation("카테고리 목록")
    public List<CategoryParent> categories() {
        String query = "SELECT C.categoryId, C.categoryParent, C.categoryName, P.categoryName as parentName, C.categoryImage FROM categories as C, categories as P WHERE C.categoryParent = P.categoryId AND C.categoryParent IS NOT NULL";
        return jdbc.query(query, new BeanPropertyRowMapper<>(CategoryParent.class));
    }

    @RequestMapping(value = "/categories/{categoryID}", method = RequestMethod.GET)
    @ApiOperation(value = "카테고리 정보", notes = "categoryID에 해당하는 카테고리 정보")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryID", value = "카테고리 기본키", required = true, dataType = "string", paramType = "path", defaultValue = "")
    })
    public CategoryParent category(@PathVariable int categoryID) {
        String query = "SELECT C.categoryId, C.categoryParent, C.categoryName, P.categoryName as parentName, C.categoryImage FROM categories as C, categories as P WHERE C.categoryParent = P.categoryId AND C.categoryParent IS NOT NULL AND C.categoryId = " + categoryID;
        List<CategoryParent> list = jdbc.query(query, new BeanPropertyRowMapper<>(CategoryParent.class));
        if(list == null || list.size() == 0) return null;
        else return list.get(0);
    }

    @RequestMapping(value = "/categories/{categoryId}/keywords", method = RequestMethod.GET)
    @ApiOperation(value = "카테고리 관련 키워드", notes = "categoryID에 해당하는 카테고리 관련 키워드리스트")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "카테고리 기본키", required = true, dataType = "string", paramType = "path", defaultValue = "")
    })
    public CategoryKeyword[] getKeywords(@PathVariable int categoryId) {
        Random random = new Random();
        CategoryKeyword[] keywords = Iterables.toArray(keywordRepository.findByCategoryId(categoryId), CategoryKeyword.class);

        int size = Math.min(keywords.length, 4);
        int[] indices = new int[size];
        for(int i = 0; i < size; i++) {
            indices[i] = random.nextInt(keywords.length - i);
        }

        Arrays.sort(indices);
        for(int i = 1; i < size; i++) {
            for(int j = 0; j < i; j++) {
                if(indices[i] == indices[j])
                    indices[i]++;
            }
        }

        CategoryKeyword[] result = new CategoryKeyword[size];
        for(int i = 0; i < size; i++) {
            result[i] = keywords[indices[i]];
        }

        return result;
    }


    @RequestMapping(value = "/todaycategory", method = RequestMethod.GET)
    @ApiOperation(value = "오늘의 인기 카테고리", notes = "홈화면의 오늘의 인기 카테고리 3개")
    public void todayCategory() {
//        스터디룸 수가 많은 상위 3개 카테고리 리턴
    }

}