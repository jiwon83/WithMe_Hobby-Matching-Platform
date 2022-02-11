package com.appwithme.toolbar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.appwithme.R;
import com.appwithme.navigation.AlarmFragment;
import com.appwithme.navigation.HomeFragment;

public class MainActivity4 extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();//1
    AlarmFragment alarmFragment = new AlarmFragment();//2
    HomeFragment homeFragment = new HomeFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);



    }
}