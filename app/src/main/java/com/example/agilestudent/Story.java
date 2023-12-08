package com.example.agilestudent;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Represents a story entity with attributes such as title, description, sprint, duration, purpose,
 * and completion status. Each story is associated with a user through the userId attribute.
 * The class is annotated with Room's @Entity to define its structure for database persistence.
 * Foreign key relationship is established with the User entity, ensuring data integrity.
 */
@Entity(tableName = "story", foreignKeys = @ForeignKey(entity = User.class,
                                                        parentColumns = "userId",
                                                        childColumns = "userId",
                                                        onDelete = ForeignKey.CASCADE))
public class Story {
    /**
     * The unique identifier for the story (auto-generated).
     */
    @PrimaryKey(autoGenerate = true)
    public int storyId;

    /**
     * The title of the story.
     */
    @ColumnInfo(name = "title")
    public String title;

    /**
     * The description of the story.
     */
    @ColumnInfo(name = "description")
    public String description;

    /**
     * The sprint in which the story is associated.
     */
    @ColumnInfo(name = "sprint")
    public int sprint;

    /**
     * The duration (in minutes) estimated for the story.
     */
    @ColumnInfo(name = "duration")
    public int duration;

    /**
     * The purpose or goal of the story.
     */
    @ColumnInfo(name = "purpose")
    public String purpose;

    /**
     * A flag indicating whether the story is marked as complete.
     */
    @ColumnInfo(name = "isComplete")
    public boolean isComplete;

    /**
     * The identifier of the user associated with the story.
     */
    @ColumnInfo(name = "userId")
    public int userId;

    /**
     * Initializes a new Story instance with the provided attributes.
     *
     * @param title       The title of the story.
     * @param description The description of the story.
     * @param sprint      The sprint in which the story is associated.
     * @param duration    The duration (in minutes) estimated for the story.
     * @param purpose     The purpose or goal of the story.
     * @param userId      The identifier of the user associated with the story.
     */
    public Story(String title, String description, int sprint, int duration, String purpose, int userId) {
        this.title = title;
        this.description = description;
        this.sprint = sprint;
        this.duration = duration;
        this.purpose = purpose;
        this.isComplete = false;
        this.userId = userId;
    }

    /**
     * Retrieves the unique identifier of the story.
     *
     * @return The unique identifier of the story.
     */
    public int getStoryId() {
        return storyId;
    }

    /**
     * Sets the unique identifier of the story.
     *
     * @param storyId The unique identifier to set.
     */
    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    /**
     * Retrieves the title of the story
     *
     * @return The title of the story
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the story.
     *
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retrieves the description of the story
     *
     * @return the description of the story
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the story
     *
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the story's sprint
     *
     * @return The story's sprint
     */
    public int getSprint() {
        return sprint;
    }

    /**
     * Sets the sprint of the story
     *
     * @param sprint The sprint to set.
     */
    public void setSprint(int sprint) {
        this.sprint = sprint;
    }

    /**
     * Retrieves the duration of the story
     *
     * @return The duration of the story
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets the duration of the story
     *
     * @param duration The duration to set
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Retrieves the purpose of the story
     *
     * @return The purpose of the story
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Sets the purpose of the story
     *
     * @param purpose The purpose to set.
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * Retrieves the completion status of the story
     *
     * @return true if complete, otherwise false
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * Sets the completion status of the story
     * @param complete The completion status to set.
     */
    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    /**
     * Retrieves the foreign key user identifier that this story belongs to
     *
     * @return the user identifier
     */
    public int getUserId() {
        return userId;
    }
}
