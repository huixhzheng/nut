// ICommandCallback.aidl
package com.hyxbiao.nut.client;

// Declare any non-default types here with import statements
import com.hyxbiao.nut.client.IRemotePluginInstallCallback;

interface ICommandCallback {
    void installPlugin(in Intent intent, IRemotePluginInstallCallback callback);

    void invokePlugin(String method);
}
