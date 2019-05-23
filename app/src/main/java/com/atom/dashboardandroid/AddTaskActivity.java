package com.atom.dashboardandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

    private void addListener() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EDIT_TASK_MODE) {
                    currentTask.setTitle(editTextTitle.getText().toString());
                    currentTask.setContent(editTextContent.getText().toString());
                    taskViewModel.update(currentTask);
                } else {
                    taskViewModel.insert(new Task(editTextTitle.getText().toString(),
                            editTextContent.getText().toString(), false));
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
        addListener();
    }
}
