package com.teamtoast.toast.category;

import com.google.common.collect.Iterables;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Random;

@RestController
public class CategoryController {

    @Autowired
    private CategoryRepository repository;
    @Autowired
    private CategoryKeywordRepository keywordRepository;

    @GetMapping("/categories")
    @ApiOperation("카테고리 목록")
    public Category[] categories() {
        return Iterables.toArray(repository.findAll(), Category.class);
    }

    @RequestMapping(value = "/categories/{categoryID}", method = RequestMethod.GET)
    @ApiOperation(value = "카테고리 정보", notes = "categoryID에 해당하는 카테고리 정보")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryID", value = "카테고리 기본키", required = true, dataType = "string", paramType = "path", defaultValue = "")
    })
    public Category category(@PathVariable int categoryID) {
        return repository.findById(categoryID).get();
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