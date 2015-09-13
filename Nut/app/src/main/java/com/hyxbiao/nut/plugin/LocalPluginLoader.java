package com.hyxbiao.nut.plugin;

import android.content.Context;

import java.io.File;

/**
 * Created by hyxbiao on 15/9/11.
 */
public class LocalPluginLoader extends PluginLoader {

    private final static String TAG = "LocalPluginLoader";

    @Override
    public void load(Context context, File pluginFile, IPluginInstallCallback callback) {
        callback.onResult(IPluginCallback.STATUS_SUCCESS, "Success");
    }

    @Override
    public void invoke(Context context) {

    }

}
