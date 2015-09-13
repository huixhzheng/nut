// IDataService.aidl
package com.hyxbiao.nut.server;

// Declare any non-default types here with import statements
import com.hyxbiao.nut.client.ICommandCallback;

interface IRemoteData {

    int registerCallback(int pid, ICommandCallback cb);

    int unregisterCallback(int pid, ICommandCallback cb);

    void debug(String msg);
}
