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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
    private Button deleteStoryButton;
    private ImageView highFive;
    private ListView storyListView;
    private Spinner purposeSpinner;
    private Spinner sprintSpinner;
    private ProgressBar progressBar;
    private TextView welcomeMessage;
    private TextView currentSprint;
    private TextView storySprint;
    private TextView minutesCompleted;
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
        Log.d("WEEK", getCurrentSprint() + "");

    }

    public void switchToDashboard() {
        setContentView(R.layout.layout_dashboard);
        progressBar = findViewById(R.id.progressBar);


        welcomeMessage = findViewById(R.id.welcomeMessage);
        welcomeMessage.setText("Welcome, " + activeUser.getUsername().toString());
        currentSprint = findViewById(R.id.currentSprint);
        currentSprint.setText("Current Sprint: Sprint " + getCurrentSprint());

        minutesCompleted = findViewById(R.id.minutesCompleted);

        newStoryButton = findViewById(R.id.newStoryButton);

        storyListView = findViewById(R.id.storyListView);
        purposeSpinner = findViewById(R.id.purposeSpinner);
        sprintSpinner = findViewById(R.id.sprintSpinner);
        List<Story> storyList = db.storyDao().getStoriesByUserId(activeUser.getUserId());

        int completed = 0;
        int totalDuration = 0;
        ArrayList<String> purposes = new ArrayList<>();
        ArrayList<String> sprints = new ArrayList<>();
        for (Story s : storyList) {
            if (s.isComplete()) completed += s.getDuration();
            totalDuration += s.getDuration();
            if(!purposes.contains(s.getPurpose())) purposes.add(s.getPurpose());
            if(!sprints.contains(s.getSprint() + "")) sprints.add(s.getSprint() + "");
        }
        String[] storyArray = new String[storyList.size()];
        String[] purposeArray = new String[purposes.size() + 1];
        String[] sprintArray = new String[sprints.size() + 1];
        if (storyList.size() > 0) {
            progressBar.setProgress((completed * 100) / totalDuration);
            minutesCompleted.setText("Minutes completed: " + completed + "/" + totalDuration);
            for (int i = 0; i < storyList.size(); i++) {
                storyArray[i] = "STORY" + storyList.get(i).getStoryId() + ": " + storyList.get(i).getTitle();
                if (storyList.get(i).isComplete()) {
                    storyArray[i] += " --> COMPLETED";
                }
            }
        } else {
            storyArray = new String[1];
            storyArray[0] = "No active stories.";
        }

        if(purposes.size() > 0) {
            purposeArray[0] = "All purposes";
            for(int i = 1; i < purposes.size() + 1; i++) {
                purposeArray[i] = purposes.get(i - 1).equals("")? "No purpose" : purposes.get(i - 1);
            }
        } else {
            purposeArray = new String[1];
            purposeArray[0] = "No active stories.";
        }

        if(sprints.size() > 0) {
            sprintArray[0] = "All Sprints";
            for(int i = 1; i < sprints.size() + 1; i++) {
                sprintArray[i] = "Sprint " + sprints.get(i - 1);
            }
        } else {
            sprintArray = new String[1];
            sprintArray[0] = "No active stories.";
        }

        ArrayAdapter<String> stories = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, storyArray);
        stories.setDropDownViewResource(R.layout.layout_dashboard);
        storyListView.setAdapter(stories);
        if(storyList.size() > 0) {
            storyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String storyNum = ((TextView) view).getText().toString();
                    storyNum = storyNum.substring(0, storyNum.indexOf(':'));
                    storyNum = storyNum.substring(storyNum.indexOf('Y') + 1);

                    for (Story s : storyList) {
                        if (s.getStoryId() == Integer.parseInt(storyNum)) {
                            switchToEditStory(s);
                        }
                    }
                }
            });
        }

        ArrayAdapter<String> purposeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, purposeArray);
        purposeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        purposeSpinner.setAdapter(purposeAdapter);
        if(purposes.size() > 0) {
            purposeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String purpose = ((TextView)view).getText().toString();
                    updateListView(purpose, "purpose");
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        ArrayAdapter<String> sprintAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sprintArray);
        sprintAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sprintSpinner.setAdapter(sprintAdapter);
        if(sprints.size() > 0) {
            sprintSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String sprint = ((TextView)view).getText().toString();
                    updateListView(sprint, "sprint");
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
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
        deleteStoryButton = findViewById(R.id.deleteStoryButton);
        storySprint = findViewById(R.id.storySprint);

        if(storyToEdit.isComplete()) {
            completeStoryButton.setText(R.string.undoComplete);
        } else {
            completeStoryButton.setText(R.string.completeStory);
        }

        editStoryTitle.setText(storyToEdit.getTitle());
        editStoryDescription.setText(storyToEdit.getDescription());
        editStoryDuration.setText(storyToEdit.getDuration() + "");
        editStoryPurpose.setText(storyToEdit.getPurpose());
        storySprint.setText("Sprint: " + storyToEdit.getSprint());

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
        editStory.setDuration(editStoryDuration.getText().toString().equals("")? 0 : Integer.parseInt(editStoryDuration.getText().toString()));
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


        Story story = new Story(title, description, getCurrentSprint(), intDuration, purpose, activeUser.getUserId());

        Log.d("Story Insert", "Insert story with userId: " + story.getUserId());
        db.storyDao().insertStory(story);


        switchToDashboard();
    }

    public void onDeleteStoryClicked(View view) {
        db.storyDao().deleteStory(editStory);
        editStory = null;
        switchToDashboard();
    }

    public void onBackClicked(View view) {
        switchToDashboard();
    }

    public void onNewStoryClicked(View view) {
        switchToCreateStory();
    }

    public void onPullInClicked(View view) {
        editStory.setSprint(getCurrentSprint());
        db.storyDao().updateStory(editStory);
        switchToDashboard();
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

    public void updateListView(String filter, String type) {
        String lowerFilter = filter.toLowerCase();

        if(type.equals("purpose")) {

            List<Story> storyList;
            if(lowerFilter.equals("all purposes")) {
                storyList = db.storyDao().getStoriesByUserId(activeUser.getUserId());
            } else {
                storyList = db.storyDao().getStoriesByPurpose(activeUser.getUserId(), lowerFilter.equals("no purpose")? "": lowerFilter);
            }

            String[] storyArray = new String[storyList.size()];
            if (storyList.size() > 0) {
                for (int i = 0; i < storyList.size(); i++) {
                    storyArray[i] = "STORY" + storyList.get(i).getStoryId() + ": " + storyList.get(i).getTitle();
                    if (storyList.get(i).isComplete()) {
                        storyArray[i] += " --> COMPLETED";
                    }
                }
            } else {
                storyArray = new String[1];
                storyArray[0] = "No active stories.";
            }
            storyListView = findViewById(R.id.storyListView);
            ArrayAdapter<String> stories = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, storyArray);
            stories.setDropDownViewResource(R.layout.layout_dashboard);
            storyListView.setAdapter(stories);
            if(storyList.size() > 0) {
                storyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String storyNum = ((TextView) view).getText().toString();
                        storyNum = storyNum.substring(0, storyNum.indexOf(':'));
                        storyNum = storyNum.substring(storyNum.indexOf('Y') + 1);

                        for (Story s : storyList) {
                            if (s.getStoryId() == Integer.parseInt(storyNum)) {
                                switchToEditStory(s);
                            }
                        }
                    }
                });
            }
        }

        if(type.equals("sprint")) {

            List<Story> storyList;
            if(lowerFilter.equals("all sprints")) {
                storyList = db.storyDao().getStoriesByUserId(activeUser.getUserId());
            } else {
                lowerFilter = lowerFilter.substring(lowerFilter.indexOf(' ') + 1);
                storyList = db.storyDao().getStoriesBySprint(activeUser.getUserId(), Integer.parseInt(lowerFilter));
            }

            String[] storyArray = new String[storyList.size()];
            if (storyList.size() > 0) {
                for (int i = 0; i < storyList.size(); i++) {
                    storyArray[i] = "STORY" + storyList.get(i).getStoryId() + ": " + storyList.get(i).getTitle();
                    if (storyList.get(i).isComplete()) {
                        storyArray[i] += " --> COMPLETED";
                    }
                }
            } else {
                storyArray = new String[1];
                storyArray[0] = "No active stories.";
            }
            storyListView = findViewById(R.id.storyListView);
            ArrayAdapter<String> stories = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, storyArray);
            stories.setDropDownViewResource(R.layout.layout_dashboard);
            storyListView.setAdapter(stories);
            if(storyList.size() > 0) {
                storyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String storyNum = ((TextView) view).getText().toString();
                        storyNum = storyNum.substring(0, storyNum.indexOf(':'));
                        storyNum = storyNum.substring(storyNum.indexOf('Y') + 1);

                        for (Story s : storyList) {
                            if (s.getStoryId() == Integer.parseInt(storyNum)) {
                                switchToEditStory(s);
                            }
                        }
                    }
                });
            }
        }


    }

    private int getCurrentSprint() {
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.WEEK_OF_YEAR);
    }
}