package com.cookandroid.withmetabbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.cookandroid.withmetabbar.certify.JoinStartFragment;
import com.cookandroid.withmetabbar.certify.MainActivity2;
import com.cookandroid.withmetabbar.chat.GroupMessageActivity;

import static android.app.Activity.RESULT_OK;

public class MainActivityWebView extends AppCompatActivity {

    String address;



    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;

    private EditText et_address,et_address_detail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_web_view);

        et_address = (EditText)findViewById(R.id.et_address);
        et_address_detail =(EditText)findViewById(R.id.et_address_detail);

        Button btn_ok = (Button)findViewById(R.id.button);

        if (et_address != null)
            et_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivityWebView.this, WebViewActivity.class);
                    startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);
                }
            });



        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //상세주소도 합치기
                if (et_address_detail.getText().toString() != null){
                    //address.concat(et_address_detail.getText().toString());
                    address += " "+ et_address_detail.getText().toString();
                }
                //받은주소 전달 - bundle사용
//                Bundle bundle = new Bundle();
//                bundle.putString("address",address);
//                JoinStartFragment joinStartFragment = new JoinStartFragment();
//                joinStartFragment.setArguments(bundle);
//
//                Log.d("address", address);

                Bundle extra = new Bundle();
                Intent intent = new Intent();//startActivity()를 할것이 아니므로 그냥 빈 인텐트로 만듦
                extra.putString("address", address);
                intent.putExtras(extra);
                setResult(RESULT_OK, intent);
                finish();

            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){

        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode){

            case SEARCH_ADDRESS_ACTIVITY:

                if(resultCode == RESULT_OK){

                    String data = intent.getExtras().getString("data");
                    if (data != null){
                        et_address.setText(data);
                        address =data;

                    }

                }
                break;

        }

    }
}
