package com.example.agilestudent;

import android.service.autofill.FillEventHistory;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "userId")
    public int userId;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "numStories")
    public int numStories;

    @ColumnInfo(name = "storyList")
    public String storyListJson;

    @ColumnInfo(name = "sprintDuration")
    public int sprintDuration;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.numStories = 0;
        this.storyListJson = "";
        this.sprintDuration = 14;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getNumStories() {
        return numStories;
    }

    public void setNumStories(int numStories) {
        this.numStories = numStories;
    }

    public String getStoryListJson() {
        return storyListJson;
    }

    public void setStoryListJson(String storyListJson) {
        this.storyListJson = storyListJson;
    }

    public int getSprintDuration() {
        return sprintDuration;
    }

    public void setSprintDuration(int sprintDuration) {
        this.sprintDuration = sprintDuration;
    }

    @Override
    public boolean equals(Object o) {
        if(o.getClass() != this.getClass()) return false;
        if(((User)o).getUsername().equals(this.getUsername())) return true;
        return false;
    }

    public String toString() {
        return username + " " + password;
    }

}
