package com.cookandroid.withmetabbar.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cookandroid.withmetabbar.R;
import com.cookandroid.withmetabbar.model.ChatModel;
import com.cookandroid.withmetabbar.model.Meet;
import com.cookandroid.withmetabbar.model.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class GroupMessageActivity extends AppCompatActivity {
    //그룹채팅

    //Map<String, UserModel> users = new HashMap<>();
    Map<String, Member> users = new HashMap<>();
    String destinationRoom;
    String uid;
    private List<String> chatmacro = new ArrayList<>();
    private List<String> chatmacro2 = new ArrayList<>();
    private GridView gridView;
    private Button button;
    private EditText editText;
    private RecyclerView recyclerView;
    private String destinationUid ;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.HH:mm");
    List<ChatModel.Comment> comments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ChatModel chatModel = new ChatModel();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);
//        ListView listView = (ListView)findViewById(R.id.groupmessageactivity_listview);
//        ListView listView2 = (ListView)findViewById(R.id.groupmessageactivity_listview2);
        GridView gridView = (GridView)findViewById(R.id.groupmessageactivity_gridview);
        chatmacro.addAll(Arrays.asList(getResources().getStringArray(R.array.macro)));
        //chatmacro2.addAll(Arrays.asList(getResources().getStringArray(R.array.macro2)));
        destinationRoom = getIntent().getStringExtra("destinationRoom");
        destinationUid = getIntent().getStringExtra("destinationUid");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        editText = (EditText) findViewById(R.id.groupmessageActivity_editText);
        button = (Button) findViewById(R.id.groupmessageActivity_button);
        //채팅방안에있는 user  받아오고
        //그 유저의 데이터 검색


        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item: snapshot.getChildren()){
                    users.put(item.getKey(),item.getValue(Member.class));
                    Log.d("users", String.valueOf(users));
                    Log.d("item.getKey()",item.getKey());


                }
                init1();
                init2();
                recyclerView = (RecyclerView)findViewById(R.id.groupmessageactivity_recyclerview);
                recyclerView.setAdapter(new GroupMessageRecyclerviewAdapter());
                recyclerView.setLayoutManager(new LinearLayoutManager(GroupMessageActivity.this));



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    //메세지 입력

    void init1(){
        GridView gridView =(GridView)findViewById(R.id.groupmessageactivity_gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                comments.clear();
                ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = chatmacro.get(i).toString();
                comment.timestamp = ServerValue.TIMESTAMP; // 파이어베이스에서 제공하는 서버시간
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        editText.setText("");
                    }
                });
            }
        });
    }

    void init2() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(uid, true);
                chatModel.users.put(destinationUid, true);

                ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = editText.getText().toString();
                comment.timestamp = ServerValue.TIMESTAMP; // 파이어베이스에서 제공하는 서버시간
                if (!(comment.message.length() == 0)){ //채팅 메세지가 없을 때 공백값의 전송을 방
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //sendGcm();
                            editText.setText("");
                        }
                    });

                }

            }
        });

    }

    class GroupMessageRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public GroupMessageRecyclerviewAdapter() {
            setGridView();
            getMessageList();
        }

        //메세지 데이터 받아오기
        void getMessageList() {
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    comments.clear();

                    for (DataSnapshot item : snapshot.getChildren()) {
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

            setGridView();
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
                messageViewHolder.textView_timestamp.setGravity(Gravity.RIGHT);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);

            }else{ // 상대방이 보낸 메세지


//                Glide.with(holder.itemView.getContext())
//                        .load(users.get(comments.get(position).uid).profileImageUrl)
//                        .apply(new RequestOptions().circleCrop())
//                        .into(messageViewHolder.imageView_profile);
                messageViewHolder.textView_name.setText(users.get(comments.get(position).uid).mName);
                Log.d("usersRecyclerView", String.valueOf( comments.get(position).uid ));
                Log.d("usersRecyclerView", String.valueOf( users.get(comments.get(position).uid) ));

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

                textView_message = (TextView)view.findViewById(R.id.messageItem_textView_message);
                textView_name = (TextView)view.findViewById(R.id.messageItem_textView_name);
                imageView_profile = (ImageView)view.findViewById(R.id.messageItem_imageView_profile);
                linearLayout_destination = (LinearLayout)view.findViewById(R.id.messageItem_linearlayout_destination);
                linearLayout_main = (LinearLayout)view.findViewById(R.id.messageItem_linearlayout_main);
                textView_timestamp = (TextView)view.findViewById(R.id.messageItem_textView_timestamp);
            }
        }
        private void setGridView(){
            GridView gridView = (GridView)findViewById(R.id.groupmessageactivity_gridview);
            GridAdapter gridAdapter = new GridAdapter(getBaseContext(),chatmacro);
            gridView.setAdapter(gridAdapter);
            gridView.setItemChecked(0,true);
            gridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }
}