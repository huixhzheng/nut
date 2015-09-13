package com.hyxbiao.nut.client;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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

            Uri fileUri = intent.getData();

            Log.d(TAG, "file uri: " + fileUri.toString());
//            try {
//                ContentResolver contentResolver =  getContentResolver();
//                Log.d(TAG, "get contentResolver");
//                ParcelFileDescriptor pfd = contentResolver.openFileDescriptor(fileUri, "r");
//                Log.d(TAG, "get pdf: " + pfd.toString());
//                FileDescriptor fd = pfd.getFileDescriptor();
//                Log.d(TAG, "get fd: " + fd.toString());
//                readFile(fd);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                callback.onResult(-1, e.toString());
//            }
            callback.onResult(0, "install plugin finish");
        }
        public void invokePlugin(String method) {
            Log.d(TAG, "start");
        }
    };

    private void readFile(FileDescriptor fd) {
        FileInputStream fis = new FileInputStream(fd);
        Log.d(TAG, "file input stream");

        StringBuffer fileContent = new StringBuffer("");

        byte[] buffer = new byte[1024];

        int n;
        try {
            while ((n = fis.read(buffer)) != -1) {
                Log.d(TAG, "read file");
                fileContent.append(new String(buffer, 0, n));
            }
            Log.d(TAG, "file content: " + fileContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
