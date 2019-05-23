package com.atom.dashboardandroid.TaskList.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.atom.dashboardandroid.AddTaskActivity;
import com.atom.dashboardandroid.R;
import com.atom.dashboardandroid.Room.Entities.Task;
import com.atom.dashboardandroid.TaskList.ViewModel.TaskViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskListFragment extends Fragment {

    public static final String EDIT_TASK = "TaskListFragment.EditTask";

    private Orientation defaultOrientation = Orientation.VERTICAL;

    private TaskViewModel taskViewModel;
    private TaskListAdapter taskListAdapter;
    private RecyclerView.LayoutManager taskListLayoutManager;
    private OnActionListener onActionListener;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.task_list)
    RecyclerView taskListView;
    @BindView(R.id.btn_scroll_top)
    AppCompatButton btnScrollTop;
    @BindView(R.id.btn_add_task)
    AppCompatButton btnAddTask;

    public TaskListFragment(Orientation defaultOrientation) {
        super();
        this.defaultOrientation = defaultOrientation;
    }

    private void addListener() {
        onActionListener = new OnActionListener() {
            @Override
            public void OnShowDetail(Task task, int position) {
            }

            @Override
            public void OnDeleteTask(Task task, int position) {
                AlertDialog deleteAlertDialog = new AlertDialog.Builder(getContext())
                        .setMessage("Confirm to delete "+task.getTitle()+"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Toast.makeText(getContext(), "Item " + task.getTitle() + " Deleted", Toast.LENGTH_SHORT).show();
                                removeItem(task, position);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        }).create();
                deleteAlertDialog.show();
            }

            @Override
            public void OnEditTask(Task task) {
                editTask(task);
            }

            @Override
            public void OnTaskChange(Task task, int position) {
                changeItem(task, position);
            }
        };
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("REF", "onRefresh: OK");
                new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 3000);
            }
        });
        btnScrollTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskListView.smoothScrollToPosition(0);
            }
        });

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddTaskActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        taskListLayoutManager = new LinearLayoutManager(getContext(),
                this.defaultOrientation == Orientation.HORIZONTAL ?
                        RecyclerView.HORIZONTAL : RecyclerView.VERTICAL,
                false);
        taskListView.setLayoutManager(taskListLayoutManager);
        addListener();
        taskListAdapter = new TaskListAdapter(this.defaultOrientation, this.onActionListener);
        taskListView.setAdapter(taskListAdapter);
    }

    private void changeLayoutDimension(Orientation orientation) {
        Log.d("AAA", "changeLayoutDimension: " + orientation);
        if (orientation == Orientation.VERTICAL) {
            ((LinearLayoutManager) taskListLayoutManager).setOrientation(RecyclerView.VERTICAL);
        } else {
            ((LinearLayoutManager) taskListLayoutManager).setOrientation(RecyclerView.HORIZONTAL);
        }
        taskListAdapter = new TaskListAdapter(orientation, this.onActionListener);
        taskListView.setAdapter(taskListAdapter);
        taskListAdapter.notifyDataSetChanged();
    }
    private void changeItem(Task task,int position) {
        taskViewModel.update(task);
        taskListAdapter.notifyItemChanged(position);
        taskListAdapter.notifyDataSetChanged();
    }
    private void removeItem(Task task, int position) {
        taskViewModel.delete(task);
        taskListAdapter.notifyItemRemoved(position);
        taskListAdapter.notifyDataSetChanged();
    }

    public void editTask(Task task){
        Intent intent = new Intent(getActivity(), AddTaskActivity.class);
        intent.putExtra(EDIT_TASK,task);
        startActivity(intent);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        taskViewModel.getAll().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable final List<Task> tasks) {
                taskListAdapter.setTasks(tasks);
                taskListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_task_list, container, false);
        ButterKnife.bind(this, v);
        init();
        return v;
    }
}
