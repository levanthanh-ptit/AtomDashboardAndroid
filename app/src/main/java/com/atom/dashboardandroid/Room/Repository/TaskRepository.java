package com.atom.dashboardandroid.Room.Repository;

import android.app.Application;
import android.util.Log;

import com.atom.dashboardandroid.Room.AtomDatabase;
import com.atom.dashboardandroid.Room.DAO.DaoTask;
import com.atom.dashboardandroid.Room.DataSource;
import com.atom.dashboardandroid.Room.Entities.Task;

import java.util.List;

import io.reactivex.Flowable;

public class TaskRepository implements DataSource<Task> {
    public static final String TAG = "TASK_REPOSITORY";
    private DaoTask daoTask;
    private Flowable<List<Task>> mAllTasks;

    public TaskRepository(Application application) {
        AtomDatabase db = AtomDatabase.getInstance(application);
        daoTask = db.daoTask();
        mAllTasks = daoTask.getAll();
    }

    @Override
    public void insert(Task task) {
        daoTask.insert(task);
    }

    @Override
    public void update(Task task) {
        daoTask.update(task);
    }

    @Override
    public void delete(Task task) {
        daoTask.delete(task);
    }

    @Override
    public Flowable<List<Task>> getAll() {
        return daoTask.getAll();
    }

    @Override
    public Flowable<List<Task>> getAllCompleted() {
        return daoTask.getAllCompleted();
    }

    @Override
    public Flowable<List<Task>> getAllUncompleted() {
        return daoTask.getAllUncompleted();
    }


}
