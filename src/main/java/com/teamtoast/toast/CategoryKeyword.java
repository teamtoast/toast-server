package com.teamtoast.toast;

public class CategoryKeyword {

    private int categoryID;
    private String keyword;
    private String mean;

    public CategoryKeyword(int categoryID, String keyword, String mean) {
        this.categoryID = categoryID;
        this.keyword = keyword;
        this.mean = mean;
    }

    public int getCategoryID() {
        return categoryID;
    }


    public String getMean() {

        return mean;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }
}
