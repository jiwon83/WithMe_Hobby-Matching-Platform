package com.cookandroid.withmetabbar;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cookandroid.withmetabbar.certify.JoinStartFragment;
import com.cookandroid.withmetabbar.certify.MainActivity2;
import com.cookandroid.withmetabbar.chat.GroupMessageActivity;
import com.cookandroid.withmetabbar.model.ChatModel;
import com.cookandroid.withmetabbar.model.Meet;
import com.cookandroid.withmetabbar.model.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private final ArrayList<com.cookandroid.withmetabbar.model.Meet> arrayList;
    private AlertDialog dialog,dialog2;
    private CustomDialog customDialog;
    private final Context context;
    private int userCount=1;
    private Member memberObj = new Member(); // 채팅방 입장을 위한 맴버 객체 생성
    private Button button_check;//캘린더
    private View v_d;
    private TextView tv1, tv2;
    private EditText et1, et2;




    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();//채팅방 구현

    public CustomAdapter(ArrayList<Meet> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    /**ListView가 어덥터를 만들어 냈을 때 최초로 실행된다.*/
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_meet, parent, false);

        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull  CustomViewHolder holder, int position) {


        Glide.with(holder.itemView)
                .load(arrayList.get(position).getImgUrl())
                .override(150,200)
                .into(holder.iv_meet);
        holder.tv_meetTitle.setText(" "+ arrayList.get(position).getTitle());
        //Log.d("custom_imgUrl",arrayList.get(position).getImgUrl());
        //Log.d("holder.itemView", String.valueOf(holder.itemView));

        String myFormat = "yyyy/ MM/ dd/ HH:mm";    // 출력형식   2018/11/28
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA); //string형태로 바뀐다.
        try{
            holder.tv_meetDate.setText(" "+sdf.format(arrayList.get(position).getMeetDate().getTime()));
        }catch (Exception e){

        }

        //holder.tv_meetDate.setText("시간: "+ arrayList.get(position).getMeetDate());

        holder.tv_meetAge.setText(" "+ arrayList.get(position).getMeetAge());
        //성별
        if (arrayList.get(position).getMeetGen()==1){
            holder.tv_meetGen.setText("남");
            holder.tv_meetGen.setTextColor(Color.parseColor("#255AAC"));

        }else if (arrayList.get(position).getMeetGen()==2){
            holder.tv_meetGen.setText("여");
            holder.tv_meetGen.setTextColor(Color.parseColor("#A5472A"));
        }else{
            holder.tv_meetGen.setText("무관");
            holder.tv_meetGen.setTextColor(Color.parseColor("#555353"));
        }
        //holder.tv_numMember.setText(" "+ arrayList.get(position).getNumMember());
        holder.tv_numMember.setText(""+ arrayList.get(position).getNumMember());
        holder.tv_place.setText(" "+ arrayList.get(position).getPlace());
        holder.tv_hobbyCate.setText(""+arrayList.get(position).getHobbyCate());








        //imageView클릭하면 MessageActivity 생성, Firebase dataBase 채팅방 데이터 생성\
        holder.button_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View OnCickview) {





                AlertDialog.Builder builder = new AlertDialog.Builder(OnCickview.getContext());
                builder.setTitle((" "+ arrayList.get(position).getTitle()));


                //View.inflate 이용하여 그 뷰에 해당하는 것을 '구현/실행'해주고,
                v_d = (View) View.inflate(OnCickview.getContext(), R.layout.cutomdialog, null);
                //실행한 것을 setView 함수로 전달.
                builder.setView(v_d);


                ImageView cu_iv_meet=v_d.findViewById(R.id.iv_meet);
                TextView cu_meetDate = v_d.findViewById(R.id.cu_meetDate);
                TextView cu_meetAge = v_d.findViewById(R.id.cu_meetAge);
                TextView cu_meetGen = v_d.findViewById(R.id.cu_meetGen);
                TextView cu_numMember = v_d.findViewById(R.id.cu_numMember);
                TextView cu_place = v_d.findViewById(R.id.cu_place);
                TextView cu_content = v_d.findViewById(R.id.cu_content);
                Button button_go = v_d.findViewById(R.id.button_go);

                //meet정보출력 onClickview
                Glide.with(holder.itemView)
                        .load(arrayList.get(position).getImgUrl())
                        .override(150,200)
                        .into(cu_iv_meet);

                //Log.d("custom_imgUrl",arrayList.get(position).getImgUrl());
                //Log.d("holder.itemView", String.valueOf(holder.itemView));

                String myFormat = "yyyy/ MM/ dd/ HH:mm";    // 출력형식   2018/11/28
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA); //string형태로 바뀐다.
                try{
                    cu_meetDate.setText(" "+sdf.format(arrayList.get(position).getMeetDate().getTime()));
                }catch (Exception e){

                }

                cu_meetAge.setText(" "+ arrayList.get(position).getMeetAge());
                //성별
                if (arrayList.get(position).getMeetGen()==1){
                    cu_meetGen.setText("남");
                    cu_meetGen.setTextColor(Color.parseColor("#255AAC"));

                }else if (arrayList.get(position).getMeetGen()==2){
                    cu_meetGen.setText("여");
                    cu_meetGen.setTextColor(Color.parseColor("#A5472A"));
                }else{
                    cu_meetGen.setText("무관");
                    cu_meetGen.setTextColor(Color.parseColor("#555353"));
                }
                //holder.tv_numMember.setText(" "+ arrayList.get(position).getNumMember());
                cu_numMember.setText(""+ arrayList.get(position).getNumMember());
                cu_place.setText(" "+ arrayList.get(position).getPlace());
                cu_content.setText(""+arrayList.get(position).getHobbyCate());

                button_go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{

                            //제한 사항이 맞는지 검사.
                            //user의 Gen, Age 가 meet의(또는 chatroom의) Gen, Age 와 동일한지 확인
                            //userData 가져오기
                            List<Member> members = new ArrayList<>();

                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
                                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(arrayList.get(position).mid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {


                                            for (DataSnapshot item : snapshot.getChildren()){
                                                keysChatroomUsers.add(item.getKey());
                                            }
                                            userCount = keysChatroomUsers.size(); //chatroom에 있는 user의 수


                                            boolean agePass = memberObj.mAge>arrayList.get(position).getMeetAge();
                                            boolean genPass = memberObj.mGen>=arrayList.get(position).getMeetGen() || (arrayList.get(position).getMeetGen()==0) ;
                                            boolean numMemberPass =( userCount < arrayList.get(position).getNumMember() );


                                            //조건 비교
                                            try {
                                                if (  agePass && genPass && numMemberPass){


                                                    Map<String,Object> map = new HashMap<>();
                                                    map.put(myUid,true);

                                                    Intent intent = new Intent (v.getContext(), GroupMessageActivity.class);

                                                    //chatrooms의 user에 내 uid 넣는것.
                                                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(arrayList.get(position).mid)
                                                            .child("users").updateChildren(map);

                                                    //chatroom 의 userCount에 현재 userCount 넣기.
                                                    userCount += 1;
                                                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(arrayList.get(position).mid)
                                                            .child("userCount").setValue(userCount);

                                                    intent.putExtra("destinationRoom",arrayList.get(position).mid);// 채팅방을 띄우기 위한 chatroom uid 전송.



                                                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(),R.anim.fromright,R.anim.toleft);
                                                    v.getContext().startActivity(intent,activityOptions.toBundle());

                                                }else {

                                                    //조건 불만족시 처리
                                                    if (agePass==false){
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                                        dialog = builder.setMessage("모임 연령을 확인하세요. 모임연령: "+arrayList.get(position).getMeetAge())
                                                                .setNegativeButton("OK", null)
                                                                .create();
                                                        dialog.show();
                                                    }else if (genPass ==false ){
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                                        dialog = builder.setMessage("모임 성별을 확인하세요.")
                                                                .setNegativeButton("OK", null)
                                                                .create();
                                                        dialog.show();
                                                    }else {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                                        dialog = builder.setMessage("인원이 마감되었습니다.")
                                                                .setNegativeButton("OK", null)
                                                                .create();
                                                        dialog.show();
                                                    }
//
                                                }
                                            }catch (Exception e){
                                                Toast.makeText(v.getContext(), e.getMessage(),  Toast.LENGTH_SHORT);
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
                });


//                builder.setButton("입력",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                                //dialog라는 레이아웃이 실제로 생성(inflate)되고 난 다음에, v_d라는 애를 통해서,
//                                //그 안에 속한 것 중에서 찾아오는 것이 가능하다.
//                                //setPositiveButton이 눌리게 되었을 때, dialog에 해당하는 우리가 만든 View (=v_d)로부터 et를 찾아옴
//                                et1 = (EditText) v_d.findViewById(R.id.et_mail);
//                                et2 = (EditText) v_d.findViewById(R.id.et_name);
//
//                                //사용자에게 입력받은 et1, et2를 tv1, tv2에 표시
//                                tv1.setText(et1.getText());
//                                tv2.setText(et2.getText());
//
//                            }
//                        });

                builder.show();



            }
        });



//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {



//                try{
//
//                    //제한 사항이 맞는지 검사.
//                    //user의 Gen, Age 가 meet의(또는 chatroom의) Gen, Age 와 동일한지 확인
//                    //userData 가져오기
//                    List<Member> members = new ArrayList<>();
//
//                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                            //member 객체에 user에서 받아온 값 넣기
//                            Member member = snapshot.getValue(Member.class);
//                            memberObj.mGen =member.mGen;
//                            memberObj.mAge =member.mAge;
//
//
//                            List<String> keysChatroomUsers = new ArrayList<>();//chatrooms - uid -users 의 uid 값들을 답을 리스트
//                            //구조변경
//                            //채팅방에서 userCount 받아오기 chatrooms - chatroom uid(=meet uid)
//                            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(arrayList.get(position).mid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//
//
//                                    for (DataSnapshot item : snapshot.getChildren()){
//                                        keysChatroomUsers.add(item.getKey());
//                                    }
//                                    userCount = keysChatroomUsers.size(); //chatroom에 있는 user의 수
//
//
//                                    boolean agePass = memberObj.mAge>arrayList.get(position).getMeetAge();
//                                    boolean genPass = memberObj.mGen>=arrayList.get(position).getMeetGen() || (arrayList.get(position).getMeetGen()==0) ;
//                                    boolean numMemberPass =( userCount < arrayList.get(position).getNumMember() );
//
//
//                                    //조건 비교
//                                    try {
//                                        if (  agePass && genPass && numMemberPass){
//
//
//                                            Map<String,Object> map = new HashMap<>();
//                                            map.put(myUid,true);
//
//                                            Intent intent = new Intent (view.getContext(), GroupMessageActivity.class);
//
//                                            //chatrooms의 user에 내 uid 넣는것.
//                                            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(arrayList.get(position).mid)
//                                                    .child("users").updateChildren(map);
//
//                                            //chatroom 의 userCount에 현재 userCount 넣기.
//                                            userCount += 1;
//                                            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(arrayList.get(position).mid)
//                                                    .child("userCount").setValue(userCount);
//
//                                            intent.putExtra("destinationRoom",arrayList.get(position).mid);// 채팅방을 띄우기 위한 chatroom uid 전송.
//
//
//
//                                            ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(),R.anim.fromright,R.anim.toleft);
//                                            view.getContext().startActivity(intent,activityOptions.toBundle());
//
//                                        }else {
//
//                                            //조건 불만족시 처리
//                                            if (agePass==false){
//                                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
//                                                dialog = builder.setMessage("모임 연령을 확인하세요. 모임연령: "+arrayList.get(position).getMeetAge())
//                                                        .setNegativeButton("OK", null)
//                                                        .create();
//                                                dialog.show();
//                                            }else if (genPass ==false ){
//                                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
//                                                dialog = builder.setMessage("모임 성별을 확인하세요.")
//                                                        .setNegativeButton("OK", null)
//                                                        .create();
//                                                dialog.show();
//                                            }else {
//                                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
//                                                dialog = builder.setMessage("인원이 마감되었습니다.")
//                                                        .setNegativeButton("OK", null)
//                                                        .create();
//                                                dialog.show();
//                                            }
////
//                                        }
//                                    }catch (Exception e){
//                                        Toast.makeText(view.getContext(), e.getMessage(),  Toast.LENGTH_SHORT);
//                                    }
//
//
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//
//
//
//
//                }catch (Exception e){
//
//                }


//            }
//        });









        }







    @Override
    public int getItemCount() {
        return (arrayList !=null ? arrayList.size() : 0);
    }
    /**MeetViewHolder*/
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_meet;
        TextView tv_meetTitle,tv_meetDate, tv_meetAge, tv_numMember, tv_meetGen, tv_place, tv_hobbyCate;
        ImageView img_meetDate, img_place;
        Button button_check;






        public CustomViewHolder(@NonNull View itemView) {




            super(itemView);
            this.iv_meet=itemView.findViewById(R.id.iv_meet);
            this.tv_meetTitle=itemView.findViewById(R.id.tv_meetTitle);
            this.tv_meetDate=itemView.findViewById(R.id.tv_meetDate);
            this.tv_meetAge=itemView.findViewById(R.id.tv_meetAge);
            this.tv_numMember=itemView.findViewById(R.id.tv_numMember);
            this.tv_meetGen=itemView.findViewById(R.id.tv_meetGen);
            this.tv_place=itemView.findViewById(R.id.tv_place);
            this.tv_hobbyCate=itemView.findViewById(R.id.tv_hobbyCate);
            this.img_meetDate=itemView.findViewById(R.id.img_meetDate);
            this.img_place=itemView.findViewById(R.id.img_place);
            this.button_check=itemView.findViewById(R.id.button_check);



            tv_meetTitle.setTextSize(Dimension.SP, 17);
            tv_meetAge.setTextSize(Dimension.SP, 16);
            tv_numMember.setTextSize(Dimension.SP, 16);
            tv_meetGen.setTextSize(Dimension.SP, 16);
            tv_place.setEllipsize(TextUtils.TruncateAt.END);
            tv_place.setMaxLines(10);
            tv_place.setSelected(true);
            tv_place.setSingleLine(true);
            tv_meetDate.setTextColor(Color.parseColor("#3E3B3B"));
            tv_meetAge.setTextColor(Color.parseColor("#3E3B3B"));
            tv_numMember.setTextColor(Color.parseColor("#3E3B3B"));
            tv_place.setTextColor(Color.parseColor("#3E3B3B"));
            img_meetDate.setColorFilter(Color.parseColor("#F49C19"));
            img_place.setColorFilter(Color.parseColor("#F49C19"));









        }




}





}
