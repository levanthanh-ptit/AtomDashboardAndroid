package com.atom.dashboardandroid.TaskList.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;


import com.atom.dashboardandroid.Room.Entities.Task;
import com.atom.dashboardandroid.Room.Repository.TaskRepository;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class TaskViewModel extends AndroidViewModel {
    private static final String TAG = "TASK_VIEW_MODEL";

    public class InsertResponse {
        Throwable throwable;
        Task task;

        public InsertResponse(Task task, Throwable throwable) {
            this.throwable = throwable;
            this.task = task;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public Task getTask() {
            return task;
        }
    }

    public class UpdateResponse {
        Throwable throwable;
        Task task;

        public UpdateResponse(Task task, Throwable throwable) {
            this.throwable = throwable;
            this.task = task;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public Task getTask() {
            return task;
        }
    }

    public class DeleteResponse {
        Throwable throwable;
        Task task;

        public DeleteResponse(Task task, Throwable throwable) {
            this.throwable = throwable;
            this.task = task;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public Task getTask() {
            return task;
        }
    }

    public TaskViewModel(@NonNull Application application) {
        super(application);
        this.taskRepository = new TaskRepository(application);
        getAll();
    }

    private TaskRepository taskRepository;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    BehaviorSubject<List<Task>> listTaskBehaviorSubject = BehaviorSubject.create();
    BehaviorSubject<InsertResponse> insertBehaviorSubject = BehaviorSubject.create();
    BehaviorSubject<UpdateResponse> updateBehaviorSubject = BehaviorSubject.create();
    BehaviorSubject<DeleteResponse> deleteBehaviorSubject = BehaviorSubject.create();

    public BehaviorSubject<List<Task>> getListTaskBehaviorSubject() {
        return listTaskBehaviorSubject;
    }

    public BehaviorSubject<InsertResponse> getInsertBehaviorSubject() {
        return insertBehaviorSubject;
    }

    public BehaviorSubject<UpdateResponse> getUpdateBehaviorSubject() {
        return updateBehaviorSubject;
    }

    public BehaviorSubject<DeleteResponse> getDeleteBehaviorSubject() {
        return deleteBehaviorSubject;
    }

    public void insert(Task task) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                taskRepository.insert(task);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        insertBehaviorSubject.onNext(new InsertResponse(task, null));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d(TAG, "error: " + throwable.getMessage() + "/n" + throwable.getCause());
                    }
                });

    }

    public void update(Task task) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                taskRepository.update(task);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        updateBehaviorSubject.onNext(new UpdateResponse(task, null));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d(TAG, "error: " + throwable.getMessage() + "/n" + throwable.getCause());
                    }
                });
    }

    public void delete(Task task) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                taskRepository.delete(task);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        deleteBehaviorSubject.onNext(new DeleteResponse(task, null));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d(TAG, "error: " + throwable.getMessage() + "/n" + throwable.getCause());
                    }
                });
    }

    public void getAll() {
        Disposable disposableGetAll = taskRepository.getAll().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<List<Task>>() {
                    @Override
                    public void accept(List<Task> tasks) throws Exception {
                        listTaskBehaviorSubject.onNext(tasks);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "error: " + throwable.getMessage() + "/n" + throwable.getCause());
                    }
                });
        compositeDisposable.add(disposableGetAll);
    }
    public static boolean compareTo(Task task1, Task task2){
        return task1.getId() == task2.getId();
    }
}