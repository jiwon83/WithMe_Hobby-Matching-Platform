package com.cookandroid.withmetabbar.navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.withmetabbar.MainActivity;
import com.cookandroid.withmetabbar.R;
import com.cookandroid.withmetabbar.certify.FragmentSelectHobby;
import com.cookandroid.withmetabbar.certify.JoinStartFragment;
import com.cookandroid.withmetabbar.certify.MainActivity2;
import com.cookandroid.withmetabbar.model.Hobby;
import com.cookandroid.withmetabbar.model.HobbyBig;
import com.cookandroid.withmetabbar.model.Meet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.collection.LLRBNode;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.GRAY;
import static android.graphics.Color.MAGENTA;
import static android.graphics.Color.WHITE;
import static android.graphics.Color.YELLOW;

public class FragmentPlusSelectHobby extends AppCompatActivity implements OnItemClick{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ImageButton imgBtn;
    TextView tv;//취미분야 선택
    ProgressBar progressBar;
    private String selectedHobbyBig;
    Button btnFinish; //메인화면으로 화면전환버튼
    ArrayList<String> list = new ArrayList<>(); //bundle 전달을 위해 선택한 취미값들을 받아서 저장할 배열

    //중첩 recyclerView에 넣을 데이터
    private final ArrayList<ArrayList<Hobby>> allHobbyList = new ArrayList();
    private final ArrayList<HobbyBig> HobbyBigList2 = new ArrayList();//대분류


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    int color; //색깔 넣기


    public FragmentPlusSelectHobby() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSelectHobby.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSelectHobby newInstance(String param1, String param2) {
        FragmentSelectHobby fragment = new FragmentSelectHobby();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_select_hobby2);

        tv=findViewById(R.id.textView);
        progressBar=findViewById(R.id.progressBar);
        btnFinish= findViewById(R.id.selectHobby2_btn_next);

        tv.setText("취미 분야 선택 ");




//        int[] img ={
//                R.drawable.hobby_music,R.drawable.hb_craft,R.drawable.hb_sport
//
//        };



        //바로 해보기
        ArrayList<ArrayList<String>> hbLists = new ArrayList<ArrayList<String>>();

        ArrayList<String> hbList_music = new ArrayList<>();
        hbList_music.add("음악");
        hbList_music.add("클래식");
        hbList_music.add("k-pop");
        hbList_music.add("뉴에이지");
        hbList_music.add("발라드");
        hbList_music.add("랩/힙합");
        hbList_music.add("인디음악");
        hbList_music.add("록/메탈");
        hbList_music.add("포크/블루스");

        //2차원배열에 답기
        hbLists.add(hbList_music);

        //tv.setText((CharSequence) hbLists.get(0).get(0));

        //응용참고
        /*
        System.out.println(datas.size());
        	//결과: 3
		System.out.println(datas.toString());
        	//결과: [[1, 2, 3], [11, 22, 33], [111, 222, 333]]

		System.out.println(datas.get(0));
		//결과: [1, 2, 3]
         */

        //리사이클러뷰
        this.initializeData();
        this.initializeData2();

        RecyclerView view = findViewById(R.id.recyclerViewVertical);

        VerticalAdapter verticalAdapter = new VerticalAdapter(getApplicationContext(), allHobbyList, HobbyBigList2,this);
        //여기의 this는 이 클래스 자체를 의미한다. 아래의 OnItemClick 을 override한 onClick()메서드가 실행되어야 하기 때문이다.
        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        view.setAdapter(verticalAdapter);


        //화면전환
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                Intent intent = new Intent();//startActivity()를 할것이 아니므로 그냥 빈 인텐트로 만듦
                //list 내부의 중복을 제거

                bundle.putStringArrayList("hobby", list);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
//                FragmentPlus fragmentPlus = new FragmentPlus();
//                fragmentPlus.setArguments(bundle);


                //취미 result api 구현
//                Bundle result = new Bundle();
//                result.putStringArrayList("bundleKey", list);
//                getParentFragmentManager().setFragmentResult("requestKey",result);//fragmentPlus의 fragmentManager에 전달


                //hide & show
//                ((MainActivity)getActivity()).hideFragment(FragmentPlusSelectHobby.this);
                //FragmentPlus fragmentPlus = new FragmentPlus();
//                ((MainActivity)getActivity()).showFragment(fragmentPlus);

                //remove
                //((MainActivity)getActivity()).removeFragment(FragmentPlusSelectHobby.this);
                //FragmentPlus fragmentPlus = new FragmentPlus();

                //replace
                //((MainActivity)getActivity()).replaceFragment(fragmentPlus);


            }
        });


    }


////        int[] img ={
////                R.drawable.hobby_music,R.drawable.hb_craft,R.drawable.hb_sport
////
////        };


    @Override
    public void onClick(String value) {
        //value this data you receive when selectedItems() called
        if (UniqueCheckAndAdd(list,value)==true)
        list.add(value);
        Log.d("onClickSuccess?",value); //성공적으로 받아진다.
    }

    //소분류 취미목록 데이터 입력
    public void initializeData()
    {
        ArrayList<Hobby> hobbyList1 = new ArrayList();

        hobbyList1.add(new Hobby( "클래식"));
        hobbyList1.add(new Hobby("K-pop"));
        hobbyList1.add(new Hobby("뉴에이지"));
        hobbyList1.add(new Hobby("발라드"));
        hobbyList1.add(new Hobby("랩/힙합"));
        hobbyList1.add(new Hobby("인디음악"));
        hobbyList1.add(new Hobby("록/메탈"));
        hobbyList1.add(new Hobby("포크.블루스"));


        allHobbyList.add(hobbyList1);

        ArrayList<Hobby> hobbyList2 = new ArrayList();

        hobbyList2.add(new Hobby( "야구"));
        hobbyList2.add(new Hobby( "축구"));
        hobbyList2.add(new Hobby( "농구"));
        hobbyList2.add(new Hobby( "볼링"));
        hobbyList2.add(new Hobby( "당구"));
        hobbyList2.add(new Hobby( "수영"));
        hobbyList2.add(new Hobby( "탁구"));
        hobbyList2.add(new Hobby( "폴댄스"));
        hobbyList2.add(new Hobby( "골프"));
        hobbyList2.add(new Hobby( "배드민턴"));
        hobbyList2.add(new Hobby( "클라이밍"));

        allHobbyList.add(hobbyList2);

        ArrayList<Hobby> hobbyList3 = new ArrayList();

        hobbyList3.add(new Hobby( "캠핑"));
        hobbyList3.add(new Hobby( "바다"));
        hobbyList3.add(new Hobby( "국내"));
        hobbyList3.add(new Hobby( "해외"));
        hobbyList3.add(new Hobby( "당일치기"));
        hobbyList3.add(new Hobby( "배낭여행"));
        hobbyList3.add(new Hobby( "익스트림"));
        hobbyList3.add(new Hobby( "기타"));

        allHobbyList.add(hobbyList3);
        ArrayList<Hobby> hobbyList4 = new ArrayList();

        hobbyList4.add(new Hobby( "전시"));
        hobbyList4.add(new Hobby( "뮤지컬"));
        hobbyList4.add(new Hobby( "영화"));
        hobbyList4.add(new Hobby( "해외"));
        hobbyList4.add(new Hobby( "콘서트"));
        hobbyList4.add(new Hobby( "연극"));
        hobbyList4.add(new Hobby( "오페라"));
        hobbyList4.add(new Hobby( "연주회"));
        hobbyList4.add(new Hobby( "오케스트라"));
        hobbyList4.add(new Hobby( "기타"));

        allHobbyList.add(hobbyList4);

        ArrayList<Hobby> hobbyList5 = new ArrayList();

        hobbyList5.add(new Hobby( "리본공예"));
        hobbyList5.add(new Hobby( "비즈공예"));
        hobbyList5.add(new Hobby( "도자기공예"));
        hobbyList5.add(new Hobby( "넵킨공예"));
        hobbyList5.add(new Hobby( "포크아트"));
        hobbyList5.add(new Hobby( "북아트"));
        hobbyList5.add(new Hobby( "클레이아트"));
        hobbyList5.add(new Hobby( "가죽공예"));
        hobbyList5.add(new Hobby( "유리공예"));
        hobbyList5.add(new Hobby( "플라워아트"));

        allHobbyList.add(hobbyList5);

        ArrayList<Hobby> hobbyList6 = new ArrayList();

        hobbyList6.add(new Hobby( "양식"));
        hobbyList6.add(new Hobby( "한식"));
        hobbyList6.add(new Hobby( "일식"));
        hobbyList6.add(new Hobby( "중식"));
        hobbyList6.add(new Hobby( "베이킹"));
        hobbyList6.add(new Hobby( "설탕공예"));


        allHobbyList.add(hobbyList6);

        ArrayList<Hobby> hobbyList7 = new ArrayList();

        hobbyList7.add(new Hobby( "강아지"));
        hobbyList7.add(new Hobby( "고양이"));
        hobbyList7.add(new Hobby( "조류"));
        hobbyList7.add(new Hobby( "햄스터"));
        hobbyList7.add(new Hobby( "파충류"));
        hobbyList7.add(new Hobby( "곤충"));


        allHobbyList.add(hobbyList7);

        ArrayList<Hobby> hobbyList8 = new ArrayList();

        hobbyList8.add(new Hobby( "등산"));
        hobbyList8.add(new Hobby( "서핑"));
        hobbyList8.add(new Hobby( "패들보드"));
        hobbyList8.add(new Hobby( "카약"));
        hobbyList8.add(new Hobby( "번지점프"));
        hobbyList8.add(new Hobby( "패러글라이딩"));
        hobbyList8.add(new Hobby( "스카이다이빙"));
        hobbyList8.add(new Hobby( "스킨스쿠버"));


        allHobbyList.add(hobbyList8);

        ArrayList<Hobby> hobbyList9 = new ArrayList();

        hobbyList9.add(new Hobby( "피아노"));
        hobbyList9.add(new Hobby( "기타"));
        hobbyList9.add(new Hobby( "드럼"));
        hobbyList9.add(new Hobby( "베이스"));
        hobbyList9.add(new Hobby( "바이올린"));
        hobbyList9.add(new Hobby( "첼로"));
        hobbyList9.add(new Hobby( "작곡"));


        allHobbyList.add(hobbyList9);

        ArrayList<Hobby> hobbyList10 = new ArrayList();

        hobbyList10.add(new Hobby( "중국어"));
        hobbyList10.add(new Hobby( "일본어"));
        hobbyList10.add(new Hobby( "불어"));
        hobbyList10.add(new Hobby( "스페인어"));
        hobbyList10.add(new Hobby( "토플"));
        hobbyList10.add(new Hobby( "토익"));
        hobbyList10.add(new Hobby( "코딩"));


        allHobbyList.add(hobbyList10);
    }

    //대분류 취미목록 데이터 입력
    public void initializeData2()
    {
//        ArrayList<HobbyBig> hobbyBigList = new ArrayList();
//        hobbyBigList.add(new HobbyBig( "음악"));
//        hobbyBigList.add(new HobbyBig("스포츠"));
//        hobbyBigList.add(new HobbyBig("여행"));
//        hobbyBigList.add(new HobbyBig("문화"));
//        hobbyBigList.add(new HobbyBig("공예"));
//        hobbyBigList.add(new HobbyBig("요리"));
//        hobbyBigList.add(new HobbyBig("게임"));
//        hobbyBigList.add(new HobbyBig("팬모임"));

        HobbyBigList2.add(new HobbyBig( "음악"));
        HobbyBigList2.add(new HobbyBig( "스포츠"));
        HobbyBigList2.add(new HobbyBig( "여행"));
        HobbyBigList2.add(new HobbyBig( "문화"));
        HobbyBigList2.add(new HobbyBig( "공예"));
        HobbyBigList2.add(new HobbyBig( "요리"));
        HobbyBigList2.add(new HobbyBig( "반려동물"));
        HobbyBigList2.add(new HobbyBig( "익스트림"));
        HobbyBigList2.add(new HobbyBig( "악기연주"));
        HobbyBigList2.add(new HobbyBig( "공부"));

    }
    public boolean UniqueCheckAndAdd(ArrayList<String> array, String stringPresent){
        return !array.contains(stringPresent);
    }
}


class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.HorizontalViewHolder> {

    private OnItemClick mCallback; //OnItemClick 인터페이스 객체
   private ArrayList<Hobby> dataList;
    private final SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);//클릭하면 색상변경//클릭 안하면 0 하면 1??
    private final ArrayList<Hobby> selectedList= new ArrayList<>(); //선택한 취미 값

    public HorizontalAdapter(){}

    public HorizontalAdapter(ArrayList<Hobby> data, OnItemClick listener)
    {
        this.dataList = data;
        this.mCallback = listener;
    }

//    public interface OnItemClickListener{
//        void onItemClick(View v, int position);
//    }//어텝터 내에서 커스텀 리스너 인터페이스 정의


    public class HorizontalViewHolder extends RecyclerView.ViewHolder{
        protected TextView tv;
        private final ArrayList<String> selectData = null ;
        private final String selected="";
        int count=1;

        //받아올 값
        public void selectedItems(){
            mCallback.onClick(tv.getText().toString());//tv를 클릭하면?
        }

        public HorizontalViewHolder(View view)
        {
            super(view);
            tv = view.findViewById(R.id.btn_hbList_in);
            //클릭하면 색상변경
            view.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    Log.d("tv",tv.getText().toString());
                    selectedItems();



                    if ( mSelectedItems.get(position, false) ){

                        mSelectedItems.put(position, false);
                        v.setBackgroundColor(ContextCompat.getColor(v.getContext().getApplicationContext(), R.color.gray));



                    } else {
                        mSelectedItems.put(position, true);
                        v.setBackgroundColor(ContextCompat.getColor(v.getContext().getApplicationContext(), R.color.Orange));
                    }
                    Log.d("test", "position = " + position);

                }


            });

        }

    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.select_hobby2_grid_in2, null);




        return new HorizontalViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(HorizontalViewHolder horizontalViewHolder, int position)
    {
//        horizontalViewHolder
//                .image
//                .setImageResource(dataList.get(position).getResourceID());
        horizontalViewHolder
                .tv
                .setText(dataList.get(position).getHobbyname());

        //클릭하면 색상 변경
        if ( mSelectedItems.get(position, false) ){
            horizontalViewHolder.itemView.setBackgroundColor(R.color.Orange);//선택했을 때
        } else {
            horizontalViewHolder.itemView.setBackgroundColor(R.color.gray);//기본, 선택 안했을 때
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public ArrayList<Hobby> getSelected(){

        return selectedList;
    }




}

class VerticalAdapter extends RecyclerView.Adapter<VerticalAdapter.VerticalViewHolder>{

    private OnItemClick mCallback;
    private ArrayList<ArrayList<Hobby>> AllHobbyList;//전체 취미목록들을 2차원 배열에 넣어준다.
    private Context context;
    //test 취미목록 대분류 넣기
    private final ArrayList<HobbyBig> DataListBig;

    //test
    public VerticalAdapter(ArrayList<HobbyBig> data)
    {
        this.DataListBig = data;
    }
    //

    public VerticalAdapter(Context context, ArrayList<ArrayList<Hobby>> data, ArrayList<HobbyBig> data2,OnItemClick listener)
    {
        this.mCallback =listener;
        this.context = context;
        this.AllHobbyList = data;
        this.DataListBig = data2;//대분류
    }
    //VerticalViewHolder 맨 처음 10개의 뷰객체를 기억하고 있을(홀딩) 객체
    public class VerticalViewHolder extends RecyclerView.ViewHolder{
        protected RecyclerView recyclerView;
        protected TextView textView;

        public VerticalViewHolder(View view)
        {
            super(view);
            textView = view.findViewById(R.id.tv_hobby);
            this.recyclerView = view.findViewById(R.id.recyclerViewVertical);
        }
    }

    @NonNull
    @Override
    public VerticalViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.select_hobby2_grid_in, null);
        return new VerticalViewHolder(v);
    }
    //수직 RecyclerView의 데이터 항목은 앞서 구현한 수평 RecyclerView가 배치된다고 하였습니다.
    // 그러기 위해 onBindViewHolder에서 HorizontalAdapter 객체를 생성
    @Override
    public void onBindViewHolder(@NonNull VerticalViewHolder verticalViewHolder, int position) {
        HorizontalAdapter adapter = new HorizontalAdapter(AllHobbyList.get(position),mCallback);
        //adapter.getSelected();
        //현재 위치의 arrayList를 AllHobbyList로 받아서 adapter생성.

        //대분류 배열 처리- textView에 넣기
        verticalViewHolder
                .textView
                .setText(DataListBig.get(position).getHobbyBigname());

        verticalViewHolder.recyclerView.setHasFixedSize(true);
        verticalViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context
                , LinearLayoutManager.HORIZONTAL
                ,false));
        verticalViewHolder.recyclerView.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return AllHobbyList.size();
    }
}
