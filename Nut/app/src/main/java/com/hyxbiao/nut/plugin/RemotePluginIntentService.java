package com.hyxbiao.nut.plugin;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;

import com.hyxbiao.nut.client.ICommand;
import com.hyxbiao.nut.client.IRemotePluginInstallCallback;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RemotePluginIntentService extends IntentService implements ServiceConnection {
    private final static String TAG = "RemotePluginService";

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_INSTALL_PLUGIN = "action_install_plugin";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.hyxbiao.nut.plugin.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.hyxbiao.nut.plugin.extra.PARAM2";

    private ICommand mCommandService;
    private boolean mBound;

    public static void installPlugin(Context context, Intent intent1, final IPluginInstallCallback callback) {
        Intent intent = new Intent(context, RemotePluginIntentService.class);
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


    public RemotePluginIntentService() {
        super("RemotePluginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INSTALL_PLUGIN.equals(action)) {
                final Intent intent1 = intent.getParcelableExtra(EXTRA_PARAM1);
                final ResultReceiver recevier = intent.getParcelableExtra(EXTRA_PARAM2);
                handleInstallPlugin(intent1, recevier);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleInstallPlugin(Intent intent, final ResultReceiver recevier) {
        Log.d(TAG, "handleInstallPlugin");

        final Bundle bundle = new Bundle();
        if (mCommandService == null) {
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


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        super.onCreate();

        final Intent intent = new Intent();
        intent.setAction("com.hyxbiao.nut.client.CommandService");
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        if (mBound) {
            unbindService(this);
        }
        super.onDestroy();
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
}
