package com.flashproductions.android.apps.taskreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import com.flashproductions.android.apps.taskreminder.Adapters.ReminderDbAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class OnBootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive ( Context context, Intent intent )
    {
        ReminderManager reminderMgr = new ReminderManager ( context );
        ReminderDbAdapter dbHelper = new ReminderDbAdapter ( context );
        dbHelper.open ();
        Cursor cursor = dbHelper.fetchAllReminders ();

        if ( cursor != null )
        {
            cursor.moveToFirst ();

            int rowIdColumnIndex = cursor.getColumnIndex ( ReminderDbAdapter.KEY_ROWID );
            int dateTimeColumnIndex = cursor.getColumnIndex ( ReminderDbAdapter.KEY_DATE_TIME );

            while ( cursor.isAfterLast () == false )
            {
                Long rowId = cursor.getLong ( rowIdColumnIndex );
                String dateTime = cursor.getString ( dateTimeColumnIndex );

                Calendar cal = Calendar.getInstance ();
                SimpleDateFormat format = new SimpleDateFormat ( ReminderEditActivity.DATE_TIME_FORMAT );

                try
                {
                    java.util.Date date = format.parse ( dateTime );
                    cal.setTime ( date );
                    reminderMgr.setReminder ( rowId, cal );
                }
                catch ( ParseException e )
                {
                    Log.e ( "OnBootReciever", e.getMessage (), e );
                }

                cursor.moveToNext ();
            }

            cursor.close ();
        }

        dbHelper.close ();
    }
}
