package com.example.agilestudent;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE username = :username AND password = :password")
    User findUser(String username, String password);

    @Query("SELECT storyList FROM user WHERE username = :username AND password = :password")
    String getStories(String username, String password);

    @Query("SELECT sprintDuration FROM user WHERE username = :username AND password = :password")
    int getSprintDuration(String username, String password);

    @Query("SELECT numStories FROM user WHERE username = :username AND password = :password")
    int getNumStories(String username, String password);

    @Query("UPDATE user SET storyList = :storiesJson WHERE username = :username AND password = :password")
    void updateStories(String storiesJson, String username, String password);

    @Update
    void updateUser(User user);

    @Insert
    void insert(User user);

    @Delete
    void delete(User user);
}
