package com.example.application080719;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.application080719.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
//        FloatingActionButton fab = findViewById(R.id.fab);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
//        for (int i = 0; i < bottomNavigationView.getTabCount(); i++) {
//            BottomBarTab tab = mBottomBar.getTabAtPosition(i);
//            tab.setGravity(Gravity.CENTER);
//
//            View icon = tab.findViewById(com.roughike.bottombar.R.id.bb_bottom_bar_icon);
//            // the paddingTop will be modified when select/deselect,
//            // so, in order to make the icon always center in tab,
//            // we need set the paddingBottom equals paddingTop
//            icon.setPadding(0, icon.getPaddingTop(), 0, icon.getPaddingTop());
//
//            View title = tab.findViewById(com.roughike.bottombar.R.id.bb_bottom_bar_title);
//            title.setVisibility(View.GONE);
//        }
    }
}