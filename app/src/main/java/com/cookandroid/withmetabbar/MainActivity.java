package com.cookandroid.withmetabbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cookandroid.withmetabbar.certify.CertifyStartFragment;
import com.cookandroid.withmetabbar.certify.JoinStartFragment;
import com.cookandroid.withmetabbar.certify.MainActivity2;
import com.cookandroid.withmetabbar.model.Member;
import com.cookandroid.withmetabbar.navigation.AlarmFragment;
import com.cookandroid.withmetabbar.navigation.ChatFragment;
import com.cookandroid.withmetabbar.navigation.CoinMainFragment;
import com.cookandroid.withmetabbar.navigation.DetailViewFragment;
import com.cookandroid.withmetabbar.navigation.FragmentMyMeetHome;
import com.cookandroid.withmetabbar.navigation.FragmentPlus;
import com.cookandroid.withmetabbar.navigation.FragmentPlusSelectHobby;
import com.cookandroid.withmetabbar.navigation.HomeFragment;
import com.cookandroid.withmetabbar.navigation.MyPageFragment;
import com.cookandroid.withmetabbar.navigation.PlaceCheckFragment;
import com.cookandroid.withmetabbar.navigation.TalkPlaceFragment;
import com.cookandroid.withmetabbar.navigation.UserFragment;
import com.cookandroid.withmetabbar.toolbar.MainActivity3;
import com.cookandroid.withmetabbar.toolbar.MainActivity4;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity  {

    private long lastTimeBackPressed; //액비티비종료구현
    //주석으로 test

    UserFragment userFragment= new UserFragment();
    AlarmFragment alarmFragment= new AlarmFragment();
    //HomeFragment homeFragment= new HomeFragment();
    MainActivityHome mainActivityHome=new MainActivityHome();
    FragmentPlus fragmentPlus= new FragmentPlus();
    MyPageFragment myPageFragment = new MyPageFragment();
    ChatFragment chatFragment= new ChatFragment();//채팅프래그먼트
    //DetailViewFragment detailViewFragment = new DetailViewFragment();
    FragmentManager fragmentManager = getSupportFragmentManager();//for 프래그먼트 전환
    CoinMainFragment coinMainFragment = new CoinMainFragment(); //코인 충전 프레그 먼트
    PlaceCheckFragment placeCheckFragment = new PlaceCheckFragment();//위치인증 프레그 먼트
    TalkPlaceFragment talkPlaceFragment = new TalkPlaceFragment();//톡방 프레그먼트 아직 xml없음
    FragmentMyMeetHome fragmentMyMeetHome = new FragmentMyMeetHome(); //내 모임 보기 화면

    Toolbar tb;






    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentTransaction transaction = fragmentManager.beginTransaction();//트랜잭션 생성
        //FragmentTransaction.add(R.id.main_content, MyPageFragment.newInstance()).commit();//new

        //transaction.replace(R.id.main_content, homeFragment).commitAllowingStateLoss();//처음에 띄우는 fragment
        transaction.replace(R.id.main_content, mainActivityHome).commitAllowingStateLoss();//처음에 띄우는 fragment



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()   {

            //로그인에서 넣어준 intent 받기
            final Intent intentMain = getIntent();
            public final String uid = intentMain.getStringExtra("uid");
            final Member member = new Member();



            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                switch (item.getItemId()) {
                    case R.id.action_home:
                        //Intent intentHome= new Intent(getApplicationContext(), MainActivityHome.class);
                        //startActivity(intentHome);
                        transaction.replace(R.id.main_content, mainActivityHome).commitAllowingStateLoss();

                        //homeFragment.setArguments(bundle);
                        //transaction.replace(R.id.main_content, homeFragment).commitAllowingStateLoss();
                        //transaction.addToBackStack(null);
                        //transaction.commit();
                        //fragmentManager.beginTransaction().remove(detailViewFragment.this).commit;
                        //fragmentManager.popBackStack();

                        break;
                    case R.id.action_love:
                        transaction.replace(R.id.main_content, fragmentMyMeetHome).commitAllowingStateLoss();
                        break;
                    case R.id.action_plus:
                        //transaction.add(R.id.main_content,fragmentPlus).addToBackStack(null).commit();
                        transaction.replace(R.id.main_content, fragmentPlus).commitAllowingStateLoss();
                        Bundle bundle = new Bundle();
                        bundle.putString("uid",uid);
                        fragmentPlus.setArguments(bundle);
                        break;
                    case R.id.action_chatroom:
                        transaction.replace(R.id.main_content, chatFragment).commitAllowingStateLoss();
                        break;
                    case R.id.action_mypage:
                        transaction.replace(R.id.main_content, myPageFragment).commitAllowingStateLoss();
                        break;

                }


                return true;
            }
        });


        //toolbar선언
        Toolbar tb = findViewById(R.id.my_toolbar);
        setSupportActionBar(tb);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로 가기 버튼 생성


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (item.getItemId()){
            case R.id.action_category:
                Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                startActivity(intent);
                return  true;
            case R.id.action_alarm:
                Intent intent1 = new Intent(MainActivity.this, MainActivity4.class);
                startActivity(intent1);

            default:
                return super.onOptionsItemSelected(item);

        }

    }






    public void replaceFragment(Fragment fragment){
//        getSupportFragmentManager().beginTransaction().replace(R.id.main_content2, fragment).commit();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_content, fragment).commit();
        
    }
    public void removeFragment(Fragment fragment) {
        //getSupportFragmentManager().beginTransaction().remove(R.id.main_content2, fragment).commit();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment).commit();                                //로그아웃, 뒤로가기버튼 누를시
    }

    public void addFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_content, fragment).addToBackStack(null).commit();
    }

    public void hideFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(fragment).commit();
    }
    public void showFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.show(fragment).commit();
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



}
