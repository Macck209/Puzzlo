package com.macck209.puzzlo2;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImageButtonClickListener implements View.OnClickListener{
    public int buttonIndex;
    public boolean buttonSelected;
    private Context context;


    public ImageButtonClickListener(int index,Context context) {
        buttonIndex = index;
        buttonSelected=false;
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        buttonSelected = !buttonSelected;
        for (ImageButtonClickListener clickListener : GameActivity.clickListeners) {
            if(clickListener.buttonSelected) {
                if (clickListener.buttonIndex != this.buttonIndex) {
                    if(GameActivity.swapTiles(this.buttonIndex,clickListener.buttonIndex,context)){
                        GameActivity.setScoreboard(context);

                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    }else{
                        clickListener.buttonSelected=false;
                        this.buttonSelected=false;
                    }
                }
            }
        }
    }


}
