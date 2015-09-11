package com.hyxbiao.nut.plugin;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.hyxbiao.nut.client.ICommand;
import com.hyxbiao.nut.client.IRemotePluginInstallCallback;

public class RemotePluginService extends Service implements ServiceConnection {
    private final static String TAG = "RemotePluginService";

    private static final String ACTION_INSTALL_PLUGIN = "action_install_plugin";

    private static final String EXTRA_PARAM1 = "plugin.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "plugin.extra.PARAM2";

    private final static String COMMAND_SERVICE = "com.hyxbiao.nut.client.CommandService";

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private ICommand mCommandService;
    private boolean mBound;

    public static void installPlugin(Context context, Intent intent1, final IPluginInstallCallback callback) {
        Intent intent = new Intent(context, RemotePluginService.class);
        intent.setAction(ACTION_INSTALL_PLUGIN);
        intent.putExtra(EXTRA_PARAM1, intent1);
        intent.putExtra(EXTRA_PARAM2, new ResultReceiver(null) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                String result = resultData.getString("result", "");
                callback.onResult(resultCode, result);
            }
        });
        context.startService(intent);
    }

    private void handleInstallPlugin(Intent intent, final ResultReceiver recevier) {
        Log.d(TAG, "handleInstallPlugin");

        final Bundle bundle = new Bundle();
        if (mBound) {
            bundle.putString("result", "CommandService is null");
            recevier.send(IPluginCallback.STATUS_FAIL, bundle);
            return;
        }
        try {
            mCommandService.installPlugin(intent, new IRemotePluginInstallCallback.Stub() {

                @Override
                public void onResult(int statusCode, String result) throws RemoteException {
                    bundle.putString("result", result);
                    recevier.send(statusCode, bundle);
                }
            });
        } catch (RemoteException e) {
            bundle.putString("result", e.toString());
            recevier.send(IPluginCallback.STATUS_FAIL, bundle);
        }
    }


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {

            Intent intent = (Intent)msg.obj;
            if (intent != null) {
                final String action = intent.getAction();
                if (ACTION_INSTALL_PLUGIN.equals(action)) {
                    final Intent intent1 = intent.getParcelableExtra(EXTRA_PARAM1);
                    final ResultReceiver recevier = intent.getParcelableExtra(EXTRA_PARAM2);
                    handleInstallPlugin(intent1, recevier);
                }
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            // stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        HandlerThread thread = new HandlerThread("RemotePluginService");
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        //bind remote service
        final Intent intent = new Intent();
        intent.setAction(COMMAND_SERVICE);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onDestroy");

        if (mBound) {
            unbindService(this);
        }
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i(TAG, "CommandService connect service");
        mCommandService = ICommand.Stub.asInterface(service);
        mBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i(TAG, " CommandService disconnect service");
        mCommandService = null;
        mBound = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

}
