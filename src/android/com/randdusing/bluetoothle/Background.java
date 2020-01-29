package com.randdusing.bluetoothle;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import androidx.core.app.NotificationCompat;
import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;



import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;

import android.os.IBinder;
import android.os.Handler;
import android.os.Build;
import android.os.ParcelUuid;

import android.widget.Toast;

import android.util.Log;

import java.util.HashMap;
import java.util.UUID;
import java.util.Arrays;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanCallback;


public class Background extends Service {
    private String TAG = "BIBA";
    private String whereFrom = "Background";

    private static Handler mHandler = new Handler();
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private AdvertiseCallback advertiseCallback = null;

    private BluetoothGattServer gattServer;
    private BluetoothAdapter bluetoothAdapter;
    private HashMap<Object, HashMap<Object, Object>> connections;
    private final UUID clientConfigurationDescriptorUuid = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");



    private static final int ID_SERVICE = 101;

    @Override
    public void onCreate() {
        super.onCreate();

        // do stuff like register for BroadcastReceiver, etc.

        // Create the Foreground Service
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(ID_SERVICE, notification);
    }

//    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "my_service_channelid";
        String channelName = "My Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
        Log.e("BIBA", "onStartCommand");

        Notification notification = new Notification();
        startForeground(1, notification);


        String paramsInitBluetooth = "{ \"request\": \"true\", \"statusReceiver\": \"true\", \"restoreKey\": \"bluetoothleplugin\" }";
        initialize();
        initializePeripheral();
        createAdvertiseCallback();
        startAdvertisingAction();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this, "Service destroyed by user.", Toast.LENGTH_LONG).show();
    }

    private void initialize() {
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                Log.e("BIBA", "bluetoothAdapter is enabled");
            } else {
                Log.e("BIBA", "bluetoothAdapter is disabled");
            }
            return;
        }


        //Add a receiver to pick up when Bluetooth state changes
//            this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        //Get Bluetooth adapter via Bluetooth Manager
        BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        connections = new HashMap<Object, HashMap<Object, Object>>();

        //If it's already enabled,
        if (bluetoothAdapter.isEnabled()) {
            Log.e("BIBA", "bluetoothAdapter is enabledx2");
            return;
        }

        //Request user to enable Bluetooth
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            this.startActivityForResult(this, enableBtIntent, 59627); // random number


    }


    private void initializePeripheral() {
        if (gattServer == null) {
            BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(BLUETOOTH_SERVICE);
            gattServer = bluetoothManager.openGattServer(this.getApplicationContext(), bluetoothGattServerCallback);
        }
    }
    private void createAdvertiseCallback() {
        advertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartFailure(int errorCode) {


                if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED) {
                    Log.e(TAG, "Already started");
                } else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE) {
                    Log.e(TAG, "Too large data");
                } else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
                    Log.e(TAG, "Feature unsupported");
                } else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR) {
                    Log.e(TAG, "Internal error");
                } else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
                    Log.e(TAG, "Too many advertisers");
                } else {
                    Log.e(TAG, "Advertising error");
                }

            }

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.e(TAG, "advertisingStarted");
            }
        };
    }

    private void startAdvertisingAction() {

        //set adapter name
        bluetoothAdapter.setName("BIBA");

        BluetoothLeAdvertiser advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (advertiser == null || !bluetoothAdapter.isMultipleAdvertisementSupported()) {
            Log.e(TAG, "Advertising isn't supported");
            return;
        }

        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();

        int mode = AdvertiseSettings.ADVERTISE_MODE_BALANCED;
        settingsBuilder.setAdvertiseMode(mode);

        settingsBuilder.setConnectable(true);

//        settingsBuilder.setTimeout(timeout);

        int txPowerLevel = AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM;

        settingsBuilder.setTxPowerLevel(txPowerLevel);
        AdvertiseSettings advertiseSettings = settingsBuilder.build();

        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();


//        let service = {
//                service: "6E400001-B5A3-F393-E0A9-E50E24DCCA9E",
//                characteristics: [
//        {
//            uuid: "6E400002-B5A3-F393-E0A9-E50E24DCCA9E",
//                    permissions: {
//            read: true,
//                    write: true,
//                    readEncryptionRequired: true,
//                    writeEncryptionRequired: true,
//        },
//            properties: {
//                read: true,
//                        writeWithoutResponse: true,
//                        write: true,
//                        notify: true,
//                        indicate: true,
//                        authenticatedSignedWrites: true,
//                        notifyEncryptionRequired: true,
//                        indicateEncryptionRequired: true,
//            }
//        },
//        {
//            uuid: "6E400003-B5A3-F393-E0A9-E50E24DCCA9E",
//                    permissions: {
//            read: true,
//                    write: true,
//                    readEncryptionRequired: true,
//                    writeEncryptionRequired: true,
//        },
//            properties: {
//                read: true,
//                        writeWithoutResponse: true,
//                        write: true,
//                        notify: true,
//                        indicate: true,
//                        authenticatedSignedWrites: true,
//                        notifyEncryptionRequired: true,
//                        indicateEncryptionRequired: true,
//            }
//        }
//            ]
//        };
//        //dataBuilder.addServiceData();
//        UUID uuid = getUUID(obj.optString("service", null));
//        if (uuid != null) {
        UUID uuid =  UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
        dataBuilder.addServiceUuid(new ParcelUuid(uuid));
//        }

        dataBuilder.setIncludeDeviceName(true);

        dataBuilder.setIncludeTxPowerLevel(true);

        AdvertiseData advertiseData = dataBuilder.build();

        advertiser.startAdvertising(advertiseSettings, advertiseData, advertiseCallback);
    }


//    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
//                switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
//                    case BluetoothAdapter.STATE_OFF:
//                        connections = new HashMap<Object, HashMap<Object, Object>>();
//                        break;
//                    case BluetoothAdapter.STATE_ON:
//                        break;
//                }
//            }
//        }
//    };


    private BluetoothGattServerCallback bluetoothGattServerCallback = new BluetoothGattServerCallback() {
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            Log.e(TAG, String.format("onCharacteristicReadRequest: device = %s, characteristic = %s", device, characteristic));
        }


        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {

            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            Log.e(TAG, String.format("onCharacteristicWriteRequest=%s, value=%s " + characteristic.getUuid().toString(), value));

            if (responseNeeded) {
                gattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        value);
                Log.d(TAG, "Received  data on " + characteristic.getUuid().toString());
                Log.d(TAG, "Received data " + bytesToHex(value));

            }
            //IMP: Respond
            mHandler.post(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(this, "We received data ", Toast.LENGTH_SHORT).show();
                }
            });

        }


        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.e(TAG, String.format("onConnectionStateChange: newState = %s, status = %s", newState, status));
        }


        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
            Log.e(TAG, String.format("onDescriptorReadRequest device=%s, descriptor=%s", device, descriptor));
        }


        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            if (descriptor.getUuid().equals(clientConfigurationDescriptorUuid)) {
                if (Arrays.equals(value, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)) {
                    Log.e(TAG, String.format("device=%s status unsubscribed", device));
                } else {
                    Log.e(TAG, String.format("device=%s status subscribed", device));

                }

                gattServer.sendResponse(device, requestId, 0, offset, value);

                return;
            }

        }


        //TODO implement this later
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            //Log.d("BLE", "execute write");
        }


        public void onMtuChanged(BluetoothDevice device, int mtu) {
            Log.e(TAG, String.format("mtuChanged device=%s mtu=%s", device, mtu));

        }


        public void onNotificationSent(BluetoothDevice device, int status) {
            Log.e(TAG, String.format("onNotificationSent device=%s, status=%s", device, status));
        }


        public void onServiceAdded(int status, BluetoothGattService service) {
            Log.e(TAG, String.format("onServiceAdded status=%s, service=%s", status, service));

        }

    };

    /**
     * Helper function converts hex string into
     * byte array
     **/
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Helper function converts byte array to hex string
     * for printing
     **/
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


};

