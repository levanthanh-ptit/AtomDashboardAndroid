package com.atom.dashboardandroid.TaskList.View;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import com.atom.dashboardandroid.R;
import com.atom.dashboardandroid.Room.Entities.Task;
import com.atom.dashboardandroid.TaskList.ViewModel.TaskViewModel;
import com.atom.dashboardandroid.utils.AlarmUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTaskDialog extends AlertDialog {
    private static final String TAG = "ADD_TASK_DIALOG";
    private boolean EDIT_TASK_MODE;
    private boolean ON_INIT;
    private Context context;
    private TaskViewModel taskViewModel;
    private Task currentTask;
    private int hour;
    private int minute;
    private int year;
    private int month;
    private int day;
    private Calendar calendar = Calendar.getInstance();
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
    private View container;

    private void init() {
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    @OnClick(R.id.time_picker)
    public void setTimePickerOnClick(View v) {
        new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timePicker.setText(hourOfDay + ":" + minute);
            }
        }, hour, minute, true).show();
    }

    @OnClick(R.id.date_picker)
    public void setDatePickerOnClick(View v) {
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                datePicker.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, day).show();
    }

    @OnClick(R.id.btn_action_task)
    public void setAddButtonOnClick(View v) {
        if (!ON_INIT) {
            String title = editTextTitle.getText().toString();
            String content = editTextContent.getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm dd/MM/yyyy");
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
                AlarmUtil.cancelAlarm(context, currentTask);
                AlarmUtil.createService(context, currentTask);
            } else {

                Task task = new Task(title, content, Task.UNCOMPLETED, date);
                taskViewModel.insert(task);
                AlarmUtil.createService(context, task);
            }
            cancel();
        }
    }

    public AddTaskDialog(Context context,
                         LayoutInflater layoutInflater,
                         ViewGroup parent,
                         TaskViewModel taskViewModel,
                         boolean EDIT_TASK_MODE,
                         Task task
    ) {
        super(context);
        init();
        this.context = context;
        this.taskViewModel = taskViewModel;
        this.EDIT_TASK_MODE = EDIT_TASK_MODE;
        container = layoutInflater.inflate(R.layout.dialog_add_task, parent);
        Log.d(TAG, "AddTaskDialog: container: "+container);
        ON_INIT = true;
        ButterKnife.bind(this, container);
        ON_INIT = false;
        Log.d(TAG, "AddTaskDialog: time_picker: "+this.timePicker);
        if (EDIT_TASK_MODE) {
            this.currentTask = task;
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
        timePicker.setText(hour + ":" + minute);
        datePicker.setText(day + "/" + (month + 1) + "/" + year);
        this.setView(this.container);
    }
}
