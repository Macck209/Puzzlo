package com.macck209.puzzlo2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
        ImageButton toggleButton = findViewById(R.id.sounds_btn);
        ImageButton difficultyButton = findViewById(R.id.difficulty_btn);
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        boolean sounds = preferences.getBoolean("sounds",true);
        setSoundImage(sounds,toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            boolean sounds = preferences.getBoolean("sounds",true);

            @Override
            public void onClick(View v) {
                sounds=!sounds;

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("sounds",sounds);
                editor.apply();

                setSoundImage(sounds,toggleButton);
            }
        });

        int diff = preferences.getInt("difficulty", 1);
        setDifficultyImage(diff,difficultyButton);
        difficultyButton.setOnClickListener(new View.OnClickListener() {
            int diff = preferences.getInt("difficulty", 1);

            @Override
            public void onClick(View v) {
                diff+=1;
                if(diff>4){diff=1;}

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("difficulty", diff);
                editor.apply();

                setDifficultyImage(diff,difficultyButton);
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
    private void setSoundImage(boolean isToggled,ImageButton toggleButton){
        if (isToggled) {
            toggleButton.setImageResource(R.drawable.img_checkbox_on);
        } else {
            toggleButton.setImageResource(R.drawable.img_checkbox);
        }
    }
    private void setDifficultyImage(int diff,ImageButton difficultyButton) {
        switch (diff) {
            default:
                difficultyButton.setImageResource(R.drawable.img_difficulty_1_btn);
                break;
            case 2:
                difficultyButton.setImageResource(R.drawable.img_difficulty_2_btn);
                break;
            case 3:
                difficultyButton.setImageResource(R.drawable.img_difficulty_3_btn);
                break;
            case 4:
                difficultyButton.setImageResource(R.drawable.img_difficulty_4_btn);
                break;
        }
    }
}