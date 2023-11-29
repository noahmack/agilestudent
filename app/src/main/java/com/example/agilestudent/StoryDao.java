package com.example.agilestudent;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StoryDao {
    @Query("SELECT * FROM story WHERE userId = :userId")
    List<Story> getStoriesByUserId(int userId);

    @Insert
    void insertStory(Story story);

    @Update
    void updateStory(Story story);
}
