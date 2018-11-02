package com.teamtoast.toast.category;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryKeywordRepository extends CrudRepository<CategoryKeyword, Integer> {

    List<CategoryKeyword> findByCategoryId(int categoryId);

}
