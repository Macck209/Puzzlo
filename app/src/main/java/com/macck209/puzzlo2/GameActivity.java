package com.macck209.puzzlo2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private static ImageButton[] imageButtons; // Array to hold ImageButtons
    private static Bitmap[] bitmaps; // Array to hold Bitmaps
    private static int[] puzzleBoard; // Current puzzle board state
    public static ImageButtonClickListener[] clickListeners;
    //Timer
    private Handler handler;
    private Runnable runnable;
    public static float time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Return btn
        ImageButton returnButton = findViewById(R.id.return_btn);
        //Puzzle grid variables
        final GridLayout puzzleGrid = findViewById(R.id.puzzleGrid);
        //Preferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int diff = preferences.getInt("difficulty", 1);
        int gridSize = diff+3;

        boolean sounds = preferences.getBoolean("sounds",true);
        puzzleGrid.setColumnCount(gridSize);
        puzzleGrid.setRowCount(gridSize);
        final int[] cropSize = {0};
        final int[] cropOffset = {0};
        Bitmap puzzleImage = BitmapFactory.decodeResource(getResources(), R.drawable.puzzle_img_3);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                time+=0.1f;
                handler.postDelayed(this, 100);
            }
        };


        //Phone GUI bars hiding
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);

        //Setting up puzzle grid
        puzzleGrid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                imageButtons = new ImageButton[gridSize * gridSize];
                bitmaps = new Bitmap[gridSize * gridSize];
                puzzleBoard = new int[gridSize*gridSize];
                for (int i = 0; i < puzzleBoard.length; i++) {
                    puzzleBoard[i] = i;
                }
                clickListeners = new ImageButtonClickListener[gridSize*gridSize];

                //Generating independent tiles (slicing image)
                for (int i = 0; i < gridSize * gridSize; i++) {
                    ImageButton imageButton = new ImageButton(GameActivity.this);

                    int cellSize = puzzleGrid.getWidth()  / gridSize;
                    int cutSize = puzzleImage.getWidth()  / gridSize;

                    int row = i / gridSize;
                    int col = i % gridSize;

                    // Set the layout parameters for the ImageButton
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = cellSize;
                    params.height = cellSize;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        params.rowSpec = GridLayout.spec(row,GridLayout.FILL);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        params.columnSpec = GridLayout.spec(col,GridLayout.FILL);
                    }
                    imageButton.setLayoutParams(params);
                    imageButton.setScaleType(ImageButton.ScaleType.CENTER_CROP);
                    imageButton.setBackgroundColor(Color.TRANSPARENT);
                    puzzleGrid.setUseDefaultMargins(false);
                    puzzleGrid.setPadding(0,0,0,0);
                    puzzleGrid.setUseDefaultMargins(false);

                    if (cropSize[0] == 0) {
                        cropSize[0] = puzzleImage.getWidth() / gridSize;
                        cropOffset[0] = cropSize[0] / 2;
                    }

                    int startX = col * cutSize;
                    int startY = row * cutSize;

                    Bitmap croppedImage = cropImage(puzzleImage, startX, startY, cropSize[0], cropSize[0]);
                    bitmaps[i]=croppedImage;
                    imageButton.setImageBitmap(croppedImage);
                    imageButtons[i]=imageButton;

                    ImageButtonClickListener clickListener = new ImageButtonClickListener(i,GameActivity.this);
                    imageButton.setOnClickListener(clickListener);
                    clickListeners[i] = clickListener;

                    puzzleGrid.addView(imageButton);
                }

                //Shuffling tiles
                for(int i = 0; i<gridSize*gridSize; i++){
                    Bitmap tempBitmap;
                    int tempPos, tempVal;
                    Random rand = new Random();

                    tempPos= rand.nextInt(gridSize*gridSize);
                    tempVal = puzzleBoard[i];

                    puzzleBoard[i]=puzzleBoard[tempPos];
                    puzzleBoard[tempPos]=tempVal;

                    tempBitmap=bitmaps[tempPos];
                    bitmaps[tempPos]=bitmaps[i];
                    bitmaps[i]=tempBitmap;

                    imageButtons[i].setImageBitmap(bitmaps[i]);
                    imageButtons[tempPos].setImageBitmap(bitmaps[tempPos]);
                }
                puzzleGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        //Return btn
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, PreGameActivity.class);
            startActivity(intent);
        });

        //Navigation bar detection
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
                hideNavigationBar();
            }
        });
    }

    @Override
    protected void onStart() {
        time=0;
        super.onStart();
        handler.postDelayed(runnable, 100);
    }
    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    //Re-hiding navigation bar
    private void hideNavigationBar()
    {
        Handler handler = new Handler();
        handler.postDelayed(() ->
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE), 3000);
    }

    //Creating a slice of full puzzle image
    private Bitmap cropImage(Bitmap source, int startX, int startY, int width, int height) {
        if (startX < 0) {
            startX = 0;
        }
        if (startY < 0) {
            startY = 0;
        }
        if (startX + width > source.getWidth()) {
            width = source.getWidth() - startX;
        }
        if (startY + height > source.getHeight()) {
            height = source.getHeight() - startY;
        }
        return Bitmap.createBitmap(source, startX, startY, width, height);
    }

    public static boolean swapTiles(int pos1, int pos2,Context context) {
        Bitmap tempBitmap;
        int temp;

        temp= puzzleBoard[pos2];

        puzzleBoard[pos2]=puzzleBoard[pos1];
        puzzleBoard[pos1]=temp;

        tempBitmap=bitmaps[pos2];
        bitmaps[pos2]=bitmaps[pos1];
        bitmaps[pos1]=tempBitmap;

        imageButtons[pos1].setImageBitmap(bitmaps[pos1]);
        imageButtons[pos2].setImageBitmap(bitmaps[pos2]);

        SharedPreferences preferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean sounds = preferences.getBoolean("sounds",true);
        if(sounds) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.czyk);
            mediaPlayer.start();
        }

        return isSorted(puzzleBoard,context);
    }

    public static boolean isSorted(int[] array,Context context) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        SharedPreferences preferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean sounds = preferences.getBoolean("sounds",true);
        if(sounds) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.next);
            mediaPlayer.start();
        }
        return true;
    }

    public static void setScoreboard(Context context){
        SharedPreferences preferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int diff = preferences.getInt("difficulty", 1);
        String scoreboard = preferences.getString("scoreboard", "0,0,0,0,0,0,0,0,0,0");
        String nicknames = preferences.getString("nicknames", "---,---,---,---,---,---,---,---,---,---");
        String currentNickname = preferences.getString("currentNickname","Player");

        String[] sArr = scoreboard.split(",");
        String[] nArr = nicknames.split(",");


        List<Record> scoreArr = new ArrayList<>();

        for (int i = 0; i < sArr.length; i++) {
            scoreArr.add(new Record(nArr[i],Integer.parseInt(sArr[i])));
        }
        int currentScore = (int)(diff*1000/time);
        //Log.d("test","time: "+time+ " diff: "+ diff+ " score: "+currentScore);
        if(currentScore>scoreArr.get(scoreArr.size()-1).getInt()){
            List<Record> newList = new ArrayList<>(scoreArr);
            newList.add(new Record(currentNickname,currentScore));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Collections.sort(newList, Comparator.comparingInt(Record::getInt).reversed());
            }
            newList.remove(newList.size() - 1);

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < newList.size(); i++) {
                builder.append(newList.get(i).getInt());
                if (i < newList.size() - 1) {
                    builder.append(",");
                }
            }
            String newScores = builder.toString();
            builder.setLength(0);
            for (int i = 0; i < newList.size(); i++) {
                builder.append(newList.get(i).getString());
                if (i < newList.size() - 1) {
                    builder.append(",");
                }
            }
            String newNames = builder.toString();


            SharedPreferences.Editor editor = preferences.edit();
            //editor.putString("scoreboard","0,0,0,0,0,0,0,0,0,0"); //Reset scores
            //editor.putString("nicknames","---,---,---,---,---,---,---,---,---,---");
            editor.putString("scoreboard",newScores);
            editor.putString("nicknames",newNames);
            editor.apply();
        }
    }
}