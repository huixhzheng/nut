package com.hyxbiao.nut.plugin;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hyxbiao on 15/9/9.
 */
public class PluginManager {

    private final static String TAG = "PluginManager";

    private List<Plugin> mPlugins;

    private static PluginManager instance = new PluginManager();

    public PluginManager() {
    }

    public static PluginManager getInstance() {
        return instance;
    }

    public List<Plugin> getPluginList(Context context) {
        if (mPlugins != null) {
            return mPlugins;
        }
        AssetManager assetManager = context.getAssets();
        try {
            String[] list = assetManager.list(Plugin.AssetPluginDir);
            for (String file: list) {
                mPlugins.add(new Plugin(file));
                Log.d(TAG, "file: " + file);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return mPlugins;
    }
}
