package com.hyxbiao.nut.plugin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.hyxbiao.nut.server.RemoteDataService;

import java.io.File;


/**
 * Created by hyxbiao on 15/9/11.
 */
public class RemotePluginLoader extends PluginLoader {

    private final static String TAG = "RemotePluginLoader";

    private final static String AUTHORITY = "com.hyxbiao.nut.pluginfile";

    public RemotePluginLoader() {

    }

    @Override
    public void load(Context context, File pluginFile, IPluginInstallCallback callback) {

        Uri fileUri = FileProvider.getUriForFile(context, AUTHORITY, pluginFile);

        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_SEND);
//        intent.setType(context.getContentResolver().getType(fileUri));
//        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.setDataAndType(fileUri, context.getContentResolver().getType(fileUri));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

//        RemotePluginService.installPlugin(context, intent, callback);

        RemoteDataService.installPlugin(context, 3387, intent, callback);
    }

    @Override
    public void invoke(Context context) {
        RemoteDataService.invokePlugin(context, 3387, new Intent());
    }

}
