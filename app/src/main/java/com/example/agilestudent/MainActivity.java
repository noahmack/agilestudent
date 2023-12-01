package com.example.agilestudent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    //Components
    private EditText username;
    private EditText password;
    private Button loginButton;
    private Button newStoryButton;
    private Button createStoryBackButton;
    private Button createStoryButton;
    private ListView storyListView;
    private ProgressBar progressBar;
    private TextView welcomeMessage;
    private TextInputEditText storyTitle;
    private TextInputEditText storyDescription;
    private TextInputEditText storyDuration;
    private TextInputEditText storyPurpose;

    //Database
    private UserDatabase db;

    private User activeUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.usernameEntryBox);
        password = findViewById(R.id.passwordEntryBox);
        loginButton = findViewById(R.id.loginButton);

        db = Room.databaseBuilder(getApplicationContext(), UserDatabase.class, "user").allowMainThreadQueries().fallbackToDestructiveMigration().build();


    }

    public void switchToDashboard() {
        setContentView(R.layout.layout_dashboard);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(90);

        welcomeMessage = findViewById(R.id.welcomeMessage);
        welcomeMessage.setText("Welcome, " + activeUser.getUsername().toString());

        newStoryButton = findViewById(R.id.newStoryButton);

        storyListView = findViewById(R.id.storyListView);
        List<Story> storyList = db.storyDao().getStoriesByUserId(activeUser.getUserId());

        String[] storyArray = new String[storyList.size()];
        for(int i = 0; i < storyList.size(); i++) {
            storyArray[i] = "STORY" + storyList.get(i).getStoryId() + ": " + storyList.get(i).getTitle();
        }
        ArrayAdapter<String> stories = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, storyArray);
        stories.setDropDownViewResource(R.layout.layout_dashboard);
        storyListView.setAdapter(stories);
    }

    public void switchToCreateStory() {
        setContentView(R.layout.layout_create_story);
        storyTitle = findViewById(R.id.storyTitleEditStory);
        storyDescription = findViewById(R.id.storyDescriptionEditText);
        storyDuration = findViewById(R.id.storyDurationEditStory);
        storyPurpose = findViewById(R.id.storyPurposeEditStory);
        createStoryButton = findViewById(R.id.editStoryButton);
        createStoryBackButton = findViewById(R.id.editStoryBackButton);
    }

    public void onCreateStoryClicked(View view) {
        String title = storyTitle.getText().toString();
        String description = storyDescription.getText().toString();
        String duration = storyDuration.getText().toString();
        int intDuration = Integer.parseInt(duration);
        String purpose = storyPurpose.getText().toString();

        Story story = new Story(title, description, 1, intDuration, purpose, activeUser.getUserId());

        Log.d("Story Insert", "Insert story with userId: " + story.getUserId());
        db.storyDao().insertStory(story);


        switchToDashboard();
    }

    public void onBackClicked(View view) {
        switchToDashboard();
    }

    public void onNewStoryClicked(View view) {
        switchToCreateStory();
    }

    public void onLoginClicked(View view) {
        List<User> users = db.userDao().getAll();
        User user = new User(username.getText().toString(), password.getText().toString());
        if(users.contains(user)) {
            User dbUser = users.get(users.indexOf(user));
            if(!dbUser.getPassword().equals(user.getPassword())) {
                Toast.makeText(getApplicationContext(), "Incorrect password, try again.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("DB", "User " + user.getUsername() + " already exists under id " + user.getUserId());
                Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();
                activeUser = db.userDao().findUser(user.getUsername(), user.getPassword());;
                switchToDashboard();
            }
        } else {
            Toast.makeText(getApplicationContext(), "New user " + user.getUsername() + " created, logging in...", Toast.LENGTH_SHORT).show();
            db.userDao().insert(user);
            activeUser = db.userDao().findUser(user.getUsername(), user.getPassword());
            Log.d("DB", db.userDao().getAll().get(0).toString());
            switchToDashboard();
        }


    }
}