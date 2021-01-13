package com.tms.android;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TsmReflect {

    private static Handler mHandler;

    private static ExecutorService mThreadPool;

    static {
        mHandler = new Handler(Looper.getMainLooper());
        mThreadPool = Executors.newCachedThreadPool();
    }

    private static void invoke(Object target, String methodName, Class<?>[] parameterTypes, Object[] parameter) {
        try {
            Class<?> className = target.getClass();
            Method method = className.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            method.invoke(target, parameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void invokeStatic(Class<?> target, String methodName, Class<?>[] parameterTypes, Object[] parameter) {
        try {
            Method method = target.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            method.invoke(null, parameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void invokeChild(Object target, String methodName, Object[] parameter) {
        final Object var1 = target;
        final String var2 = methodName;
        final Class<?>[] var3 = getParameterTypes(parameter);
        final Object[] var4 = parameter;
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                invoke(var1, var2, var3, var4);
            }
        });
    }

    private static Class<?>[] getParameterTypes(Object[] parameter) {
        if (parameter == null) {
            return null;
        }
        Class<?>[] parameterTypes = new Class<?>[parameter.length];
        for (int i = 0; i < parameter.length; i++) {
            parameterTypes[i] = parameter[i].getClass();
        }
        return parameterTypes;
    }

    public static void invokeMain(Object target, String methodName, Object[] parameter) {
        final Object var1 = target;
        final String var2 = methodName;
        final Class<?>[] var3 = getParameterTypes(parameter);
        final Object[] var4 = parameter;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                invoke(var1, var2, var3, var4);
            }
        });
    }

    public static void invokeChildStatic(Class<?> target, String methodName, Object[] parameter) {
        final Class<?> var1 = target;
        final String var2 = methodName;
        final Class<?>[] var3 = getParameterTypes(parameter);
        final Object[] var4 = parameter;
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                invokeStatic(var1, var2, var3, var4);
            }
        });
    }

    public static void invokeMainStatic(Class<?> target, String methodName, Object[] parameter) {
        final Class<?> var1 = target;
        final String var2 = methodName;
        final Class<?>[] var3 = getParameterTypes(parameter);
        final Object[] var4 = parameter;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                invokeStatic(var1, var2, var3, var4);
            }
        });
    }

}
