package com.withme.certify;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.withme.R;

public class MainActivity2 extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();//1
    LoginStartFragment loginStartFragment = new LoginStartFragment();//2
    JoinStartFragment joinstartfragment = new JoinStartFragment(); //3 new

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        FragmentTransaction transaction = fragmentManager.beginTransaction();//3
        transaction.replace(R.id.main_content2, loginStartFragment).commitAllowingStateLoss();//실제 프래그 먼트 전환하는 코드

//        //만약 address값이 있다면 joinstratfragment로 전환
//        try {
//            Intent intent  = new Intent();
//            if (intent.getExtras().getString("address") !=null){
//                transaction.replace(R.id.main_content2, joinstartfragment).commitAllowingStateLoss();
//
//            }
//        }catch (NullPointerException e){
//
//        }

    }



    public void replaceFragment(Fragment fragment){
//        getSupportFragmentManager().beginTransaction().replace(R.id.main_content2, fragment).commit();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_content2, fragment).commit();
    }
    public void addFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_content2, fragment).addToBackStack(null).commit();
    }
} //new


