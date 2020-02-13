package com.randdusing.bluetoothle;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;
import android.widget.Toast;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import static com.randdusing.bluetoothle.Actions.stringBluetoothOnAction;
import static com.randdusing.bluetoothle.Background.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import static com.randdusing.bluetoothle.Constants.*;

public class EnableBluetoothActivity extends Activity {
    private static String TAG = "EnableBluetooth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, String.format("onActivityResult requestCode=%s, resultCode=%s, data=%s", requestCode, resultCode, data));

        final Intent intent = getIntent();
        Intent serv = new Intent(intent.getAction(), intent.getData(), getApplicationContext(), Background.class);
        serv.setAction(stringBluetoothOnAction);

        if (resultCode == REQUEST_BT_ENABLE) {
            Log.e(TAG, " BIBA REQUEST_BT_ENABLE");
            //Whether the result code was successful or not, just check whether Bluetooth is enabled
            if (bluetoothAdapter.isEnabled()) {
                if (isAutoStart) {
                    Log.e(TAG, "ВКЛЮЧИЛИ BLUETOOTH");
                }
                startService(serv);
            } else {
                android.widget.Toast.makeText(this, "Включите Bluetooth, чтобы ZONT Метка заработала", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
