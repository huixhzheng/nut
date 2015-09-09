package com.hyxbiao.nut.client;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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

    private final ICommandService.Stub mBinder = new ICommandService.Stub() {
        public boolean monitorNetwork() {
            return true;
        }
    };
}
