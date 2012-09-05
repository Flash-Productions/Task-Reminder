package com.flashproductions.android.apps.taskreminder;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.flashproductions.android.apps.taskreminder.Adapters.ReminderDbAdapter;
import com.google.ads.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ReminderViewActivity extends Activity implements AdListener
{
    private AdView adView;

    private Button mDateButton;
    private Button mTimeButton;

    private Calendar mCalender;
    private static final String DATE_FORMAT = "MM-dd-yyyy";
    private static final String TIME_FORMAT = "kk:mm";


    private EditText mTitleText;
    private Button   mEditButton;
    private Button   mDeleteButton;
    private EditText mBodyText;
    private Button   mBackButton;

    private ReminderDbAdapter mDbHelper;
    public static final String DATE_TIME_FORMAT = "MM-dd-yyy kk:mm:ss";

    private Long mRowId;


    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate ( savedInstanceState );


        NotificationManager note = ( NotificationManager ) getSystemService ( NOTIFICATION_SERVICE );

        mDbHelper = new ReminderDbAdapter ( this );

        setContentView ( R.layout.reminder_view );

        mDateButton = ( Button ) findViewById ( R.id.reminder_date );
        mTimeButton = ( Button ) findViewById ( R.id.reminder_time );
        mEditButton = ( Button ) findViewById ( R.id.edit );
        mDeleteButton = ( Button ) findViewById ( R.id.delete );
        mTitleText = ( EditText ) findViewById ( R.id.title );
        mBackButton = ( Button ) findViewById ( R.id.back_to_list );

        // Create the adView
        adView = new AdView ( this, AdSize.BANNER, "a14f79d62bb5c4f" );


        // Lookup your LinearLayout assuming it’s been given
        // the attribute android:id="@+id/main"
        RelativeLayout layout = ( RelativeLayout ) findViewById ( R.id.reminderView );

        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams ( RelativeLayout.LayoutParams.FILL_PARENT,
                                                  RelativeLayout.LayoutParams.WRAP_CONTENT );

        layoutParams.addRule ( RelativeLayout.ALIGN_LEFT, mEditButton.getId () );
        layoutParams.addRule ( RelativeLayout.BELOW, mEditButton.getId () );

        //Set the Ad Listener
        adView.setAdListener ( this );


        // Add the adView to it
        layout.addView ( adView, layoutParams );


        //Set new request
        AdRequest request = new AdRequest ();


        // Initiate a generic request to load it with an ad
        adView.loadAd ( request );

        mCalender = Calendar.getInstance ();


        mBodyText = ( EditText ) findViewById ( R.id.body );

        mRowId = savedInstanceState != null ? savedInstanceState.getLong ( ReminderDbAdapter.KEY_ROWID ) : null;

        setRowIdFromIntent ();
        int id = ( int ) ( ( long ) mRowId );
        note.cancel ( id );

        mTitleText.setEnabled ( false );
        mTitleText.setFocusable ( false );
        mTitleText.setTextColor ( Color.WHITE );
        mBodyText.setEnabled ( false );
        mBodyText.setFocusable ( false );
        mBodyText.setTextColor ( Color.WHITE );

        mDateButton.setEnabled ( false );
        mDateButton.setFocusable ( false );
        mDateButton.setTextColor ( Color.WHITE );
        mTimeButton.setEnabled ( false );
        mTimeButton.setFocusable ( false );
        mTimeButton.setTextColor ( Color.WHITE );


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


        mEditButton.setOnClickListener ( new View.OnClickListener ()
        {
            @Override
            public void onClick ( View view )
            {

                Intent i = new Intent ( ReminderViewActivity.this, ReminderEditActivity.class );
                i.putExtra ( ReminderDbAdapter.KEY_ROWID, mRowId );
                startActivity ( i );
                populateFields ();

            }
        } );

        mBackButton.setOnClickListener ( new View.OnClickListener ()
        {
            @Override
            public void onClick ( View view )
            {
                setResult ( RESULT_OK );
                finish ();
            }
        } );

        mDeleteButton.setOnClickListener ( new View.OnClickListener ()
        {
            NotificationManager note = ( NotificationManager ) getSystemService ( NOTIFICATION_SERVICE );

            @Override
            public void onClick ( View view )
            {
                mDbHelper.deleteReminder ( mRowId );
                setRowIdFromIntent ();
                int id = ( int ) ( ( long ) mRowId );
                note.cancel ( id );
                setResult ( RESULT_OK );
                finish ();

            }
        } );

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

        updateDateButtonText ();
        updateTimeButtonText ();


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