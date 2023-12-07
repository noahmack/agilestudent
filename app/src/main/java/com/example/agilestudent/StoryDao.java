package com.example.agilestudent;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StoryDao {
    @Query("SELECT * FROM story WHERE userId = :userId")
    List<Story> getStoriesByUserId(int userId);

    @Query("SELECT * FROM story WHERE userId = :userId AND purpose LIKE LOWER(:purpose)")
    List<Story> getStoriesByPurpose(int userId, String purpose);

    @Query("SELECT * FROM story WHERE userId = :userId AND sprint = :sprint")
    List<Story> getStoriesBySprint(int userId, int sprint);

    @Query("SELECT * FROM story WHERE userId = :userId AND isComplete = 1")
    List<Story> getCompletedStories(int userId);

    @Insert
    void insertStory(Story story);

    @Update
    void updateStory(Story story);

    @Delete
    void deleteStory(Story story);
}
