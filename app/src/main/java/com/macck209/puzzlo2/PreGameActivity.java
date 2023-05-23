package com.macck209.puzzlo2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

public class PreGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_game);

        // Hide the action bar
        getSupportActionBar().hide();
        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Hide the navigation bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);

        // Set up the listener to re-hide the navigation bar when it becomes visible
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
                    hideNavigationBar();
                }
            }
        });


        ImageButton returnButton = findViewById(R.id.return_btn);
        ImageButton goButton = findViewById(R.id.go_btn);
        EditText nicknameInput = findViewById(R.id.nickname_input);
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean sounds = preferences.getBoolean("sounds",true);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreGameActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //String currentNickname = preferences.getString("currentNickname","Player");
        goButton.setOnClickListener(new View.OnClickListener() {
            String currentNickname = preferences.getString("currentNickname","Player");
            @Override
            public void onClick(View v) {
                String nickname = nicknameInput.getText().toString();
                if(nickname.length()>3 && nickname.length()<9) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("currentNickname",currentNickname);
                    editor.apply();

                    if(sounds) {
                        MediaPlayer mediaPlayer = MediaPlayer.create(PreGameActivity.this, R.raw.next);
                        mediaPlayer.start();
                    }

                    Intent intent = new Intent(PreGameActivity.this, GameActivity.class);
                    startActivity(intent);
                }else {
                    if(sounds) {
                        MediaPlayer mediaPlayer = MediaPlayer.create(PreGameActivity.this, R.raw.nope);
                        mediaPlayer.start();
                    }
                }
            }
        });
    }

    private void hideNavigationBar()
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
            }
        }, 3000);
    }
}