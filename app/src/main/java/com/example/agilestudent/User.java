package com.example.agilestudent;

import android.service.autofill.FillEventHistory;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity class representing a User in the database.
 * This class is annotated with Room's @Entity annotation, specifying the table name.
 */
@Entity(tableName = "user")
public class User {
    /**
     * Primary key for the User entity. Auto-generates unique IDs.
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "userId")
    public int userId;

    /**
     * Username associated with the User.
     */
    @ColumnInfo(name = "username")
    public String username;

    /**
     * Password associated with the User.
     */
    @ColumnInfo(name = "password")
    public String password;

    /**
     * Number of stories associated with the User.
     */
    @ColumnInfo(name = "numStories")
    public int numStories;

    /**
     * JSON representation of the User's story list.
     */
    @ColumnInfo(name = "storyList")
    public String storyListJson;

    /**
     * Duration of the User's sprint.
     */
    @ColumnInfo(name = "sprintDuration")
    public int sprintDuration;

    /**
     * Constructor for creating a User instance with a username and password.
     *
     * @param username      The username for the User.
     * @param password      The password for the User.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.numStories = 0;
        this.storyListJson = "";
        this.sprintDuration = 14;
    }

    /**
     * Getter method for retrieving the User's ID.
     *
     * @return The User's ID.
     */
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Getter method for retrieving the User's username.
     *
     * @return The User's username.
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter method for retrieving the User's password.
     *
     * @return The User's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Unused method.
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Unused method.
     */
    public int getNumStories() {
        return numStories;
    }

    /**
     * Unused method.
     * @param numStories
     */
    public void setNumStories(int numStories) {
        this.numStories = numStories;
    }

    /**
     * Unused method.
     */
    public String getStoryListJson() {
        return storyListJson;
    }

    /**
     * Unused method.
     * @param storyListJson
     */
    public void setStoryListJson(String storyListJson) {
        this.storyListJson = storyListJson;
    }

    /**
     * Unused method.
     */
    public int getSprintDuration() {
        return sprintDuration;
    }

    /**
     * Unused method.
     * @param sprintDuration
     */
    public void setSprintDuration(int sprintDuration) {
        this.sprintDuration = sprintDuration;
    }

    /**
     * Overrides the equals method to compare User objects based on their username.
     *
     * @param o The object to compare with.
     * @return True if the usernames are equal; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if(o.getClass() != this.getClass()) return false;
        if(((User)o).getUsername().equals(this.getUsername())) return true;
        return false;
    }

    /**
     * Overrides the toString method to provide a string representation of the User object.
     *
     * @return A string representation of the User object.
     */
    public String toString() {
        return username + " " + password;
    }

}
