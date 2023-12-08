package com.example.agilestudent;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object (DAO) interface for the Story entity, providing methods to interact with
 * the underlying database. This interface is annotated with Room's @Dao annotation.
 */
@Dao
public interface StoryDao {
    /**
     * Retrieves a list of stories associated with a specific user.
     *
     * @param userId The identifier of the user.
     * @return A list of stories associated with the specified user.
     */
    @Query("SELECT * FROM story WHERE userId = :userId")
    List<Story> getStoriesByUserId(int userId);

    /**
     * Retrieves a list of stories associated with a specific user and matching a given purpose.
     *
     * @param userId  The identifier of the user.
     * @param purpose The purpose to filter stories.
     * @return A list of stories associated with the specified user and purpose.
     */
    @Query("SELECT * FROM story WHERE userId = :userId AND purpose LIKE LOWER(:purpose)")
    List<Story> getStoriesByPurpose(int userId, String purpose);

    /**
     * Retrieves a list of stories associated with a specific user and matching a given sprint.
     *
     * @param userId The identifier of the user.
     * @param sprint The sprint to filter stories.
     * @return A list of stories associated with the specified user and sprint.
     */
    @Query("SELECT * FROM story WHERE userId = :userId AND sprint = :sprint")
    List<Story> getStoriesBySprint(int userId, int sprint);

    /**
     * Retrieves a list of completed stories associated with a specific user.
     *
     * @param userId The identifier of the user.
     * @return A list of completed stories associated with the specified user.
     */
    @Query("SELECT * FROM story WHERE userId = :userId AND isComplete = 1")
    List<Story> getCompletedStories(int userId);

    /**
     * Inserts a new story into the database.
     *
     * @param story The story to insert.
     */
    @Insert
    void insertStory(Story story);

    /**
     * Updates an existing story in the database.
     *
     * @param story The story to update.
     */
    @Update
    void updateStory(Story story);

    /**
     * Deletes a story from the database.
     *
     * @param story The story to delete.
     */
    @Delete
    void deleteStory(Story story);
}
