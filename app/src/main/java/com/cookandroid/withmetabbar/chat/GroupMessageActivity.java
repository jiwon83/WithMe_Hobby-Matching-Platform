package com.cookandroid.withmetabbar.chat;


import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cookandroid.withmetabbar.R;
import com.cookandroid.withmetabbar.model.ChatModel;
import com.cookandroid.withmetabbar.model.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class GroupMessageActivity extends AppCompatActivity {
    //그룹채팅
    Map<String, Member> users = new HashMap<>();
    String destinationRoom;
    String uid;
    private final List<String> chatmacro = new ArrayList<>();
    private ListView listView;
    private RecyclerView recyclerView;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.HH:mm");

    List<ChatModel.Comment> comments = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);
        ListView listView = findViewById(R.id.groupmessageactivity_listview);
        chatmacro.addAll(Arrays.asList(getResources().getStringArray(R.array.macro)));
        destinationRoom = getIntent().getStringExtra("destinationRoom");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //editText = (EditText)findViewById(R.id.groupmessageactivity_edittext);
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item: snapshot.getChildren()){
                    users.put(item.getKey(),item.getValue(Member.class));
                }
                init();
                recyclerView = findViewById(R.id.groupmessageactivity_recyclerview);
                recyclerView.setAdapter(new GroupMessageRecyclerviewAdapter());
                recyclerView.setLayoutManager(new LinearLayoutManager(GroupMessageActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void init(){
        ListView listView = findViewById(R.id.groupmessageactivity_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                comments.clear();
                ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = chatmacro.get(position);
                comment.timestamp = ServerValue.TIMESTAMP;
                FirebaseDatabase.getInstance().getReference().child("chatrooms")
                        .child(destinationRoom).child("comments").push()
                        .setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //editText.setText("");
                    }
                });
            }

        });

    }
    class GroupMessageRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public GroupMessageRecyclerviewAdapter(){
            getMessageList();
        }
        void getMessageList(){

            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    comments.clear();

                    for (DataSnapshot item : snapshot.getChildren()){
                        comments.add(item.getValue(ChatModel.Comment.class));
                    }
                    notifyDataSetChanged(); // 리스트 갱신
                    recyclerView.scrollToPosition(comments.size() - 1); // 하단으로 내려감 -> 코멘트 사이즈의 -1
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);

            setListViews();
            return new GroupMessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            GroupMessageViewHolder messageViewHolder = ((GroupMessageViewHolder)holder);

            // 내가 보낸 메세지
            if(comments.get(position).uid.equals(uid)){ // 앞은 코멘트의 uid 뒤는 로그인한 나의 uid
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                // 상대방이 보낸 메세지
            }else{
                Glide.with(holder.itemView.getContext())
                        .load(users.get(comments.get(position).uid).profileImageUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile);
                messageViewHolder.textView_name.setText(users.get(comments.get(position).uid).mName);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);

            }
            // 이거 안쓰면 밀리언즈 시간? 이상한 숫자 나옴 한국 시간으로 초기화 시키기
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.textView_timestamp.setText(time);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class GroupMessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;
            public GroupMessageViewHolder(View view) {
                super(view);

                textView_message = view.findViewById(R.id.messageItem_textView_message);
                textView_name = view.findViewById(R.id.messageItem_textView_name);
                imageView_profile = view.findViewById(R.id.messageItem_imageView_profile);
                linearLayout_destination = view.findViewById(R.id.messageItem_linearlayout_destination);
                linearLayout_main = view.findViewById(R.id.messageItem_linearlayout_main);
                textView_timestamp = view.findViewById(R.id.messageItem_textView_timestamp);
            }
        }
        private void setListViews() {
            ListView listView = findViewById(R.id.groupmessageactivity_listview);
            MacroAdapter macroAdapter = new MacroAdapter(getBaseContext(), chatmacro);
            listView.setAdapter(macroAdapter);
            listView.setItemChecked(0, true);
            listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        }
    }


}

