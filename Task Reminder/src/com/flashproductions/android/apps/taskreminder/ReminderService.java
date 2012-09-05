package com.flashproductions.android.apps.taskreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import com.flashproductions.android.apps.taskreminder.Adapters.ReminderDbAdapter;


public class ReminderService extends WakeReminderIntentService
{
    public ReminderService ()
    {
        super ( "ReminderService" );
    }


    private ReminderDbAdapter mDbHelper = new ReminderDbAdapter ( this );

    @Override
    void doReminderWork ( Intent intent )
    {
        mDbHelper.open ();

        Long rowId = intent.getExtras ().getLong ( ReminderDbAdapter.KEY_ROWID );

        NotificationManager mgr = ( NotificationManager ) getSystemService ( NOTIFICATION_SERVICE );

        Intent notificationIntent = new Intent ( this, ReminderViewActivity.class );
        notificationIntent.putExtra ( ReminderDbAdapter.KEY_ROWID, rowId );

        PendingIntent pi = PendingIntent.getActivity ( this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT );

        Cursor reminder = mDbHelper.fetchReminder ( rowId );


        Notification note =
                new Notification ( R.drawable.ic_launcher, getString ( R.string.notify_new_task_message ),
                                   System.currentTimeMillis () );

        note.setLatestEventInfo ( this,
                                  reminder.getString ( reminder.getColumnIndexOrThrow ( ReminderDbAdapter.KEY_TITLE ) ),
                                  getString ( R.string.notify_new_task_message ), pi );

        note.defaults |= Notification.DEFAULT_ALL;
        note.flags |= Notification.FLAG_AUTO_CANCEL;

        int id = ( int ) ( ( long ) rowId );
        mDbHelper.close ();
        mgr.notify ( id, note );
    }
}