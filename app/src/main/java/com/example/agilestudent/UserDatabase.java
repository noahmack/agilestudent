package com.example.agilestudent;

import androidx.room.Database;
import androidx.room.RoomDatabase;
@Database(entities = {User.class, Story.class}, version=2, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract StoryDao storyDao();
}
