package com.withme.toolbar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.withme.R;
import com.withme.navigation.HomeFragment;
import com.withme.navigation.PlaceCheckFragment;

public class MainActivity3  extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();//1
    PlaceCheckFragment placeCheckFragment = new PlaceCheckFragment();//2
    HomeFragment homeFragment = new HomeFragment();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);




    }






} //new

