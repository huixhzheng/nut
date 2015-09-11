package com.hyxbiao.nut.plugin;

/**
 * Created by hyxbiao on 15/9/11.
 */
public interface IPluginInstallCallback extends IPluginCallback {

    void onResult(int statusCode, String result);
}
