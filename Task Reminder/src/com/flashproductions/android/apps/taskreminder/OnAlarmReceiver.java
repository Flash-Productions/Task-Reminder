package com.flashproductions.android.apps.taskreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.flashproductions.android.apps.taskreminder.Adapters.ReminderDbAdapter;


public class OnAlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive ( Context context, Intent intent )
    {
        long rowid = intent.getExtras ().getLong ( ReminderDbAdapter.KEY_ROWID );

        WakeReminderIntentService.acquireStaticLock ( context );

        Intent i = new Intent ( context, ReminderService.class );
        i.putExtra ( ReminderDbAdapter.KEY_ROWID, rowid );
        context.startService ( i );
    }
}
