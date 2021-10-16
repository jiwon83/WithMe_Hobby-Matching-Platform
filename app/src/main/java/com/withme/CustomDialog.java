package com.withme;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.withme.R;

public class CustomDialog extends Dialog {
    EditText ed1,ed2;
    Button btn;
    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);   //다이얼로그의 타이틀바를 없애주는 옵션입니다.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //다이얼로그의 배경을 투명으로 만듭니다.
        setContentView(R.layout.cutomdialog);     //다이얼로그에서 사용할 레이아웃입니다.
        ed1 = (EditText) findViewById(R.id.editText);
        ed2 = (EditText) findViewById(R.id.editText2);
        //btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), ed1.getText().toString() + ed2.getText().toString(), Toast.LENGTH_LONG).show();
                dismiss();   //다이얼로그를 닫는 메소드입니다.
            }
        });

    }

    public static class Builder {

        public Builder(Context context) {

        }
    }
}


