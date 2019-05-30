package com.atom.dashboardandroid.Room.Entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "task")
public class Task implements Serializable {
    @Ignore
    public static final String TITLE = "TITLE";
    @Ignore
    public static final String CONTENT = "CONTENT";
    @Ignore
    public static final String COMPLETED = "COMPLETED";
    @Ignore
    public static final String UNCOMPLETED = "UNCOMPLETED";
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public String content;
    public String state;
    public Date date;

    public Task(@NonNull String title, @NonNull String content,@NonNull String state,@NonNull Date date) {
        this.title = title;
        this.content = content;
        this.state = state;
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getState() {
        return state;
    }

    public Date getDate() {
        return date;
    }
}
