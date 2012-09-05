package com.flashproductions.android.apps.taskreminder;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.flashproductions.android.apps.taskreminder.Adapters.ReminderDbAdapter;
import com.flashproductions.android.apps.taskreminder.DialogFragments.DateDialogFragment;
import com.google.ads.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ReminderEditActivity extends FragmentActivity implements AdListener
{

    DateDialogFragment frag;

    private AdView adView;

    private Button mDateButton;
    private Button mTimeButton;

    private static final int TIME_PICKER_DIALOG = 1;
    private Calendar mCalender;
    private static final String DATE_FORMAT = "MM-dd-yyyy";
    private static final String TIME_FORMAT = "kk:mm";


    private EditText mTitleText;
    private Button   mSaveButton;
    private EditText mBodyText;

    private ReminderDbAdapter mDbHelper;
    public static final String DATE_TIME_FORMAT = "MM-dd-yyy kk:mm:ss";

    private Long mRowId;


    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate ( savedInstanceState );

        mDbHelper = new ReminderDbAdapter ( this );

        setContentView ( R.layout.reminder_edit );

        // Create the adView
        adView = new AdView ( this, AdSize.BANNER, "a14f79d62bb5c4f" );


        // Lookup your LinearLayout assuming it’s been given
        // the attribute android:id="@+id/main"
        LinearLayout layout = ( LinearLayout ) findViewById ( R.id.reminderEdit );

        //Set the Ad Listener
        adView.setAdListener ( this );


        // Add the adView to it
        layout.addView ( adView );

        //Set new request
        AdRequest request = new AdRequest ();


        // Initiate a generic request to load it with an ad
        adView.loadAd ( request );

        mCalender = Calendar.getInstance ();

        mDateButton = ( Button ) findViewById ( R.id.reminder_date );
        mTimeButton = ( Button ) findViewById ( R.id.reminder_time );
        mSaveButton = ( Button ) findViewById ( R.id.save );
        mTitleText = ( EditText ) findViewById ( R.id.title );

        mBodyText = ( EditText ) findViewById ( R.id.body );

        mRowId = savedInstanceState != null ? savedInstanceState.getLong ( ReminderDbAdapter.KEY_ROWID ) : null;


        registerButtonListenersAndSetDefaultText ();
    }

    private void setRowIdFromIntent ()
    {
        if ( mRowId == null )
        {
            Bundle extras = getIntent ().getExtras ();
            mRowId = extras != null ? extras.getLong ( ReminderDbAdapter.KEY_ROWID ) : null;
        }
    }

    @Override
    protected void onPause ()
    {
        super.onPause ();
        mDbHelper.close ();
    }

    @Override
    protected void onResume ()
    {
        super.onResume ();
        mDbHelper.open ();
        setRowIdFromIntent ();
        populateFields ();
    }

    private void registerButtonListenersAndSetDefaultText ()
    {

        mDateButton.setOnClickListener ( new View.OnClickListener ()
        {
            @Override
            public void onClick ( View v )
            {
                showDialog ();
            }
        } );

        mTimeButton.setOnClickListener ( new View.OnClickListener ()
        {

            @Override
            public void onClick ( View v )
            {
                showDialog ( TIME_PICKER_DIALOG );
            }
        } );


        updateDateButtonText ();
        updateTimeButtonText ();


        mSaveButton.setOnClickListener ( new View.OnClickListener ()
        {
            @Override
            public void onClick ( View view )
            {
                if ( mTitleText.getText ().toString ().trim ().equals ( "" ) )
                {
                    mTitleText.setBackgroundColor ( Color.RED );
                    Toast.makeText ( ReminderEditActivity.this, R.string.empty_title, Toast.LENGTH_SHORT ).show ();
                    mTitleText.requestFocus ();
                }
                else if ( mBodyText.getText ().toString ().trim ().equals ( "" ) )
                {
                    mTitleText.setBackgroundColor ( Color.WHITE );
                    mBodyText.setBackgroundColor ( Color.RED );
                    Toast.makeText ( ReminderEditActivity.this, R.string.empty_description, Toast.LENGTH_SHORT )
                         .show ();
                    mBodyText.requestFocus ();
                }
                else
                {
                    saveState ();
                    setResult ( RESULT_OK );
                    Toast.makeText ( ReminderEditActivity.this, getString ( R.string.task_saved_msg ),
                                     Toast.LENGTH_SHORT )
                         .show ();
                    finish ();
                }
            }
        } );

    }

    public void showDialog ()
    {
        FragmentTransaction ft = getSupportFragmentManager ().beginTransaction (); //get the fragment
        frag = DateDialogFragment.newInstance ( this, new DateDialogFragmentListener ()
        {

            public void updateChangedDate ( int year, int month, int day )
            {
                mDateButton.setText (
                        String.valueOf ( month + 1 ) + "-" + String.valueOf ( day ) + "-" + String.valueOf ( year ) );
                mCalender.set ( year, month, day );
            }
        }, mCalender );

        frag.show ( ft, "DateDialogFragment" );
    }

    public interface DateDialogFragmentListener
    {
        //this interface is a listener between the Date Dialog fragment and the activity to update the buttons date
        public void updateChangedDate ( int year, int month, int day );
    }


    @Override
    protected Dialog onCreateDialog ( int id )
    {
        switch ( id )
        {

            case TIME_PICKER_DIALOG:
                return showTimePicker ();
        }
        return super.onCreateDialog ( id );
    }


    private TimePickerDialog showTimePicker ()
    {

        return new TimePickerDialog ( this, new TimePickerDialog.OnTimeSetListener ()
        {
            @Override
            public void onTimeSet ( TimePicker view, int hourOfDay, int minute )
            {
                mCalender.set ( Calendar.HOUR_OF_DAY, hourOfDay );
                mCalender.set ( Calendar.MINUTE, minute );
                updateTimeButtonText ();
            }
        }, mCalender.get ( Calendar.HOUR_OF_DAY ), mCalender.get ( Calendar.MINUTE ), true );
    }

    private void updateDateButtonText ()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat ( DATE_FORMAT );
        String dateForButton = dateFormat.format ( mCalender.getTime () );
        mDateButton.setText ( dateForButton );
    }

    private void updateTimeButtonText ()
    {
        SimpleDateFormat timeFormat = new SimpleDateFormat ( TIME_FORMAT );
        String timeForButton = timeFormat.format ( mCalender.getTime () );
        mTimeButton.setText ( timeForButton );
    }

    private void populateFields ()
    {
        if ( mRowId != null )
        {

            Cursor reminder = mDbHelper.fetchReminder ( mRowId );
            startManagingCursor ( reminder );

            mTitleText
                    .setText ( reminder.getString ( reminder.getColumnIndexOrThrow ( ReminderDbAdapter.KEY_TITLE ) ) );
            mBodyText.setText ( reminder.getString ( reminder.getColumnIndexOrThrow ( ReminderDbAdapter.KEY_BODY ) ) );
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat ( DATE_TIME_FORMAT );
            Date date;
            try
            {
                String dateString =
                        reminder.getString ( reminder.getColumnIndexOrThrow ( ReminderDbAdapter.KEY_DATE_TIME ) );
                date = dateTimeFormat.parse ( dateString );
                mCalender.setTime ( date );
            }
            catch ( ParseException e )
            {
                Log.e ( "ReminderEditActivity", e.getMessage (), e );
            }

        }
        else
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences ( this );
            String defaultTitleKey = getString ( R.string.pref_task_title_key );
            String defaultTimeKey = getString ( R.string.pref_default_time_from_now_key );

            String defaultTitle = prefs.getString ( defaultTitleKey, "" );
            String defaultTime = prefs.getString ( defaultTimeKey, "" );
            if ( ! "".equals ( defaultTitle ) )
            { mTitleText.setText ( defaultTitle ); }
            if ( ! "".equals ( defaultTime ) )
            { mCalender.add ( Calendar.MINUTE, Integer.parseInt ( defaultTime ) ); }
        }

        updateDateButtonText ();
        updateTimeButtonText ();
    }

    @Override
    protected void onSaveInstanceState ( Bundle outState )
    {
        super.onSaveInstanceState ( outState );
        outState.putLong ( ReminderDbAdapter.KEY_ROWID, mRowId );
    }

    private void saveState ()
    {


        String title = mTitleText.getText ().toString ();
        String body = mBodyText.getText ().toString ();

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat ( DATE_TIME_FORMAT );
        String reminderDateTime = dateTimeFormat.format ( mCalender.getTime () );


        if ( mRowId == null )
        {
            long id = mDbHelper.createReminder ( title, body, reminderDateTime );

            if ( id > 0 )

            {
                mRowId = id;
            }
        }
        else
        {
            mDbHelper.updateReminder ( mRowId, title, body, reminderDateTime );
        }

        new ReminderManager ( this ).setReminder ( mRowId, mCalender );

    }

    @Override
    public void onReceiveAd ( Ad ad )
    {

    }

    @Override
    public void onFailedToReceiveAd ( Ad ad, AdRequest.ErrorCode errorCode )
    {
        AdRequest request = new AdRequest ();
        adView.loadAd ( request );
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onPresentScreen ( Ad ad )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onDismissScreen ( Ad ad )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onLeaveApplication ( Ad ad )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}