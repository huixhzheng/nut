package com.hyxbiao.nut.dexinject;

import android.util.Log;

/**
 * Created by hyxbiao on 15/9/2.
 */
public class InjectProxy {
    public static final String TAG = "InjectProxy";

    public static void invokeEntry() {
        Log.d(TAG, "hook entry");
    }
}
