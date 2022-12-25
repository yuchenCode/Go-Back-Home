package com.example.game;

import static com.king.zxing.CaptureFragment.KEY_RESULT;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.king.zxing.CaptureActivity;

/**
 This activity and TcpServer activity is created with the assistance of this tutorial:
 https://www.cnblogs.com/demodashi/p/8481551.html
 which introduces how to build socket connection to realize online chatting.
 */
public class TcpClientActivity extends Activity implements OnClickListener {
    private final String TAG = "TcpClientActivity";
    private TextView result;
    private ImageView img;
//    private Button mBtnSet, mBtnConnect, mBtnSend;
//    private EditText mEditMsg;
//    private TextView mClientState, mTvReceive;
    public Socket mSocket;

    private int time_c = 0;
    private int step_c = 0;
    private int time_s = 0;
    private int step_s = 0;

    private String ipAddress;
    private AchieveDBHelper achieveDBHelper;

    private SharedPreferences mSharedPreferences;
    private final int DEFAULT_PORT= 10010;
    private String mIpAddress;
    private int mClientPort;
    private static final String IP_ADDRESS = "ip_address";
    private static final String CLIENT_PORT = "client_port";
    private static final String CLIENT_MESSAGETXT = "client_msgtxt";

    private OutputStream mOutStream;
    private InputStream mInStream;
    private SocketConnectThread mConnectThread;
    private SocketReceiveThread mReceiveThread;

    private HandlerThread mHandlerThread;
    // Handler object in subThread
    private Handler mSubThreadHandler;

    private final int STATE_DISCONNECTED = 1;
    private final int STATE_CONNECTING= 2;
    private final int STATE_CONNECTED = 3;
    private int mSocketConnectState = STATE_DISCONNECTED;

    private static final int MSG_TIME_SEND = 1;
    private static final int MSG_SOCKET_CONNECT = 2;
    private static final int MSG_SOCKET_DISCONNECT = 3;
    private static final int MSG_SOCKET_CONNECTFAIL = 4;
    private static final int MSG_RECEIVE_DATA = 5;
    private static final int MSG_SEND_DATA = 6;
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_TIME_SEND:
                    break;
                case MSG_SOCKET_CONNECT:
                    mSocketConnectState = STATE_CONNECTED;
//                    mClientState.setText(R.string.state_connected);
//                    mBtnConnect.setText(R.string.disconnect);
                    mReceiveThread = new SocketReceiveThread();
                    mReceiveThread.start();
                    sendTxt("23");
                    Intent intent = new Intent(TcpClientActivity.this, SingleGame.class);
                    intent.putExtra("difficulty", 2);
                    intent.putExtra("mission", 3);
                    intent.putExtra("mode", 2);
                    startActivityForResult(intent, 2);
                    break;
                case MSG_SOCKET_DISCONNECT:
//                    mClientState.setText(R.string.state_disconected);
//                    mBtnConnect.setText(R.string.connect);
                    mSocketConnectState = STATE_DISCONNECTED;
                    closeConnection();
                    break;
                case MSG_SOCKET_CONNECTFAIL:
                    mSocketConnectState = STATE_DISCONNECTED;
//                    mBtnConnect.setText(R.string.connect);
//                    mClientState.setText(R.string.state_connect_fail);
                    break;
                case MSG_RECEIVE_DATA:
                    String[] info = ((String) msg.obj).split(" ");
                    time_s = Integer.parseInt(info[0]);
                    step_s = Integer.parseInt(info[1]);
//                    String text = mTvReceive.getText().toString() +"\r\n" + (String)msg.obj;
//                    mTvReceive.setText(text);
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_client);

        setContentView(R.layout.activity_scan);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        result = findViewById(R.id.result_c);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();

        achieveDBHelper = new AchieveDBHelper(this, "AchievementRecord.db", null, 1);

        /**
         * The QR code scanner comes from Github project:
         * https://github.com/jenly1314/ZXingLite/tree/1.1.7-androidx
         */
        PermissionUtils.applyPermission(this);
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, 1);


//        mBtnSet = (Button)findViewById(R.id.bt_client_set);
//        mBtnConnect = (Button)findViewById(R.id.bt_client_connect);
//        mBtnSend = (Button)findViewById(R.id.bt_client_send);
//        mEditMsg = (EditText)findViewById(R.id.client_sendMsg);
//        mClientState = (TextView) findViewById(R.id.client_state);
//        mTvReceive = (TextView) findViewById(R.id.client_receive);
//        mBtnSet.setOnClickListener(this);
//        mBtnConnect.setOnClickListener(this);
//        mBtnSend.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSocketConnectState == STATE_CONNECTED){
//            mBtnConnect.setText(R.string.disconnect);
//            mClientState.setText(R.string.state_connected);
        }else if(mSocketConnectState == STATE_DISCONNECTED){
//            mBtnConnect.setText(R.string.connect);
//            mClientState.setText(R.string.state_disconected);
        }
        else if(mSocketConnectState == STATE_CONNECTING){
//            mClientState.setText(R.string.state_connecting);
//            mClientState.setText(R.string.state_connected);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mHandlerThread.quit();
        } catch (Exception e){

        }
        closeConnection();
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
//            case R.id.bt_client_set:
//                set();
//                break;
//            case R.id.bt_client_connect:
//                if(mSocketConnectState == STATE_CONNECTED){
//                    closeConnection();
//                }else{
//                    startConnect();
//                }
//                break;
//            case R.id.bt_client_send:
//                sendTxt();
//                break;
//            default:
//                break;
        }
    }

//    private void set(){
//        View setview = LayoutInflater.from(this).inflate(R.layout.dialog_clientset, null);
//        final EditText ipAddress = (EditText) setview.findViewById(R.id.edtt_ipaddress);
//        final EditText editport = (EditText)setview.findViewById(R.id.client_port);
//        Button ensureBtn = (Button)setview.findViewById(R.id.client_ok);
//
//        ipAddress.setText(mSharedPreferences.getString(IP_ADDRESS, null));
//        editport.setText(mSharedPreferences.getInt(CLIENT_PORT, 8086) + "");
//
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(setview);
//        final AlertDialog dialog = builder.show();
//        ensureBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String port = editport.getText().toString();
//                mIpAddress = ipAddress.getText().toString();
//                if(port != null && port.length() >0){
//                    mClientPort = Integer.parseInt(port);
//                }
//                SharedPreferences.Editor editor=mSharedPreferences.edit();
//                editor.putString(IP_ADDRESS, mIpAddress);
//                editor.putInt(CLIENT_PORT, mClientPort);
//                editor.commit();
//                dialog.dismiss();
//            }
//        });
//
//    }
    private void startConnect() {
        Log.i(TAG,"startConnect");
        if(mIpAddress == null || mIpAddress.length() == 0){
            Toast.makeText(this, "Please set IP Address", Toast.LENGTH_LONG).show();
            return;
        }
        if(mSocketConnectState != STATE_DISCONNECTED) return;
        mConnectThread = new SocketConnectThread();
        mConnectThread.start();
        mSocketConnectState = STATE_CONNECTING;
//        mClientState.setText(R.string.state_connecting);
    }

    private void sendTxt(String str){
//        if(mSocket == null){
//            Toast.makeText(this, "no connect", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        String str = mEditMsg.getText().toString();
//        if(str.length() == 0)
//            return;
        Message msg = new Message();
        msg.what = MSG_SEND_DATA;
        msg.obj = str;
        mSubThreadHandler.sendMessage(msg);
    }

    private void writeMsg(String msg){

        Log.i(TAG, "writeMsg msg="+msg);
        if(msg.length() == 0 || mOutStream == null)
            return;
        try {
            mOutStream.write(msg.getBytes());
            mOutStream.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void closeConnection(){
        try {
            if (mOutStream != null) {
                mOutStream.close();
                mOutStream = null;
            }
            if (mInStream != null) {
                mInStream.close();
                mInStream = null;
            }
            if(mSocket != null){
                mSocket.close();
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(mReceiveThread != null){
            mReceiveThread.threadExit();
            mReceiveThread = null;
        }
        mSocketConnectState = STATE_DISCONNECTED;
//        mBtnConnect.setText(R.string.connect);
//        mClientState.setText(R.string.state_disconected);
    }

    class SocketConnectThread extends Thread{
        public void run(){
            try {
                // connect server
                mSocket = new Socket(mIpAddress,mClientPort);
                if(mSocket != null){
                    mOutStream = mSocket.getOutputStream();
                    mInStream = mSocket.getInputStream();
                }
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(MSG_SOCKET_CONNECTFAIL);
                return;
            }
            Log.i(TAG,"connect success");
            mHandler.sendEmptyMessage(MSG_SOCKET_CONNECT);
        }

    }
    class SocketReceiveThread extends Thread{
        private boolean threadExit;
        public SocketReceiveThread() {
            threadExit = false;
        }
        public void run(){
            byte[] buffer = new byte[1024];
            while(threadExit == false){
                try {
                    int count = mInStream.read(buffer);
                    if( count == -1){
                        Log.i(TAG, "read read -1");
                        mHandler.sendEmptyMessage(MSG_SOCKET_DISCONNECT);
                        break;
                    }else{
                        String receiveData = new String(buffer, 0, count);
                        Log.i(TAG, "read buffer:"+receiveData+",count="+count);
                        Message msg = new Message();
                        msg.what = MSG_RECEIVE_DATA;
                        msg.obj = receiveData;
                        mHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        void threadExit(){
            threadExit = true;
        }
    }

    private void initHandlerThraed() {
        // create HandlerThread object
        mHandlerThread = new HandlerThread("handler_thread");
        mHandlerThread.start();
        Looper loop = mHandlerThread.getLooper();
        // create Handler and bind the thread。
        mSubThreadHandler = new Handler(loop){
            public void handleMessage(Message msg) {
                Log.i(TAG, "mSubThreadHandler handleMessage thread:"+Thread.currentThread());
                switch(msg.what){
                    case MSG_SEND_DATA:
                        writeMsg((String)msg.obj);
                        break;
                    default:
                        break;
                }
            };
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                ipAddress = data.getStringExtra(KEY_RESULT);
                mSharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
                mIpAddress = ipAddress;
                mClientPort = DEFAULT_PORT;
                initHandlerThraed();
                startConnect();
            } else if (requestCode == 2) {
                time_c = data.getIntExtra("time", 0);
                step_c = data.getIntExtra("step", 0);
                sendTxt(time_c + " " + step_c);
                if (time_s != 0) {
                    result.setText("You lose...\n\nYour time is " + time_c + " seconds\nstep is " +
                            step_c + ".\n\nYour rival's time is " + time_s + " seconds\nstep is " +
                            step_s + ".\n\n");
                } else {
                    result.setText("You win!!!\n\nYour time is " + time_c + " seconds\nstep is " +
                            step_c + ".\n\nYour rival hasn't finish the game.\n\n");
                    SQLiteDatabase db = achieveDBHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("complete", 1);
                    db.update("achievement", values, "id = 2", null);
                }
                if (data.getIntExtra("star", 0) == 1) {
                    img.setImageResource(R.drawable.one_star);
                } else if (data.getIntExtra("star", 0) == 2) {
                    img.setImageResource(R.drawable.two_star);
                } else if (data.getIntExtra("star", 0) == 3) {
                    img.setImageResource(R.drawable.three_star);
                }
            }
        }
    }
}
