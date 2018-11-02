package com.teamtoast.toast.category;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "category_keywords")
@IdClass(CategoryKeyword.PK.class)
public class CategoryKeyword {

    @Id
    @Column(name = "categoryId")
    private int categoryId;
    @Id
    private String keyword;
    private String mean;

    public CategoryKeyword(int categoryId, String keyword, String mean) {
        this.categoryId = categoryId;
        this.keyword = keyword;
        this.mean = mean;
    }

    public int getCategoryId() {
        return categoryId;
    }


    public String getMean() {

        return mean;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }

    public static class PK implements Serializable {

        int categoryId;
        String keyword;

    }
}
