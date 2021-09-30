package com.cookandroid.withmetabbar.googlemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.cookandroid.withmetabbar.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;//create google map object



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_gmap); //justify fragment

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {

        mGoogleMap = googleMap;

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
}