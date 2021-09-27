package com.cookandroid.withmetabbar;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
    private AlertDialog dialog;
    private final Context context;
    private int userCount=1;
    private Member memberObj = new Member(); //맴버객체 생성

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
                .into(holder.iv_meet);
        holder.tv_meetTitle.setText("모임명: "+ arrayList.get(position).getTitle());

        String myFormat = "yyyy년 MM월 dd일 HH:mm";    // 출력형식   2018/11/28
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA); //string형태로 바뀐다.
        try{
            holder.tv_meetDate.setText("날짜: "+sdf.format(arrayList.get(position).getMeetDate().getTime()));
        }catch (Exception e){

        }

        //holder.tv_meetDate.setText("시간: "+ arrayList.get(position).getMeetDate());

        holder.tv_meetAge.setText("나이: "+ arrayList.get(position).getMeetAge());
        //성별
        if (arrayList.get(position).getMeetGen()==1){
            holder.tv_meetGen.setText("성별: 남");

        }else if (arrayList.get(position).getMeetGen()==2){
            holder.tv_meetGen.setText("성별: 여");
        }else{
            holder.tv_meetGen.setText("성별: 무관");
        }
        holder.tv_meetId.setText("모임아이디: "+ arrayList.get(position).getMeetId());
        holder.tv_numMember.setText("인원: "+ arrayList.get(position).getNumMember());
        holder.tv_place.setText("위치: "+ arrayList.get(position).getPlace());
        holder.tv_content.setText("내용: "+ arrayList.get(position).getContent());

        //imageView클릭하면 MessageActivity 생성, Firebase dataBase 채팅방 데이터 생성

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    Log.d("arrayList.get(position).mid",arrayList.get(position).mid);

                    //제한 사항이 맞는지 검사.
                    //user의 Gen, Age 가 meet의(또는 chatroom의) Gen, Age 와 동일한지 확인
                    //userData 가져오기
                    List<Member> members = new ArrayList<>();

                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//.orderByChild("users/"+uid).equalTo(true)
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

                                            Intent intent = new Intent (view.getContext(), GroupMessageActivity.class);

                                            //chatrooms의 user에 내 uid 넣는것.
                                            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(arrayList.get(position).mid)
                                                    .child("users").updateChildren(map);

                                            //chatroom 의 userCount에 현재 userCount 넣기.
                                            userCount += 1;
                                            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(arrayList.get(position).mid)
                                                    .child("userCount").setValue(userCount);

                                            intent.putExtra("destinationRoom",arrayList.get(position).mid);// 채팅방을 띄우기 위한 chatroom uid 전송.

                                            ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(),R.anim.fromright,R.anim.toleft);
                                            view.getContext().startActivity(intent,activityOptions.toBundle());

                                        }else {

                                            //조건 불만족시 처리
                                            if (agePass==false){
                                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                                dialog = builder.setMessage("모임 연령을 확인하세요. 모임연령: "+arrayList.get(position).getMeetAge())
                                                        .setNegativeButton("OK", null)
                                                        .create();
                                                dialog.show();
                                            }else if (genPass ==false ){
                                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                                dialog = builder.setMessage("모임 성별을 확인하세요.")
                                                        .setNegativeButton("OK", null)
                                                        .create();
                                                dialog.show();
                                            }else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                                dialog = builder.setMessage("인원이 마감되었습니다.")
                                                        .setNegativeButton("OK", null)
                                                        .create();
                                                dialog.show();
                                            }
//
                                        }
                                    }catch (Exception e){
                                        Toast.makeText(view.getContext(), e.getMessage(),  Toast.LENGTH_SHORT);
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

    }

    @Override
    public int getItemCount() {
        return (arrayList !=null ? arrayList.size() : 0);
    }
    /**MeetViewHolder*/
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_meet;
        TextView tv_meetTitle,tv_meetDate, tv_meetAge, tv_meetId, tv_numMember, tv_content, tv_meetGen, tv_place;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_meet=itemView.findViewById(R.id.iv_meet);
            this.tv_meetTitle=itemView.findViewById(R.id.tv_meetTitle);
            this.tv_meetDate=itemView.findViewById(R.id.tv_meetDate);
            this.tv_meetAge=itemView.findViewById(R.id.tv_meetAge);
            this.tv_meetId=itemView.findViewById(R.id.tv_meetId);
            this.tv_numMember=itemView.findViewById(R.id.tv_numMember);
            this.tv_content=itemView.findViewById(R.id.tv_content);
            this.tv_meetGen=itemView.findViewById(R.id.tv_meetGen);
            this.tv_place=itemView.findViewById(R.id.tv_place);

        }
    }
}
