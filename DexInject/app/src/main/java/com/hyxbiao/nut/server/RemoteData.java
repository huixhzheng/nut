package com.hyxbiao.nut.server;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.os.Process;

import com.hyxbiao.nut.client.ICommandCallback;
import com.hyxbiao.nut.client.IRemotePluginInstallCallback;
import com.hyxbiao.nut.plugin.ChoreographerHook;

/**
 * Created by hyxbiao on 15/9/13.
 */
public class RemoteData implements ServiceConnection {
    private final static String TAG = "NutHook-RemoteData";

    private final static String REMOTEDATA_SERVICE = "com.hyxbiao.nut.server.RemoteDataService";

    private IRemoteData mRemoteDataService;
    private boolean mBound;

    private static RemoteData instance = new RemoteData();

    public RemoteData() {
    }

    public static RemoteData getInstance() { return instance; }

    public void bindService(Context context) {
        //bind remote service
        final Intent intent = new Intent();
        intent.setAction(REMOTEDATA_SERVICE);
        context.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    public void unBindService(Context context) {
        if (mBound) {
            context.unbindService(this);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i(TAG, "mRemoteDataService connect service");
        mRemoteDataService = IRemoteData.Stub.asInterface(service);
        mBound = true;
        try {
            Log.d(TAG, "register callback");
            if (mRemoteDataService != null) {
                mRemoteDataService.registerCallback(Process.myPid(), mCallback);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d(TAG, "exception: " + e.toString());
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i(TAG, " mRemoteDataService disconnect service");
        mRemoteDataService = null;
        mBound = false;
    }

    public void debug(String msg) {
        if (mBound) {
            try {
                mRemoteDataService.debug(msg);
            } catch (RemoteException e) {
                Log.d(TAG, "exception: " + e.toString());
                e.printStackTrace();
            }
        }
    }

    private ICommandCallback.Stub mCallback = new ICommandCallback.Stub() {
        @Override
        public void installPlugin(Intent intent, IRemotePluginInstallCallback callback) throws RemoteException {
            Log.d(TAG, "start install");

            Uri fileUri = intent.getData();

            Log.d(TAG, "file uri: " + fileUri.toString());
        }

        @Override
        public void invokePlugin(String method) throws RemoteException {
            Log.d(TAG, "invoke plugin");
            ChoreographerHook.instance().start();
        }
    };
}
