package com.atom.dashboardandroid.TaskList.View;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.atom.dashboardandroid.R;
import com.atom.dashboardandroid.Room.Entities.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.Holder> {
    private static final String TAG = "TASK_LIST_ADAPTER";
    private List<Task> tasks = new ArrayList<>();
    private final OnActionListener listener;
    private boolean ON_BIND;
    Context context;

    public TaskListAdapter(OnActionListener listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    @NonNull
    public TaskListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row_item, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(position, listener);
    }

    @Override
    public int getItemCount() {
        if (tasks != null)
            return tasks.size();
        else
            return 0;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public void insertTask(Task task) {
        tasks.add(task);
        notifyItemInserted(getItemCount());
    }

    public void updateTask(Task task) {
        int index = -1;
        for (int i = 0; i < tasks.size(); i++) {
            if (task.getId() == tasks.get(i).getId()) index = i;
        }
        notifyItemChanged(index);
    }

    public void deleteTask(Task task) {
        int index = tasks.indexOf(task);
        tasks.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, getItemCount());
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.check_box_complete)
        CheckBox cb_complete;
        @BindView(R.id.row_title)
        TextView textView_title;
        @BindView(R.id.row_content)
        TextView textView_content;
        @BindView(R.id.btn_edit_task)
        AppCompatButton btnEditTask;
        @BindView(R.id.task_date)
        TextView textView_date;
        @BindView(R.id.text_row_container)
        ViewGroup container;
        private int normalVisibility;
        ValueAnimator blinkAnimator;
        boolean taskIsDone;

        Holder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            normalVisibility = View.GONE;
            setupAnimation();
        }

        void setupAnimation() {
            blinkAnimator = ValueAnimator.ofFloat(0.2f, 1.0f);
            blinkAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            blinkAnimator.setDuration(500);
        }

        void bind(int position, final OnActionListener actionListener) {
            ON_BIND = true;
            SimpleDateFormat dateFormat;
            Calendar calendar = Calendar.getInstance();
            Task currentTask = tasks.get(position);
            taskIsDone = currentTask.getState().compareTo(Task.COMPLETED) == 0;
            boolean contentIsEmpty = currentTask.getContent().compareTo("") == 0;
            if (taskIsDone || contentIsEmpty) {
                normalVisibility = View.GONE;
            } else {
                normalVisibility = View.VISIBLE;
            }
            int textColor = ContextCompat.getColor(context, taskIsDone ? R.color.textColorSecondary : R.color.textColorPrimary);
            if (taskIsDone)
                this.textView_title.setPaintFlags(textView_title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            else
                this.textView_title.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
            this.textView_title.setTextColor(textColor);
            this.textView_title.setText(currentTask.getTitle());
            blinkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (!ON_BIND) {
                        float progress = (float) animation.getAnimatedValue();
                        container.setAlpha(progress);
                    }
                }
            });
            this.cb_complete.setChecked(taskIsDone);
            this.cb_complete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!ON_BIND) {
                        currentTask.setState(isChecked ? Task.COMPLETED : Task.UNCOMPLETED);
                        actionListener.OnTaskChange(currentTask, position);
                    }
                }
            });
            this.btnEditTask.setVisibility(normalVisibility);
            this.btnEditTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionListener.OnEditTask(currentTask);
                }
            });
            this.textView_content.setTextColor(textColor);
            if (taskIsDone)
                this.textView_content.setPaintFlags(textView_content.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            else
                this.textView_content.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
            this.textView_content.setVisibility(normalVisibility);
            this.textView_content.setText(currentTask.getContent());
            if (currentTask.getDate() != null) {
                Calendar taskDate = Calendar.getInstance();
                taskDate.setTimeInMillis(currentTask.getDate().getTime());
                if (calendar.get(Calendar.YEAR) == taskDate.get(Calendar.YEAR)
                        && calendar.get(Calendar.MONTH) == taskDate.get(Calendar.MONTH)
                        && calendar.get(Calendar.DAY_OF_MONTH) == taskDate.get(Calendar.DAY_OF_MONTH)) {
                    dateFormat = new SimpleDateFormat("HH:mm");
                } else {
                    dateFormat = new SimpleDateFormat("HH:mm dd/MM");
                }
                this.textView_date.setText(dateFormat.format(currentTask.getDate()));
            }
            Drawable background;
            if (taskIsDone)
                background = ContextCompat.getDrawable(context, R.drawable.round_corners_bg_disable);
            else
                background = ContextCompat.getDrawable(context, R.drawable.round_corners_bg);
            this.container.setBackground(background);
            this.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    blinkAnimator.start();
                    actionListener.OnShowDetail(currentTask, position);
                    if (textView_content.getVisibility() == View.GONE) {
                        if (!taskIsDone) btnEditTask.setVisibility(View.VISIBLE);
                        textView_content.setVisibility(View.VISIBLE);
                    } else {
                        textView_content.setVisibility(View.GONE);
                        if (!taskIsDone) btnEditTask.setVisibility(View.GONE);
                    }
                }
            });
            this.container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    actionListener.OnDeleteTask(currentTask, position);
                    return true;
                }
            });
            ON_BIND = false;
        }

    }

}
