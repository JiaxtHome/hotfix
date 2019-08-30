package com.xibeixue.hotfix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.meituan.robust.PatchExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private Button run;
    private Button fix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        run = findViewById(R.id.run);
        fix = findViewById(R.id.fix);
        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BugClass ssssssss = new BugClass();
                ssssssss.run();
//                try {
//                    getClassLoader().loadClass("com.xibeixue.hotfix.BugClass2");
//                    Class<?> clazz = Class.forName("com.xibeixue.hotfix.BugClass2");
//                    Method run = clazz.getMethod("run2", new Class[]{String.class});
//                    run.invoke(clazz.newInstance(), "111");
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InstantiationException e) {
//                    e.printStackTrace();
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
            }
        });
        fix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                fix();
                fixByRobust();
            }
        });
//        verifyStoragePermissions(this);
    }

    private void fix() {
        try {
            String dexPath = Environment.getExternalStorageDirectory().getPath() + "/patch.jar";  //记得添加SD卡读写权限
            Log.i("hotfix","dexPath=" + dexPath);
            Hotfix.patch(this, dexPath, "com.xibeixue.hotfix.BugClass");
            Log.i("hotfix", "修复成功");
        } catch (Exception e) {
            Log.i("hotfix", "修复失败:" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fixByRobust(){
        new PatchExecutor(getApplicationContext(), new PatchManipulateImp(), new RobustCallBackSample()).start();
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * 在对sd卡进行读写操作之前调用这个方法
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
//        }
    }
}
