package com.example.game;

import android.Manifest;
import android.content.Context;
import android.util.Log;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;

public class PermissionUtils {

    private static String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.VIBRATE
    };

    /**
     * ===========permission
     * @param context
     */
    public static void applyPermission(Context context) {

        try {

            AndPermission.with(context)
                    .runtime()
                    .permission(permissions)
                    .onGranted(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            // granted permission
                            Log.e("aaa", "granted-->" + data.toString());
                        }
                    })
                    .onDenied(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            // denied permission
                            Log.e("aaa", "onDenied-->" + data.toString());


                        }
                    }).start();

        } catch (Exception e) {
            Log.e("aaa", e.getMessage());
        }

    }

}
