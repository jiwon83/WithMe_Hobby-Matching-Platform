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
import com.cookandroid.withmetabbar.model.Meet;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<com.cookandroid.withmetabbar.model.Meet> arrayList;
    private Context context;

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
        holder.tv_meetTitle.setText("모임명: "+String.valueOf(arrayList.get(position).getTitle()));
        holder.tv_meetAge.setText("나이: "+String.valueOf(arrayList.get(position).getMeetAge()));
        holder.tv_meetId.setText("모임아이디: "+String.valueOf(arrayList.get(position).getMeetId()));
        holder.tv_numMember.setText("인원: "+String.valueOf(arrayList.get(position).getNumMember()));
        holder.tv_content.setText("내용: "+String.valueOf(arrayList.get(position).getContent()));

        //imageView클릭하면 MessageActivity 생성, Firebase dataBase 채팅방 데이터 생성

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GroupMessageActivity.MessageActivity.class);
                intent.putExtra("destinationUid",arrayList.get(position).uid);
                intent.putExtra("meetTitle",arrayList.get(position).title);//모임명
                intent.putExtra("meetAge",arrayList.get(position).meetAge);//나이
                intent.putExtra("meetNumMember",arrayList.get(position).numMember);//인원
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(),R.anim.fromright,R.anim.toleft);
                view.getContext().startActivity(intent,activityOptions.toBundle());
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
        TextView tv_meetTitle,tv_meetAge, tv_meetId, tv_numMember, tv_content;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_meet=itemView.findViewById(R.id.iv_meet);
            this.tv_meetTitle=itemView.findViewById(R.id.tv_meetTitle);
            this.tv_meetAge=itemView.findViewById(R.id.tv_meetAge);
            this.tv_meetId=itemView.findViewById(R.id.tv_meetId);
            this.tv_numMember=itemView.findViewById(R.id.tv_numMember);
            this.tv_content=itemView.findViewById(R.id.tv_content);
        }
    }
}
