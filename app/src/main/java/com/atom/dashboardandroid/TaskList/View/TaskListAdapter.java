package com.atom.dashboardandroid.TaskList.View;

import android.animation.ValueAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.atom.dashboardandroid.R;
import com.atom.dashboardandroid.Room.Entities.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.Holder> {
    private static final String TAG = "ATOMDEBUG";
    private List<Task> tasks = new ArrayList<>();
    private final OnActionListener listener;
    private Orientation orientation;
    private boolean ON_BIND;
    public TaskListAdapter(Orientation orientation, OnActionListener listener) {
        this.listener = listener;
        this.orientation = orientation;
    }

    @Override
    @NonNull
    public TaskListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View v;
        if (this.orientation == Orientation.HORIZONTAL) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_item_horizontal, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.text_row_item, parent, false);
        }
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(position, listener);
    }

    @Override
    public int getItemCount() {
        if(tasks!=null)
            return tasks.size();
        else
            return 0;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        super.notifyDataSetChanged();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
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
        @BindView(R.id.text_row_container)
        ViewGroup container;
        private int normalVisibility;
        ValueAnimator valueAnimator;

        Holder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            if (orientation == Orientation.HORIZONTAL) {
                normalVisibility = View.VISIBLE;
            } else {
                normalVisibility = View.GONE;
            }
            valueAnimator = ValueAnimator.ofFloat(0.2f,1.0f);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.setDuration(500);
        }

        void bind(int position, final OnActionListener actionListener) {
            ON_BIND = true;
            Task currentTask = tasks.get(position);
            this.cb_complete.setChecked(currentTask.getComplete());
            this.textView_title.setText(currentTask.getTitle());
            this.textView_content.setText(currentTask.getContent());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if(!ON_BIND){
                        float progress = (float) animation.getAnimatedValue();
                        container.setAlpha(progress);
                    }
                }
            });
            this.cb_complete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d(TAG, "onCheckedChanged: "+(isChecked?"true":"false"));
                    if(!ON_BIND){
                        currentTask.setComplete(isChecked);
                        actionListener.OnTaskChange(currentTask, position);
                    }
                }
            });
            this.btnEditTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                actionListener.OnEditTask(currentTask);
                }
            });
            this.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    valueAnimator.start();
                    actionListener.OnShowDetail(currentTask, position);
                    if (normalVisibility == textView_content.getVisibility()) {
                        textView_content.setVisibility(View.VISIBLE);
                        btnEditTask.setVisibility(View.VISIBLE);
                    } else {
                        textView_content.setVisibility(View.GONE);
                        btnEditTask.setVisibility(View.GONE);
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
