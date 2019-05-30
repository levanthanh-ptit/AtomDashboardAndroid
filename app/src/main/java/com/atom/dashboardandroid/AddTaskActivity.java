package com.atom.dashboardandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.atom.dashboardandroid.Room.Entities.Task;
import com.atom.dashboardandroid.TaskList.View.TaskListFragment;
import com.atom.dashboardandroid.TaskList.ViewModel.TaskViewModel;
import com.atom.dashboardandroid.utils.AlarmUtil;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddTaskActivity extends AppCompatActivity {
    private static final String TAG = "ADD_TASK_ACTIVITY";
    boolean EDIT_TASK_MODE = false;
    private boolean ON_INIT;
    TaskViewModel taskViewModel;
    Task currentTask;
    int hour;
    int minute;
    int year;
    int month;
    int day;
    Calendar calendar = Calendar.getInstance();
    @BindView(R.id.label_layout_title)
    TextView layoutTitle;
    @BindView(R.id.edit_text_title)
    EditText editTextTitle;
    @BindView(R.id.edit_text_content)
    EditText editTextContent;
    @BindView(R.id.time_picker)
    AppCompatButton timePicker;
    @BindView(R.id.date_picker)
    AppCompatButton datePicker;
    @BindView(R.id.btn_action_task)
    AppCompatButton addButton;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);

    }

    private void addListener(Context context) {
        ON_INIT = true;
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timePicker.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, true).show();
            }
        });
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(AddTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        datePicker.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day).show();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ON_INIT) {
                    String title = editTextTitle.getText().toString();
                    String content = editTextContent.getText().toString();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm-dd/MM/yyyy");
                    Date date = new Date();
                    try {
                        date = dateFormat.parse(timePicker.getText() + "-" + datePicker.getText());
                        Log.d(TAG, "Date: " + date.toString());
                        Log.d(TAG, "Date in milis: " + date.getTime());
                    } catch (ParseException pe) {
                        Log.d(TAG, "try parse date failed");
                    }
                    if (title.compareTo("") == 0) {
                        Toast.makeText(context, "Title should not be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (EDIT_TASK_MODE) {
                        currentTask.setTitle(title);
                        currentTask.setContent(content);
                        currentTask.setDate(date);
                        taskViewModel.update(currentTask);
                        AlarmUtil.cancelAlarm(getApplicationContext(), currentTask);
                        AlarmUtil.createService(getApplicationContext(), currentTask);
                    } else {

                        Task task = new Task(title, content, Task.UNCOMPLETED, date);
                        taskViewModel.insert(task);
                        AlarmUtil.createService(getApplicationContext(), task);
                    }
                    finish();
                }

            }
        });
        ON_INIT = false;
    }

    private void init() {
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        ButterKnife.bind(this);
        init();
        Intent intent = getIntent();
        if (intent.getSerializableExtra(TaskListFragment.EDIT_TASK) != null) {
            EDIT_TASK_MODE = true;
            currentTask = (Task) intent.getSerializableExtra(TaskListFragment.EDIT_TASK);
            layoutTitle.setText("EDIT TASK");
            editTextTitle.setText(currentTask.getTitle());
            editTextContent.setText(currentTask.getContent());
            addButton.setText("EDIT");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTask.getDate());
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
        }
        Log.d(TAG, "onCreate: "+hour);
        timePicker.setText(hour + ":" + minute);
        datePicker.setText(day + "/" + (month + 1) + "/" + year);
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        addListener(this);
    }
}
