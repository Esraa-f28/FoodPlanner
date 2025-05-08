package com.example.foodplanner.model.pojo;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String email;
    private boolean isGuest;

    // Default constructor for Firebase
    public User() {}

    public User(String uid, String email, boolean isGuest) {
        this.uid = uid;
        this.email = email;
        this.isGuest = isGuest;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }
}