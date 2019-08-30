package com.xibeixue.hotfix;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.meituan.robust.Patch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PatchManipulateImp extends com.meituan.robust.PatchManipulate {
    @Override
    protected List<Patch> fetchPatchList(Context context) {
        //将app自己的robustApkHash上报给服务端，服务端根据robustApkHash来区分每一次apk build来给app下发补丁
        //apkhash is the unique identifier for  apk,so you cannnot patch wrong apk.
        //String robustApkHash = RobustApkHashUtils.readRobustApkHash(context);
        Patch patch = new Patch();
        patch.setName("123");
        //we recommend LocalPath store the origin patch.jar which may be encrypted,while TempPath is the true runnable jar
        patch.setLocalPath(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "patch");
        /*上面的路径看似设置的是目录,其实不是,在get方法中默认追加了.jar;temp默认则追加_temp.jar.可以理解为设置补丁的文件名.建议放在程序内部目录,提高安全性*/
        /*com.example.huangxiaoyu.hotfixtest.patch 要和robuts中patchPackname节点里面的值保持一样并且存在的目录*/
        patch.setPatchesInfoImplClassFullName("com.xibeixue.hotfix.PatchesInfoImpl");
        List patches = new ArrayList<Patch>();
        patches.add(patch);
        return patches;
    }

    @Override

    protected boolean verifyPatch(Context context, Patch patch) {
        patch.setTempPath(context.getCacheDir() + File.separator + "robust" + File.separator + "patch");
        //in the sample we just copy the file
        try {
            copy(patch.getLocalPath(), patch.getTempPath());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("copy source patch to local patch error, no patch execute in path " + patch.getTempPath());
        }


        return true;
    }

    @Override
    protected boolean ensurePatchExist(Patch patch) {
        return true;
    }

    public void copy(String srcPath, String dstPath) throws IOException {
        Log.i("hotfix","srcPath=" + srcPath);
        File src = new File(srcPath);
        if (!src.exists()) {
            throw new RuntimeException("source patch does not exist ");
        }
        File dst = new File(dstPath);
        if (!dst.getParentFile().exists()) {
            dst.getParentFile().mkdirs();
        }
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

}