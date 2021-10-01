package com.cookandroid.withmetabbar.googlemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cookandroid.withmetabbar.CustomAdapter;
import com.cookandroid.withmetabbar.R;
import com.cookandroid.withmetabbar.model.Meet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;//create google map object
    LatLng myPosition;//my position
    String[] REQUIRED_PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    private FirebaseDatabase database;//database
    private DatabaseReference databaseReference;//dataref
    private ArrayList<Meet> allMeetList;//list all of meet


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        //view googlemap in fragment
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_gmap); //justify fragment
        mapFragment.getMapAsync(this);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria(); //베스트 프로바이더 기준
        String provider = locationManager.getBestProvider(criteria, true);//best위치결정
        ActivityCompat.requestPermissions(GoogleMapActivity.this, REQUIRED_PERMISSIONS,
                PERMISSIONS_REQUEST_CODE);//for위치정보사용가능

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //폰의 위치정보
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            double latitude = location.getLatitude();//위도
            double longitude = location.getLongitude();//경도
            myPosition = new LatLng(latitude, longitude);
        }


    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {

        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mGoogleMap.setMyLocationEnabled(true);//나의 위치정보찾기버튼활성화여부

        //위도경도값 객체 생성
        LatLng SEOUL = new LatLng(37.56,126.97);

        //마커생성
        MarkerOptions markerOptions = new MarkerOptions();//create markerOPtions object
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");//sinppet : 작은 설명
        mGoogleMap.addMarker(markerOptions);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL,10));//put zoom size


    }
    public void getMeetDataFromFirebase(){
        database= FirebaseDatabase.getInstance();
        databaseReference=database.getReference("meet");//DB Table Connect
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //실제적으로 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                //arrayList.clear(); //기존 배열리스트가 존재하지 않게 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){//반복문으로 데이터 List를 추출해냄.

                    Meet meet = snapshot.getValue(Meet.class); // 만들어놨던 Meet 객체에 데이터를 담는다.
                    allMeetList.add(meet);



                    //단어 검색
                    //2021-08-16 검색기능 구현
//                    // meet의 값이 null값이 아니면, list_search_recycle이라는 리스트에 넣어라.
//                    if (meet.title!=null){
//                        list_search_recycle.add(meet.title);//list_search_recycle에 title값 저장
//                        //취미목록 검색 리스트에 넣기 2021-09-13
//                        int totalHobbyCount2 = meet.hobbyCate.size();
//                        for (int index = 0; index < totalHobbyCount2; index++) {
//                            list_search_recycle.add(meet.hobbyCate.get(index)); //hobbyCate의 배열값을 넣는다.
//                        }
//
//                    }
                }

                //2021-08-16 검색기능 구현
//                arrayList_search_recycle.addAll(list_search_recycle);//제목으로 모임검색 구현,복사해준다.
//
//                customAdapter= new CustomAdapter(arrayList,getContext());
//                recyclerView.setAdapter(customAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //디비를 가져오는 도중 에러 발생 시
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e("GoogleMapActivity_getData",String.valueOf(error.toException()));

            }
        });
    }
}