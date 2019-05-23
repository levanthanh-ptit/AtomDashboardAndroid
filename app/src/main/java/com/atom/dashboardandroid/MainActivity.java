package com.atom.dashboardandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.atom.dashboardandroid.TaskList.View.Orientation;
import com.atom.dashboardandroid.TaskList.View.TaskListFragment;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    TabAdapter tabAdapter;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.tab_pager)
    ViewPager tabPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        tabAdapter = new TabAdapter(getSupportFragmentManager());
        tabPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(tabPager);
        tabPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //add fragment to tabAdapter
        TaskListFragment taskListFragment = new TaskListFragment(Orientation.VERTICAL);
        tabAdapter.addFragment(taskListFragment, "Tasks", tabLayout, R.drawable.ic_tab);
        for (int i = 0; i<tabAdapter.getCount(); i++ ){
            tabLayout.getTabAt(i).setIcon(R.drawable.ic_tab);
        }

    }
}