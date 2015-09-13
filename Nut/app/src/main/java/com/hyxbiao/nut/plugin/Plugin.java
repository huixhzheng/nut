package com.hyxbiao.nut.plugin;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hyxbiao on 15/9/9.
 */
public class Plugin {

    private final static String TAG = "Plugin";

    public final static String AssetPluginDir = "plugins";
    public final static String PluginDir = "nutplugins";

    private String mFileName;

    private PluginLoader mPluginLoader;
    private PluginLoader mLocalPluginLoader;
    private PluginLoader mRemotePluginLoader;

    public Plugin(String fileName) {
        mFileName = fileName;

        mLocalPluginLoader = new LocalPluginLoader();
        mRemotePluginLoader = new RemotePluginLoader();

        mPluginLoader = mRemotePluginLoader;
    }

    public void setLocalLoader() {
        mPluginLoader = mLocalPluginLoader;
    }

    public void setRemoteLoader() {
        mPluginLoader = mRemotePluginLoader;
    }

    public void install(Context context, IPluginInstallCallback callback) {
        File pluginDir = new File(context.getFilesDir(), PluginDir);

        if (!pluginDir.exists()) {
            pluginDir.mkdir();
        }

        File pluginFile = new File(pluginDir, mFileName);

        //copy from asset to files
        if (!pluginFile.exists()) {
            AssetManager assetManager = context.getAssets();
            File assetFile = new File(AssetPluginDir, mFileName);
            try {
                InputStream is = assetManager.open(assetFile.getPath());
                copy(is, pluginFile);
            } catch (IOException e) {
                Log.w(TAG, e.toString());
                callback.onResult(IPluginCallback.STATUS_FAIL, e.toString());
                return;
            }
        }

        //remote copy
        //dexclassloader
        mPluginLoader.load(context, pluginFile, callback);
    }

    public void invoke(Context context) {
        mPluginLoader.invoke(context);
    }

    public void uninstall() {

    }

    private void copy(InputStream in, File dst) throws IOException {
        FileOutputStream out = new FileOutputStream(dst);
        byte[] buf = new byte[1024];
        int len;

        while((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
    }
}
