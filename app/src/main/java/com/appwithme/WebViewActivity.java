package com.appwithme;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.appwithme.R;

public class WebViewActivity extends AppCompatActivity {

    private WebView browser;

    class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processDATA(String data) {

            //bundle을 사용하여 넘겨주는 방식 : 여러 묶음의 데이터를 전달할 수 있다.
//            Bundle extra = new Bundle();
//            Intent intent = new Intent();
//            extra.putString("data", data);
//            intent.putExtras(extra);
//            setResult(RESULT_OK, intent);
//            finish();

            //intent로만 넘겨주는 방식
            Intent intent = new Intent();
            //intent.putExtras(extra); //extras는 묶음이기 때문에 bundle만 넣을 수있다.
            intent.putExtra("data",data);
            setResult(RESULT_OK, intent);
            finish();

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        browser = findViewById(R.id.webView);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new MyJavaScriptInterface(), "Android");

        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                browser.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });

        //browser.loadUrl("file:///android_asset/daum.html");
        //browser.loadUrl("http://www.daddyface.com/public/daum.html");
        //browser.loadUrl("http://cdn.rawgit.com/jolly73-df/DaumPostcodeExample/master/DaumPostcodeExample/app/src/main/assets/daum.html");
        //browser.loadUrl("http://www.inspond.com/daum.html");
//        browser.loadUrl("http://3.34.140.2/search_navi_address.php");
        //browser.loadUrl("http://3.34.140.2/index.html");
        browser.loadUrl("http://54.180.154.192/index.html");
        // 경고! 위 주소대로 서비스에 사용하시면 파일이 삭제됩니다.
        // 꼭 자신의 웹 서버에 해당 파일을 복사해서 주소를 변경해 사용하세요.

    }
}