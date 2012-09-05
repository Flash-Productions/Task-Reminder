package com.flashproductions.android.apps.taskreminder;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * Created by Flash Productions.
 * Date: 7/23/12
 * Time: 6:14 PM
 */
public class ColorTheme extends Activity
{
    public static String color1 = "#000000";
    public static String color2 = "#000000";

    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate ( savedInstanceState );

        String defaultThemeKey = getString ( R.string.pref_default_theme_key );


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences ( this );
        int theme = Integer.parseInt ( prefs.getString ( defaultThemeKey, "1" ) );


        switch ( theme )
        {
            case 1:
                color1 = "#000000";
                color2 = "#000000";
                break;
            case 2:
                color1 = "#FF0000";
                color2 = "#FF6347";
                break;
            case 3:
                color1 = "#808080";
                color2 = "#A9A9A9";

                break;
            case 4:
                color1 = "#319B31";
                color2 = "#32CD32";
                break;
            case 5:
                color1 = "#4169E1";
                color2 = "#41D6C6";
                break;
            case 6:
                color1 = "#FF00FF";
                color2 = "#FFC0CB";
                break;
            case 7:
                color1 = "#800080";
                color2 = "#9400D3";
                break;
        }


        finish ();
    }


}