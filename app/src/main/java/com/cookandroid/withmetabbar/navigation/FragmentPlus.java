package com.cookandroid.withmetabbar.navigation;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cookandroid.withmetabbar.MainActivity;
import com.cookandroid.withmetabbar.MainActivityHome;
import android.Manifest;
import com.cookandroid.withmetabbar.R;
import com.cookandroid.withmetabbar.certify.LivePlaceFragment;
import com.cookandroid.withmetabbar.certify.MainActivity2;
import com.cookandroid.withmetabbar.model.Meet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;


@SuppressWarnings("deprecation")
public class FragmentPlus extends Fragment {

    private DatabaseReference mDatabase;
    private final int GET_GALLERY_IMAGE = 200;//?무슨의미
    private ImageView imageView, imageView7;
    private ImageButton imageButton;
    private ToggleButton toggleButton, toggleButton1,toggleButton2,toggleButton3,toggleButton4;
    private Button btnMeet;
    private MediaPlayer MP;
    private Uri imageUri;//모임이미지
    private String uid="";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup vGroup = (ViewGroup) inflater.inflate(R.layout.fragment_plus, container, false);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.ic_launcher);
        //setTitle("레이아웃 선택");


        btnMeet= vGroup.findViewById(R.id.btn_meet);//버튼
        EditText etTitle=vGroup.findViewById(R.id.etTitle);
        EditText etAge=vGroup.findViewById(R.id.etMeetAge);
        EditText etNumMem=vGroup.findViewById(R.id.etNumMem);
        EditText etContent=vGroup.findViewById(R.id.etContent);


        Intent intent = new Intent();


        Bundle bundle = getArguments();
        uid=bundle.getString("uid");//null?






        imageView = (ImageView)vGroup.findViewById(R.id.imageView7);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GET_GALLERY_IMAGE);


            }
        });
        //버튼 누르면 데이터에 저장
        btnMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FirebaseStorage storage= FirebaseStorage.getInstance();
                //StorageReference storageRef = storage.getReference();
                //파일명 만들기
                String filename="meet"+uid+".jpeg";
                Uri file = imageUri;//
                Log.d("imageUri", String.valueOf(file));
                Log.d("filename", filename);
                //여기서 원하는 이름 넣어준다.(filename 넣어주기)
                //StorageReference riversRef = storageRef.child("meetImage/"+filename);
                //UploadTask uploadTask = riversRef.putFile(file);//storage에 이미지 업로드


                mDatabase = FirebaseDatabase.getInstance().getReference().child("meet").child(uid);
                //meet라는 이름으로 uid밑에 저장

                //이미지 strage에 저장
                FirebaseStorage.getInstance().getReference().child("meetImages/"+imageUri.getLastPathSegment()).child(uid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        Meet meet = new Meet();
                        meet.uid =uid; //uid정보 추가
                        meet.title = etTitle.getText().toString();
                        meet.meetAge = Integer.parseInt(etAge.getText().toString());
                        meet.numMember = Integer.parseInt(etNumMem.getText().toString());
                        meet.content = etContent.getText().toString();
                        meet.imgUrl = file.toString();

                        //member.mAge = Integer.parseInt(etAge.getText().toString());

                        //meet라는 데이터 넣기
                        FirebaseDatabase.getInstance().getReference().child("meet").push().setValue(meet).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                MainActivityHome mainActivityHome= new MainActivityHome();
                                ((MainActivity)getActivity()).replaceFragment(mainActivityHome);

                                //이 코드는 fragement 뒤로가기 코드
                                //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                //fragmentManager.beginTransaction().remove(FragmentPlus.this).commit();
                                //fragmentManager.popBackStack();

                            }
                        });
                    }
                });
                //모임정보 업데이트
                //mDatabase.child("users").child(userId).child("username").setValue(name);
            }
        });

        return vGroup;
    }
    //내용 업로드
    private void contentUpload() {

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis());

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
    } //갤러리에서 사진 불러와서 넣기



    protected void onMainActivity(int requestCode, int resultCode, Intent data) {
        if(requestCode == GET_GALLERY_IMAGE&&resultCode == RESULT_OK&&data !=
                null && data.getData()!=null){
            Uri selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }


    /*public class MyGalleryAdapter extends BaseAdapter {

        Context context;
        Integer[] posterID = { R.drawable.layout1, R.drawable.layout2,
                R.drawable.layout3, R.drawable.layout4, R.drawable.layout5,
                R.drawable.layout6, R.drawable.layout7, R.drawable.layout8,
                R.drawable.layout9, R.drawable.layout10};
        String[] title = {"레이아웃1","레이아웃2", "레이아웃3",
                "레이아웃4","레이아웃5","레이아웃6","레이아웃7","레이아웃8","레이아웃9","레이아웃10"};

        public MyGalleryAdapter(Context c) {
            context = c;
        }

        public int getCount() {
            return posterID.length;
        }



        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        /*public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageview = new ImageView(context);
            imageview.setLayoutParams(new Gallery.LayoutParams(200, 300));
            imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageview.setPadding(5, 5, 5, 5);
            imageview.setImageResource(posterID[position]);


            final int pos = position;
            imageview.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch (View v, MotionEvent event) {
                    ImageView ivPoster = (ImageView) findViewById(R.id.ivPoster);
                    ivPoster.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    ivPoster.setImageResource(posterID[pos]);
                    Toast.makeText(getApplicationContext(),title[pos],Toast.LENGTH_SHORT).show();

                    //dlg.setIcon(R.drawable.movie_icon);

                    return false;
                }
            });*/

    //return imageview
}
