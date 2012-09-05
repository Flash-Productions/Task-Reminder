package com.flashproductions.android.apps.taskreminder;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.flashproductions.android.apps.taskreminder.Adapters.CustomListAdapter;
import com.flashproductions.android.apps.taskreminder.Adapters.ReminderDbAdapter;

public class ReminderListActivity extends ListActivity
{
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT   = 1;
    private static final int ACTIVITY_VIEW   = 2;

    private ReminderDbAdapter mDbHelper;


    @Override
    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.reminder_list );


        Intent mc = new Intent ( this, ColorTheme.class );
        startActivityForResult ( mc, 1 );


        mDbHelper = new ReminderDbAdapter ( this );
        mDbHelper.open ();
        fillData ();
        registerForContextMenu ( getListView () );


    }


    private void fillData ()
    {
        Cursor remindersCursor = mDbHelper.fetchAllReminders ();


        // Create an array to specify the fields we want (ont the TITLE)
        String[] from = new String[] { ReminderDbAdapter.KEY_TITLE };

        // and an array of the fields we want to bind in the view
        int[] to = new int[] { R.id.text1 };

        // Now create a simple cursor adapter and set it to display

        CustomListAdapter reminders =
                new CustomListAdapter ( this, R.layout.reminder_row, remindersCursor, from, to );

        setListAdapter ( reminders );


    }

    @Override
    protected void onListItemClick ( ListView l, View v, int position, long id )
    {
        super.onListItemClick ( l, v, position, id );
        Intent i = new Intent ( this, ReminderViewActivity.class );
        i.putExtra ( ReminderDbAdapter.KEY_ROWID, id );
        startActivityForResult ( i, ACTIVITY_VIEW );
    }

    @Override
    public void onCreateContextMenu ( ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo )
    {
        super.onCreateContextMenu ( menu, v, menuInfo );
        MenuInflater mi = getMenuInflater ();
        mi.inflate ( R.menu.list_menu_item_longpress, menu );
    }

    @Override
    public boolean onCreateOptionsMenu ( Menu menu )
    {
        super.onCreateOptionsMenu ( menu );
        MenuInflater mi = getMenuInflater ();
        mi.inflate ( R.menu.list_menu, menu );
        return true;
    }

    @Override
    public boolean onMenuItemSelected ( int featureId, MenuItem item )
    {
        switch ( item.getItemId () )
        {
            case R.id.menu_add:
                createReminder ();
                return true;
            case R.id.menu_settings:
                Intent i = new Intent ( this, TaskPreferences.class );
                startActivity ( i );
                return true;
        }

        return super.onMenuItemSelected ( featureId, item );
    }


    private void createReminder ()
    {
        Intent i = new Intent ( this, ReminderEditActivity.class );
        startActivityForResult ( i, ACTIVITY_CREATE );
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent intent )
    {
        fillData ();
        super.onActivityResult ( requestCode, resultCode, intent );
    }


    public boolean onContextItemSelected ( MenuItem item )
    {

        AdapterView.AdapterContextMenuInfo info = ( AdapterView.AdapterContextMenuInfo ) item.getMenuInfo ();
        NotificationManager note = ( NotificationManager ) getSystemService ( NOTIFICATION_SERVICE );

        switch ( item.getItemId () )
        {

            case R.id.menu_edit:
                Intent i = new Intent ( this, ReminderEditActivity.class );
                i.putExtra ( ReminderDbAdapter.KEY_ROWID, info.id );
                startActivityForResult ( i, ACTIVITY_EDIT );
                return true;


            case R.id.menu_delete:
                mDbHelper.deleteReminder ( info.id );
                int id = ( int ) ( ( long ) info.id );
                note.cancel ( id );
                fillData ();
                return true;
        }

        return super.onContextItemSelected ( item );
    }


}