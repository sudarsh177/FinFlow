package com.example.finflow.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finflow.R;
import com.example.finflow.Reminder;

import java.util.List;

class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;
    private Context context;

    public ReminderAdapter(Context context, List<Reminder> reminderList) {
        this.context = context;
        this.reminderList = reminderList;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_items, parent, false);
        return new ReminderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.title.setText(reminder.getTitle());
        holder.dateTime.setText(reminder.getDate() + " " + reminder.getTime());
        holder.repeatInfo.setText("Repeat: " + reminder.getRepeatNo() + " " + reminder.getRepeatType());
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder {
        public TextView title, dateTime, repeatInfo;

        public ReminderViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.recycle_title);
            dateTime = view.findViewById(R.id.recycle_date_time);
            repeatInfo = view.findViewById(R.id.recycle_repeat_info);
        }
    }
}
