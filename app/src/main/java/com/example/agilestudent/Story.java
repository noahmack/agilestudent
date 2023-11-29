package com.example.agilestudent;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "story", foreignKeys = @ForeignKey(entity = User.class,
                                                        parentColumns = "userId",
                                                        childColumns = "userId",
                                                        onDelete = ForeignKey.CASCADE))
public class Story {
    @PrimaryKey
    public int storyId;
    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "description")
    public String description;
    @ColumnInfo(name = "sprint")
    public int sprint;
    @ColumnInfo(name = "duration")
    public int duration;
    @ColumnInfo(name = "purpose")
    public String purpose;
    @ColumnInfo(name = "isComplete")
    public boolean isComplete;
    @ColumnInfo(name = "userId")
    public int userId;

    public Story(int storyId, String title, String description, int sprint, int duration, String purpose, int userId) {
        this.storyId = storyId;
        this.title = title;
        this.description = description;
        this.sprint = sprint;
        this.duration = duration;
        this.purpose = purpose;
        this.isComplete = false;
        this.userId = userId;
    }

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSprint() {
        return sprint;
    }

    public void setSprint(int sprint) {
        this.sprint = sprint;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}
