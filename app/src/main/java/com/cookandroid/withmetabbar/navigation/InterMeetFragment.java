package com.cookandroid.withmetabbar.navigation;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.withmetabbar.CustomAdapter;
import com.cookandroid.withmetabbar.R;
import com.cookandroid.withmetabbar.SearchAdapter;
import com.cookandroid.withmetabbar.model.Meet;
import com.cookandroid.withmetabbar.model.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class InterMeetFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Meet> arrayList;//검색 후에도 recycler view에 올라갈 진짜 meet 데이터들
    private ArrayList<Meet> arrayList_copy; //recycler view에 올라갈 전체 meet 데이터들
    private CustomAdapter customAdapter;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReference2;

    private List<String> list_search; //자동완성 단어들을 담을 리스트
    private AutoCompleteTextView autoCompleteTextView; //검색어입력창
    private SearchView searchView; //검색어 입력 창
    private SearchAdapter searchAdapter;
    private EditText editSearch;//검색어를 입력할 Input창
    private ListView listView;//검색을 보여줄 리스변수
    private Button btn_inter,btn_all,btn_time;//캘린더
    private DatePickerDialog datePickerDialog;


    //2021-08-16 검색기능 구현
    private List<String> list_search_recycle; //검색후에도 갱신될 검색창 데이터들
    private ArrayList<String> arrayList_search_recycle; //모든 검색창 데이터들

    //2021-09-15 취미목록으로 필터링 구현
    private ArrayList<String> listUserHobby = new ArrayList<>(); //user의 hobby list

    //2021-09-16 날짜로 필터링 구현
    private Date selectDate;



    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ViewGroup vGroup = (ViewGroup) inflater.inflate(R.layout.inter_meet_fragment, container, false);

        recyclerView=vGroup.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);//기존 리사이클러뷰의 성능 강화
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();//Meet객체를 담을 어레이리스트 (어뎁터 쪽으로)
        arrayList_copy = new ArrayList<>();



        //Button btn_back= vGroup.findViewById(R.id.btn_back);
        Button btn_search= vGroup.findViewById(R.id.btn_search);
        btn_inter = vGroup.findViewById(R.id.btn_inter);
        btn_time = vGroup.findViewById(R.id.btn_time);



        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                selectDate = myCalendar.getTime();
                filterDateInMeetToRecyclerView();
            }
        };



        //달력클릭하면 날짜 검색
        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        //2021-08-16 검색기능 구현
        list_search_recycle= new ArrayList<String>();
        arrayList_search_recycle= new ArrayList<String>();//리스트의 모든 데이터를 arraylist_search에 복사


        //data
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("meet");//DB Table Connect

        //관심목록으로 필터링
        databaseReference2 = FirebaseDatabase.getInstance().getReference();
        Query recentPostsQuery = databaseReference2.child("meet")
                .equalTo(100); //내 hobby목록이랑 같은 지

        //getUserHobby();//현재 유저의 취미값 업데이트
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot dataSnapshot) {
                //실제적으로 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); //기존 배열리스트가 존재하지 않게 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){//반복문으로 데이터 List를 추출해냄.

                    Meet meet = snapshot.getValue(Meet.class); // 만들어놨던 Meet 객체에 데이터를 담는다.


                    arrayList.add(meet); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비.
                    arrayList_copy.addAll(arrayList);//arrayList_copy에 복사


                    //단어 검색
                    //2021-08-16 검색기능 구현
                    // meet의 값이 null값이 아니면, list_search_recycle이라는 리스트에 넣어라.
                    if (meet.title!=null){
                        list_search_recycle.add(meet.title);//list_search_recycle에 title값 저장
                        //취미목록 검색 리스트에 넣기 2021-09-13
                        int totalHobbyCount2 = meet.hobbyCate.size();
                        for (int index = 0; index < totalHobbyCount2; index++) {
                            list_search_recycle.add(meet.hobbyCate.get(index)); //hobbyCate의 배열값을 넣는다.
                        }

                    }

                }
                Log.d("arrayList_copy", String.valueOf(arrayList_copy));

                //2021-08-16 검색기능 구현
                arrayList_search_recycle.addAll(list_search_recycle);//제목으로 모임검색 구현,복사해준다.



//                customAdapter= new CustomAdapter(arrayList,getContext());
//                recyclerView.setAdapter(customAdapter);

                //
                //filterHobbyCateInMeetToRecyclerView();
                String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                database= FirebaseDatabase.getInstance();
                databaseReference=database.getReference("users").child(uid);//DB Table Connect
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //firebase에서 데이터 받아오기
                        Member member = dataSnapshot.getValue(Member.class);

                        listUserHobby = member.getHobbyCate(); //listUserHobby에 유저의 취미값 받아오기

                        Log.d("listUserHobby.get(0)", String.valueOf(listUserHobby.get(0)));
                        //Log.d("listUserHobby.get(1)", String.valueOf(listUserHobby.get(1)));

                        Log.d("getHobbyCate", String.valueOf(member.getHobbyCate()));
                        Log.d("getUser_listUserHobby", String.valueOf(listUserHobby));
                        Log.d("after_listUserHobby", String.valueOf(listUserHobby));

                        arrayList.clear();//되랏, 수박 등 실제 meet에서 가져온 정보들 list_search와 동일

                        for (int i=0;i<arrayList_copy.size();i++){// 전체 meet 데이터 중에서
                            //만약 listUserHobby의 값 중  listMeetsHobbyd의 값이 일치한다면
                            int totalUserHobby = listUserHobby.size(); //user취미 리스트의 크기
                            int totalHobbyCateSize = arrayList_copy.get(i).getHobbyCate().size(); //현재 meet데이터의 취미리스트 크기

                            Log.d("totalUserHobby", String.valueOf(totalUserHobby));//1이여야하는데 왜 0이지?
                            Log.d("totalHobbyCateSize", String.valueOf(totalHobbyCateSize));

                            for (int j=0; j< totalHobbyCateSize; j++){
                                for (int index =0; index<totalUserHobby; index++){
                                    // 그에 해당하는 meet의 데이터만
                                    if (listUserHobby.get(index).equals(arrayList_copy.get(i).getHobbyCate().get(j))  ){
                                        Log.d("listuserhobbtgetindex",listUserHobby.get(index));//일식
                                        Log.d("arrayList_copy_getHobby",arrayList_copy.get(i).getHobbyCate().get(j));//일식
                                        if (UniqueCheckAndAdd(arrayList,arrayList_copy.get(i)) == true){
                                            //arrayList에 업데이트
                                            arrayList.add(arrayList_copy.get(i));//검색된 데이터를 리스트에 추가
                                            //검색한 값만 잘 들어온다.
                                            Log.d("arrayList_new", String.valueOf(arrayList));
                                            //Log.d("size", String.valueOf(arrayList_copy.size()));//253??
                                        }
                                        //Log.d("if조건2", arrayList_copy.get(i).getHobbyCate().get(j));
                                    }
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //디비를 가져오는 도중 에러 발생 시
                        Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", error.toException());

                    }
                });

                customAdapter= new CustomAdapter(arrayList,getContext());
                Log.d("arrayListget(0)getHobbyCate()", String.valueOf(arrayList.get(0).getHobbyCate()));//도자기공예, 한식
                recyclerView.setAdapter(customAdapter);


                //test
                for (int i=0;i<arrayList_copy.size();i++){
                    Log.d("arrayList_copy vaule", String.valueOf(arrayList_copy.get(i).getTitle()));
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                //디비를 가져오는 도중 에러 발생 시
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e("MainActivityHome",String.valueOf(error.toException()));

            }
        });
        Log.d("custom_arraylist", String.valueOf(arrayList));
        //filterHobbyCateInMeetToRecyclerView(); //유저의 취미값에 해당하는 모임 검색


        //검색기능
        editSearch= vGroup.findViewById(R.id.editSearch);
        listView= vGroup.findViewById(R.id.listView);


        //arrayList_search.addAll(list_search); 궅이 필요 x 제거해도 된다.
        searchAdapter = new SearchAdapter(list_search_recycle,getContext());
        listView.setAdapter(searchAdapter);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력하기 전에 조치
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력란에 변화가 있을 시 조치
                listView.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력이 끝났을 때
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String searchText = editSearch.getText().toString();
                searchInMeet(searchText);

                //2021-08-16 검색기능 구현
                //검색된 단어가 제목에 들어가는 게시물 띄어보이기(recycler view)
                //검색한 단어와 meetName이 같은 모임만 다시 띄운다.
                //customAdapter= new CustomAdapter(arrayList_search_recycle,getContext());
                //recyclerView.setAdapter(customAdapter);

            }

        });

        //검색버튼 클릭하면면
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText =editSearch.getText().toString();//검색어를 가져와서
                searchInMeetToRecyclerView(searchText);//검색기능 메서드호출

            }
        });
        return vGroup;
    }



    //검색기능메서드
    private void searchInMeet(String searchText) {

        list_search_recycle.clear();//되랏, 수박 등 실제 meet에서 가져온 정보들 list_search와 동일

         //문자 입력이 없을때는 모든 데이터를 보여준다.
        if (searchText.length()==0){
            list_search_recycle.addAll(arrayList_search_recycle);//list_search_recycler실제 검색이 끝난 후 리스트, arrayList_search_recycle: 모든 값이 들어있는 리스트

        }
        //문자 입력 있을 때
        else {
            // 리스트의 모든 데이터를 검색
            for (int i=0;i<arrayList_search_recycle.size();i++){

                // arraylist_search의 모든 데이터에 입력받은 단어(searchText)가 포함되어 있으면 true를 반환
                if(arrayList_search_recycle.get(i).toLowerCase().contains(searchText)){
                    list_search_recycle.add(arrayList_search_recycle.get(i));//검색된 데이터를 리스트에 추가
                    //검색한 값만 잘 들어온다.
                    Log.d("list_search_recycle", String.valueOf(list_search_recycle));

                }
            }//for
        }//else
        searchAdapter.notifyDataSetChanged();

    }

    //검색기능-> recyclerview
    private void searchInMeetToRecyclerView(String searchText) {

        arrayList.clear();//되랏, 수박 등 실제 meet에서 가져온 정보들 list_search와 동일

        //문자 입력이 없을때는 모든 데이터를 보여준다.
        if (searchText.length()==0){
            //arrayList.addAll(arrayList_copy);
            Log.d("arrayList_copy_method", String.valueOf(arrayList));//값이 안나온다

        }
        //문자 입력 있을 때
        else {
            // 리스트의 모든 데이터를 검색
            for (int i=0;i<arrayList_copy.size();i++){

                try {
                    // arraylist_search의 모든 데이터에 입력받은 단어(searchText)가 포함되어 있으면 true를 반환
                    if(arrayList_copy.get(i).getTitle().toLowerCase().contains(searchText)){
                        if (UniqueCheckAndAdd(arrayList,arrayList_copy.get(i)) == true){
                            arrayList.add(arrayList_copy.get(i));//검색된 데이터를 리스트에 추가
                            //검색한 값만 잘 들어온다.
                            Log.d("arrayList_new", String.valueOf(arrayList));
                            Log.d("size", String.valueOf(arrayList_copy.size()));//253??
                        }
                    }
                }catch (NullPointerException exception){

                }
                try {
                    // 취미목록으로 검색클릭하면 recyclerview에 반영
                    for (int j=0; j<arrayList_copy.get(i).getHobbyCate().size(); j++){
                        if(arrayList_copy.get(i).getHobbyCate().get(j).toLowerCase().contains(searchText)){
                            if (UniqueCheckAndAdd(arrayList,arrayList_copy.get(i)) == true){
                                arrayList.add(arrayList_copy.get(i));//검색된 데이터를 리스트에 추가
                                //검색한 값만 잘 들어온다.
                                Log.d("arrayList_new", String.valueOf(arrayList));
                                Log.d("size", String.valueOf(arrayList_copy.size()));//253??
                            }
                        }
                    }
                }catch (NullPointerException exception){

                }



            }//for
        }//else
        //arrayList_uniqe = new ArrayList<>();
        //arrayList_uniqe = new Set<arrayList>();
        customAdapter.notifyDataSetChanged();

    }
    public boolean UniqueCheckAndAdd(ArrayList<Meet> array, Meet meetPresent){
        return !array.contains(meetPresent);
    }


    // 유저가 선택한 취미카테고리인 모임정보만 보여준다.
    private void filterHobbyCateInMeetToRecyclerView() {

        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        database= FirebaseDatabase.getInstance();
        databaseReference=database.getReference("users").child(uid);//DB Table Connect
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //firebase에서 데이터 받아오기
                Member member = dataSnapshot.getValue(Member.class);

                listUserHobby = member.getHobbyCate(); //listUserHobby에 유저의 취미값 받아오기

                Log.d("listUserHobby.get(0)", String.valueOf(listUserHobby.get(0)));
                //Log.d("listUserHobby.get(1)", String.valueOf(listUserHobby.get(1)));

                Log.d("getHobbyCate", String.valueOf(member.getHobbyCate()));
                Log.d("getUser_listUserHobby", String.valueOf(listUserHobby));
                Log.d("after_listUserHobby", String.valueOf(listUserHobby));

                arrayList.clear();//되랏, 수박 등 실제 meet에서 가져온 정보들 list_search와 동일

                for (int i=0;i<arrayList_copy.size();i++){// 전체 meet 데이터 중에서
                    //만약 listUserHobby의 값 중  listMeetsHobbyd의 값이 일치한다면
                    int totalUserHobby = listUserHobby.size(); //user취미 리스트의 크기
                    int totalHobbyCateSize = arrayList_copy.get(i).getHobbyCate().size(); //현재 meet데이터의 취미리스트 크기

                    Log.d("totalUserHobby", String.valueOf(totalUserHobby));//1이여야하는데 왜 0이지?
                    Log.d("totalHobbyCateSize", String.valueOf(totalHobbyCateSize));

                    for (int j=0; j< totalHobbyCateSize; j++){
                        for (int index =0; index<totalUserHobby; index++){
                            // 그에 해당하는 meet의 데이터만
                            if (listUserHobby.get(index).equals(arrayList_copy.get(i).getHobbyCate().get(j))  ){

                                if (UniqueCheckAndAdd(arrayList,arrayList_copy.get(i)) == true){
                                    //arrayList에 업데이트
                                    arrayList.add(arrayList_copy.get(i));//검색된 데이터를 리스트에 추가
                                    //검색한 값만 잘 들어온다.
                                    Log.d("arrayList_new", String.valueOf(arrayList));
                                    //Log.d("size", String.valueOf(arrayList_copy.size()));//253??
                                }
                                //Log.d("if조건2", arrayList_copy.get(i).getHobbyCate().get(j));
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //디비를 가져오는 도중 에러 발생 시
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", error.toException());

            }
        });



        //customAdapter.notifyDataSetChanged();

    }
    // 유저가 선택한 날짜에 해당하는 모임정보만 보여준다.
    private void filterDateInMeetToRecyclerView() {

        arrayList.clear();//되랏, 수박 등 실제 meet에서 가져온 정보들 list_search와 동일

        //selectDate = new Date(121,8,13); // 선택한 데이터 값.
        //샘플 데이터 ( 입력받은 날짜 넣기)
//        selectDate = new Date();
//        //selectDate.setYear(2021);
//        selectDate.setDate(14);
//        selectDate.setMonth(8);
//        selectDate.setYear(121);


        //방법 2 날짜 일치 확인을 위한 데이터 포멧 설정
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateFormatSelect =simpleDateFormat.format(selectDate);//선택한 날짜 String 형 변수

        for (int i=0;i<arrayList_copy.size();i++){// 전체 meet 데이터 중에서

            //format예외처리
            try {
                String dateFormatMeet = simpleDateFormat.format(arrayList_copy.get(i).getMeetDate());//Meet의 날짜 String 형 변수

                try {
                    if (dateFormatMeet.equals(dateFormatSelect)){

                        if (UniqueCheckAndAdd(arrayList,arrayList_copy.get(i)) == true){
                            arrayList.add(arrayList_copy.get(i));//검색된 데이터를 리스트에 추가
                            Log.d("날짜검색meet", String.valueOf(arrayList));
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(),"해당 날짜의 모임이 없습니다.",Toast.LENGTH_SHORT);
                }


            }catch (NullPointerException nullPointerException){
                Toast.makeText(getContext(),"fomat Error.",Toast.LENGTH_SHORT);
            }


            //방법 1. getMeetDate와 getMeetMonth, getMeetYar 가 모두 일치한다면
//            if (arrayList_copy.get(i).getMeetDate().getYear().equals(selectDate.getYear())&(arrayList_copy.get(i).getMeetDate().getMonth().equals(selectDate.getMonth())
//            &(arrayList_copy.get(i).getMeetDate().getDate().equals(selectDate.getDate())){
//
//                arrayList.add(arrayList_copy.get(i));//검색된 데이터를 리스트에 추가
//            }
        }
        customAdapter.notifyDataSetChanged();

    }


    private void allInRecyclerView(){



    }


}