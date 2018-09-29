package com.teamtoast.toast;

public class Category {

    private int categoryID;
    private Integer categoryParent;
    private String categoryName;

    public Category(int categoryID, Integer categoryParent, String categoryName) {
        this.categoryID = categoryID;
        this.categoryParent = categoryParent;
        this.categoryName = categoryName;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public Integer getCategoryParent() {
        return categoryParent;
    }

    public void setCategoryParent(Integer categoryParent) {
        this.categoryParent = categoryParent;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String name) {
        this.categoryName = categoryName;
    }

}
