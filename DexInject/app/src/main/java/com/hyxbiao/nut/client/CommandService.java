package com.hyxbiao.nut.client;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class CommandService extends Service {
    private final static String TAG = "CommandService";

    public CommandService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
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
        Log.i(TAG,"service on rebind");
        super.onRebind(intent);
    }

    private final ICommand.Stub mBinder = new ICommand.Stub() {

        public void installPlugin(Intent intent, IRemotePluginInstallCallback callback) throws RemoteException {
            Log.d(TAG, "start install");
            callback.onResult(0, "install plugin finish");
        }
        public void invokePlugin(String method) {
            Log.d(TAG, "start");
        }
    };
}
