package com.appwithme.model;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {

    public int userCount;
    public Map<String,Boolean> users = new HashMap<>(); // 채팅방 유저들
    public Map<String,String> meetInfo = new HashMap<>(); // 모임방 정보들
    public Map<String,Comment> comments = new HashMap<>(); // 채팅방 내용들


    public static  class Comment {
        public String uid;
        public String message;
        public Object timestamp;

    }

    public Map<String, Boolean> getUsers() {
        return users;
    }

    public void setUsers(Map<String, Boolean> users) {
        this.users = users;
    }

    public Map<String, String> getMeetInfo() {

        return meetInfo;
    }

    public void setMeetInfo(Map<String, String> meetInfo) {
        this.meetInfo = meetInfo;
    }

    public Map<String, Comment> getComments() {
        return comments;
    }

    public void setComments(Map<String, Comment> comments) {
        this.comments = comments;
    }

}


