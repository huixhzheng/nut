package com.hyxbiao.nut.plugin;

import android.content.Context;

import java.io.File;

/**
 * Created by hyxbiao on 15/9/11.
 */
public abstract class PluginLoader {

    public abstract void load(Context context, File pluginFile, IPluginInstallCallback callback);

    public abstract void invoke(Context context);
}
