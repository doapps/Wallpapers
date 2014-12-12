package com.example.util;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by jonathan on 04/12/2014.
 */
public class UtilFonts {
    public static Typeface setFight(Context context){
        return Typeface.createFromAsset(context.getAssets(), "fonts/FightToTheFinishBB.otf");
    }
}
