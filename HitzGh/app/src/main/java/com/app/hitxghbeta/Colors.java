package com.app.hitxghbeta;

import android.content.Context;

import java.util.Random;

/**
 * Created by anubhav on 21/12/17.
 **/

public class Colors {

    private int[] colors500 = {
            R.color.md_red_500,
            R.color.md_pink_500,
            R.color.md_purple_500,
            R.color.md_deep_purple_500,
            R.color.md_indigo_500,
            R.color.md_blue_500,
            R.color.md_light_blue_500,
            R.color.md_cyan_500,
            R.color.md_teal_500,
            R.color.md_green_500,
            R.color.md_light_green_500,
            R.color.md_lime_500,
            R.color.md_yellow_500,
            R.color.md_amber_500,
            R.color.md_orange_500,
            R.color.md_deep_orange_500,
            R.color.md_brown_500,
            R.color.md_grey_500,
            R.color.md_blue_grey_500};

    private int [] brightColors = {
            R.color.md_red_A700,
            R.color.md_pink_A700,
            R.color.md_purple_A700,
            R.color.md_deep_purple_A700,
            R.color.md_indigo_A700,
            R.color.md_blue_A700,
            R.color.md_light_blue_A700,
            R.color.md_blue_grey_800,
            R.color.md_grey_800,
            R.color.md_brown_900,
            R.color.md_orange_900,
            R.color.md_light_green_900,
            R.color.md_green_900,
            R.color.md_teal_900,
            R.color.md_deep_orange_A700
    };

    public int getRandomColor(){
        int i = new Random().nextInt(brightColors.length);
        return brightColors[i];
    }

    public String getRandomHexCode(Context context){
        return context.getResources().getString(getRandomColor());
    }
}
