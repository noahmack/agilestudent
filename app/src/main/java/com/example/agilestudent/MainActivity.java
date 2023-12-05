package com.example.agilestudent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import android.widget.ImageView;
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
    private Button editStoryButton;
    private Button editStoryBackButton;
    private Button completeStoryButton;
    private ImageView highFive;
    private ListView storyListView;
    private ProgressBar progressBar;
    private TextView welcomeMessage;
    private TextInputEditText storyTitle;
    private TextInputEditText storyDescription;
    private TextInputEditText storyDuration;
    private TextInputEditText storyPurpose;
    private TextInputEditText editStoryTitle;
    private TextInputEditText editStoryDescription;
    private TextInputEditText editStoryDuration;
    private TextInputEditText editStoryPurpose;

    private Story editStory;

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


        welcomeMessage = findViewById(R.id.welcomeMessage);
        welcomeMessage.setText("Welcome, " + activeUser.getUsername().toString());

        newStoryButton = findViewById(R.id.newStoryButton);

        storyListView = findViewById(R.id.storyListView);
        List<Story> storyList = db.storyDao().getStoriesByUserId(activeUser.getUserId());

        int completed = 0;
        for(Story s: storyList) {
            if(s.isComplete()) completed++;
        }
        progressBar.setProgress((completed * 100) / storyList.size());
        String[] storyArray = new String[storyList.size()];
        for(int i = 0; i < storyList.size(); i++) {
            storyArray[i] = "STORY" + storyList.get(i).getStoryId() + ": " + storyList.get(i).getTitle();
            if(storyList.get(i).isComplete()) {
                storyArray[i] += " --> COMPLETED";
            }
        }
        ArrayAdapter<String> stories = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, storyArray);
        stories.setDropDownViewResource(R.layout.layout_dashboard);
        storyListView.setAdapter(stories);
        storyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String storyNum = ((TextView)view).getText().toString();
                storyNum = storyNum.substring(0, storyNum.indexOf(':'));
                storyNum = storyNum.substring(storyNum.indexOf('Y') + 1);

                for(Story s : storyList) {
                    if(s.getStoryId() == Integer.parseInt(storyNum)) {
                        switchToEditStory(s);
                    }
                }
            }
        });
    }

    public void switchToCreateStory() {
        setContentView(R.layout.layout_create_story);
        storyTitle = findViewById(R.id.storyTitleCreateStory);
        storyDescription = findViewById(R.id.storyDescriptionCreateStory);
        storyDuration = findViewById(R.id.storyDurationCreateStory);
        storyPurpose = findViewById(R.id.storyPurposeCreateStory);
        createStoryButton = findViewById(R.id.createStoryButton);
        createStoryBackButton = findViewById(R.id.createStoryBackButton);
    }

    public void switchToEditStory(Story storyToEdit) {
        setContentView(R.layout.layout_edit_story);
        editStoryTitle = findViewById(R.id.storyTitleEditStory);
        editStoryDescription = findViewById(R.id.storyDescriptionEditStory);
        editStoryDuration = findViewById(R.id.storyDurationEditStory);
        editStoryPurpose = findViewById(R.id.storyPurposeEditStory);
        editStoryButton = findViewById(R.id.editStoryButton);
        editStoryBackButton = findViewById(R.id.editStoryBackButton);
        completeStoryButton = findViewById(R.id.completeStoryButton);

        if(storyToEdit.isComplete()) {
            completeStoryButton.setText(R.string.undoComplete);
        } else {
            completeStoryButton.setText(R.string.completeStory);
        }

        editStoryTitle.setText(storyToEdit.getTitle());
        editStoryDescription.setText(storyToEdit.getDescription());
        editStoryDuration.setText(storyToEdit.getDuration() + "");
        editStoryPurpose.setText(storyToEdit.getPurpose());

        editStory = storyToEdit;
    }

    public void switchToHighFive() {
        setContentView(R.layout.layout_high_five);
        highFive = findViewById(R.id.highFive);
    }

    public void onHighFive(View view) {
        switchToDashboard();
    }

    public void onEditStoryClicked(View view) {
        editStory.setTitle(editStoryTitle.getText().toString());
        editStory.setDescription(editStoryDescription.getText().toString());
        editStory.setDuration(Integer.parseInt(editStoryDuration.getText().toString()));
        editStory.setPurpose(editStoryPurpose.getText().toString());
        db.storyDao().updateStory(editStory);
        editStory = null;
        switchToDashboard();
    }

    public void onCompleteStoryClicked(View view) {
        editStory.setComplete(!editStory.isComplete());
        db.storyDao().updateStory(editStory);
        if(editStory.isComplete()) {
            editStory = null;
            switchToHighFive();
        } else {
            editStory = null;
            switchToDashboard();
        }
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