package com.xibeixue.hotfix;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class Hotfix {

    public static void patch(Context context, String patchDexFile, String patchClassName)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        //获取系统PathClassLoader的"dexElements"属性值
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        Object origDexElements = getDexElements(pathClassLoader);

        //新建DexClassLoader并获取“dexElements”属性值
        String otpDir = context.getDir("dex", 0).getAbsolutePath();
        Log.i("hotfix", "otpdir=" + otpDir);
        DexClassLoader nDexClassLoader = new DexClassLoader(patchDexFile, otpDir, patchDexFile, context.getClassLoader());
        Object patchDexElements = getDexElements(nDexClassLoader);

        //将patchDexElements插入原origDexElements前面
        Object allDexElements = combineArray(origDexElements, patchDexElements);

        //将新的allDexElements重新设置回pathClassLoader
        setDexElements(pathClassLoader, allDexElements);

        //重新加载类
        pathClassLoader.loadClass(patchClassName);
    }

    private static Object getDexElements(ClassLoader classLoader) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        //首先获取ClassLoader的“pathList”实例
        Field pathListField = Class.forName("dalvik.system.BaseDexClassLoader").getDeclaredField("pathList");
        pathListField.setAccessible(true);//设置为可访问
        Object pathList = pathListField.get(classLoader);

        //然后获取“pathList”实例的“dexElements”属性
        Field dexElementField = pathList.getClass().getDeclaredField("dexElements");
        dexElementField.setAccessible(true);

        //读取"dexElements"的值
        Object elements = dexElementField.get(pathList);
        return elements;
    }

    //合拼dexElements
    private static Object combineArray(Object obj, Object obj2) {
        Class componentType = obj2.getClass().getComponentType();
        //读取obj长度
        int length = Array.getLength(obj);
        //读取obj2长度
        int length2 = Array.getLength(obj2);
        Log.i("hotfix", "length=" + length + ",length2=" + length2);
        //创建一个新Array实例，长度为ojb和obj2之和
        Object newInstance = Array.newInstance(componentType, length + length2);
        for (int i = 0; i < length + length2; i++) {
            //把obj2元素插入前面
            if (i < length2) {
                Array.set(newInstance, i, Array.get(obj2, i));
            } else {
                //把obj元素依次放在后面
                Array.set(newInstance, i, Array.get(obj, i - length2));
            }
        }
        //返回新的Array实例
        return newInstance;
    }

    private static void setDexElements(ClassLoader classLoader, Object dexElements) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        //首先获取ClassLoader的“pathList”实例
        Field pathListField = Class.forName("dalvik.system.BaseDexClassLoader").getDeclaredField("pathList");
        pathListField.setAccessible(true);//设置为可访问
        Object pathList = pathListField.get(classLoader);

        //然后获取“pathList”实例的“dexElements”属性
        Field declaredField = pathList.getClass().getDeclaredField("dexElements");
        declaredField.setAccessible(true);

        //设置"dexElements"的值
        declaredField.set(pathList, dexElements);
    }
}
