package com.example.finflow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity1 extends AppCompatActivity {

    private RecyclerView mList;
    private SimpleAdapter mAdapter;
    private androidx.appcompat.widget.Toolbar mToolbar;
    private TextView mNoReminderView;
    private FloatingActionButton mAddReminderButton;
    private int mTempPost;
    private ReminderDatabase rb;
    private MultiSelector mMultiSelector = new MultiSelector();
    private AlarmReceiver mAlarmReceiver;

    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 123;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, you can proceed with showing notifications
                } else {
                    // Permission denied, handle the case where the user didn't grant the permission
                }
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        // Initialize reminder database
        rb = new ReminderDatabase(getApplicationContext());

        // Initialize views
//        mToolbar = findViewById(R.id.toolbar);
        mAddReminderButton = findViewById(R.id.add_reminder);
        mList = findViewById(R.id.reminder_list);
        mNoReminderView = findViewById(R.id.no_reminder_text);
        if (mNoReminderView != null) {
            mNoReminderView.setVisibility(View.VISIBLE);
        } else {
            Log.e("MainActivity1", "TextView no_reminder_text not found");
        }

        // Create recycler view
        mList.setLayoutManager(getLayoutManager());
        registerForContextMenu(mList);
        mAdapter = new SimpleAdapter();
        mList.setAdapter(mAdapter);

        // Setup toolbar
//        mToolbar.setTitle(R.string.app_name);

        // On clicking the floating action button
        mAddReminderButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ReminderAddActivity.class);
            startActivity(intent);
        });

        // Initialize alarm
        mAlarmReceiver = new AlarmReceiver();
    }

    private void setAlarm(Calendar calendar, int ID) {
        // Set alarm with notification channel ID
        mAlarmReceiver.setAlarm(getApplicationContext(), calendar, ID);
    }

    private void setRepeatAlarm(Calendar calendar, int ID, long RepeatTime) {
        // Set repeat alarm with notification channel ID
        mAlarmReceiver.setRepeatAlarm(getApplicationContext(), calendar, ID, RepeatTime);
    }

    // Create context menu for long press actions
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
    }

    // Multi select items in recycler view
    private final ActionMode.Callback mDeleteMode = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                // On clicking discard reminders
                case R.id.discard_reminder:
                    // Close the context menu
                    actionMode.finish();
                    // Handle deleting reminders
                    deleteSelectedReminders();
                    return true;

                // On clicking save reminders
                case R.id.save_reminder:
                    // Close the context menu
                    actionMode.finish();
                    return true;

                default:
                    break;
            }
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {

        }
    };

    // Method to delete selected reminders
    private void deleteSelectedReminders() {
        // Get selected reminders
        List<Integer> selectedIndexes = mMultiSelector.getSelectedIndexes();
        // Loop through selected reminders and delete them
        for (int index : selectedIndexes) {
            Reminder reminder = mAdapter.getReminder(index);
            rb.deleteReminder(reminder);
            mAlarmReceiver.cancelAlarm(getApplicationContext(), reminder.getID());
        }
        // Clear selected items in recycler view
        mMultiSelector.clearSelections();
        // Refresh the list
        mAdapter.refreshList(rb.getAllReminders());
        // Show message if no reminders left
        if (mAdapter.getItemCount() == 0) {
            mNoReminderView.setVisibility(View.VISIBLE);
        }
        // Display toast to confirm delete
        Toast.makeText(getApplicationContext(),
                "Deleted",
                Toast.LENGTH_SHORT).show();
    }

    // On clicking a reminder item
    private void selectReminder(int mClickID) {
        String mStringClickID = Integer.toString(mClickID);

        // Create intent to edit the reminder
        // Put reminder id as extra
        Intent i = new Intent(this, ReminderEditActivity.class);
        i.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, mStringClickID);
        startActivityForResult(i, 1);
    }

    // Recreate recycler view
    // This is done so that newly created reminders are displayed
    @Override
    public void onResume() {
        super.onResume();

        // Load reminders from the database
        List<Reminder> reminders = rb.getAllReminders();

        // Update the RecyclerView with loaded reminders
        mAdapter.refreshList(reminders);

        // Show or hide the "no reminders" message based on the number of reminders
        if (reminders.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        } else {
            mNoReminderView.setVisibility(View.GONE);
        }
    }

    // Layout manager for recycler view
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    // Adapter class for recycler view
    public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.VerticalItemHolder> {
        private List<Reminder> mReminders;

        public SimpleAdapter() {
            mReminders = new ArrayList<>();
        }

        // Method to set the list of reminders and notify the adapter of changes
        public void refreshList(List<Reminder> reminders) {
            mReminders.clear();
            mReminders.addAll(reminders);
            notifyDataSetChanged();
        }

        // Method to get a reminder at a specific position
        public Reminder getReminder(int position) {
            if (position >= 0 && position < mReminders.size()) {
                return mReminders.get(position);
            }
            return null;
        }

        // View holder for recycler view items
        @Override
        public VerticalItemHolder onCreateViewHolder(ViewGroup container, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View root = inflater.inflate(R.layout.recycle_items, container, false);
            return new VerticalItemHolder(root);
        }

        @Override
        public void onBindViewHolder(VerticalItemHolder itemHolder, int position) {
            Reminder reminder = mReminders.get(position);
            itemHolder.itemView.setTag(position);
            itemHolder.bind(reminder);
        }

        @Override
        public int getItemCount() {
            return mReminders.size();
        }

        // Class for recycler view items
        public class VerticalItemHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, View.OnLongClickListener {
            private TextView mTitleText, mDateAndTimeText, mRepeatInfoText;
            private ImageView mActiveImage;

            public VerticalItemHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
                itemView.setLongClickable(true);

                // Initialize views
                mTitleText = itemView.findViewById(R.id.recycle_title);
                mDateAndTimeText = itemView.findViewById(R.id.recycle_date_time);
                mRepeatInfoText = itemView.findViewById(R.id.recycle_repeat_info);
                mActiveImage = itemView.findViewById(R.id.active_image);
            }

            // Bind reminder data to views
            public void bind(Reminder reminder) {
                mTitleText.setText(reminder.getTitle());
                mDateAndTimeText.setText(reminder.getDate() + " " + reminder.getTime());
                if (reminder.isRepeat()) {
                    mRepeatInfoText.setText("Every " + reminder.getRepeatNo() + " " + reminder.getRepeatType() + "(s)");
                    Calendar calendar = Calendar.getInstance();
                    // Set the calendar to the date and time of the reminder
                    // ... (set calendar properties based on your reminder data)
                    mAlarmReceiver.setRepeatAlarm(MainActivity1.this, calendar, reminder.getID(), reminder.getID());
                } else {
                    mRepeatInfoText.setText("Repeat Off");
                    Calendar calendar = Calendar.getInstance();
                    // Set the calendar to the date and time of the reminder
                    // ... (set calendar properties based on your reminder data)
                    mAlarmReceiver.setAlarm(MainActivity1.this, calendar, reminder.getID());
                }
                if (reminder.isActive()) {
                    mActiveImage.setImageResource(R.drawable.ic_notifications_on_white_24dp);
                } else {
                    mActiveImage.setImageResource(R.drawable.ic_notifications_off_grey600_24dp);
                }
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Reminder reminder = mReminders.get(position);
                selectReminder(reminder.getID());
            }

            @Override
            public boolean onLongClick(View v) {
                mMultiSelector.setSelected(itemView, true);
                startSupportActionMode(mDeleteMode);
                mMultiSelector.setSelected(this, true);
                return true;
            }
        }
    }

    // Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Setup menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // start licenses activity
            case R.id.user_profile:
                Intent intent = new Intent(this, UserProfile.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
