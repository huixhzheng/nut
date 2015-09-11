// ICommandCallback.aidl
package com.hyxbiao.nut.client;

// Declare any non-default types here with import statements

interface IRemotePluginInstallCallback {

    void onResult(int statusCode, String result);
}
