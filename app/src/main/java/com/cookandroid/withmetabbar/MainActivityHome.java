package com.cookandroid.withmetabbar;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.withmetabbar.model.Meet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainActivityHome extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Meet> arrayList;//? 검색을 보여줄 리스트 변수
    //
    private List<Meet> list;
    private CustomAdapter customAdapter;
    //
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private List<String> list_search; //자동완성 단어들을 담을 리스트
    private AutoCompleteTextView autoCompleteTextView; //검색어입력창
    private SearchView searchView; //검색어 입력 창
    private SearchAdapter searchAdapter;
    private EditText editSearch;//검색어를 입력할 Input창
    private ListView listView;//검색을 보여줄 리스변수
    private ArrayList<String> arrayList_search;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ViewGroup vGroup = (ViewGroup) inflater.inflate(R.layout.activity_main_home, container, false);

        recyclerView=vGroup.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);//기존 리사이클러뷰의 성능 강화
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();//Meet객체를 담을 어레이리스트 (어뎁터 쪽으로)


        //data
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("meet");//DB Table Connect
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot dataSnapshot) {
                //실제적으로 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); //기존 배열리스트가 존재하지 않게 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){//반복문으로 데이터 List를 추출해냄.
                    Meet meet = snapshot.getValue(Meet.class); // 만들어놨던 Meet 객체에 데이터를 담는다.
                    arrayList.add(meet); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비.
                    Log.d("arrayList", String.valueOf(arrayList));

                }
                customAdapter= new CustomAdapter(arrayList,getContext());
                recyclerView.setAdapter(customAdapter);
                //adapter.notifyDataSetChanged();//리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                //디비를 가져오는 도중 에러 발생 시
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e("MainActivityHome",String.valueOf(error.toException()));

            }
        });

        //검색기능

        editSearch=(EditText)vGroup.findViewById(R.id.editSearch);
        listView=(ListView)vGroup.findViewById(R.id.listView);


        //검색기능-리스트 생성
        list_search= new ArrayList<String>();
        settingList();// 검색에 사용할 데이터 저장
        arrayList_search= new ArrayList<String>();//리스트의 모든 데이터를 arraylist_search에 복사
        arrayList_search.addAll(list_search);
        searchAdapter = new SearchAdapter(list_search,getContext());
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
                search(searchText);


            }

        });


        return vGroup;
    }



    private void settingList() {
        list_search.add("채수빈");
        list_search.add("박지현");
        list_search.add("수지");
        list_search.add("남태현");
        list_search.add("하성운");
        list_search.add("크리스탈");
        list_search.add("강승윤");
        list_search.add("손나은");
        list_search.add("남주혁");
        list_search.add("루이");
        list_search.add("진영");
        list_search.add("슬기");
        list_search.add("이해인");

    }

    private void search(String searchText) {

        list_search.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (searchText.length()==0){
            list_search.addAll(arrayList_search);

        }
        //문자 입력 있을 때
        else {
            // 리스트의 모든 데이터를 검색
            for (int i=0;i<arrayList_search.size();i++){

                // arraylist_search의 모든 데이터에 입력받은 단어(searchText)가 포함되어 있으면 true를 반환
                if(arrayList_search.get(i).toLowerCase().contains(searchText)){
                    list_search.add(arrayList_search.get(i));//검색된 데이터를 리스트에 추가
                }
            }//for
        }//else
        searchAdapter.notifyDataSetChanged();

    }


}