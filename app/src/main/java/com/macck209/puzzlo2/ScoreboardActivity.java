package com.macck209.puzzlo2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ScoreboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        //From xml
        ImageButton returnButton = findViewById(R.id.return_btn);
        TextView scoreboard = findViewById(R.id.scoreboard_scores);
        TextView nicknames = findViewById(R.id.scoreboard_names);
        //Preferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String scores = preferences.getString("scoreboard", "0,0,0,0,0,0,0,0,0,0");
        String names = preferences.getString("nicknames", "---,---,---,---,---,---,---,---,---,---");


        //Hiding navigation stuff & gui bars
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);

        loadScoreboard(scoreboard,scores,nicknames,names);

        //Phone GUI bars detection
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
                hideNavigationBar();
            }
        });

        //Return to main menu
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScoreboardActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void loadScoreboard(TextView scoreboard, String scores,TextView nicknames,String names){
        String formattedScores = scores.replace(",", "\n");
        scoreboard.setText(formattedScores);
        String formattedNames = names.replace(",", "\n");
        nicknames.setText(formattedNames);
    }

    //Re-hiding phones navigation bars
    private void hideNavigationBar()
    {
        Handler handler = new Handler();
        handler.postDelayed(() -> getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE), 3000);
    }
}