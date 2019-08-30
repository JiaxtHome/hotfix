package com.xibeixue.hotfix;

import android.content.Context;
import android.util.Log;

import com.meituan.robust.patch.annotaion.Add;
import com.meituan.robust.patch.annotaion.Modify;

public class BugClass {

    public BugClass() {

    }

    //修复代码，需要添加Modify注释或者调用RobustModify.modify()方法，作为修复标记
    @Modify
    public void run() {
//        Log.i("hotfix", "我有一个严重Bug需要修复！");
        Log.i("hotfix", "我的Bug已经被修复！");
    }

    //添加代码需要添加Add注释，作为标记
    @Add
    public void run2(){
        Log.i("hotfix", "我是一个新添加的方法！");
    }
}
