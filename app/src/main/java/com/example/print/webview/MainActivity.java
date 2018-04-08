package com.example.print.webview;

import android.app.admin.DevicePolicyManager;
import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.kioskmode.KioskMode;
import android.app.enterprise.license.EnterpriseLicenseManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.print.webview.Dialog.DialogConfirm;
import com.example.print.webview.Dialog.DialogInfo;
import com.example.print.webview.Dialog.EventListener;
import com.example.print.webview.knox.Receiver;
import com.example.print.webview.knox.SettingKnox;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private DevicePolicyManager mDPM;
    private EnterpriseDeviceManager mEDM;
    private ComponentName mDeviceAdmin;
    static final int RESULT_ENABLE = 1;
    KioskMode kioskMode;
    EnterpriseLicenseManager licenseManager;
    String TAG = "Test";
    String LicenseKey="697AE726D3B93427B4EC20E7CEEB440E4C415552B4136B2263DA8E53CBF4AB5F0D089A7E0592AC2856A6311F2239AE8CFC098286171D7192A02BA0511F19B4AA";
    List<Integer> listHwKey = null;
    WebView webView;
    String packageName = "com.example.print.webview";
    final String URL = "https://www.shopat24.com/";
    ProgressBar progressBar,progressBar2;
    int State=0;
    SharedPreferences preferences ;
    SharedPreferences.Editor editor;
    int count_time = 0,count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        webView = (WebView)findViewById(R.id.WebView);
        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);
        preferences = (SharedPreferences) getSharedPreferences("DataUser",Context.MODE_PRIVATE);
        editor = preferences.edit();

        // setTouch web page
        webView.setOnTouchListener(new View.OnTouchListener() {
            public final static int FINGER_RELEASED = 0;
            public final static int FINGER_TOUCHED = 1;
            public final static int FINGER_DRAGGING = 2;
            public final static int FINGER_UNDEFINED = 3;
            private int fingerState = FINGER_RELEASED;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // get pointer index from the event object
                int pointerIndex = motionEvent.getActionIndex();
                // get pointer ID
                int pointerId = motionEvent.getPointerId(pointerIndex);
                if(pointerId==2){
                    count_time=0;
                    State=1;
                }
                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        if (fingerState == FINGER_RELEASED) {
                            fingerState = FINGER_TOUCHED;
                        }
                        else {
                            fingerState = FINGER_UNDEFINED;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if(fingerState != FINGER_DRAGGING) {
                            fingerState = FINGER_RELEASED;
                        }
                        else if (fingerState == FINGER_DRAGGING){
                            State=0;
                            fingerState = FINGER_RELEASED;
                        }
                        else{
                            State=0;
                            fingerState = FINGER_UNDEFINED;}
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING) {
                            fingerState = FINGER_DRAGGING;
                        }
                        else {
                            fingerState = FINGER_UNDEFINED;
                            if(State==1){
                                count_time++;
                                System.out.println(count_time);
                                if(count_time==100){
                                    DialogConfirm dialogConfirm = new DialogConfirm(MainActivity.this, new EventListener() {
                                        @Override
                                        public void onConfirm() {
                                            SettingKnox settingKnox = new SettingKnox(MainActivity.this);
                                            settingKnox.stop();
                                            mEDM.getApplicationPolicy().stopApp(packageName);
                                        }
                                        @Override
                                        public void onCancle() {

                                        }
                                    });
                                    dialogConfirm.setTitle("UserID");
                                    dialogConfirm.setTitle1("Password");
                                    dialogConfirm.setHeadText("คุณต้องการออกจากแอพพลิเคชั่นใช่หรือไม่");
                                    dialogConfirm.show();
                                    State=0;
                                }
                            }
                        }
                        break;

                    default:
                        fingerState = FINGER_UNDEFINED;
                }
                return false;
            }
        });

        Toast toast = new Toast(getApplicationContext());
        EnterpriseLicenseManager.getInstance(MainActivity.this).activateLicense(LicenseKey);
        Log.i("Enable: ",EnterpriseLicenseManager.ACTION_LICENSE_STATUS);
        kioskMode = KioskMode.getInstance(MainActivity.this);
        mEDM = new EnterpriseDeviceManager(MainActivity.this);

        try {
            if(mDPM == null){
                mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
            }
            if (mDeviceAdmin == null){
                mDeviceAdmin = new ComponentName(MainActivity.this, Receiver.class);
            }
            if (mDPM!=null && !mDPM.isAdminActive(mDeviceAdmin)){
                Intent intent1= new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent1.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
                intent1.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why this needs to be added.");
                startActivityForResult(intent1, RESULT_ENABLE);
            }
            else {
                if(checkPermission()){
                    SettingKnox knox = new SettingKnox(getApplicationContext());
                    knox.start();
                    loadWeb();
                }else {
                    activateLicense();
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Exception: " + e);
            licenseManager.getInstance(MainActivity.this).activateLicense(LicenseKey);
            kioskMode = KioskMode.getInstance(MainActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_ENABLE){
            if(resultCode==RESULT_OK){
                final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        switch (msg.what){
                            case 0:
                                SettingKnox knox = new SettingKnox(getApplicationContext());
                                knox.start();
                                loadWeb();
                                break;
                        }

                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        do{
                            licenseManager=EnterpriseLicenseManager.getInstance(getApplicationContext());
                            licenseManager.activateLicense(LicenseKey);
                            kioskMode = KioskMode.getInstance(getApplicationContext());
                        }
                        while (!(getApplicationContext().checkCallingOrSelfPermission("android.permission.sec.MDM_HW_CONTROL")== PackageManager.PERMISSION_GRANTED));
                        {
                            Message message = new Message();
                            message.what = 0;
                            handler.sendMessage(message);
                        }
                    }
                }).start();
            }
        }
    }


    private boolean checkPermission(){
        if(getApplicationContext().checkCallingOrSelfPermission("android.permission.sec.MDM_HW_CONTROL")== PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void activateLicense(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        SettingKnox knox = new SettingKnox(getApplicationContext());
                        knox.start();
                        loadWeb();
                        System.out.println(checkPermission());
                        break;
                }

            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                do{
                    licenseManager=EnterpriseLicenseManager.getInstance(getApplicationContext());
                    licenseManager.activateLicense(LicenseKey);
                    kioskMode = KioskMode.getInstance(getApplicationContext());
                }
                while (!(getApplicationContext().checkCallingOrSelfPermission("android.permission.sec.MDM_HW_CONTROL")== PackageManager.PERMISSION_GRANTED));
                {
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }
    private void loadWeb(){
        progressBar2.setVisibility(View.GONE);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                progressBar.setProgress(webView.getProgress());
                progressBar.setSecondaryProgress(25);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                System.out.println("Url: "+url+" "+url.toString().contains(URL));
//                if(url.length()<=URL.length()){
//                    if(!url.toString().contains(URL)){
//                        DialogInfo dialogInfo = new DialogInfo(MainActivity.this);
//                        dialogInfo.setText("ไม่สามารถใช้งานหน้าเว็บไซต์ได้");
//                        dialogInfo.show();
//                        webView.loadUrl(URL);
//                    }else {
//                    }
//                }
                if(!url.toString().contains(URL)){
                    DialogInfo dialogInfo = new DialogInfo(MainActivity.this);
                    dialogInfo.setText("ไม่สามารถใช้งานหน้าเว็บไซต์ได้");
                    dialogInfo.show();
                    webView.loadUrl(URL);
                }else {
                    if(url.length()>26){
                        if(!url.substring(0,25).equals(URL)){
                            DialogInfo dialogInfo = new DialogInfo(MainActivity.this);
                            dialogInfo.setText("ไม่สามารถใช้งานหน้าเว็บไซต์ได้");
                            dialogInfo.show();
                            webView.loadUrl(URL);
                        }
                    }

                }
                return super.shouldOverrideUrlLoading(view, url);
            }


        });
        editor.putString("Status","accept");
        editor.commit();
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(URL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        preferences = (SharedPreferences) getSharedPreferences("DataUser",Context.MODE_PRIVATE);
        String s = preferences.getString("Status","");
        System.out.println("data: "+s);
        if(s.equals("accept")){
            mEDM.getApplicationPolicy().setEnableApplication(packageName);
            mEDM.getApplicationPolicy().startApp(packageName,"");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEDM.getApplicationPolicy().stopApp(packageName);
    }
}

