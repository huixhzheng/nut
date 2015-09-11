package com.hyxbiao.nut.plugin;

import android.content.Context;

/**
 * Created by hyxbiao on 15/9/11.
 */
public abstract class PluginLoader {

    public abstract void load(Context context, String path, IPluginInstallCallback callback);
}
