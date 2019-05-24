package com.atom.dashboardandroid.Room.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.atom.dashboardandroid.Room.Entities.Task;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface DaoTask {
    @Insert
    void insert(Task task);
    @Delete
    void delete(Task task);
    @Update
    void update(Task task);
    @Query("SELECT * FROM task")
    Flowable<List<Task>> getAll();
}
