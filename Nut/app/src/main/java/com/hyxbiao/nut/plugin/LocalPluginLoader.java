package com.hyxbiao.nut.plugin;

import android.content.Context;

/**
 * Created by hyxbiao on 15/9/11.
 */
public class LocalPluginLoader extends PluginLoader {

    private final static String TAG = "LocalPluginLoader";

    @Override
    public void load(Context context, String path, IPluginInstallCallback callback) {
        callback.onResult(IPluginCallback.STATUS_SUCCESS, "Success");
    }

}
