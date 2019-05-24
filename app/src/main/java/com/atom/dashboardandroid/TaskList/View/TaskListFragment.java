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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TaskListFragment extends Fragment {
    public static final String TAG = "TASK_LIST_FRAGMENT";
    public static final String EDIT_TASK = "TaskListFragment.EditTask";

    CompositeDisposable compositeDisposable = new CompositeDisposable();
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

    private void addListener() {
        Disposable disposableGetAllTask = taskViewModel.getListTaskBehaviorSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<List<Task>>() {
                    @Override
                    public void accept(List<Task> tasks) throws Exception {
                        taskListAdapter.setTasks(tasks);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, throwable.getMessage());
                    }
                });
        Disposable disposableInsertTask = taskViewModel.getInsertBehaviorSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<TaskViewModel.InsertResponse>() {
                    @Override
                    public void accept(TaskViewModel.InsertResponse insertRespose) throws Exception {
                        Task t = insertRespose.getTask();
                        taskListAdapter.insertTask(t);
                        Toast.makeText(getContext(), "Item " + t.getTitle() + " Inserted", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, throwable.getMessage());
                    }
                });
        Disposable disposableDeleteTask = taskViewModel.getDeleteBehaviorSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<TaskViewModel.DeleteResponse>() {
                    @Override
                    public void accept(TaskViewModel.DeleteResponse deleteResponse) throws Exception {
                        Task t = deleteResponse.getTask();
                        taskListAdapter.deleteTask(t);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, throwable.getMessage());
                    }
                });
        Disposable disposableUpdateTask = taskViewModel.getUpdateBehaviorSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<TaskViewModel.UpdateResponse>() {
                    @Override
                    public void accept(TaskViewModel.UpdateResponse updateResponse) throws Exception {
                        Task t = updateResponse.getTask();
                        taskListAdapter.updateTask(t);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, throwable.getMessage());
                    }
                });
        this.compositeDisposable.add(disposableGetAllTask);
        this.compositeDisposable.add(disposableInsertTask);
        this.compositeDisposable.add(disposableDeleteTask);
        this.compositeDisposable.add(disposableUpdateTask);
        onActionListener = new OnActionListener() {
            @Override
            public void OnShowDetail(Task task, int position) {
            }

            @Override
            public void OnDeleteTask(Task task, int position) {
                AlertDialog deleteAlertDialog = new AlertDialog.Builder(getContext())
                        .setMessage("Confirm to delete " + task.getTitle() + "?")
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
        taskListLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        taskListView.setLayoutManager(taskListLayoutManager);
        addListener();
        taskListAdapter = new TaskListAdapter(this.onActionListener);
        taskListView.setAdapter(taskListAdapter);
    }

    private void changeItem(Task task, int position) {
        taskViewModel.update(task);
        taskListAdapter.notifyItemChanged(position);
    }

    private void removeItem(Task task, int position) {
        taskViewModel.delete(task);
        taskListAdapter.notifyItemRemoved(position);
    }

    public void editTask(Task task) {
        Intent intent = new Intent(getActivity(), AddTaskActivity.class);
        intent.putExtra(EDIT_TASK, task);
        startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
