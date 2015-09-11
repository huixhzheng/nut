package com.hyxbiao.nut;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.hyxbiao.nut.client.ICommand;
import com.hyxbiao.nut.plugin.IPluginInstallCallback;
import com.hyxbiao.nut.plugin.Plugin;
import com.hyxbiao.nut.plugin.PluginManager;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private final static String TAG = "MainActivity";

//    private ICommand mCommandService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button bt_transfer = (Button) findViewById(R.id.bt_transfer);
        final Button bt_plugin_install = (Button) findViewById(R.id.bt_plugin_install);

        bt_transfer.setOnClickListener(this);
        bt_plugin_install.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_transfer:
                break;
            case R.id.bt_plugin_install:
                pluginInstall();
                break;
            default:
                break;
        }
    }

    private void pluginInstall() {
        Log.d(TAG, "install click");

        Plugin plugin = new Plugin("test.txt");
        plugin.install(MainActivity.this, new IPluginInstallCallback() {
            @Override
            public void onResult(int statusCode, String result) {
                Log.d(TAG, "status: " + statusCode + ", result: " + result);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
