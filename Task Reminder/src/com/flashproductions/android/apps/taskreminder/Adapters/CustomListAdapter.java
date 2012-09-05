package com.flashproductions.android.apps.taskreminder.Adapters;

/**
 * Created by Flash Productions.
 * Date: 7/22/12
 * Time: 5:42 PM
 */

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.flashproductions.android.apps.taskreminder.ColorTheme;
import com.flashproductions.android.apps.taskreminder.R;


public class CustomListAdapter extends SimpleCursorAdapter
{

    private String color1 = ColorTheme.color1;
    private String color2 = ColorTheme.color2;


    private int[] colors = new int[] { Color.parseColor ( color1 ), Color.parseColor ( color2 ) };

    public CustomListAdapter ( Context context, int layout, Cursor c,
                               String[] from, int[] to )
    {
        super ( context, layout, c, from, to );


    }


    /**
     * Display rows in alternating colors
     */
    @Override
    public View getView ( int position, View convertView, ViewGroup parent )
    {
        View view = super.getView ( position, convertView, parent );
        int colorPos = position % colors.length;
        view.setBackgroundColor ( colors[ colorPos ] );
        TextView text = ( TextView ) view.findViewById ( R.id.text1 );


        if ( colors[ colorPos ] == Color.parseColor ( "#000000" ) )
        {
            text.setTextColor ( Color.parseColor ( "#FFFFFF" ) );
        }
        else
        {
            text.setTextColor ( Color.parseColor ( "#000000" ) );
        }

        return view;
    }
}
