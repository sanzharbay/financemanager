package com.example.financemanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import Adapters.DateTransactionAdapter;

public class TransactionHistoryActivity extends AppCompatActivity{

    public TabLayout timeTabLayout;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_history);

        timeTabLayout = findViewById(R.id.timeTabLayout);
        viewPager = findViewById(R.id.viewPager);

        FragmentStateAdapter pageAdapter = new DateTransactionAdapter(this);
        viewPager.setAdapter(pageAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(timeTabLayout, viewPager, (tab, i) -> {
            switch (i) {
                case 0:
                    tab.setText("Day");
                    break;
                case 1:
                    tab.setText("Period");
                    break;
            }
        });
        tabLayoutMediator.attach();
    }
}