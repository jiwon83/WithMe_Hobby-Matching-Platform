package com.withme.model;

public class MeetInfo {

    public int meetId;
    public String imgUrl;
    public String title;
    //public Date meetDate;
    public int meetAge;
    public int numMember;
    public int meetGen;



    public MeetInfo() {
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

    public int getMeetGen() {
        return meetGen;
    }

    public void setMeetGen(int meetGen) {
        this.meetGen = meetGen;
    }
}
