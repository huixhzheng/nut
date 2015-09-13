package com.hyxbiao.nut.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.hyxbiao.nut.client.ICommandCallback;
import com.hyxbiao.nut.client.IRemotePluginInstallCallback;
import com.hyxbiao.nut.plugin.IPluginCallback;
import com.hyxbiao.nut.plugin.IPluginInstallCallback;

import java.util.HashMap;
import java.util.Map;

public class RemoteDataService extends Service {
    private final static String TAG = "RemoteDataService";

    private final static int SUCCESS = 0;
    private final static int FAIL = -1;

    private static final String ACTION_INSTALL_PLUGIN = "action_install_plugin";
    private static final String ACTION_INVOKE_PLUGIN = "action_invoke_plugin";

    private static final String EXTRA_PARAM1 = "plugin.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "plugin.extra.PARAM2";
    private static final String EXTRA_PARAM3 = "plugin.extra.PARAM3";

    private int mDebugPid;

    private final RemoteCallbackList<ICommandCallback> mCallbacks = new RemoteCallbackList<>();
    private final Map<Integer, ICommandCallback> mCallbacksMap = new HashMap<>();

    public RemoteDataService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {

        Log.d(TAG, "onDestroy");
        mCallbacks.kill();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "service on unbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "service on rebind");
        super.onRebind(intent);
    }

    private final IRemoteData.Stub mBinder = new IRemoteData.Stub() {
        @Override
        public int registerCallback(int pid, ICommandCallback cb) throws RemoteException {
            if (cb != null) {
                Log.d(TAG, "register callback pid: " + pid);
                mDebugPid = pid;
                mCallbacks.register(cb);
                mCallbacksMap.put(pid, cb);
                return SUCCESS;
            }
            return FAIL;
        }

        @Override
        public int unregisterCallback(int pid, ICommandCallback cb) throws RemoteException {
            if (cb != null) {
                mCallbacksMap.remove(pid);
                mCallbacks.unregister(cb);
                return SUCCESS;
            }
            return FAIL;
        }

        @Override
        public void debug(String msg) throws RemoteException {
            Log.d(TAG, "msg: " + msg);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = (Intent)msg.obj;
            if (intent != null) {
                final String action = intent.getAction();
                if (ACTION_INSTALL_PLUGIN.equals(action)) {
//                    final int pid = intent.getIntExtra(EXTRA_PARAM1, -1);
                    final int pid = mDebugPid;
                    final Intent intent1 = intent.getParcelableExtra(EXTRA_PARAM2);
                    final ResultReceiver recevier = intent.getParcelableExtra(EXTRA_PARAM3);
                    handleInstallPlugin(pid, intent1, recevier);
                } else if (ACTION_INVOKE_PLUGIN.equals(action)) {
                    final int pid = mDebugPid;
                    handleInvokePlugin(pid);
                }
            }
            stopSelf(msg.arg1);
        }
    };

    public static void installPlugin(Context context, int pid, Intent intent1, final IPluginInstallCallback callback) {
        Intent intent = new Intent(context, RemoteDataService.class);
        intent.setAction(ACTION_INSTALL_PLUGIN);
        intent.putExtra(EXTRA_PARAM1, pid);
        intent.putExtra(EXTRA_PARAM2, intent1);
        intent.putExtra(EXTRA_PARAM3, new ResultReceiver(null) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                String result = resultData.getString("result", "");
                callback.onResult(resultCode, result);
            }
        });
        context.startService(intent);
    }

    public static void invokePlugin(Context context, int pid, Intent intent1) {
        Intent intent = new Intent(context, RemoteDataService.class);
        intent.setAction(ACTION_INVOKE_PLUGIN);
        intent.putExtra(EXTRA_PARAM1, pid);
        intent.putExtra(EXTRA_PARAM2, intent1);
        context.startService(intent);
    }

    public void handleInstallPlugin(int pid, Intent intent, final ResultReceiver recevier) {
        final Bundle bundle = new Bundle();

        ICommandCallback ccb = mCallbacksMap.get(pid);
        if (ccb == null) {
//            cb.onResult(IPluginCallback.STATUS_FAIL, "remote callback is null");
            bundle.putString("result", "remote callback is null");
            recevier.send(IPluginCallback.STATUS_FAIL, bundle);
            return;
        }
        final int n = mCallbacks.beginBroadcast();
        try {
            ccb.installPlugin(intent, new IRemotePluginInstallCallback.Stub() {
                @Override
                public void onResult(int statusCode, String result) throws RemoteException {
                    bundle.putString("result", result);
                    recevier.send(statusCode, bundle);
//                    cb.onResult(statusCode, result);
                }
            });
        } catch (RemoteException e) {
//            cb.onResult(IPluginCallback.STATUS_FAIL, "exception: " + e.toString());
            bundle.putString("result", e.toString());
            recevier.send(IPluginCallback.STATUS_FAIL, bundle);
            e.printStackTrace();
        }
        mCallbacks.finishBroadcast();
    }

    public void handleInvokePlugin(int pid) {

        ICommandCallback ccb = mCallbacksMap.get(pid);
        if (ccb == null) {
            Log.d(TAG, "remote callback is null");
            return;
        }
        final int n = mCallbacks.beginBroadcast();
        try {
            ccb.invokePlugin("");
        } catch (RemoteException e) {
            Log.w(TAG, e.toString());
            e.printStackTrace();
        }
        mCallbacks.finishBroadcast();
    }
}
