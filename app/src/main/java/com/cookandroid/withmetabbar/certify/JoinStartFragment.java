package com.cookandroid.withmetabbar.certify;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.RadioGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cookandroid.withmetabbar.MainActivity;
import com.cookandroid.withmetabbar.MainActivityHome;
import com.cookandroid.withmetabbar.MainActivityWebView;
import com.cookandroid.withmetabbar.R;
import com.cookandroid.withmetabbar.model.ChatModel;
import com.cookandroid.withmetabbar.model.Meet;
import com.cookandroid.withmetabbar.model.MeetInfo;
import com.cookandroid.withmetabbar.model.Member;
import com.cookandroid.withmetabbar.navigation.FragmentPlusSelectHobby;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class JoinStartFragment extends Fragment {

    ArrayList<String> list = new ArrayList<>();
    private Date meetDate;
    private static final int MAIN_ACTIVITY_WEBVIEW = 10000;
    private AlertDialog dialog;
    private final int GET_GALLERY_IMAGE = 200;//?무슨의미
    private ImageView imageView, imageView7;
    private Uri imageUri;//프로필이미지
    private MediaPlayer MP;
    private static final int SELECT_HOBBY = 30000;
    private String uid;
    private EditText etId,etPw,etName,etNick,etBirth,etAge,etPhoneNum,et_Birth;
    private RadioButton cb_male,cb_female,cb_no;



    Button btn_join,btn_live,btn_hobbys;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }





    protected void onMainActivity(int requestCode, int resultCode, Intent data) {
        if(requestCode == GET_GALLERY_IMAGE&&resultCode == RESULT_OK&&data !=
                null && data.getData()!=null){
            Uri selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup vGroup = (ViewGroup) inflater.inflate(R.layout.join_start_fragment, container, false);
        etId = vGroup.findViewById(R.id.etId);
        etPw = vGroup.findViewById(R.id.etPw);
        etName = vGroup.findViewById(R.id.etName);
        etNick = vGroup.findViewById(R.id.etNick);
        etBirth = vGroup.findViewById(R.id.etBirth);
        etAge = vGroup.findViewById(R.id.etAge);
        etPhoneNum = vGroup.findViewById(R.id.etPhoneNum);
        btn_join= vGroup.findViewById(R.id.button6);
        btn_live = vGroup.findViewById(R.id.btn_live);
        btn_hobbys = vGroup.findViewById(R.id.btn_hobbys);
        cb_male = vGroup.findViewById(R.id.check_male);
        cb_female = vGroup.findViewById(R.id.check_female);
        cb_no = vGroup.findViewById(R.id.checkNo);
        et_Birth = vGroup.findViewById(R.id.etBirth);
        Button check_overlap= vGroup.findViewById(R.id.button_check);
        RadioGroup gender = vGroup.findViewById(R.id.gender);

        Intent intent = new Intent();

        //라디오 그룹 설정

        Bundle bundle = getArguments();
        etPhoneNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        //id 중복확인
        check_overlap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        //ArrayList<Member> arrayList = new ArrayList<>();

                        ArrayList<String> stringArrayList = new ArrayList<>();

                        for (DataSnapshot dataSnapshott : snapshot.getChildren()){//반복문으로 데이터 List를 추출해냄.

                            Member member = dataSnapshott.getValue(Member.class);

                            stringArrayList.add(member.id);

                            Log.d("stringArrayList", String.valueOf(stringArrayList));

                            //arrayList.add(member); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비.



                            //arrayList_copy.addAll(arrayList);//arrayList_copy에 복사

                            //단어 검색
                            //2021-08-16 검색기능 구현
                            // meet의 값이 null값이 아니면, list_search_recycle이라는 리스트에 넣어라.
//                            if (meet.title!=null){
//                                list_search_recycle.add(meet.title);//list_search_recycle에 title값 저장
//                                //취미목록 검색 리스트에 넣기 2021-09-13
//                                int totalHobbyCount2 = meet.hobbyCate.size();
//                                for (int index = 0; index < totalHobbyCount2; index++) {
//                                    list_search_recycle.add(meet.hobbyCate.get(index)); //hobbyCate의 배열값을 넣는다.
//                                }
//
//                            }
                        }
                        for (int i=0; i<stringArrayList.size(); i++){

                            if (etId.getText().toString().equals(stringArrayList.get(i))){

                                Log.d("stringArrayList.get(i)",stringArrayList.get(i));
                                Log.d("etId.getText().toString()",etId.getText().toString());

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                dialog = builder.setMessage("이미 존재하는 email입니다.")
                                        .setNegativeButton("OK", null)
                                        .create();
                                dialog.show();
                                return;
                                //Toast.makeText(getContext(),"이미 존재하는 email입니다.",Toast.LENGTH_SHORT).show();//토스메세지 출력
                                //etId.setText("");

                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                dialog = builder.setMessage("중복확인 되었습니다.")
                                        .setNegativeButton("OK", null)
                                        .create();
                                dialog.show();
                                return;
                            }
                        }
//                        String value = snapshot.getValue(String.class);
//                        if(value!=null){
//                            Toast.makeText(getContext(),"이미 존재하는 email입니다.",Toast.LENGTH_SHORT).show();//토스메세지 출력
//                            etId.setText("");
//                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


            }
        });


        //거주지역선택
        btn_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getContext(), MainActivityWebView.class);
                startActivityForResult(i, MAIN_ACTIVITY_WEBVIEW);//requestcode 2000 전송


            }
        });






        //날짜 선택

        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();

                meetDate=myCalendar.getTime();
            }

            private void updateLabel() {
                String myFormat = "yyyy/MM/dd";    // 출력형식   2018/11/28
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA); //string형태로 바뀐다.

                et_Birth.setText(sdf.format(myCalendar.getTime()));
                DateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");//필요x

                //데이터 firebase저장위해 meetDate 변수에 대입
                try {
                    //meetDate=myCalendar.getTime(); //캘린더타입
                    //meetDate.=myCalendar.get(Calendar.DAY_OF_MONTH);

                    //meetDate.setYear(myCalendar.get(Calendar.YEAR));//년
                    //meetDate.setMonth(myCalendar.get(Calendar.MONTH));//월
                    //meetDate.setDate(myCalendar.get(Calendar.DAY_OF_MONTH));//일

                    //meetDate=myCalendar.get(Calendar.YEAR);
                    //meetDate=myCalendar.get(Calendar.DAY_OF_WEEK);

                    Log.d("meetDate", String.valueOf(meetDate));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        et_Birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        //취미선택
        btn_hobbys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_hobbys.setText("");//etHobby 초기화
                Intent i = new Intent(getContext(), FragmentPlusSelectHobby.class);
                startActivityForResult(i, SELECT_HOBBY);//

//                FragmentSelectHobby2 fragmentSelectHobby2= new FragmentSelectHobby2();
//                ((MainActivity2)getActivity()).addFragment(fragmentSelectHobby2);

            }
        });


        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri file = imageUri;

                if (etId.getText().toString() == null || etName.getText().toString() == null || etPw.getText().toString() == null
                        || etNick.getText().toString() == null || etAge.getText().toString() == null
                        || btn_live.getText().toString() == null|| meetDate == null || list== null){
                    Toast.makeText(getContext(),"정보 입력을 완료하세요 :)",Toast.LENGTH_SHORT).show();


                }else{
                    try {
                        FirebaseAuth.getInstance()
                                .createUserWithEmailAndPassword(etId.getText().toString(),etPw.getText().toString())
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){

                                            uid = task.getResult().getUser().getUid();

                                            contentUpload();

                                        }else{
                                            Log.e(TAG, "Error getting sign in methods for user", task.getException());
                                        }
                                    }
                                });

                        return;
                    }catch (Exception e){
                        Toast.makeText(getContext(),"모든 정보를 입력하세요.",Toast.LENGTH_SHORT);
                    }

                }

            }//onclidk
        });


        imageView = vGroup.findViewById(R.id.imagejoin);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GET_GALLERY_IMAGE);


            }
        });

        return vGroup;
    }

    //갤러리로 가는 법
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE) {
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(data.getData());
                imageUri = data.getData();
                Log.d("갤러리에서 불러온 이미지 경로", String.valueOf(imageUri));

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getContext(), "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
        switch(requestCode){

            case MAIN_ACTIVITY_WEBVIEW:

                if(resultCode == RESULT_OK){

                    String address = data.getExtras().getString("address");
                    if (data != null){
                        btn_live.setText(address);
                        //address =data;


                    }


                }
                break;

        }
        switch(requestCode){

            case SELECT_HOBBY:

                if(resultCode == RESULT_OK){

                    list = data.getExtras().getStringArrayList("hobby");
                    if (data != null){
                        //list = bundle.getStringArrayList("hobby");
                        //Log.d("getBundleInPlus", String.valueOf(bundle.getStringArrayList("hobby")));
                        //받은 취미 목록을 차례로 tv에 입력
                        int totalHobbyCount = list.size();
                        for (int index =0; index<totalHobbyCount; index++){
                            btn_hobbys.append(","+list.get(index));
                        }

                        //address =data;


                    }


                }
                break;

        }
    } //갤러리에서 사진 불러와서 넣기

    //업로드
    public void contentUpload(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");
        sdf.format(timestamp);

        String imageFileName = "IMAGE_"+ sdf.format(timestamp) + "_.png";

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("userImages").child(imageFileName);

        storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //String imageUrl2 = task.getResult().getUploadSessionUri().toString();
                        Member member = new Member();
                        member.uid =FirebaseAuth.getInstance().getCurrentUser().getUid();
                        member.id = etId.getText().toString().trim();
                        member.pw = etPw.getText().toString().trim();
                        member.mName = etName.getText().toString().trim();
                        member.nick =etNick.getText().toString().trim();
//                                                    member.profileImageUrl= file.toString();
                        member.profileImageUrl= uri.toString();
                        try {
                            if (Integer.parseInt(etAge.getText().toString())!=0){
                                member.mAge = Integer.parseInt(etAge.getText().toString());
                            }

                        }catch (Exception e){

                        }
                        member.mPlace = btn_live.getText().toString();
                        member.mBirth=meetDate;

                        //성별체크
                        if (cb_male.isChecked()){
                            member.mGen =1; //남자는 1
                        }else if (cb_female.isChecked()){
                            member.mGen =2; //여자는 2
                        }else if (cb_no.isChecked()){
                            member.mGen =0; //무관은 0
                        }else {
                            Toast.makeText(getContext(),"성별을 체크하세요.",Toast.LENGTH_SHORT);
                        }


                        //
                        int totalHobbyCount2 = list.size();
                        for (int index = 0; index < totalHobbyCount2; index++) {
                            member.hobbyCate.add(list.get(index));
                        }


                        //빈곳이 없는지 체크
                        if(member.id.equals("")||member.pw.equals("")||member.mName.equals("")||member.nick.equals("")||member.mAge==0||member.mPlace.equals("")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            dialog = builder.setMessage("Empty text exist")
                                    .setNegativeButton("OK", null)
                                    .create();
                            dialog.show();
                            return;
                        }


                        //member.meetDate
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(member).addOnSuccessListener(new OnSuccessListener<Void>() {

                            @Override
                            public void onSuccess(Void aVoid) {
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction().remove(JoinStartFragment.this).commit();
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);

                            }
                        });
                    }
                });
                Toast.makeText(getContext(),"image upload success", Toast.LENGTH_SHORT).show();
            }
        });

    }


    //이부분 데이터 TaskJOin 수정
    public class TaskJoin extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        String serverIp = "http://13.125.233.177:8080/AppWithMe/member/insert.jsp";

        @Override
        protected String doInBackground(String... strings) {//뒤에서 돌아가는 가장 중요한 메소드
            try {
                String str;
                URL url = new URL(serverIp);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");//POST 방식, GET방식으로 넘겨주면 한글처리부터 문제가 생기게 됨
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                //sendMsg = "memberId="+ strings[0]+"&password="+ strings[1];

                //sendMsg = "meetId="+ strings[0]+"&meetName="+ strings[1];
                sendMsg = "memberId="+ strings[0]+"&password="+ strings[1]+"&memberName="+ strings[2]+"&phoneNum="+ strings[3]
                        +"&birth="+ strings[4];
                osw.write(sendMsg);
                osw.flush();
                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);//UTF-8로 데이터 읽어옴
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                } else {
                    Log.i("통신 결과", conn.getResponseCode()+"에러");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;//String 타입인데 안에 형식이 json임
        }

    }
    //2021-09-19 주소검색
//    public void onActivityResult(int requestCode, int resultCode, Intent intent){
//
//        super.onActivityResult(requestCode, resultCode, intent);
//
//        switch(requestCode){
//
//            case SEARCH_ADDRESS_ACTIVITY:
//
//                if(resultCode == RESULT_OK){
//
//                    String data = intent.getExtras().getString("data");
//                    if (data != null)
//                        et_address.setText(data);
//
//                }
//                break;
//
//        }
//
//    }






}