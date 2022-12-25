package com.example.game;

import static com.king.zxing.CaptureFragment.KEY_RESULT;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.king.zxing.util.CodeUtils;

/**
 This activity and TcpClient activity is created with the assistance of this tutorial:
 https://www.cnblogs.com/demodashi/p/8481551.html
 which introduces how to build socket connection to realize online chatting.
 */
public class TcpServerActivity extends Activity implements OnClickListener {
    private final String TAG = "TcpServerActivity";
    private TextView result;
    private ImageView ivQr;
//    private TextView mServerState, mTvReceive;
//    private Button mBtnSet, mBtnStrat, mBtnSend;
//    private EditText mEditMsg;
    private ServerSocket mServerSocket;
    public Socket mSocket;

    private int time_c = 0;
    private int step_c = 0;
    private int time_s = 0;
    private int step_s = 0;

    private String ipAddress;
    private AchieveDBHelper achieveDBHelper;

    private SharedPreferences mSharedPreferences;
    private final int DEFAULT_PORT = 10010;
    private int mServerPort;

    private static final String SERVER_PORT = "server_port";
    private static final String SERVER_MESSAGETXT = "server_msgtxt";
    private OutputStream mOutStream;
    private InputStream mInStream;
    private SocketAcceptThread mAcceptThread;
    private SocketReceiveThread mReceiveThread;

    private HandlerThread mHandlerThread;
    private Handler mSubThreadHandler;

    private final int STATE_CLOSED = 1;
    private final int STATE_ACCEPTING= 2;
    private final int STATE_CONNECTED = 3;
    private final int STATE_DISCONNECTED = 4;

    private int mSocketConnectState = STATE_CLOSED;

    private String mRecycleMsg;
    private static final int MSG_TIME_SEND = 1;
    private static final int MSG_SOCKET_CONNECT = 2;
    private static final int MSG_SOCKET_DISCONNECT = 3;
    private static final int MSG_SOCKET_ACCEPTFAIL = 4;
    private static final int MSG_RECEIVE_DATA = 5;
    private static final int MSG_SEND_DATA = 6;
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_TIME_SEND:
                    writeMsg(mRecycleMsg);
                    break;
                case MSG_SOCKET_CONNECT:
                    mSocketConnectState = STATE_CONNECTED;
//                    mServerState.setText(R.string.state_connected);
                    mReceiveThread = new SocketReceiveThread();
                    mReceiveThread.start();
                    break;
                case MSG_SOCKET_DISCONNECT:
                    mSocketConnectState = STATE_DISCONNECTED;
//                    mServerState.setText(R.string.state_disconect_accept);
                    startAccept();
                    break;
                case MSG_SOCKET_ACCEPTFAIL:
                    startAccept();
                    break;
                case MSG_RECEIVE_DATA:
//                    String text = mTvReceive.getText().toString() +"\r\n" + (String)msg.obj;
//                    mTvReceive.setText(text);
                    if (((String) msg.obj).length() == 2) {
                        Intent intent = new Intent(TcpServerActivity.this, SingleGame.class);
                        intent.putExtra("difficulty", Integer.parseInt(String.valueOf(((String) msg.obj).charAt(0))));
                        intent.putExtra("mission", Integer.parseInt(String.valueOf(((String) msg.obj).charAt(1))));
                        intent.putExtra("mode", 2);
                        startActivityForResult(intent, 2);
                    } else {
                        String[] info = ((String) msg.obj).split(" ");
                        time_c = Integer.parseInt(info[0]);
                        step_c = Integer.parseInt(info[1]);
                    }
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_server);

        super.setContentView(R.layout.activity_display);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        result = findViewById(R.id.result_s);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();

        achieveDBHelper = new AchieveDBHelper(this, "AchievementRecord.db", null, 1);

        /**
         * The QR code generator comes from Github project:
         * https://github.com/jenly1314/ZXingLite/tree/1.1.7-androidx
         */
        PermissionUtils.applyPermission(this);
        ivQr = findViewById(R.id.iv_qr);
        ipAddress = GetIpAddress();
        Bitmap qrCode = CodeUtils.createQRCode(ipAddress, 600, null);
        ivQr.setImageBitmap(qrCode);

//        mServerState = (TextView) findViewById(R.id.serverState);
//        mBtnSet = (Button)findViewById(R.id.bt_server_set);
//        mBtnStrat = (Button)findViewById(R.id.bt_server_start);
//        mBtnSend = (Button)findViewById(R.id.bt_server_send);
//        mEditMsg = (EditText)findViewById(R.id.server_sendMsg);
//        mTvReceive = (TextView) findViewById(R.id.server_receive);
//        mBtnSet.setOnClickListener(this);
//        mBtnStrat.setOnClickListener(this);
//        mBtnSend.setOnClickListener(this);
        mSharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE);

        mServerPort = DEFAULT_PORT;

        initHandlerThraed();
        startServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSocketConnectState == STATE_CLOSED) {
//            mServerState.setText(R.string.state_closed);
        }
        else if(mSocketConnectState == STATE_CONNECTED) {
//            mServerState.setText(R.string.state_connected);
        }
        else if(mSocketConnectState == STATE_DISCONNECTED || mSocketConnectState == STATE_ACCEPTING) {
//            mServerState.setText(R.string.state_disconect_accept);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        // exit Looper of HandlerThread
        mHandlerThread.quit();
        closeConnect();
        if(mServerSocket != null){
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
//            case R.id.bt_server_set:
//                set();
//                break;
//            case R.id.bt_server_start:
//                startServer();
//                break;
//            case R.id.bt_server_send:
//                sendTxt();
//                break;
//            default:
//                break;
        }
    }

//    private void set(){
//        View setview = LayoutInflater.from(this).inflate(R.layout.dialog_serverset, null);
//        final EditText editport = (EditText)setview.findViewById(R.id.server_port);
//        Button ensureBtn = (Button)setview.findViewById(R.id.server_ok);
//
//        editport.setText(mSharedPreferences.getInt(SERVER_PORT, 8086) + "");
//
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(setview);
//        final AlertDialog dialog = builder.show();
//        ensureBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String port = editport.getText().toString();
//                if(port != null && port.length() >0){
//                    mServerPort = Integer.parseInt(port);
//                }
//                SharedPreferences.Editor editor=mSharedPreferences.edit();
//                editor.putInt(SERVER_PORT, mServerPort);
//                editor.commit();
//                dialog.dismiss();
//            }
//        });
//
//    }
    private void startServer() {
        if(mSocketConnectState != STATE_CLOSED) return;
        try {
            // start service
            mServerSocket = new ServerSocket(mServerPort);
        } catch (IOException e) {
            e.printStackTrace();
            mSocketConnectState = STATE_DISCONNECTED;
            Toast.makeText(this, "server failed to start", Toast.LENGTH_SHORT).show();
            return;
        }
        startAccept();
//        mServerState.setText(getString(R.string.state_opened));
    }

    private void startAccept(){
        mSocketConnectState = STATE_ACCEPTING;
        mAcceptThread = new SocketAcceptThread();
        mAcceptThread.start();
    }

    private void sendTxt(String str){
//        if(mRecycleMsg != null){
//            mHandler.removeMessages(MSG_TIME_SEND);
//            mRecycleMsg = null;
//        }
//        if(mSocket == null){
//            Toast.makeText(this, "no client connect", Toast.LENGTH_SHORT).show();
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
        if(msg.length() == 0 || mOutStream == null)
            return;
        try {
            mOutStream.write(msg.getBytes());
            mOutStream.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnect(){
        try {
            if (mOutStream != null) {
                mOutStream.close();
            }
            if (mInStream != null) {
                mInStream.close();
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
    }

    class SocketAcceptThread extends Thread{
        @Override
        public void run() {
            try {
                // waiting for connection of client
                mSocket = mServerSocket.accept();
                mInStream = mSocket.getInputStream();
                mOutStream = mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(MSG_SOCKET_ACCEPTFAIL);
                return;
            }
            Log.i(TAG, "accept success");
            mHandler.sendEmptyMessage(MSG_SOCKET_CONNECT);
        }
    }

    class SocketReceiveThread extends Thread{
        private boolean threadExit = false;
        public void run(){
            byte[] buffer = new byte[1024];
            while(threadExit == false){
                try {
                    int count = mInStream.read(buffer);
                    if(count == -1){
                        Log.i(TAG, "read read -1");
                        mHandler.sendEmptyMessage(MSG_SOCKET_DISCONNECT);
                        break;
                    }else{

                        String receiveData;
                        receiveData = new String(buffer, 0, count);
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
        mHandlerThread = new HandlerThread("handler_thread");
        mHandlerThread.start();
        Looper loop = mHandlerThread.getLooper();
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

    public String GetIpAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int i = wifiInfo.getIpAddress();
        return (i & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF)+ "." +
                ((i >> 24 ) & 0xFF );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            System.out.println("y");
            time_s = data.getIntExtra("time", 0);
            step_s = data.getIntExtra("step", 0);
            sendTxt(time_s + " " + step_s);
            if (time_c != 0) {
                result.setText("You lose...\n\nYour time is " + time_s + " seconds\nstep is " +
                        step_s + ".\n\nYour rival's time is " + time_c + " seconds\nstep is " +
                        step_c + ".\n\n");
            } else {
                result.setText("You win!!!\n\nYour time is " + time_s + " seconds\nstep is " +
                        step_s + ".\n\nYour rival hasn't finish the game.\n\n");
                SQLiteDatabase db = achieveDBHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("complete", 1);
                db.update("achievement", values, "id = 2", null);
            }
            if (data.getIntExtra("star", 0) == 1) {
                ivQr.setImageResource(R.drawable.one_star);
            } else if (data.getIntExtra("star", 0) == 2) {
                ivQr.setImageResource(R.drawable.two_star);
            } else if (data.getIntExtra("star", 0) == 3) {
                ivQr.setImageResource(R.drawable.three_star);
            }
        }
    }
}