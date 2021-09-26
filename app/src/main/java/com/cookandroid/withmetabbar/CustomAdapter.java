package com.cookandroid.withmetabbar;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cookandroid.withmetabbar.chat.GroupMessageActivity;
import com.cookandroid.withmetabbar.model.ChatModel;
import com.cookandroid.withmetabbar.model.Meet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private final ArrayList<com.cookandroid.withmetabbar.model.Meet> arrayList;
    private final Context context;

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
            //남(1) 이면,
            holder.tv_meetGen.setText("성별: 남");

        }else if (arrayList.get(position).getMeetGen()==2){
            //여(2) 이면,
            holder.tv_meetGen.setText("성별: 여");
        }else{
            //무관(0) 이면,
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
                Map<String,Object> map = new HashMap<>();
                map.put(myUid,true);

                Intent intent = new Intent (view.getContext(), GroupMessageActivity.class);
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(arrayList.get(position).mid)
                        .child("users").updateChildren(map);
                //destinationUsers = map.get();
                intent.putExtra("destinationRoom",arrayList.get(position).mid);

                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(),R.anim.fromright,R.anim.toleft);
                view.getContext().startActivity(intent,activityOptions.toBundle());


                //다이아로그 띄우고
                //채팅방으로 입장
//                Intent intent = new Intent(view.getContext(), MessageActivity.class);
//                intent.putExtra("destinationUid",arrayList.get(position).uid);
//                intent.putExtra("meetTitle",arrayList.get(position).title);//모임명
//                intent.putExtra("meetAge",arrayList.get(position).meetAge);//나이
//                intent.putExtra("meetNumMember",arrayList.get(position).numMember);//인원
//                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(),R.anim.fromright,R.anim.toleft);
//                view.getContext().startActivity(intent,activityOptions.toBundle());

//                Intent intent = null;
//                if(chatModels.get(position).users.size() > 2){
//                    intent = new Intent(view.getContext(), GroupMessageActivity.class);
//                    intent.putExtra("destinationRoom",keys.get(position));
//                }else {
//                    intent = new Intent(view.getContext(), MessageActivity.class);
//                    intent.putExtra("destinationUid", destinationUsers.get(position));
//                }
//                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright, R.anim.toleft);
//                view.getContext().startActivity(intent, activityOptions.toBundle());
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
