package com.teamtoast.toast;

public class Category {

    private int categoryID;
    private Integer categoryParent;
    private String categoryName;
    private String parentName;

    public Category(int categoryID, String categoryName, Integer categoryParent, String parentName) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.categoryParent = categoryParent;
        this.parentName = parentName;

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

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

}
