package com.atom.dashboardandroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atom.dashboardandroid.Room.Entities.Task;
import com.atom.dashboardandroid.TaskList.View.AddTaskDialog;
import com.atom.dashboardandroid.TaskList.View.TASKLIST_TYPE;
import com.atom.dashboardandroid.TaskList.View.TaskListFragment;
import com.atom.dashboardandroid.TaskList.ViewModel.TaskViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MAIN_ACTIVITY";
    private int currentTabIndex = 0;
    TaskViewModel taskViewModel;
    TabAdapter tabAdapter;
    MainActivityListener mainActivityListener;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.quick_add_task_container)
    ConstraintLayout quickAddTaskContainer;
    ValueAnimator animatorQuickAddTaskContainer;
    @BindView(R.id.edt_quick_add_task)
    EditText edtQuickAddTask;
    @BindView(R.id.tab_pager)
    ViewPager tabPager;
    @BindView(R.id.btn_scroll_top)
    AppCompatButton btnScrollTop;
    @BindView(R.id.btn_add_task)
    AppCompatButton btnAddTask;

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

    private void init() {
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        tabAdapter = new TabAdapter(getSupportFragmentManager());
        tabPager.setAdapter(tabAdapter);
        tabPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(tabPager);
    }

    private void addFragment() {
        //add fragment to tabAdapter
        tabAdapter.addFragment(new TaskListFragment(TASKLIST_TYPE.UNCOMPLETE, mainActivityListener), "Todo");
        tabAdapter.addFragment(new TaskListFragment(TASKLIST_TYPE.COMPLETE, mainActivityListener), "Done");
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_tasks_solid);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_calendar_check_regular);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                MainActivity.this.currentTabIndex = tab.getPosition();
                onChangeTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        btnScrollTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TaskListFragment) tabAdapter.getItem(MainActivity.this.currentTabIndex)).smoothScrollToTop();
            }
        });
    }

    private void addListener() {
        mainActivityListener = new MainActivityListener() {
            @Override
            public void showScrollTop(boolean visibility) {
                if (visibility)
                    btnScrollTop.setVisibility(View.VISIBLE);
                else
                    btnScrollTop.setVisibility(View.GONE);
            }
        };
        edtQuickAddTask.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handle = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    quickAddTask();
                    handle = true;
                }
                return handle;
            }
        });

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
//                startActivity(intent);
                openAddTaskDialog();
            }
        });
    }

    private void quickAddTask() {
        String title = edtQuickAddTask.getText().toString();
        if (title.compareTo("") != 0) {
            taskViewModel.insert(new Task(title, "", Task.UNCOMPLETED, new Date()));
            edtQuickAddTask.setText("");
        } else {
            Toast.makeText(MainActivity.this, "Task's title can not be empty", Toast.LENGTH_SHORT);
        }
    }
    @SuppressLint("ResourceType")
    private void openAddTaskDialog(){
        AddTaskDialog addTaskDialog = new AddTaskDialog(MainActivity.this,
                getLayoutInflater(),
                MainActivity.this.findViewById(R.layout.activity_main),
                taskViewModel,
                false,
                null);
        addTaskDialog.show();
    }
    private void onChangeTab() {
        if (animatorQuickAddTaskContainer.isRunning()) {
            animatorQuickAddTaskContainer.end();
        }
        if (this.currentTabIndex != 0) {
            animatorQuickAddTaskContainer.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (MainActivity.this.currentTabIndex != 0)
                        quickAddTaskContainer.setVisibility(View.GONE);
                }
            }, animatorQuickAddTaskContainer.getDuration());
        } else {
            quickAddTaskContainer.setVisibility(View.VISIBLE);
            animatorQuickAddTaskContainer.reverse();
        }
        btnScrollTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TaskListFragment) tabAdapter.getItem(MainActivity.this.currentTabIndex)).smoothScrollToTop();
            }
        });
    }

    void animationSetup() {
        animatorQuickAddTaskContainer = ValueAnimator.ofFloat(1.0f, 0.0f);
        animatorQuickAddTaskContainer.setInterpolator(new DecelerateInterpolator());
        animatorQuickAddTaskContainer.setDuration(300);
        animatorQuickAddTaskContainer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                quickAddTaskContainer.setAlpha((float) animation.getAnimatedValue());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        addListener();
        addFragment();
        animationSetup();
    }
}