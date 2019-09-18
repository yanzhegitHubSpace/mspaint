package com.yan.anr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private Button test;

    private final static int MSG_HANDLE = 1 << 1;

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button myButton;

    private MySurfaceView mySurfaceView;

    private GlobalHandler globalHandler;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_HANDLE) {
                Log.d(TAG, "handler");
            }
        }
    };

    private Handler mH = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };


    private Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_HANDLE) {
                Log.d(TAG, "接收到消息 == " + msg.arg1 + "  已经过了: " + (msg.arg1 * 5) / 60 + "分" + (msg.arg1 * 5) % 60 + "秒");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        myButton = findViewById(R.id.myButton);
        mySurfaceView = findViewById(R.id.mySurfaceView);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("yanzhe", "clear");
                // 主线程给子线程发消息
                globalHandler = GlobalHandler.getInstance();
                globalHandler.setHandleMsgListener(mySurfaceView);
                globalHandler.sendEmptyMessage(2);
//                Message msg = Message.obtain();
//                msg.what = 2;
//                msg.setTarget(globalHandler);
//                msg.sendToTarget();
            }
        });

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

//        test = (Button) findViewById(R.id.test);
//        test.setOnClickListener(this);
//        sendMsg();
        // 多渠道打包
        try {
            ApplicationInfo applicationInfo = getApplication().getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Log.d("yanzhe", applicationInfo.metaData.getString("channel_name"));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


    }

    private void sendMsg() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                int num = 1;
                for (; ; ) {
                    try {
                        Thread.sleep(5000);
                        Message obtain = Message.obtain();
                        obtain.what = MSG_HANDLE;
                        obtain.arg1 = num;
//                        obtain.setTarget(handler);
                        handler.sendMessage(obtain);
                        num++;
                    } catch (Exception e) {
                        new Throwable();
                    }
                }
            }
        }.start();

    }

    @Override
    public void onClick(View v) {
//        SystemClock.sleep(10000);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(MSG_HANDLE);
    }

    @SuppressLint("NewApi")
    private void requestPermission() {

        Log.i(TAG, "requestPermission");
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "checkSelfPermission");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                Log.i(TAG, "shouldShowRequestPermissionRationale");
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            } else {
                Log.i(TAG, "requestPermissions");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
//                ActivityManagerS
                //
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            Log.d(TAG, result + "");
            Log.d(TAG, requestCode + "");
            Log.d(TAG, result + "");
        }
    }
}
