package com.example.agilestudent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Components
    private EditText username;
    private EditText password;
    private Button loginButton;
    private ProgressBar progressBar;
    private TextView welcomeMessage;

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
    }

    public void onLoginClicked(View view) {
        List<User> users = db.userDao().getAll();
        User user = new User(users.size(), username.getText().toString(), password.getText().toString());
        if(users.contains(user)) {
            User dbUser = users.get(users.indexOf(user));
            if(!dbUser.getPassword().equals(user.getPassword())) {
                Toast.makeText(getApplicationContext(), "Incorrect password, try again.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("DB", "User " + user.getUsername() + " already exists under id " + user.getUserId());
                Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();
                activeUser = user;
                switchToDashboard();
            }
        } else {
            Toast.makeText(getApplicationContext(), "New user " + user.getUsername() + " created, logging in...", Toast.LENGTH_SHORT).show();
            db.userDao().insert(user);
            activeUser = user;
            Log.d("DB", db.userDao().getAll().get(0).toString());
            switchToDashboard();
        }


    }
}