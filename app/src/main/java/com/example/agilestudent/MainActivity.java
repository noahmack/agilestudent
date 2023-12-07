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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void switchToSprintReport(String type) {
        if(type.equals("current")) {
            setContentView(R.layout.layout_current_report);
            TextView text = findViewById(R.id.currentOrPrevious);
            text.setText(R.string.currentSprint);
            ProgressBar bar = findViewById(R.id.currentSprintProgressBar);
            List<Story> storyList = db.storyDao().getStoriesBySprint(activeUser.getUserId(), getCurrentSprint());
            int completedDuration = 0;
            int totalDuration = 0;
            int completedStories = 0;
            HashMap<String, Integer> purposeDurationMap = new HashMap<>();
            Story longest = new Story("", "", 0, 0, "", -1);
            for (Story s : storyList) {
                if (s.isComplete()) {
                    completedDuration += s.getDuration();
                    completedStories++;
                }
                totalDuration += s.getDuration();
                if (!s.isComplete() && s.getDuration() > longest.getDuration()) {
                    longest = s;
                }

                if (!purposeDurationMap.containsKey(s.getPurpose())) {
                    purposeDurationMap.put(s.getPurpose(), s.getDuration());
                } else {
                    purposeDurationMap.replace(s.getPurpose(), purposeDurationMap.get(s.getPurpose()) + s.getDuration());
                }
            }
            int remainingDuration = totalDuration - completedDuration;
            int remainingStories = storyList.size() - completedStories;

            bar.setProgress(completedDuration * 100 / totalDuration);
            TextView progress = findViewById(R.id.currentSprintProgressText);
            progress.setText(completedDuration + "/" + totalDuration);

            TextView storiesRemaining = findViewById(R.id.storiesRemaining);
            storiesRemaining.setText("Stories Remaining: " + remainingStories);

            TextView minutesRemaining = findViewById(R.id.minutesRemaining);
            minutesRemaining.setText("Minutes Remaining: " + remainingDuration);

            TextView longestStoryRemaining = findViewById(R.id.longestStoryRemaining);
            longestStoryRemaining.setText("Longest Remaining Story: STORY" + longest.getStoryId() + ", " + longest.getDuration() + " minutes");
            if(longest.getStoryId() == 0) longestStoryRemaining.setText("Longest Remaining Story: N/A");

            TextView averageStoryDuration = findViewById(R.id.averageStoryDuration);
            averageStoryDuration.setText("Average Remaining Story Duration: " + ((double) remainingDuration / remainingStories));
            if(remainingStories == 0) averageStoryDuration.setText("Average Remaining Story Duration: N/A");

            TextView biggestPurpose = findViewById(R.id.biggestPurpose);
            int maxDuration = -1;
            String maxPurpose = "";
            for (Map.Entry kv : purposeDurationMap.entrySet()) {
                if ((Integer) kv.getValue() > maxDuration) {
                    maxDuration = (Integer) kv.getValue();
                    maxPurpose = (String) kv.getKey();
                }
            }
            biggestPurpose.setText("Longest Duration Purpose: " + maxPurpose + ", " + maxDuration + " minutes");
        }

        if(type.equals("previous")) {
            setContentView(R.layout.layout_current_report);
            TextView text = findViewById(R.id.currentOrPrevious);
            text.setText("Last Sprint:");
            ProgressBar bar = findViewById(R.id.currentSprintProgressBar);
            List<Story> storyList = db.storyDao().getStoriesBySprint(activeUser.getUserId(), getCurrentSprint() - 1);
            int completedDuration = 0;
            int totalDuration = 0;
            int completedStories = 0;
            HashMap<String, Integer> purposeDurationMap = new HashMap<>();
            Story longest = new Story("", "", 0, 0, "", -1);
            for (Story s : storyList) {
                if (s.isComplete()) {
                    completedDuration += s.getDuration();
                    completedStories++;
                }
                totalDuration += s.getDuration();
                if (s.isComplete() && s.getDuration() > longest.getDuration()) {
                    longest = s;
                }

                if (!purposeDurationMap.containsKey(s.getPurpose())) {
                    purposeDurationMap.put(s.getPurpose(), s.getDuration());
                } else {
                    purposeDurationMap.replace(s.getPurpose(), purposeDurationMap.get(s.getPurpose()) + s.getDuration());
                }
            }
            int remainingDuration = totalDuration - completedDuration;
            int remainingStories = storyList.size() - completedStories;

            bar.setProgress(totalDuration == 0? 0 : completedDuration * 100 / totalDuration);
            TextView progress = findViewById(R.id.currentSprintProgressText);
            progress.setText(completedDuration + "/" + totalDuration);

            TextView storiesRemaining = findViewById(R.id.storiesRemaining);
            storiesRemaining.setText("Stories Completed: " + completedStories);

            TextView minutesRemaining = findViewById(R.id.minutesRemaining);
            minutesRemaining.setText("Minutes Completed: " + completedDuration);

            TextView longestStoryRemaining = findViewById(R.id.longestStoryRemaining);
            longestStoryRemaining.setText("Longest Completed Story: STORY" + longest.getStoryId() + ", " + longest.getDuration() + " minutes");

            TextView averageStoryDuration = findViewById(R.id.averageStoryDuration);
            averageStoryDuration.setText("Average Completed Story Duration: " + ((double) completedDuration / completedStories));

            TextView biggestPurpose = findViewById(R.id.biggestPurpose);
            int maxDuration = -1;
            String maxPurpose = "";
            for (Map.Entry kv : purposeDurationMap.entrySet()) {
                if ((Integer) kv.getValue() > maxDuration) {
                    maxDuration = (Integer) kv.getValue();
                    maxPurpose = (String) kv.getKey();
                }
            }
            biggestPurpose.setText("Longest Duration Purpose: " + maxPurpose + ", " + maxDuration + " minutes");
        }

        if(type.equals("past")) {
            setContentView(R.layout.layout_current_report);
            TextView text = findViewById(R.id.currentOrPrevious);
            text.setText("Completed Stories:");
            ProgressBar bar = findViewById(R.id.currentSprintProgressBar);
            List<Story> storyList = db.storyDao().getCompletedStories(activeUser.getUserId());
            int totalDuration = 0;
            int completedStories = storyList.size();
            HashMap<String, Integer> purposeDurationMap = new HashMap<>();
            Story longest = new Story("", "", 0, 0, "", -1);
            for (Story s : storyList) {
                totalDuration += s.getDuration();
                if (s.getDuration() > longest.getDuration()) {
                    longest = s;
                }

                if (!purposeDurationMap.containsKey(s.getPurpose())) {
                    purposeDurationMap.put(s.getPurpose(), s.getDuration());
                } else {
                    purposeDurationMap.replace(s.getPurpose(), purposeDurationMap.get(s.getPurpose()) + s.getDuration());
                }
            }

            bar.setProgress(100);
            TextView progress = findViewById(R.id.currentSprintProgressText);
            progress.setText(totalDuration + "/" + totalDuration);

            TextView storiesRemaining = findViewById(R.id.storiesRemaining);
            storiesRemaining.setText("Stories Completed: " + completedStories);

            TextView minutesRemaining = findViewById(R.id.minutesRemaining);
            minutesRemaining.setText("Minutes Completed: " + totalDuration);

            TextView longestStoryRemaining = findViewById(R.id.longestStoryRemaining);
            longestStoryRemaining.setText("Longest Completed Story: STORY" + longest.getStoryId() + ", " + longest.getDuration() + " minutes");

            TextView averageStoryDuration = findViewById(R.id.averageStoryDuration);
            averageStoryDuration.setText("Average Completed Story Duration: " + ((double) totalDuration / completedStories));

            TextView biggestPurpose = findViewById(R.id.biggestPurpose);
            int maxDuration = -1;
            String maxPurpose = "";
            for (Map.Entry kv : purposeDurationMap.entrySet()) {
                if ((Integer) kv.getValue() > maxDuration) {
                    maxDuration = (Integer) kv.getValue();
                    maxPurpose = (String) kv.getKey();
                }
            }
            biggestPurpose.setText("Longest Duration Purpose: " + maxPurpose + ", " + maxDuration + " minutes");
        }
    }

    public void switchToAchievements() {
        setContentView(R.layout.layout_achievements);
        int[] completionistLevels = {1, 10, 100, 1000, 10000};
        int[] sprinterLevels = {1, 10, 30, 52, 250};
        int[] perseveranceLevels = {30, 60, 120, 240, 600};

        int completedStories = db.storyDao().getCompletedStories(activeUser.getUserId()).size();
        List<Story> storyList = db.storyDao().getStoriesByUserId(activeUser.getUserId());
        int maxSprint = 0; int minSprint = Integer.MAX_VALUE;
        int longestStory = 0;
        for(Story s : storyList) {
            if(s.getSprint() < minSprint) minSprint = s.getSprint();
            if(s.getSprint() > maxSprint) maxSprint= s.getSprint();
            if(s.isComplete() && s.getDuration() > longestStory) longestStory = s.getDuration();
        }
        if(storyList.size() == 0) {
            minSprint = 0;
        }
        int numSprintsCompleted = maxSprint - minSprint;
        int sprinterLevel = 0;
        int completionistLevel = 0;
        int perseveranceLevel = 0;

        for(int i = 0; i < completionistLevels.length; i++) {
            if(completedStories >= completionistLevels[i]) {
                completionistLevel = i + 1;
            }
        }
        for(int i = 0; i < sprinterLevels.length; i++) {
            if(numSprintsCompleted >= sprinterLevels[i]) {
                sprinterLevel = i + 1;
            }
        }
        for(int i = 0; i < perseveranceLevels.length; i++) {
            if(longestStory >= perseveranceLevels[i]) {
                perseveranceLevel = i + 1;
            }
        }

        TextView completionistCurrent = findViewById(R.id.completionistCurrent);
        TextView completionistNext = findViewById(R.id.completionistNext);
        TextView sprinterCurrent = findViewById(R.id.sprinterCurrent);
        TextView sprinterNext = findViewById(R.id.sprinterNext);
        TextView perseveranceCurrent = findViewById(R.id.perseveranceCurrent);
        TextView perseveranceNext = findViewById(R.id.perseveranceNext);
        ProgressBar completionistProgressBar = findViewById(R.id.completionistProgressBar);
        ProgressBar sprinterProgressBar = findViewById(R.id.sprinterProgressBar);
        ProgressBar perseveranceProgressBar = findViewById(R.id.perseveranceProgressBar);
        TextView completionistProgressText = findViewById(R.id.completionistProgressText);
        TextView sprinterProgressText = findViewById(R.id.sprinterProgressText);
        TextView perseveranceProgressText = findViewById(R.id.perseveranceProgressText);

        switch(completionistLevel) {
            case 0:
                completionistCurrent.setText(R.string.completionist1Desc);
                completionistNext.setText(R.string.completionist);
                completionistProgressBar.setProgress(0);
                completionistProgressText.setText("0/1");
                break;
            case 1:
                completionistCurrent.setText(R.string.completionist2Desc);
                completionistNext.setText(R.string.completionist1);
                completionistProgressBar.setProgress(completedStories * 100 / completionistLevels[1]);
                completionistProgressText.setText(completedStories + "/" + completionistLevels[1]);
                break;
            case 2:
                completionistCurrent.setText(R.string.completionist3Desc);
                completionistNext.setText(R.string.completionist2);
                completionistProgressBar.setProgress(completedStories * 100 / completionistLevels[2]);
                completionistProgressText.setText(completedStories + "/" + completionistLevels[2]);
                break;
            case 3:
                completionistCurrent.setText(R.string.completionist4Desc);
                completionistNext.setText(R.string.completionist3);
                completionistProgressBar.setProgress(completedStories * 100 / completionistLevels[3]);
                completionistProgressText.setText(completedStories + "/" + completionistLevels[3]);
                break;
            case 4:
                completionistCurrent.setText(R.string.completionist5Desc);
                completionistNext.setText(R.string.completionist4);
                completionistProgressBar.setProgress(completedStories * 100 / completionistLevels[4]);
                completionistProgressText.setText(completedStories + "/" + completionistLevels[4]);
                break;
            case 5:
                completionistCurrent.setText(R.string.completionist5Desc);
                completionistNext.setText(R.string.completionist5);
                completionistProgressBar.setProgress(completedStories * 100 / completionistLevels[4]);
                completionistProgressText.setText(completedStories + "/" + completionistLevels[4]);
                break;
            default:
                completionistCurrent.setText(R.string.completionist1Desc);
                completionistNext.setText(R.string.completionist);
                completionistProgressBar.setProgress(0);
                completionistProgressText.setText("0/1");
                break;
        }

        switch(sprinterLevel) {
            case 0:
                sprinterCurrent.setText(R.string.sprinter1Desc);
                sprinterNext.setText(R.string.sprinter);
                sprinterProgressBar.setProgress(0);
                sprinterProgressText.setText("0/1");
                break;
            case 1:
                sprinterCurrent.setText(R.string.sprinter2Desc);
                sprinterNext.setText(R.string.sprinter1);
                sprinterProgressBar.setProgress(numSprintsCompleted * 100 / sprinterLevels[1]);
                sprinterProgressText.setText(numSprintsCompleted + "/" + sprinterLevels[1]);
                break;
            case 2:
                sprinterCurrent.setText(R.string.sprinter3Desc);
                sprinterNext.setText(R.string.sprinter2);
                sprinterProgressBar.setProgress(numSprintsCompleted * 100 / sprinterLevels[2]);
                sprinterProgressText.setText(numSprintsCompleted + "/" + sprinterLevels[2]);
                break;
            case 3:
                sprinterCurrent.setText(R.string.sprinter4Desc);
                sprinterNext.setText(R.string.sprinter3);
                sprinterProgressBar.setProgress(numSprintsCompleted * 100 / sprinterLevels[3]);
                sprinterProgressText.setText(numSprintsCompleted + "/" + sprinterLevels[3]);
                break;
            case 4:
                sprinterCurrent.setText(R.string.sprinter5Desc);
                sprinterNext.setText(R.string.sprinter4);
                sprinterProgressBar.setProgress(numSprintsCompleted * 100 / sprinterLevels[4]);
                sprinterProgressText.setText(numSprintsCompleted + "/" + sprinterLevels[4]);
                break;
            case 5:
                sprinterCurrent.setText(R.string.sprinter5Desc);
                sprinterNext.setText(R.string.sprinter5);
                sprinterProgressBar.setProgress(numSprintsCompleted * 100 / sprinterLevels[4]);
                sprinterProgressText.setText(numSprintsCompleted + "/" + sprinterLevels[4]);
                break;
            default:
                sprinterCurrent.setText(R.string.sprinter1Desc);
                sprinterNext.setText(R.string.sprinter);
                sprinterProgressBar.setProgress(0);
                sprinterProgressText.setText("0/1");
                break;
        }

        switch(perseveranceLevel) {
            case 0:
                perseveranceCurrent.setText(R.string.perseverance1Desc);
                perseveranceNext.setText(R.string.perseverance);
                perseveranceProgressBar.setProgress(0);
                perseveranceProgressText.setText("0/30");
                break;
            case 1:
                perseveranceCurrent.setText(R.string.perseverance2Desc);
                perseveranceNext.setText(R.string.perseverance1);
                perseveranceProgressBar.setProgress(longestStory * 100 / perseveranceLevels[1]);
                perseveranceProgressText.setText(longestStory + "/" + perseveranceLevels[1]);
                break;
            case 2:
                perseveranceCurrent.setText(R.string.perseverance3Desc);
                perseveranceNext.setText(R.string.perseverance2);
                perseveranceProgressBar.setProgress(longestStory * 100 / perseveranceLevels[2]);
                perseveranceProgressText.setText(longestStory + "/" + perseveranceLevels[2]);
                break;
            case 3:
                perseveranceCurrent.setText(R.string.perseverance4Desc);
                perseveranceNext.setText(R.string.perseverance3);
                perseveranceProgressBar.setProgress(longestStory * 100 / perseveranceLevels[3]);
                perseveranceProgressText.setText(longestStory + "/" + perseveranceLevels[3]);
                break;
            case 4:
                perseveranceCurrent.setText(R.string.perseverance5Desc);
                perseveranceNext.setText(R.string.perseverance4);
                perseveranceProgressBar.setProgress(longestStory * 100 / perseveranceLevels[4]);
                perseveranceProgressText.setText(longestStory + "/" + perseveranceLevels[4]);
                break;
            case 5:
                perseveranceCurrent.setText(R.string.perseverance5Desc);
                perseveranceNext.setText(R.string.perseverance5);
                perseveranceProgressBar.setProgress(longestStory * 100 / perseveranceLevels[4]);
                perseveranceProgressText.setText(longestStory + "/" + perseveranceLevels[4]);
                break;
            default:
                perseveranceCurrent.setText(R.string.perseverance1Desc);
                perseveranceNext.setText(R.string.perseverance);
                perseveranceProgressBar.setProgress(0);
                perseveranceProgressText.setText("0/30");
                break;
        }
    }

    public void onHighFive(View view) {
        switchToDashboard();
    }

    public void onCurrentReportClicked(View view) {
        switchToSprintReport("current");
    }

    public void onCompletedReportClicked(View view) {
        switchToSprintReport("past");
    }

    public void onLastReportClicked(View view) {
        switchToSprintReport("previous");
    }

    public void onAchievementsClicked(View view) {
        switchToAchievements();
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