package com.cookandroid.withmetabbar.model;

import java.util.Date;

public class Meet {
    public String uid;
    public int meetId;
    public String imgUrl;
    public String title;
    //public Date meetDate;
    public int meetAge;
    public int numMember;
    //public String meetGen;
    public String content;
    //public String hash;
    //public String cate;


    public Meet() {
    }

    public Meet(String uid, int meetId, String imgUrl, String title, int meetAge, int numMember, String content) {
        this.uid=uid;
        this.meetId = meetId;
        this.imgUrl = imgUrl;
        this.title = title;
        //this.meetDate = meetDate;
        this.meetAge = meetAge;
        this.numMember = numMember;
        //this.meetGen = meetGen;
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getMeetId() {
        return meetId;
    }

    public void setMeetId(int meetId) {
        this.meetId = meetId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMeetAge() {
        return meetAge;
    }

    public void setMeetAge(int meetAge) {
        this.meetAge = meetAge;
    }

    public int getNumMember() {
        return numMember;
    }

    public void setNumMember(int numMember) {
        this.numMember = numMember;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}