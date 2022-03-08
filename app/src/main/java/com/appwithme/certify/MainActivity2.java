package com.appwithme.certify;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.appwithme.R;

public class MainActivity2 extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();//1
    LoginStartFragment loginStartFragment = new LoginStartFragment();//2
    JoinStartFragment joinstartfragment = new JoinStartFragment(); //3 new
    private long lastTimeBackPressed; //액비티비종료구현

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

    @Override
    public void onBackPressed() {

        if(System.currentTimeMillis() - lastTimeBackPressed < 1500){

            moveTaskToBack(true);						// 태스크를 백그라운드로 이동
            finishAndRemoveTask();						// 액티비티 종료 + 태스크 리스트에서 지우기
            android.os.Process.killProcess(android.os.Process.myPid());	// 앱 프로세스 종료
            //finish();

            return;
        }
        lastTimeBackPressed = System.currentTimeMillis();
        Toast.makeText(this,"'뒤로' 버튼을 한 번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();


//        Toast.makeText(this, "Log Out", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
//        startActivity(intent);
        //super.onBackPressed();
    }
} //new


