package com.randdusing.bluetoothle;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;

import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextInitializeAction;


import android.os.Build;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.json.JSONObject;

import static com.randdusing.bluetoothle.Background.addProperty;
import static com.randdusing.bluetoothle.Background.bluetoothAdapter;
import static com.randdusing.bluetoothle.Constants.*;

class EnableBluetooth extends Activity {
    private static String TAG = "EnableBluetooth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        final Intent serv = new Intent(intent.getAction(), intent.getData(), getApplicationContext(), Background.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // ---------------------------------------
    //  BIBA Надо новое невидиммое активити создать и там из ресивера вызывать вот этот метод
    // ---------------------------------------
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @SuppressLint("MissingPermission")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //If this was a Bluetooth enablement request...
        if (requestCode == REQUEST_BT_ENABLE) {
            Log.e(TAG, "REQUEST_BT_ENABLE");

            //If callback doesnt exist, no reason to proceed

            //Whether the result code was successful or not, just check whether Bluetooth is enabled
            if (!bluetoothAdapter.isEnabled()) {
                JSONObject returnObj = new JSONObject();
                addProperty(returnObj, keyStatus, statusDisabled);
                addProperty(returnObj, keyMessage, logNotEnabled);

                PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, returnObj);
                pluginResult.setKeepCallback(true);
                callbackContextInitializeAction.sendPluginResult(pluginResult);
            }
        } else if (requestCode == REQUEST_LOCATION_SOURCE_SETTINGS) {
            Log.e(TAG, "REQUEST_LOCATION_SOURCE_SETTINGS");
        }
    }
}
