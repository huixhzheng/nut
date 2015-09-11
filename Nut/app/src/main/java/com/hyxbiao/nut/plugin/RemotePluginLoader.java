package com.hyxbiao.nut.plugin;

import android.content.Context;
import android.content.Intent;

/**
 * Created by hyxbiao on 15/9/11.
 */
public class RemotePluginLoader extends PluginLoader {

    private final static String TAG = "RemotePluginLoader";

    public RemotePluginLoader() {

    }

    @Override
    public void load(Context context, String path, IPluginInstallCallback callback) {

        RemotePluginService.installPlugin(context, new Intent(), callback);
    }

}
