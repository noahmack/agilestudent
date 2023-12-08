package com.example.agilestudent;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object (DAO) interface for User entities.
 * Contains methods annotated with Room's @Query, @Insert, @Update, and @Delete annotations
 * for database operations related to the User entity.
 */
@Dao
public interface UserDao {
    /**
     * Retrieves a list of all users from the database.
     *
     * @return List of all users in the database.
     */
    @Query("SELECT * FROM user")
    List<User> getAll();

    /**
     * Finds a user with the given username and password.
     *
     * @param username The username of the user to find.
     * @param password The password of the user to find.
     * @return The user with the given username and password, or null if not found.
     */
    @Query("SELECT * FROM user WHERE username = :username AND password = :password")
    User findUser(String username, String password);

    /**
     * Retrieves the JSON representation of a user's story list.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The JSON representation of the user's story list.
     */
    @Query("SELECT storyList FROM user WHERE username = :username AND password = :password")
    String getStories(String username, String password);

    /**
     * Retrieves the sprint duration of a user.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The sprint duration of the user.
     */
    @Query("SELECT sprintDuration FROM user WHERE username = :username AND password = :password")
    int getSprintDuration(String username, String password);

    /**
     * Retrieves the number of stories associated with a user.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The number of stories associated with the user.
     */
    @Query("SELECT numStories FROM user WHERE username = :username AND password = :password")
    int getNumStories(String username, String password);

    /**
     * Updates the JSON representation of a user's story list.
     *
     * @param storiesJson The updated JSON representation of the user's story list.
     * @param username    The username of the user.
     * @param password    The password of the user.
     */
    @Query("UPDATE user SET storyList = :storiesJson WHERE username = :username AND password = :password")
    void updateStories(String storiesJson, String username, String password);

    /**
     * Updates a user entity in the database.
     *
     * @param user The updated user entity.
     */
    @Update
    void updateUser(User user);

    /**
     * Inserts a new user entity into the database.
     *
     * @param user The user entity to insert.
     */
    @Insert
    void insert(User user);

    /**
     * Deletes a user entity from the database.
     *
     * @param user The user entity to delete.
     */
    @Delete
    void delete(User user);
}
