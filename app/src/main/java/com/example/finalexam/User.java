package com.example.finalexam;

import java.io.Serializable;
import java.util.Map;

public class User implements Serializable {
    private String uid;
    private String name;
    private Map<String, Boolean> likes;
    private Map<String, Boolean> history;
    private Map<String, Map<String, Boolean>> shared;

    public Map<String, Map<String, Boolean>> getShared() {
        return shared;
    }

    public void setShared(Map<String, Map<String, Boolean>> shared) {
        this.shared = shared;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public Map<String, Boolean> getHistory() {
        return history;
    }

    public void setHistory(Map<String, Boolean> history) {
        this.history = history;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", likes=" + likes +
                ", history=" + history +
                '}';
    }
}
