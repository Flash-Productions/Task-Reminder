package com.flashproductions.android.apps.taskreminder;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.text.method.DigitsKeyListener;

/**
 * Created by Flash Productions.
 * Date: 3/14/12
 * Time: 4:20 PM
 */
public class TaskPreferences extends PreferenceActivity
{


    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate ( savedInstanceState );
        addPreferencesFromResource ( R.xml.task_preferences );

        EditTextPreference timeDefault =
                ( EditTextPreference ) findPreference ( getString ( R.string.pref_default_time_from_now_key ) );

        timeDefault.getEditText ().setKeyListener ( DigitsKeyListener.getInstance () );

        ReminderListActivity list = new ReminderListActivity ();

        list.finish ();


    }

    @Override
    protected void onPause ()
    {
        Intent inList = new Intent ( this, ReminderListActivity.class );
        startActivity ( inList );


        super.onPause ();
    }


}