package com.hyxbiao.nut.dexinject;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Enumeration;

import com.hyxbiao.nut.server.RemoteData;
import com.taobao.android.dexposed.DeviceCheck;
import com.taobao.android.dexposed.DexposedBridge;
import com.taobao.android.dexposed.XC_MethodHook;
import com.taobao.android.dexposed.XposedHelpers;

import dalvik.system.DexFile;


/**
 * Created by hyxbiao on 15/9/2.
 */
public class InjectProxy {
    public static final String TAG = "NutHook-InjectProxy";

    public static final boolean DEBUG = true;

    public static boolean isInited = false;
//    public static native void initCallback(int status);

    public static boolean invokeEntry(final String dexPath, final int mode) {

        Log.d(TAG, "invoke entry, isInited: " + isInited);

        if (isInited) {
            return true;
        }

        try {
            Log.d(TAG, "dex path: " + dexPath);

            Context context = getContext();

            File nutDir = context.getDir("nut", Context.MODE_PRIVATE);
            Log.d(TAG, "nut dir: " + nutDir.getPath());

            SoLibManager.getSoLoader().copyPluginSoLib(context, dexPath, nutDir.getPath());

            if (!canDexposed(context, nutDir) ) {
                Log.d(TAG, "cant not dexposed!");
                return false;
            }
            Log.d(TAG, "start Hook");

            switch (mode) {
                case 0:
                    HookNetwork(context);
                    break;
                case 1:
                    HookCommon(context, "com.baidu.megapp.ProxyEnvironment");
                    break;
                default:
                    break;
            }

            Log.d(TAG, "bind remote service");
            //bind remote service
            RemoteData remoteData = RemoteData.getInstance();
            remoteData.bindService(context);


            Log.d(TAG, "bind finish");

            isInited = true;
        } catch (Exception e) {
            Log.d(TAG, "exception: " + e.toString());
            return false;
        }

        return true;
    }

    private static void Debug(Context context, String dexPath) {
        try {
            Log.d(TAG, "Debug step 1");
            DexFile dx = DexFile.loadDex(dexPath, File.createTempFile("opt", "dex",
                    context.getCacheDir()).getPath(), 0);
            Log.d(TAG, "Debug step 2");
            // Print all classes in the DexFile
            for(Enumeration<String> classNames = dx.entries(); classNames.hasMoreElements();) {
                String className = classNames.nextElement();
                Log.d(TAG, "class: " + className);
            }
        } catch (IOException e) {
            Log.w(TAG, "Error opening " + dexPath, e);
        }
    }

    private static Context getContext() {
        try {
            final Class<?> activityThreadClass =
                    Class.forName("android.app.ActivityThread");
            final Method method = activityThreadClass.getMethod("currentApplication");
            return (Application) method.invoke(null, (Object[]) null);
        } catch (final ClassNotFoundException e) {
            // handle exception
        } catch (final NoSuchMethodException e) {
            // handle exception
        } catch (final IllegalArgumentException e) {
            // handle exception
        } catch (final IllegalAccessException e) {
            // handle exception
        } catch (final InvocationTargetException e) {
            // handle exception
        }
        return null;
    }

    private static synchronized boolean canDexposed(Context context, File prefixPath) {
        if (!DeviceCheck.isDeviceSupport(context)) {
            Log.d(TAG, "Device not support");
            return false;
        }
        Log.d(TAG, "start load so");
        // load xposed lib for hook.
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(prefixPath.getPath());
            sb.append(File.separator);

            if (android.os.Build.VERSION.SDK_INT == 10
                    || android.os.Build.VERSION.SDK_INT == 9) {
                sb.append("libdexposed2.3.so");
            } else if (android.os.Build.VERSION.SDK_INT > 19){
                sb.append("libdexposed_l.so");
            } else {
                sb.append("libdexposed.so");
            }
            Log.d(TAG, "so path: " + sb.toString());
            System.load(sb.toString());
            Log.d(TAG, "load success");
            return true;
        } catch (Throwable e) {
            Log.d(TAG, "load exception: " + e.toString());
            return false;
        }
    }

    private static void HookNetwork(Context context) {
        try {
            ClassLoader cl = context.getClassLoader();
            Class<?> xclass = cl.loadClass("com.baidu.android.common.net.ProxyHttpClient");
            //Class<?> xclass = XposedHelpers.findClass("org.apache.http.impl.client.DefaultHttpClient", null);

            Method[] interfaces = xclass.getDeclaredMethods();
            for (Method ifs: interfaces) {
                Class<?>[] ts = ifs.getParameterTypes();
                StringBuilder sb = new StringBuilder();
                sb.append(ifs.getName());
                for (Class<?> t: ts) {
                    sb.append(", param:" + t.getName());
                }
                Log.d("Hook", sb.toString());
            }

            DexposedBridge.findAndHookMethod(xclass, "executeSafely", HttpUriRequest.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                    // "thisObject" keeps the reference to the instance of target class.
                    DefaultHttpClient instance = (DefaultHttpClient) param.thisObject;

                    // The array args include all the parameters.
//                    HttpUriRequest request = (HttpUriRequest) param.args[0];
//                    URI uri = request.getURI();
//                    HttpParams params = request.getParams();
//                    Log.d("Hook", "uri: " + uri.toString());
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    HttpResponse response = (HttpResponse) param.getResult();
                    // The array args include all the parameters.
                    HttpUriRequest request = (HttpUriRequest) param.args[0];
                    URI uri = request.getURI();
                    //response.getEntity();
                    int status_code = response.getStatusLine().getStatusCode();
                    Log.d("Hook", "status_code: " + status_code + ", uri: " + uri.toString());
                    RemoteData.getInstance().debug(uri.toString());
                }
            });
        } catch (ClassNotFoundException e) {
            Log.d("Hook", "ClassNotFoundException: " + e.toString());
        } catch (XposedHelpers.ClassNotFoundError e) {
            Log.d("Hook", "ClassNotFoundError: " + e.toString());
        }
    }

    private static void HookCommon(Context context, String className) {
        ClassLoader cl = context.getClassLoader();

        XC_MethodHook commonHook = new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                String method = param.method.getName();
                StringBuilder sb = new StringBuilder();
                if (param.thisObject != null) {
                    sb.append("class Name: " + param.thisObject.getClass().getName());
                }

                sb.append(", method Name: " + param.method.getName());
                if (method.equals("invokePlugin")) {
                    int idx = 1;
                    if (!param.args[1].getClass().getSimpleName().equals("String")) {
                        idx = 2;
                    }
                    sb.append(", packageName: " + param.args[idx]);
                    sb.append(", methodName: " + param.args[idx+1]);
                    sb.append(", from: " + param.args[idx+2]);
                    sb.append(", paramsJSONStr: " + param.args[idx+3]);
                } else if (method.equals("invokeHost")) {
                    sb.append(", receiverID: " + param.args[0]);
                    sb.append(", methodName: " + param.args[1]);
                    sb.append(", from: " + param.args[4]);
                }
                Log.d("Hook", sb.toString());
//                Log.d("Hook", Log.getStackTraceString(new Exception()));
            }
        };
        try {
//            ClassLoader cl = getClassLoader();
            Class<?> xclass = cl.loadClass(className);
            //Class<?> xclass = XposedHelpers.findClass("org.apache.http.impl.client.DefaultHttpClient", null);

            Method[] interfaces = xclass.getDeclaredMethods();
            for (Method ifs: interfaces) {
                Class<?>[] ts = ifs.getParameterTypes();
                StringBuilder sb = new StringBuilder();
                sb.append(ifs.getName());
                Object[] paraList = new Object[ts.length+1];
                int i = 0;
                for (Class<?> t: ts) {
                    sb.append(", param:" + t.getName());
                    paraList[i] = t;
                    i++;
                }
                Log.d("Hook", sb.toString());

                paraList[i] = commonHook;
                DexposedBridge.findAndHookMethod(xclass, ifs.getName(), paraList);
            }


        } catch (ClassNotFoundException e) {
            Log.d("Hook", "ClassNotFoundException: " + e.toString());
        } catch (XposedHelpers.ClassNotFoundError e) {
            Log.d("Hook", "ClassNotFoundError: " + e.toString());
        }
    }
}
