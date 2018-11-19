package com.teamtoast.toast.category;

import javax.persistence.*;

@Entity
public class CategoryParent {

    @Id
    @Column(name = "categoryId")
    private int categoryId;
    private int categoryParent;
    private String categoryName;
    private String parentName;
    private String categoryImage;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCategoryParent() {
        return categoryParent;
    }

    public void setCategoryParent(int categoryParent) {
        this.categoryParent = categoryParent;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }
}

