package com.atom.dashboardandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atom.dashboardandroid.Room.Entities.Task;
import com.atom.dashboardandroid.TaskList.View.TaskListFragment;
import com.atom.dashboardandroid.TaskList.ViewModel.TaskViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddTaskActivity extends AppCompatActivity {
    boolean EDIT_TASK_MODE = false;
    TaskViewModel taskViewModel;
    Task currentTask;
    @BindView(R.id.label_layout_title)
    TextView layoutTitle;
    @BindView(R.id.edit_text_title)
    EditText editTextTitle;
    @BindView(R.id.edit_text_content)
    EditText editTextContent;
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
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String content = editTextContent.getText().toString();
                if(title.compareTo("")==0){
                    Toast.makeText(context, "Title should not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (EDIT_TASK_MODE) {
                    currentTask.setTitle(title);
                    currentTask.setContent(content);
                    taskViewModel.update(currentTask);
                } else {
                    taskViewModel.insert(new Task(title, content, false));
                }
                finish();

            }
        });
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent.getSerializableExtra(TaskListFragment.EDIT_TASK) != null) {
            EDIT_TASK_MODE = true;
            currentTask = (Task) intent.getSerializableExtra(TaskListFragment.EDIT_TASK);
            layoutTitle.setText("EDIT TASK");
            editTextTitle.setText(currentTask.getTitle());
            editTextContent.setText(currentTask.getContent());
            addButton.setText("EDIT");
        }

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        addListener(this);
    }
}
