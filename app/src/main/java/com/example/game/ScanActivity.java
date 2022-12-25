package com.example.game;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.king.zxing.CaptureActivity;

import static com.king.zxing.CaptureFragment.KEY_RESULT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ScanActivity extends AppCompatActivity {

    private String ipAddress;

    private Button mBtnClient;
    private Button mBtnServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        PermissionUtils.applyPermission(this);
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, 1);

        Intent client = new Intent(this,TcpClientActivity.class);
        client.putExtra("IP", ipAddress);
        startActivity(client);

//        mBtnClient = (Button) findViewById(R.id.bt_client);
//        mBtnServer = (Button) findViewById(R.id.bt_server);
//        mBtnClient.setOnClickListener(this);
//        mBtnServer.setOnClickListener(this);

    }

//    @Override
//    public void onClick(View v) {
//        switch(v.getId()){
//            case R.id.bt_client:
//                //做Tcp客户端
//                Intent client = new Intent(this,TcpClientActivity.class);
//                startActivity(client);
//                break;
//            case R.id.bt_server:
//                //做Tcp服务端
//                Intent server = new Intent(this,TcpServerActivity.class);
//                startActivity(server);
//                break;
//            default:
//                break;
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                ipAddress = data.getStringExtra(KEY_RESULT);
            }
        }
    }

//    public String GetIpAddress() {
//        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        int i = wifiInfo.getIpAddress();
//        return (i & 0xFF) + "." +
//                ((i >> 8 ) & 0xFF) + "." +
//                ((i >> 16 ) & 0xFF)+ "." +
//                ((i >> 24 ) & 0xFF );
//    }
}