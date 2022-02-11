package com.appwithme.navigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.appwithme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.appwithme.MainActivity;
import com.appwithme.certify.MainActivity2;
import com.appwithme.model.Member;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class MyPageFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String uid="";
    private final int GET_GALLERY_IMAGE = 200;//?무슨의미
    private ImageView imageView, imageView7;
    private Uri imageUri;//모임이미지


    //private FragmentPagerAdapter fragmentPagerAdapter;//코인충전어뎁터
    Button btn_charge, btn_ondo, btn_mygroup;
    ImageButton btn_block, btn_friends_invite,btn_logout, btn_withdrawal;
    LinearLayout myMeetLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup vGroup = (ViewGroup) inflater.inflate(R.layout.mypage_fragment, container, false);
        TextView tvName= vGroup.findViewById(R.id.tv_mName);
        TextView tvNick= vGroup.findViewById(R.id.tv_nickname);

        TextView tvAge= vGroup.findViewById(R.id.tv_age);
        imageView = vGroup.findViewById(R.id.imagemy);
        myMeetLayout = vGroup.findViewById(R.id.myMeetLayout);
        btn_logout=vGroup.findViewById(R.id.btn_logout);
        btn_withdrawal=vGroup.findViewById(R.id.btn_withdrawal);


        Intent intent = new Intent();



        //data 불러오기 내 uid에 해당하는 데이터 불러오기
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        database= FirebaseDatabase.getInstance();
        databaseReference=database.getReference("users").child(uid);//DB Table Connect
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //실제적으로 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                Member member = dataSnapshot.getValue(Member.class);
                //Log.d("memberInput", String.valueOf(member.mName));
                tvName.setText(member.mName);
                tvNick.setText(member.nick);
                tvAge.setText(""+member.mAge);

                //성별
//                if (member.mGen==1){
//                    tvGen.setText("남");
//
//                }else if (member.mAge==2){
//                    tvGen.setText("여");
//
//                }else{
//                }
                member.getProfileImageUrl();
                Log.d("getProfileImageUrl",member.getProfileImageUrl());
//                imageView.setImageURI(Uri.parse(member.getProfileImageUrl()));
                Glide.with(getContext())
                        .load(member.getProfileImageUrl())
                        .into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //디비를 가져오는 도중 에러 발생 시
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", error.toException());

            }
        });

        //내모임보기
        myMeetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMyMeetHome fragmentMyMeetHome = new FragmentMyMeetHome(); //내 모임 보기 화면
                ((MainActivity)getActivity()).replaceFragment(fragmentMyMeetHome);
            }
        });

        //코인충전버튼
        btn_charge= vGroup.findViewById(R.id.charge);
        btn_charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoinMainFragment coinMainFragment= new CoinMainFragment();
                ((MainActivity)getActivity()).replaceFragment(coinMainFragment);
            }
        });

//        btn_ondo= vGroup.findViewById(R.id.btn_ondo);
//        btn_ondo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OndoFragment ondoFragment= new OndoFragment();
//
//                ((MainActivity)getActivity()).replaceFragment(ondoFragment);
//            }
//        });

        btn_block= vGroup.findViewById(R.id.btn_block);
        btn_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendsBlockFragment friendsBlockFragment= new FriendsBlockFragment();
                ((MainActivity)getActivity()).replaceFragment(friendsBlockFragment);
            }
        });

        btn_friends_invite= vGroup.findViewById(R.id.btn_friends_invite);
        btn_friends_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendsInviteFragment friendsInviteFragment = new FriendsInviteFragment();
                ((MainActivity)getActivity()).replaceFragment(friendsInviteFragment);
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GET_GALLERY_IMAGE);


            }
        });

        //내가 만든 모임 화면으로 전환
        //ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.mypage_fragment, container, false);

        //logout로그아웃
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), MainActivity2.class);
                startActivity(intent);
            }
        });
        //withdraw회원탈퇴
        btn_withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User account deleted.");
                                    Intent intent = new Intent(getContext(), MainActivity2.class);
                                    startActivity(intent);
                                }
                            }
                        });

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
//                int takeFlas = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                ContentResolver resolver = getContext().getContentResolver();
//                if (data.getData()==null){
//                    imageView.setImageURI(data.getData());
//                    imageUri = data.getData();
//                    return;
//                }
//                resolver.takePersistableUriPermission(data.getData(),takeFlas);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    try {
                        getContext().grantUriPermission(
                                getContext().getPackageName(),
                                data.getData(),
                                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        );
                        //imageView.setImageURI(data.getData());
                    } catch (IllegalArgumentException e) {
                        getContext().grantUriPermission(
                                getContext().getPackageName(),
                                data.getData(),
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );  // kikat api only 0x3 are
                        // allowed FLAG_GRANT_READ_URI_PERMISSION = 1 | FLAG_GRANT_WRITE_URI_PERMISSION = 2;
                        //imageView.setImageURI(data.getData());
                    } catch (SecurityException e) {
                        // ignore
                    }

                    int takeFlags = data.getFlags();
                    takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    try {
                        getContext().getContentResolver().takePersistableUriPermission(data.getData(), takeFlags);
                        imageView.setImageURI(data.getData());
                    } catch (SecurityException e) {
                        // ignore
                    }
                }



                imageView.setImageURI(data.getData());
                imageUri = data.getData();
                //Log.d("갤러리에서 불러온 이미지 경로", String.valueOf(imageUri));


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getContext(), "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    } //갤러리에서 사진 불러와서 넣기



    protected void onMainActivity(int requestCode, int resultCode, Intent data) {
        if(requestCode == GET_GALLERY_IMAGE&&resultCode == RESULT_OK&&data !=
                null && data.getData()!=null){
            Uri selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }

}
