package com.atom.dashboardandroid.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.atom.dashboardandroid.Room.DAO.DaoTask;
import com.atom.dashboardandroid.Room.Entities.Task;

@Database(entities = {Task.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AtomDatabase extends RoomDatabase {
    private static final String TAG = "ATOM_DATABASE";
    private static AtomDatabase instance = null;
    private static String DB_NAME = "ATOMDB";

    public abstract DaoTask daoTask();

    public static AtomDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (AtomDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AtomDatabase.class, DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
