// ICommandService.aidl
package com.hyxbiao.nut.client;

// Declare any non-default types here with import statements
import com.hyxbiao.nut.client.IRemotePluginInstallCallback;

interface ICommand {

    void installPlugin(in Intent intent, IRemotePluginInstallCallback callback);

    void invokePlugin(String method);
}
