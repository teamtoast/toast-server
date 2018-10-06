package com.teamtoast.toast;

import java.util.Date;

public class Studyroom {

    public int studyroomID;
    public int categoryID;
    public String studyroomTitle;
    public Date studyroomDate;
    public int studyroomMinLevel;
    public int studyroomTime;
    public int studyroomMaxUser;
    public String studyroomState;

    public Studyroom(int studyroomID, int categoryID, String studyroomTitle, Date studyroomDate, int studyroomMinLevel, int studyroomTime, int studyroomMaxUser, String studyroomState) {
        this.studyroomID = studyroomID;
        this.categoryID = categoryID;
        this.studyroomTitle = studyroomTitle;
        this.studyroomDate = studyroomDate;
        this.studyroomMinLevel = studyroomMinLevel;
        this.studyroomTime = studyroomTime;
        this.studyroomMaxUser = studyroomMaxUser;
        this.studyroomState = studyroomState;
    }

    public int getStudyroomID() {
        return studyroomID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public String getStudyroomTitle() {
        return studyroomTitle;
    }

    public Date getStudyroomDate() {
        return studyroomDate;
    }

    public int getStudyroomMinLevel() {
        return studyroomMinLevel;
    }

    public int getStudyroomTime() {
        return studyroomTime;
    }

    public int getStudyroomMaxUser() {
        return studyroomMaxUser;
    }

    public String getStudyroomState() {
        return studyroomState;
    }

    public void setStudyroomID(int studyroomID) {
        this.studyroomID = studyroomID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public void setStudyroomTitle(String studyroomTitle) {
        this.studyroomTitle = studyroomTitle;
    }

    public void setStudyroomDate(Date studyroomDate) {
        this.studyroomDate = studyroomDate;
    }

    public void setStudyroomMinLevel(int studyroomMinLevel) {
        this.studyroomMinLevel = studyroomMinLevel;
    }

    public void setStudyroomTime(int studyroomTime) {
        this.studyroomTime = studyroomTime;
    }

    public void setStudyroomMaxUser(int studyroomMaxUser) {
        this.studyroomMaxUser = studyroomMaxUser;
    }

    public void setStudyroomState(String studyroomState) {
        this.studyroomState = studyroomState;
    }
}
