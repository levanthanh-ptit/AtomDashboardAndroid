package com.atom.dashboardandroid.TaskList.View;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atom.dashboardandroid.MainActivityListener;
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
    private TASKLIST_TYPE taskList_type = TASKLIST_TYPE.COMPLETE;
    private boolean ON_INIT;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private TaskViewModel taskViewModel;
    private TaskListAdapter taskListAdapter;
    private RecyclerView.LayoutManager taskListLayoutManager;
    private OnActionListener onActionListener;
    private MainActivityListener mainActivityListener;
    @BindView(R.id.task_list)
    RecyclerView taskListView;

    public TaskListFragment() {
        super();
    }

    public TaskListFragment(TASKLIST_TYPE taskList_type, MainActivityListener mainActivityListener) {
        super();
        this.taskList_type = taskList_type;
        this.mainActivityListener = mainActivityListener;
    }

    private void addListener() {
        switch (taskList_type) {
            case ALL: {
                Disposable disposableGetAllTasks = taskViewModel.getListTaskBehaviorSubject()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Consumer<List<Task>>() {
                            @Override
                            public void accept(List<Task> tasks) throws Exception {
                                if (!ON_INIT) taskListAdapter.setTasks(tasks);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.d(TAG, throwable.getMessage());
                            }
                        });
                this.compositeDisposable.add(disposableGetAllTasks);
                break;
            }
            case COMPLETE: {
                Disposable disposableGetAllCompletedTasks = taskViewModel.getListCompletedTaskBehaviorSubject()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Consumer<List<Task>>() {
                            @Override
                            public void accept(List<Task> tasks) throws Exception {
                                if (!ON_INIT) taskListAdapter.setTasks(tasks);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.d(TAG, throwable.getMessage());
                            }
                        });
                this.compositeDisposable.add(disposableGetAllCompletedTasks);
                break;
            }
            case UNCOMPLETE: {
                Disposable disposableGetAllUncompletedTasks = taskViewModel.getListUncompletedTaskBehaviorSubject()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Consumer<List<Task>>() {
                            @Override
                            public void accept(List<Task> tasks) throws Exception {

                                if (!ON_INIT){
                                    taskListAdapter.setTasks(tasks);
                                    if(tasks.size()>0) {
                                        int grey = ContextCompat.getColor(getContext(),R.color.bg_grey);
                                        TaskListFragment.this.taskListView.setBackgroundColor(grey);
                                    }else
                                    {
                                        Drawable drawable = ContextCompat.getDrawable(getContext(),R.drawable.empty_task_list_bg);
                                        TaskListFragment.this.taskListView.setBackground(drawable);
                                    }
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.d(TAG, throwable.getMessage());
                            }
                        });
                this.compositeDisposable.add(disposableGetAllUncompletedTasks);
                break;
            }
        }

        Disposable disposableInsertTask = taskViewModel.getInsertBehaviorSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<TaskViewModel.InsertResponse>() {
                    @Override
                    public void accept(TaskViewModel.InsertResponse insertRespose) throws Exception {
                        Task t = insertRespose.getTask();
                        if (!ON_INIT) taskListAdapter.insertTask(t);
                        Toast.makeText(getContext(), "Item " + t.getTitle() + " Inserted", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, throwable.getMessage());
                    }
                });
        this.compositeDisposable.add(disposableInsertTask);
        Disposable disposableDeleteTask = taskViewModel.getDeleteBehaviorSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<TaskViewModel.DeleteResponse>() {
                    @Override
                    public void accept(TaskViewModel.DeleteResponse deleteResponse) throws Exception {
                        Task t = deleteResponse.getTask();
                        if (!ON_INIT) taskListAdapter.deleteTask(t);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, throwable.getMessage());
                    }
                });
        this.compositeDisposable.add(disposableDeleteTask);
        Disposable disposableUpdateTask = taskViewModel.getUpdateBehaviorSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<TaskViewModel.UpdateResponse>() {
                    @Override
                    public void accept(TaskViewModel.UpdateResponse updateResponse) throws Exception {
                        Task t = updateResponse.getTask();
                        if (!ON_INIT) taskListAdapter.updateTask(t);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, throwable.getMessage());
                    }
                });
        this.compositeDisposable.add(disposableUpdateTask);
        this.taskListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.computeVerticalScrollOffset() > 300) {
                    mainActivityListener.showScrollTop(true);
                } else {
                    mainActivityListener.showScrollTop(false);
                }
            }
        });
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
                                if (!ON_INIT) removeItem(task, position);
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
    }

    private void init() {
        ON_INIT = true;
        taskListLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        taskListView.setLayoutManager(taskListLayoutManager);
        addListener();
        taskListAdapter = new TaskListAdapter(this.onActionListener, getContext());
        taskListView.setAdapter(taskListAdapter);
        ON_INIT = false;
    }

    public void smoothScrollToTop() {
        taskListView.smoothScrollToPosition(0);
    }

    private void changeItem(Task task, int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                taskViewModel.update(task);
            }
        }, 500);
    }

    private void removeItem(Task task, int position) {
        taskViewModel.delete(task);
    }
    @SuppressLint("ResourceType")
    public void editTask(Task task) {
        AddTaskDialog editTaskDialog = new AddTaskDialog(getContext(),
                getLayoutInflater(),
                getActivity().findViewById(R.layout.activity_main),
                taskViewModel,
                true,
                task);
        editTaskDialog.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String savedTaskList_type = null;
        try {
            savedTaskList_type = savedInstanceState.getString("taskList_type");
        } catch (NullPointerException e) {

        }
        if (savedTaskList_type != null) {
            this.taskList_type = TASKLIST_TYPE.valueOf(savedTaskList_type);
        }
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("taskList_type", this.taskList_type.toString());
        super.onSaveInstanceState(outState);
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
