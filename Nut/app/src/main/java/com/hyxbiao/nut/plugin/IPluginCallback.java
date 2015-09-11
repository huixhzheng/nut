package com.hyxbiao.nut.plugin;

/**
 * Created by hyxbiao on 15/9/11.
 */
public interface IPluginCallback {

    int STATUS_SUCCESS = 0;
    int STATUS_FAIL = -1;

    void onResult(int statusCode, String result);
}
