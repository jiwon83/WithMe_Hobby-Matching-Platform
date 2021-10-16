package com.withme.googlemap;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.withme.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.withme.chat.GroupMessageActivity;
import com.withme.model.Meet;
import com.withme.model.Member;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback, LatLngCallback,GoogleMap.OnMarkerClickListener {

    private GoogleMap mGoogleMap;//create google map object
    LatLng myPosition;//my position
    String[] REQUIRED_PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    private FirebaseDatabase database;//database
    private DatabaseReference databaseReference;//dataref
    private ArrayList<Meet> allMeetList= new ArrayList<>();//list all of meet
    //private ArrayList<LatLng> la
    final Geocoder geocoder = new Geocoder(this);
    private ArrayList<String> allMeetAddress;
    private ArrayList<Address> addressesLongLat =null;
    public ArrayList<Location> allLocation=new ArrayList<>();
    private static ArrayList<LatLng> allLatLngs; //= new ArrayList<>();
    Address addressLogLat = null;
    LatLng thisLatLng = null;
    ArrayList<Marker> markers;
    private static ArrayList<LatLng> globalAllLatLngs;
    private static ArrayList<Meet> globalAllmeet;
    //모임데이터 조회
    private DatabaseReference databaseReference2;
    private ArrayList<Meet> arrayListMeet;
    //
    LatLngCallback mLatLngCallback;
    Marker marker;
    private AlertDialog dialog;
    private int userCount=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        allLatLngs= new ArrayList<>();
        arrayListMeet = new ArrayList<>();

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


        getContactsFromFirebase(this::latLangCall);

        //transferStringToAddress("서울시");
        //Log.d("thisLatLng_seul", String.valueOf(thisLatLng)); 잘 받아옴.
        //맵에 마커 표시
        //getMeetDataFromFirebase();//meet


        Log.d("globalallLatLngs_out_method", String.valueOf(globalAllLatLngs));//null

        Log.d("afterMethod_allMeetList", String.valueOf(allMeetList));//null

        Log.d("allMeetAddress_out_method", String.valueOf(allMeetAddress));//null

        //allLatLngs= new ArrayList<>();


        //Log.d("transferStringToAddress>test", String.valueOf(transferStringToAddress(allMeetAddress.get(0))));


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
        LatLng HERE = new LatLng(37.56,126.97);

        //마커생성
//        MarkerOptions markerOptions = new MarkerOptions();//create markerOPtions object
//        markerOptions.position(SEOUL);
//        markerOptions.title("서울");
//        markerOptions.snippet("한국의 수도");//sinppet : 작은 설명
//        mGoogleMap.addMarker(markerOptions);
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL,10));//put zoom size


        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 10));//내위치를 지점으로
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(7), 2000, null); //zoomto:클수록 가깝게된다.



        Log.d("allLatLngs_onMapReady", String.valueOf(allLatLngs));//null
        mGoogleMap.setOnMarkerClickListener(this::onMarkerClick);//this?
//        mGoogleMap.setOnMarkerClickListener(this::onMarkerClick);//this?



        //mGoogleMap.moveCamera(new LatLng(allLocation.get(0).getLatitude(), allLocation.get(0).getLongitude()));

        //
        // for loop를 통한 n개의 마커 생성
//        for (int idx = 0; idx < 10; idx++) {
//            // 1. 마커 옵션 설정 (만드는 과정)
//            MarkerOptions makerOptions = new MarkerOptions();
//            makerOptions // LatLng에 대한 어레이를 만들어서 이용할 수도 있다.
//                    .position(new LatLng(37.52487 + idx, 126.92723))
//                    .title("마커" + idx); // 타이틀.
//
//            // 2. 마커 생성 (마커를 나타냄)
//            mMap.addMarker(makerOptions);
//        }


    }


    //주소 ->위경도 string을 주면 그에 대항하는 위경도 값을 가져오는 역할
    //geocoder 이용
    public LatLng transferStringToAddress(String str) {
        List<Address> longlatList = null; //위경도임시적으로저장할list 10개의 비슷한 값들을 저장
        LatLng latLng = null;

        //String str = "경기도 화성시 융건로99 풍성신미주아파트";
        //allMeetAddress

        try {
            longlatList = (ArrayList<Address>) geocoder.getFromLocationName(str, 1);//maxResults : 10 -> 1  1개만 검색?

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");

        }


        if (longlatList != null) {
            if (longlatList.size() == 0) {
                Log.d("longlatList=0해당되는주소정보없다", String.valueOf(longlatList));


            } else {
                double lat = longlatList.get(0).getLatitude();
                double lng = longlatList.get(0).getLongitude();
                latLng = new LatLng(lat, lng);
                //thisLatLng = new LatLng(lat, lng);
                //addressLogLat = longlatList.get(0);//10개 중 가장 정확한 첫번째 값
                //return addressLogLat;

            }
        }
        Log.d("addressLogLat_in_method", String.valueOf(addressLogLat));
        //return addressLogLat; //return Address addressLogLat;
        Log.d("thisLatLng_in_method", String.valueOf(thisLatLng));
        return latLng;

    }

    public void getContactsFromFirebase(final LatLngCallback myCallback){
        LatLngCallback latLngCallback;
        latLngCallback=myCallback;
        allMeetList= new ArrayList<>();
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
                    Log.d("allMeetList_getMeetDataFromFirebase", String.valueOf(allMeetList));

                    //meet의 장소 allMeetAddress 에 넣기
//                    allMeetAddress= new ArrayList<>();
//                    for (int i =0; i< allMeetList.size(); i++){
//                        allMeetAddress.add(allMeetList.get(i).getPlace());
//                    }
//
//                    Log.d("allMeetAddress_in_method", String.valueOf(allMeetAddress));
//
//                    // allMeetAddress(String) to LatLng (이전방식)
//                    if (allMeetAddress != null){
//                        for (int j=0; j<allMeetAddress.size(); j++){
//                            Log.d("allMeetAddress_size", String.valueOf(allMeetAddress.size()));//14
//                            LatLng latLng1= transferStringToAddress(allMeetAddress.get(j)); //각각의 주소를 Address객체(Address addressLogLat)로 받는다.
//                            Log.d("latLng1_check", String.valueOf(latLng1));
//                            Log.d("addressLogLat_out_method", String.valueOf(addressLogLat)); //ok
//
//                            Log.d("thisLatLng_out_method", String.valueOf(thisLatLng));//ok lat/lng: (37.4831816,127.06186590000002)
//                            //LatLng객체를 다 갯수로 셀 수 없기 때무에 받아온 각각의 LatLng객체를 배열에 담아준다. LatLng 배열 객체는 전역변수여야 한다.
//                            allLatLngs.add(latLng1);
//                            Log.d("allLatLngs_size_before", String.valueOf(allLatLngs.size()));//105
//                            //double 형으로 lat long값 받기
//                            //double lat = addressesLongLat.latitde;
//
//                        }
//
//                    }
                    //모임정보에서 아예 위도경도정보를 가져오는 방법 : error 앱종료 : java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
//                    for (int i=0; i< allMeetList.size(); i++){
//                        double lat = allMeetList.get(i).getLatLng().get(0);
//                        double lng = allMeetList.get(i).getLatLng().get(1);
//                        LatLng preLatLng = new LatLng(lat, lng);
//                        allLatLngs.add(preLatLng);
//                    }




                }
                //makeMarkers(allMeetList);
                Log.d("allMeetList_1004", String.valueOf(allMeetList));
//                latLngCallback.latLangCall(allLatLngs,allMeetList);//LatingCallback interface 객체의 메서드에 값 집어 넣기
                latLngCallback.latLangCall(allMeetList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //디비를 가져오는 도중 에러 발생 시
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e("GoogleMapActivity_getData",String.valueOf(error.toException()));

            }
        });
//        getContactsFromFirebase(new LatLngCallback() {
//            @Override
//            public void latLangCall(ArrayList<LatLng> list) {
//                globalAllLatLngs=list;
//            }
//        });


    }


    @Override
    public void latLangCall(ArrayList<Meet> meets) { //ArrayList<LatLng> list
        //globalAllLatLngs=list;//latlangCall에서만 사용하는 globalAllLatLngs에 넣어주기
        globalAllmeet= meets;//latlangCall에서만 사용하는 globalAllmeet에 넣어주기
        //Log.d("globalAllLatLngs_in_callbackmethod", String.valueOf(globalAllLatLngs.size()));//ok//6
        Log.d("globalAllmeet_in_callback", String.valueOf(globalAllmeet.size()));//ok//3

        //test can make marker
//        mGoogleMap.addMarker(new MarkerOptions().
//                position(new LatLng(35,126))
//        .title("test")); //ok



        Marker [] markers = new Marker[globalAllmeet.size()];
        //모임리스트의 크기만큼 반복
//        for(int index=0; index<globalAllmeet.size(); index++){
//            MarkerOptions makerOp = new MarkerOptions();
//            try {
//                //지금까지의 마커에 같은 위도경도 정보가 존재하지 않는다면
//
//                makerOp.position(new LatLng( globalAllLatLngs.get(index).latitude, globalAllLatLngs.get(index).longitude ))//position= Lating객체, 위경도
//                .title(globalAllmeet.get(index).getTitle())
////                        .snippet(globalAllmeet.get(index).getMid());
//                .snippet(globalAllmeet.get(index).getHobbyCate().toString());
//
//
//                markers[index]=mGoogleMap.addMarker(makerOp);
//                markers[index].setTag(globalAllmeet.get(index).getMid());
//
//                Log.d("marker_mid_before",globalAllmeet.get(index).getMid());//ok
//
//
//                Log.d("makerOp", String.valueOf(makerOp));
//            }catch (Exception e){
//                //expect null value
//            }
//
//        }
        for(int index=0; index<globalAllmeet.size(); index++){
            MarkerOptions makerOp = new MarkerOptions();//1회성 마커옵셥
            try {
                //지금까지의 마커에 같은 위도경도 정보가 존재하지 않는다면

                makerOp.position(new LatLng( globalAllmeet.get(index).latLng.get(0), globalAllmeet.get(index).latLng.get(1)))
                        .title(globalAllmeet.get(index).getTitle())
//                        .snippet(globalAllmeet.get(index).getMid());
                        .snippet(globalAllmeet.get(index).getHobbyCate().toString());

                markers[index]=mGoogleMap.addMarker(makerOp);
                markers[index].setTag(globalAllmeet.get(index).getMid());




            }catch (Exception e){
                //expect null value
            }

        }

    }

    public boolean UniqueMarkerCheck(Marker[] markers, LatLng latLng){

        for (int i=0; i< markers.length; i++){
            if (markers[i].getPosition()== latLng){
                return false;
            }


        }
        return true;
    }
    @Override
    public boolean onMarkerClick(@NonNull @NotNull Marker marker) {

        Log.d("marker-getTag", String.valueOf(marker.getTag()));

        String mid = String.valueOf(marker.getTag());
        Log.d("marker_of_mid",mid);//ok
        //mid로 meet 데이터 조회
        databaseReference2 = database.getReference("meet").child(mid);


        //String myUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

       // Query myTopPostsQuery = databaseReference2.child("meet").child(mid);
                //.orderByChild("title");
        //databaseReference=database.getReference("meet");

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot dataSnapshot) {
                Meet meet = dataSnapshot.getValue(Meet.class);
                Toast.makeText(GoogleMapActivity.this,"meet"+meet.title,Toast.LENGTH_SHORT).show();
                enterChatRoom(mid,meet);
                //실제적으로 파이어베이스 데이터베이스의 데이터를 받아오는 곳
//                arrayListMeet.clear(); //기존 배열리스트가 존재하지 않게 초기화
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){//반복문으로 데이터 List를 추출해냄.
//
//                    Meet meet = snapshot.getValue(Meet.class); // 만들어놨던 Meet 객체에 데이터를 담는다.
//
//                    arrayListMeet.add(meet); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비.
//                    Log.d("arrayList-maker", String.valueOf(arrayListMeet));
//
//
//
//                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                //디비를 가져오는 도중 에러 발생 시
                Toast.makeText(GoogleMapActivity.this, error.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e("MainActivityHome",String.valueOf(error.toException()));

            }
        });
        //dailog 띄우기

        //mid만 상세화면으로 전달하던지
        //여기서 데이터를 조회하던지

        return false;
    }
    public void makeMarkers(ArrayList<Meet> meets){

        marker= mGoogleMap.addMarker(new MarkerOptions()
            .position(new LatLng(127,35))
            .title("test"));
        marker.setTag(0);

        globalAllmeet= meets;//받아온 모든 meet정보를 다시 globalAllmeet에 넣어줌.

        Log.d("globalAllmeet_in_callback", String.valueOf(globalAllmeet.size()));//2

        //마커생성
       // Marker [] markers = new Marker[globalAllmeet.size()];

        //모임리스트의 크기만큼 반복
        for(int index=0; index<globalAllmeet.size(); index++){
            MarkerOptions makerOp = new MarkerOptions();//1회성 마커옵셥
            try {
                //지금까지의 마커에 같은 위도경도 정보가 존재하지 않는다면

                makerOp.position(new LatLng( globalAllmeet.get(index).latLng.get(0), globalAllmeet.get(index).latLng.get(1)))
                        .title(globalAllmeet.get(index).getTitle())
//                        .snippet(globalAllmeet.get(index).getMid());
                        .snippet(globalAllmeet.get(index).getHobbyCate().toString());

                Log.d("maker_positon_lat", String.valueOf(globalAllmeet.get(index).latLng.get(0)));
                Log.d("maker_positon_lng", String.valueOf(globalAllmeet.get(index).latLng.get(1)));


//                markers[index]=mGoogleMap.addMarker(makerOp);
                //mGoogleMap.addMarker(makerOp);
                marker = mGoogleMap.addMarker(makerOp);

                //markers[index].setTag(globalAllmeet.get(index).getMid());

                Log.d("marker_mid_before",globalAllmeet.get(index).getMid());//ok


                Log.d("makerOp", String.valueOf(makerOp));
            }catch (Exception e){
                //expect null value
            }

        }

    }
    public void enterChatRoom(String mid, Meet meet){
        Member memberObj = new Member();
        String meetId = mid;
        Meet meet_clicked = meet;
        try {
            //제한 사항이 맞는지 검사.
            //user의 Gen, Age 가 meet의(또는 chatroom의) Gen, Age 와 동일한지 확인
            //userData 가져오기
            List<Member> members = new ArrayList<>();

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            //user data
            FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //member 객체에 user에서 받아온 값 넣기
                    Member member = snapshot.getValue(Member.class);
                    memberObj.mGen =member.mGen;
                    memberObj.mAge =member.mAge;


                    List<String> keysChatroomUsers = new ArrayList<>();//chatrooms - uid -users 의 uid 값들을 답을 리스트
                    //구조변경
                    //채팅방에서 userCount 받아오기 chatrooms - chatroom uid(=meet uid)
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(meetId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {


                            for (DataSnapshot item : snapshot.getChildren()){
                                keysChatroomUsers.add(item.getKey());
                            }
                            userCount = keysChatroomUsers.size(); //chatroom에 있는 user의 수

                            //arrayList.get(position) = 해당 meet정보
                            boolean agePass = memberObj.mAge> meet_clicked.getMeetAge();
                            boolean genPass = memberObj.mGen>=meet_clicked.getMeetGen() || (meet_clicked.getMeetGen()==0) ;
                            boolean numMemberPass =( userCount < meet_clicked.getNumMember() );


                            //조건 비교
                            try {
                                if (  agePass && genPass && numMemberPass){


                                    Map<String,Object> map = new HashMap<>();
                                    map.put(meetId,true);

                                    Intent intent = new Intent (getApplicationContext(), GroupMessageActivity.class);

                                    //chatrooms의 user에 내 uid 넣는것.
                                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(meet_clicked.mid)
                                            .child("users").updateChildren(map);

                                    //chatroom 의 userCount에 현재 userCount 넣기.
                                    userCount += 1;
                                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(meet_clicked.mid)
                                            .child("userCount").setValue(userCount);

                                    intent.putExtra("destinationRoom",meet_clicked.mid);// 채팅방을 띄우기 위한 chatroom uid 전송.



                                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(),R.anim.fromright,R.anim.toleft);
                                    getApplicationContext().startActivity(intent,activityOptions.toBundle());

                                }else {

                                    //조건 불만족시 처리
                                    if (agePass==false){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                                        dialog = builder.setMessage("모임 연령을 확인하세요. 모임연령: "+meet_clicked.getMeetAge())
                                                .setNegativeButton("OK", null)
                                                .create();
                                        dialog.show();
                                    }else if (genPass ==false ){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                                        dialog = builder.setMessage("모임 성별을 확인하세요.")
                                                .setNegativeButton("OK", null)
                                                .create();
                                        dialog.show();
                                    }else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                                        dialog = builder.setMessage("인원이 마감되었습니다.")
                                                .setNegativeButton("OK", null)
                                                .create();
                                        dialog.show();
                                    }
//
                                }
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(), e.getMessage(),  Toast.LENGTH_SHORT);
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }catch (Exception e){

        }
    }
}