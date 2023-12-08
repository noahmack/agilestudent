package com.example.agilestudent;

import androidx.room.Database;
import androidx.room.RoomDatabase;
/**
 * The Room Database class representing the database for storing User and Story entities.
 * This class is annotated with Room's @Database annotation, specifying the entities it includes,
 * the version number, and whether to export the schema.
 */
@Database(entities = {User.class, Story.class}, version=5, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {

    /**
     * Provides access to the UserDao interface for interacting with the User entity.
     *
     * @return The UserDao instance.
     */
    public abstract UserDao userDao();

    /**
     * Provides access to the StoryDao interface for interacting with the Story entity.
     *
     * @return The StoryDao instance.
     */
    public abstract StoryDao storyDao();
}
