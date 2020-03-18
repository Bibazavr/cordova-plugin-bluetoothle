package com.microline.bluetoothle;

import android.app.*;
import android.bluetooth.*;
import android.bluetooth.le.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;
import static com.microline.bluetoothle.Actions.*;
import static com.microline.bluetoothle.AutoStartParams.*;
import static com.microline.bluetoothle.BluetoothLePlugin.*;
import static com.microline.bluetoothle.Constants.*;


public class Background extends Service {
    private static String TAG = "Background";
    private static final int ID_SERVICE = 300;

    //API 21+ Scan and Advertise Callbacks
    private static ScanCallback scanCallback = null;
    private static AdvertiseCallback advertiseCallback = null;

    static BluetoothAdapter bluetoothAdapter;
    private static boolean isReceiverRegistered = false;
    private static boolean isBondReceiverRegistered = false;
    private static boolean isServiceAdded = false;
    static boolean isAutoStart = false;

    //General callback variables
    private static BluetoothGattServer gattServer;
    private static boolean isAdvertising = false;

    //Store connections and all their callbacks
    private static HashMap<Object, HashMap<Object, Object>> connections;

    //Store bonds
    private static HashMap<String, CallbackContext> bonds = new HashMap<String, CallbackContext>();

    //---------------------------------Возможно для будущего пригодятся-----------------------------------------------//
    //Quick Writes
    private LinkedList<byte[]> queueQuick = new LinkedList<byte[]>();

    //Queueing
    private LinkedList<Operation> queue = new LinkedList<Operation>();
    //---------------------------------Возможно для будущего пригодятся end--------------------------------------------//


    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show();
        Log.e(TAG, "onCreate");

        // do stuff like register for BroadcastReceiver, etc.
        // Create the Foreground Service

        createAdvertiseCallback();

        Notification notification = createNotification("ZONT Метка активна");

        startForeground(ID_SERVICE, notification);
    }

    private String createNotificationChannel(NotificationManager notificationManager) {
        String channelId = "6969";
        String channelName = TAG;
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    private Notification createNotification(String text) {
        Log.e("BIBA", String.format("createNotification %s", text));
        Intent resultIntent = new Intent(this, com.microline.bluetoothle.MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        return notificationBuilder.setOngoing(true)
                .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
                .setContentText(text)
                .setShowWhen(false)
                .setPriority(PRIORITY_MIN)
                .setContentIntent(resultPendingIntent)
                .build();
    }

    private void updateNotification(Notification notification) {
        Log.e("BIBA", "updateNotification");
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        startForeground(ID_SERVICE, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");

        String action = intent != null ? intent.getAction() : null;

        if (null == action) {
            Toast.makeText(this, "ZONT Метка запустилась.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "AutoAction");
            isAutoStart = true;

            Log.e(TAG, "InitializeAction");
            initialize(init, isAutoStart);

        } else {
            isAutoStart = false;
            switch (action) {
                case stringInitializeAction:
                    Log.e("BIBA", "stringInitializeAction");
                    Log.e(TAG, String.valueOf(argsInitializeAction));
                    initialize(argsInitializeAction, isAutoStart, callbackContextInitializeAction);
                    break;

                case stringEnableAction:
                    Log.e("BIBA", "stringEnableAction");
                    enableAction(callbackContextEnableAction);
                    break;
                case stringDisableAction:
                    Log.e("BIBA", "stringDisableAction");
                    disableAction(callbackContextDisableAction);
                    break;


                case stringGetConnectedDevices:
                    Log.e("BIBA", "stringGetConnectedDevices");
                    getConnectedDevices(callbackContextGetConnectedDevices);
                    break;
                case stringGetAdapterInfoAction:
                    Log.e("BIBA", "stringGetAdapterInfoAction");
                    getAdapterInfoAction(callbackContextGetAdapterInfoAction);
                    break;


                case stringBondAction:
                    Log.e("BIBA", "stringBondAction");
                    Log.e(TAG, String.valueOf(argsBondAction));
                    bondAction(argsBondAction, callbackContextBondAction);
                    break;
                case stringUnBondAction:
                    Log.e("BIBA", "stringUnBondAction");
                    Log.e(TAG, String.valueOf(argsUnBondAction));
                    unbondAction(argsUnBondAction, callbackContextUnBondAction);
                    break;
                case stringIsBondedAction:
                    Log.e("BIBA", "stringIsBondedAction");
                    Log.e(TAG, String.valueOf(argsIsBondedAction));
                    isBondedAction(argsIsBondedAction, callbackContextIsBondedAction);
                    break;


                case stringDisconnectAction:
                    Log.e("BIBA", "stringDisconnectAction");
                    Log.e(TAG, String.valueOf(argsDisconnectAction));
                    disconnectAction(argsDisconnectAction, callbackContextDisconnectAction);
                    break;


                case stringAddServiceAction:
                    Log.e("BIBA", "stringAddServiceAction");
                    Log.e(TAG, String.valueOf(argsAddServiceAction));
                    addServiceAction(argsAddServiceAction, isAutoStart, callbackContextAddServiceAction);
                    break;
                case stringRemoveServiceAction:
                    Log.e("BIBA", "stringRemoveServiceAction");
                    Log.e(TAG, String.valueOf(argsRemoveServiceAction));
                    removeServiceAction(argsRemoveServiceAction, callbackContextRemoveServiceAction);
                    break;
                case stringRemoveAllServiceAction:
                    Log.e("BIBA", "stringRemoveAllServiceAction");
                    Log.e(TAG, String.valueOf(argsRemoveAllServiceAction));
                    removeAllServicesAction(argsRemoveAllServiceAction, callbackContextRemoveAllServiceAction);
                    break;


                case stringStartAdvertisingAction:
                    Log.e("BIBA", "stringStartAdvertisingAction");
                    Log.e(TAG, String.valueOf(argsStartAdvertisingAction));
                    startAdvertisingAction(argsStartAdvertisingAction, isAutoStart, callbackContextStartAdvertisingAction);
                    break;
                case stringStopAdvertisingAction:
                    Log.e("BIBA", "stringStopAdvertisingAction");
                    stopAdvertisingAction(callbackContextStopAdvertisingAction);
                    break;
                case stringIsAdvertisingAction:
                    Log.e("BIBA", "stringIsAdvertisingAction");
                    isAdvertisingAction(callbackContextIsAdvertisingAction);
                    break;


                case stringRespondAction:
                    Log.e("BIBA", "stringRespondAction");
                    Log.e(TAG, String.valueOf(argsRespondAction));
                    respondAction(argsRespondAction, callbackContextRespondAction);
                    break;
                case stringNotifyAction:
                    Log.e("BIBA", "stringNotifyAction");
                    Log.e(TAG, String.valueOf(argsNotifyAction));
                    notifyAction(argsNotifyAction, callbackContextNotifyAction);
                    break;


                case stringBluetoothOnAction:
                    Log.e("BIBA", "AddServiceAction");
                    addServiceAction(service, isAutoStart);

                    Log.e("BIBA", "StartAdvertisingAction");
                    startAdvertisingAction(params_advertising, isAutoStart);
                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service destroyed by user.", Toast.LENGTH_LONG).show();
        if (isReceiverRegistered) {
            this.unregisterReceiver(mReceiver);
        }
        if (isBondReceiverRegistered) {
            this.unregisterReceiver(mBondReceiver);
        }
    }

    private void initialize(JSONArray args, Boolean isAutoStart, CallbackContext... callbackContextInitializeAction) {
        //Save init callback
        JSONObject returnObj;
        JSONObject obj = null;
        if (!isAutoStart) {
            obj = getArgsObject(args);
        }
        if (isAutoStart || obj != null && getStatusReceiver(obj)) {
            //Add a receiver to pick up when Bluetooth state changes
            this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            isReceiverRegistered = true;
        }

        BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);

        if (bluetoothAdapter == null) {
            //Get Bluetooth adapter via Bluetooth Manager
            assert bluetoothManager != null;
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        returnObj = new JSONObject();

        //If it's already enabled,
        if (bluetoothAdapter.isEnabled()) {
            //Re-opening Gatt server seems to cause some issues
            if (gattServer == null) {
                gattServer = bluetoothManager.openGattServer(this.getApplicationContext(), bluetoothGattServerCallback);

                Log.e("BIBA", "AddServiceAction");
                addServiceAction(service, isAutoStart);

                Log.e("BIBA", "StartAdvertisingAction");
                startAdvertisingAction(params_advertising, isAutoStart);
                return;
            }
        }


        boolean request = true;
        if (obj != null) {
            request = getRequest(obj);
        }

        //Request user to enable Bluetooth надо сделать невидимое активити и там сделать реквест
        if (request) {
            //Request Bluetooth to be enabled
            Intent dialogIntent = new Intent(this, EnableBluetoothActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dialogIntent);
        } else {
            //No request, so send back not enabled
            addProperty(returnObj, keyStatus, statusDisabled);
            addProperty(returnObj, keyMessage, logNotEnabled);
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
            pluginResult.setKeepCallback(true);
            callbackContextInitializeAction[0].sendPluginResult(pluginResult);
        }


        /// ---------------------------------------------------------------
        //  Инициализацию bluetooth объеденил с initializePeripheralAction
        /// ---------------------------------------------------------------

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP && !isAutoStart) {
            returnObj = new JSONObject();
            addProperty(returnObj, "error", "initializePeripheral");
            addProperty(returnObj, "message", logOperationUnsupported);

            callbackContextInitializeAction[0].error(returnObj);
        }
    }

    private void enableAction(CallbackContext callbackContext) {
        if (isNotInitialized(callbackContext, false)) {
            return;
        }

        if (isNotDisabled(callbackContext)) {
            return;
        }

        boolean result = bluetoothAdapter.enable();

        if (!result) {
            //Throw an enabling error
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, keyError, errorEnable);
            addProperty(returnObj, keyMessage, logNotEnabled);

            callbackContext.error(returnObj);
        }  //Else listen to initialize callback for enabling
    }

    private void disableAction(CallbackContext callbackContext) {
        if (isNotInitialized(callbackContext, true)) {
            return;
        }

        boolean result = bluetoothAdapter.disable();

        if (!result) {
            //Throw a disabling error
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, keyError, errorDisable);
            addProperty(returnObj, keyMessage, logNotDisabled);

            callbackContext.error(returnObj);
        }

        //Else listen to initialize callback for disabling
    }

    private void getConnectedDevices(CallbackContext callbackContext) {
        if (callbackContext == null) {
            return;
        }

        JSONObject returnObj = new JSONObject();

        if (gattServer == null) {
            addProperty(returnObj, keyConnectedDevices, logNotEnabled);
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
            callbackContext.sendPluginResult(pluginResult);
            return;
        }
        BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        Object connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);


        addProperty(returnObj, keyConnectedDevices, connectedDevices);
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
        callbackContext.sendPluginResult(pluginResult);
    }

    /**
     * Retrieves a minimal set of adapter details
     * (address, name, initialized state, enabled state, scanning state, discoverable state)
     */
    private void getAdapterInfoAction(CallbackContext callbackContext) {
        JSONObject returnObj = new JSONObject();

        // Not yet initialized
        if (bluetoothAdapter == null) {
            BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapterTmp = bluetoothManager.getAdapter();

            // Since the adapter is not officially initialized, retrieve only the address and the name from the temp ad-hoc adapter
            addProperty(returnObj, keyAddress, bluetoothAdapterTmp.getAddress());
            addProperty(returnObj, keyName, bluetoothAdapterTmp.getName());
            addProperty(returnObj, keyIsInitialized, false);
            addProperty(returnObj, keyIsEnabled, false);
            addProperty(returnObj, keyIsScanning, false);
            addProperty(returnObj, keyIsDiscoverable, false);
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
        } else {
            // Already initialized, so use the bluetoothAdapter class property to get all the info
            addProperty(returnObj, keyAddress, bluetoothAdapter.getAddress());
            addProperty(returnObj, keyName, bluetoothAdapter.getName());
            addProperty(returnObj, keyIsInitialized, true);
            addProperty(returnObj, keyIsEnabled, bluetoothAdapter.isEnabled());
            addProperty(returnObj, keyIsDiscoverable, bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
        }
    }

    private void addServiceAction(JSONArray args, Boolean isAutoStart, CallbackContext... callbackContext) {
        JSONObject obj = getArgsObject(args);


        UUID uuid = getUUID(obj.optString("service", null));

        BluetoothGattService service = new BluetoothGattService(uuid, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        JSONArray characteristicsIn = obj.optJSONArray("characteristics");

        for (int i = 0; i < characteristicsIn.length(); i++) {
            JSONObject characteristicIn = null;

            try {
                characteristicIn = characteristicsIn.getJSONObject(i);
            } catch (JSONException ex) {
                continue;
            }

            UUID characteristicUuid = getUUID(characteristicIn.optString("uuid", null));

            boolean includeClientConfiguration = false;

            JSONObject propertiesIn = characteristicIn.optJSONObject("properties");
            int properties = 0;
            if (propertiesIn != null) {
                if (propertiesIn.optString("broadcast", null) != null) {
                    properties |= BluetoothGattCharacteristic.PROPERTY_BROADCAST;
                }
                if (propertiesIn.optString("extendedProps", null) != null) {
                    properties |= BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS;
                }
                if (propertiesIn.optString("indicate", null) != null) {
                    properties |= BluetoothGattCharacteristic.PROPERTY_INDICATE;
                    includeClientConfiguration = true;
                }
                if (propertiesIn.optString("notify", null) != null) {
                    properties |= BluetoothGattCharacteristic.PROPERTY_NOTIFY;
                    includeClientConfiguration = true;
                }
                if (propertiesIn.optString("read", null) != null) {
                    properties |= BluetoothGattCharacteristic.PROPERTY_READ;
                }
                if (propertiesIn.optString("signedWrite", null) != null) {
                    properties |= BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE;
                }
                if (propertiesIn.optString("write", null) != null) {
                    properties |= BluetoothGattCharacteristic.PROPERTY_WRITE;
                }
                if (propertiesIn.optString("writeNoResponse", null) != null) {
                    properties |= BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE;
                }
                if (propertiesIn.optString(propertyNotifyEncryptionRequired, null) != null) {
                    properties |= 0x100;
                }
                if (propertiesIn.optString(propertyIndicateEncryptionRequired, null) != null) {
                    properties |= 0x200;
                }
            }

            JSONObject permissionsIn = characteristicIn.optJSONObject("permissions");
            int permissions = 0;
            if (permissionsIn != null) {
                if (permissionsIn.optString("read", null) != null) {
                    permissions |= BluetoothGattCharacteristic.PERMISSION_READ;
                }
                if (permissionsIn.optString("readEncrypted", null) != null) {
                    permissions |= BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED;
                }
                if (permissionsIn.optString("readEncryptedMITM", null) != null) {
                    permissions |= BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM;
                }
                if (permissionsIn.optString("write", null) != null) {
                    permissions |= BluetoothGattCharacteristic.PERMISSION_WRITE;
                }
                if (permissionsIn.optString("writeEncrypted", null) != null) {
                    permissions |= BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED;
                }
                if (permissionsIn.optString("writeEncryptedMITM", null) != null) {
                    permissions |= BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM;
                }
                if (permissionsIn.optString("writeSigned", null) != null) {
                    permissions |= BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED;
                }
                if (permissionsIn.optString("writeSignedMITM", null) != null) {
                    permissions |= BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM;
                }
            }

            BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(characteristicUuid, properties, permissions);

            if (includeClientConfiguration) {
                BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(clientConfigurationDescriptorUuid, BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
                characteristic.addDescriptor(descriptor);
            }

            JSONArray descriptorsIn = obj.optJSONArray("descriptors");

            if (descriptorsIn != null) {
                for (int j = 0; j < descriptorsIn.length(); j++) {
                    JSONObject descriptorIn = null;

                    try {
                        descriptorIn = descriptorsIn.getJSONObject(j);
                    } catch (JSONException ex) {
                        continue;
                    }

                    UUID descriptorUuid = getUUID(descriptorIn.optString("uuid", null));

                    permissionsIn = descriptorIn.optJSONObject("permissions");
                    permissions = 0;
                    if (permissionsIn != null) {
                        if (permissionsIn.optString("read", null) != null) {
                            permissions |= BluetoothGattDescriptor.PERMISSION_READ;
                        }
                        if (permissionsIn.optString("readEncrypted", null) != null) {
                            permissions |= BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED;
                        }
                        if (permissionsIn.optString("readEncryptedMITM", null) != null) {
                            permissions |= BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED_MITM;
                        }
                        if (permissionsIn.optString("write", null) != null) {
                            permissions |= BluetoothGattDescriptor.PERMISSION_WRITE;
                        }
                        if (permissionsIn.optString("writeEncrypted", null) != null) {
                            permissions |= BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED;
                        }
                        if (permissionsIn.optString("writeEncryptedMITM", null) != null) {
                            permissions |= BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED_MITM;
                        }
                        if (permissionsIn.optString("writeSigned", null) != null) {
                            permissions |= BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED;
                        }
                        if (permissionsIn.optString("writeSignedMITM", null) != null) {
                            permissions |= BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED_MITM;
                        }
                    }

                    BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(descriptorUuid, permissions);

                    characteristic.addDescriptor(descriptor);
                }
            }

            service.addCharacteristic(characteristic);
        }
        boolean result = gattServer.addService(service);
        isServiceAdded = result;
        Log.e(TAG, String.valueOf(result));
    }

    private void removeServiceAction(JSONArray args, CallbackContext callbackContext) {
        JSONObject obj = getArgsObject(args);
        if (isNotArgsObject(obj, callbackContext)) {
            return;
        }

        UUID uuid = getUUID(obj.optString("service", null));

        BluetoothGattService service = gattServer.getService(uuid);
        if (service == null) {
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, "service", uuid.toString());
            addProperty(returnObj, "error", "service");
            addProperty(returnObj, "message", "Service doesn't exist");

            callbackContext.error(returnObj);
            return;
        }

        boolean result = gattServer.removeService(service);
        if (result) {
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, "service", uuid.toString());
            addProperty(returnObj, "status", "serviceRemoved");

            callbackContext.success(returnObj);
        } else {
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, "service", uuid.toString());
            addProperty(returnObj, "error", "service");
            addProperty(returnObj, "message", "Failed to remove service");

            callbackContext.error(returnObj);
        }
    }

    private void removeAllServicesAction(JSONArray args, CallbackContext callbackContext) {
        gattServer.clearServices();

        JSONObject returnObj = new JSONObject();

        addProperty(returnObj, "status", "allServicesRemoved");

        callbackContext.success(returnObj);
    }

    protected void startAdvertisingAction(JSONArray args, Boolean isAutoStart, CallbackContext... callbackContext) {
        JSONObject obj = getArgsObject(args);

        assert obj != null;
        if (obj.optBoolean("includeDeviceName", false)) {
            bluetoothAdapter.setName(getAdapterName(obj));
        }

        BluetoothLeAdvertiser advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (advertiser == null || !bluetoothAdapter.isMultipleAdvertisementSupported()) {
            if (!isAutoStart) {
                JSONObject returnObj = new JSONObject();

                addProperty(returnObj, "error", "startAdvertising");
                addProperty(returnObj, "message", "Advertising isn't supported");

                callbackContext[0].error(returnObj);
            }
            return;
        }

        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();

        String modeS = obj.optString("mode", "balanced");
        int mode = AdvertiseSettings.ADVERTISE_MODE_BALANCED;
        if (modeS.equals("lowLatency")) {
            mode = AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY;
        } else if (modeS.equals("lowPower")) {
            mode = AdvertiseSettings.ADVERTISE_MODE_LOW_POWER;
        }
        settingsBuilder.setAdvertiseMode(mode);

        boolean connectable = obj.optBoolean("connectable", true);
        settingsBuilder.setConnectable(connectable);

        int timeout = obj.optInt("timeout", 0);
        if ((timeout < 0 || timeout > 180000) && !isAutoStart) {
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, "error", "startAdvertising");
            addProperty(returnObj, "message", "Invalid timeout (0 - 180000)");

            callbackContext[0].error(returnObj);
            return;
        }
        settingsBuilder.setTimeout(timeout);

        String txPowerLevelS = obj.optString("txPowerLevel", "medium");
        int txPowerLevel = AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM;
        if (txPowerLevelS.equals("high")) {
            txPowerLevel = AdvertiseSettings.ADVERTISE_TX_POWER_HIGH;
        } else if (txPowerLevelS.equals("low")) {
            txPowerLevel = AdvertiseSettings.ADVERTISE_TX_POWER_LOW;
        } else if (txPowerLevelS.equals("ultraLow")) {
            txPowerLevel = AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW;
        }
        settingsBuilder.setTxPowerLevel(txPowerLevel);
        AdvertiseSettings advertiseSettings = settingsBuilder.build();

        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();

        int manufacturerId = obj.optInt("manufacturerId", 0);
        byte[] manufacturerSpecificData = getPropertyBytes(obj, "manufacturerSpecificData");
        if (manufacturerId >= 0 && manufacturerSpecificData != null) {
            dataBuilder.addManufacturerData(manufacturerId, manufacturerSpecificData);
        }

        //dataBuilder.addServiceData();
        UUID uuid = getUUID(obj.optString("service", null));
        if (uuid != null) {
            dataBuilder.addServiceUuid(new ParcelUuid(uuid));
        }

        dataBuilder.setIncludeDeviceName(obj.optBoolean("includeDeviceName", false));

        dataBuilder.setIncludeTxPowerLevel(obj.optBoolean("includeTxPowerLevel", true));

        AdvertiseData advertiseData = dataBuilder.build();
        advertiser.startAdvertising(advertiseSettings, advertiseData, advertiseCallback);
    }

    private void stopAdvertisingAction(CallbackContext... callbackContext) {
        BluetoothLeAdvertiser advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (advertiser == null || !bluetoothAdapter.isMultipleAdvertisementSupported()) {
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, "error", "startAdvertising");
            addProperty(returnObj, "message", "Advertising isn't supported");

            callbackContext[0].error(returnObj);
            return;
        }

        advertiser.stopAdvertising(advertiseCallback);

        JSONObject returnObj = new JSONObject();
        addProperty(returnObj, "status", "advertisingStopped");
        callbackContext[0].success(returnObj);
    }

    private void isAdvertisingAction(CallbackContext callbackContext) {
        JSONObject returnObj = new JSONObject();

        addProperty(returnObj, "isAdvertising", isAdvertising);

        callbackContext.success(returnObj);
    }

    private void respondAction(JSONArray args, CallbackContext callbackContext) {
        JSONObject obj = getArgsObject(args);
        if (isNotArgsObject(obj, callbackContext)) {
            return;
        }

        String address = getAddress(obj);
        if (isNotAddress(address, callbackContext)) {
            return;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        int requestId = obj.optInt("requestId", 0); //TODO validate?
        int status = obj.optInt("status", 0);
        int offset = obj.optInt("offset", 0);
        byte[] value = getPropertyBytes(obj, "value");

        boolean result = gattServer.sendResponse(device, requestId, 0, offset, value);
        if (result) {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, "status", "responded");
            addProperty(returnObj, "requestId", requestId);
            callbackContext.success(returnObj);
        } else {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, "error", "respond");
            addProperty(returnObj, "message", "Failed to respond");
            addProperty(returnObj, "requestId", requestId);
            callbackContext.error(returnObj);
        }
    }

    private void notifyAction(JSONArray args, CallbackContext callbackContext) {
        JSONObject obj = getArgsObject(args);
        if (isNotArgsObject(obj, callbackContext)) {
            return;
        }

        String address = getAddress(obj);
        if (isNotAddress(address, callbackContext)) {
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        UUID serviceUuid = getUUID(obj.optString("service", null));
        BluetoothGattService service = gattServer.getService(serviceUuid);
        if (service == null) {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, "error", "service");
            addProperty(returnObj, "message", "Service not found");
            callbackContext.error(returnObj);
        }

        UUID characteristicUuid = getUUID(obj.optString("characteristic", null));
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUuid);
        if (characteristic == null) {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, "error", "characteristic");
            addProperty(returnObj, "message", "Characteristic not found");
            callbackContext.error(returnObj);
        }

        byte[] value = getPropertyBytes(obj, "value");
        boolean setResult = characteristic.setValue(value);
        if (!setResult) {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, "error", "respond");
            addProperty(returnObj, "message", "Failed to set value");
            callbackContext.error(returnObj);
        }

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(clientConfigurationDescriptorUuid);
        byte[] descriptorValue = descriptor.getValue();

        boolean isIndicate = false;
        if (Arrays.equals(descriptorValue, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)) {
            isIndicate = true;
        }

        //Wait for onNotificationSent event
        boolean result = gattServer.notifyCharacteristicChanged(device, characteristic, isIndicate);
        JSONObject returnObj = new JSONObject();
        addProperty(returnObj, "status", "notify");
        if (!result) {
            addProperty(returnObj, "sent", false);
        } else {
            addProperty(returnObj, "sent", true);
        }
        callbackContext.success(returnObj);
    }

    private void bondAction(JSONArray args, CallbackContext callbackContext) {
        if (!isBondReceiverRegistered) {
            this.registerReceiver(mBondReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
            isBondReceiverRegistered = true;
        }

        if (isNotInitialized(callbackContext, true)) {
            return;
        }

        JSONObject obj = getArgsObject(args);
        if (isNotArgsObject(obj, callbackContext)) {
            return;
        }

        String address = getAddress(obj);
        if (isNotAddress(address, callbackContext)) {
            return;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, keyError, errorBond);
            addProperty(returnObj, keyMessage, logNoDevice);
            addProperty(returnObj, keyAddress, address);

            callbackContext.error(returnObj);
            return;
        }

        CallbackContext checkCallback = (CallbackContext) bonds.get(address);
        if (checkCallback != null) {
            JSONObject returnObj = new JSONObject();

            addDevice(returnObj, device);

            addProperty(returnObj, keyError, errorBond);
            addProperty(returnObj, keyMessage, logBonding);

            callbackContext.error(returnObj);
            return;
        }

        int bondState = device.getBondState();
        if (bondState == BluetoothDevice.BOND_BONDED || bondState == BluetoothDevice.BOND_BONDING) {
            JSONObject returnObj = new JSONObject();

            addDevice(returnObj, device);

            addProperty(returnObj, keyError, errorBond);
            addProperty(returnObj, keyMessage, bondState == BluetoothDevice.BOND_BONDED ? logBonded : logBonding);

            callbackContext.error(returnObj);
            return;
        }

        bonds.put(address, callbackContext);

        boolean result = device.createBond();

        if (!result) {
            JSONObject returnObj = new JSONObject();

            addDevice(returnObj, device);

            addProperty(returnObj, keyError, errorBond);
            addProperty(returnObj, keyMessage, logBondFail);

            callbackContext.error(returnObj);
            bonds.remove(address);
        }
    }

    private void unbondAction(JSONArray args, CallbackContext callbackContext) {
        if (!isBondReceiverRegistered) {
            this.registerReceiver(mBondReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
            isBondReceiverRegistered = true;
        }

        if (isNotInitialized(callbackContext, true)) {
            return;
        }

        JSONObject obj = getArgsObject(args);
        if (isNotArgsObject(obj, callbackContext)) {
            return;
        }

        String address = getAddress(obj);
        if (isNotAddress(address, callbackContext)) {
            return;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, keyError, errorBond);
            addProperty(returnObj, keyMessage, logNoDevice);
            addProperty(returnObj, keyAddress, address);

            callbackContext.error(returnObj);
            return;
        }

        CallbackContext checkCallback = (CallbackContext) bonds.get(address);
        if (checkCallback != null) {
            JSONObject returnObj = new JSONObject();

            addDevice(returnObj, device);

            addProperty(returnObj, keyError, errorBond);
            addProperty(returnObj, keyMessage, logBonding);

            callbackContext.error(returnObj);
            return;
        }

        int bondState = device.getBondState();
        if (bondState == BluetoothDevice.BOND_NONE || bondState == BluetoothDevice.BOND_BONDING) {
            JSONObject returnObj = new JSONObject();

            addDevice(returnObj, device);

            addProperty(returnObj, keyError, errorUnbond);
            addProperty(returnObj, keyMessage, bondState == BluetoothDevice.BOND_NONE ? logUnbonded : logBonding);

            callbackContext.error(returnObj);
            return;
        }

        bonds.put(address, callbackContext);

        boolean result = false;
        try {
            java.lang.reflect.Method mi = device.getClass().getMethod("removeBond");
            Boolean returnValue = (Boolean) mi.invoke(device);
            result = returnValue.booleanValue();
        } catch (Exception e) {
            Log.d("Ble", e.getMessage());
        }

        if (!result) {
            JSONObject returnObj = new JSONObject();

            addDevice(returnObj, device);

            addProperty(returnObj, keyError, errorUnbond);
            addProperty(returnObj, keyMessage, logUnbondFail);

            callbackContext.error(returnObj);
            bonds.remove(address);
        }
    }

    private void isBondedAction(JSONArray args, CallbackContext callbackContext) {
        if (isNotInitialized(callbackContext, true)) {
            return;
        }

        JSONObject obj = getArgsObject(args);
        if (isNotArgsObject(obj, callbackContext)) {
            return;
        }

        String address = getAddress(obj);
        if (isNotAddress(address, callbackContext)) {
            return;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, keyError, errorBond);
            addProperty(returnObj, keyMessage, logNoDevice);
            addProperty(returnObj, keyAddress, address);

            callbackContext.error(returnObj);
            return;
        }

        boolean result = (device.getBondState() == BluetoothDevice.BOND_BONDED);

        JSONObject returnObj = new JSONObject();

        addProperty(returnObj, keyIsBonded, result);

        addDevice(returnObj, device);

        callbackContext.success(returnObj);
    }

    private void disconnectAction(JSONArray args, CallbackContext callbackContext) {
        Log.e("BIBA", "disconnectAction");
        BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        List<BluetoothDevice> connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);
        for (BluetoothDevice connectedDevice : connectedDevices) {
            gattServer.cancelConnection(connectedDevice);
            Log.e("BIBA", String.format("cancelConnection %s", connectedDevice));
        }
    }

    private void createAdvertiseCallback() {
        advertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartFailure(int errorCode) {
                Log.e(TAG, "onStartFailure");
                isAdvertising = false;

                if (callbackContextStartAdvertisingAction == null)
                    return;

                JSONObject returnObj = new JSONObject();
                addProperty(returnObj, keyError, "startAdvertising");

                if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED) {
                    addProperty(returnObj, keyMessage, "Already started");
                } else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE) {
                    addProperty(returnObj, keyMessage, "Too large data");
                } else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
                    addProperty(returnObj, keyMessage, "Feature unsupported");
                } else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR) {
                    addProperty(returnObj, keyMessage, "Internal error");
                } else if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
                    addProperty(returnObj, keyMessage, "Too many advertisers");
                } else {
                    addProperty(returnObj, keyMessage, "Advertising error");
                }

                callbackContextStartAdvertisingAction.error(returnObj);
                callbackContextStartAdvertisingAction = null;
            }

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.e(TAG, "onStartSuccess");
                isAdvertising = true;

                if (callbackContextStartAdvertisingAction == null)
                    return;

                JSONObject returnObj = new JSONObject();

                addProperty(returnObj, "mode", settingsInEffect.getMode());
                addProperty(returnObj, "timeout", settingsInEffect.getTimeout());
                addProperty(returnObj, "txPowerLevel", settingsInEffect.getTxPowerLevel());
                addProperty(returnObj, "isConnectable", settingsInEffect.isConnectable());

                addProperty(returnObj, keyStatus, "advertisingStarted");

                callbackContextStartAdvertisingAction.success(returnObj);
                callbackContextStartAdvertisingAction = null;
            }
        };
    }


    // BroadcastReceivers
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                JSONObject returnObj = new JSONObject();
                PluginResult pluginResult;

                switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                    case BluetoothAdapter.STATE_OFF:
                        updateNotification(createNotification("ZONT Mетка не активна. Включите bluetooth"));

                        isAdvertising = false;
                        android.widget.Toast.makeText(context, "Включите Bluetooth, чтобы ZONT Метка заработала", Toast.LENGTH_SHORT).show();
                        gattServer.clearServices();
                        gattServer.close();

                        if (callbackContextInitializeAction != null) {
                            addProperty(returnObj, "status", "disabled");
                            addProperty(returnObj, "message", "Bluetooth powered off");

                            pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
                            pluginResult.setKeepCallback(true);
                            callbackContextInitializeAction.sendPluginResult(pluginResult);
                        }
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.e(TAG, "STATE_ON " + params_advertising);

                        updateNotification(createNotification("ZONT Mетка активна"));

                        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                        gattServer = bluetoothManager.openGattServer(context, bluetoothGattServerCallback);

                        Log.e("BIBA", "AddServiceAction");
                        addServiceAction(service, isAutoStart);

                        Log.e("BIBA", "StartAdvertisingAction");
                        startAdvertisingAction(params_advertising, isAutoStart);
                        android.widget.Toast.makeText(context, "ZONT Метка снова активна", Toast.LENGTH_SHORT).show();


                        if (callbackContextInitializeAction != null) {
                            addProperty(returnObj, "status", "enabled");
                            addProperty(returnObj, "message", "Bluetooth powered on");

                            pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
                            pluginResult.setKeepCallback(true);
                            callbackContextInitializeAction.sendPluginResult(pluginResult);
                        }
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mBondReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);

                String address = device.getAddress();

                CallbackContext callback = (CallbackContext) bonds.get(address);
                if (callback == null) {
                    return;
                }

                JSONObject returnObj = new JSONObject();

                addDevice(returnObj, device);

                boolean keepCallback = false;

                switch (bondState) {
                    case BluetoothDevice.BOND_BONDED:
                        addProperty(returnObj, keyStatus, statusBonded);
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        addProperty(returnObj, keyStatus, statusBonding);
                        keepCallback = true;
                        break;
                    case BluetoothDevice.BOND_NONE:
                        addProperty(returnObj, keyStatus, statusUnbonded);
                        break;
                }

                if (!keepCallback) {
                    bonds.remove(address);
                }

                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
                pluginResult.setKeepCallback(keepCallback);
                callback.sendPluginResult(pluginResult);
            }
        }
    };

    private static String formatUuid(UUID uuid) {
        String uuidString = uuid.toString().toUpperCase();

        if (uuidString.startsWith(baseUuidStart) && uuidString.endsWith(baseUuidEnd)) {
            return uuidString.substring(4, 8);
        }

        return uuidString;
    }

    private static UUID getUUID(String value) {
        if (value == null) {
            return null;
        }

        if (value.length() == 4) {
            value = baseUuidStart + value + baseUuidEnd;
        }

        UUID uuid = null;

        try {
            uuid = UUID.fromString(value);
        } catch (Exception ex) {
            return null;
        }

        return uuid;
    }

    //Helpers to Check Conditions
    private static boolean isNotInitialized(CallbackContext callbackContext, boolean checkIsNotEnabled) {
        if (bluetoothAdapter == null) {
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, keyError, errorInitialize);
            addProperty(returnObj, keyMessage, logNotInit);

            callbackContext.error(returnObj);

            return true;
        }

        if (checkIsNotEnabled) {
            return isNotEnabled(callbackContext);
        } else {
            return false;
        }
    }

    private static boolean isNotEnabled(CallbackContext callbackContext) {
        if (!bluetoothAdapter.isEnabled()) {
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, keyError, errorEnable);
            addProperty(returnObj, keyMessage, logNotEnabled);

            callbackContext.error(returnObj);

            return true;
        }

        return false;
    }

    private static boolean isNotDisabled(CallbackContext callbackContext) {
        if (bluetoothAdapter.isEnabled()) {
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, keyError, errorDisable);
            addProperty(returnObj, keyMessage, logNotDisabled);

            callbackContext.error(returnObj);

            return true;
        }

        return false;
    }

    private static boolean isNotArgsObject(JSONObject obj, CallbackContext callbackContext) {
        if (obj != null) {
            return false;
        }

        JSONObject returnObj = new JSONObject();

        addProperty(returnObj, keyError, errorArguments);
        addProperty(returnObj, keyMessage, logNoArgObj);

        callbackContext.error(returnObj);

        return true;
    }

    private static boolean isNotAddress(String address, CallbackContext callbackContext) {
        if (address == null) {
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, keyError, errorConnect);
            addProperty(returnObj, keyMessage, logNoAddress);

            callbackContext.error(returnObj);
            return true;
        }

        return false;
    }

    private static boolean isDisconnected(HashMap<Object, Object> connection, BluetoothDevice device, CallbackContext callbackContext) {
        int state = Integer.valueOf(connection.get(keyState).toString());

        //Determine whether the device is currently disconnected NOT including connecting and disconnecting
        //Certain actions like disconnect can be done while connected, connecting, disconnecting
        if (state != BluetoothProfile.STATE_DISCONNECTED) {
            return false;
        }

        JSONObject returnObj = new JSONObject();

        addProperty(returnObj, keyError, errorIsDisconnected);
        addProperty(returnObj, keyMessage, logIsDisconnected);

        addDevice(returnObj, device);

        callbackContext.error(returnObj);

        return true;
    }

    private static boolean isNotConnected(HashMap<Object, Object> connection, BluetoothDevice device, CallbackContext callbackContext) {
        int state = Integer.valueOf(connection.get(keyState).toString());

        //Determine whether the device is currently disconnected including connecting and disconnecting
        //Certain actions like read/write operations can only be done while completely connected
        if (state == BluetoothProfile.STATE_CONNECTED) {
            return false;
        }

        JSONObject returnObj = new JSONObject();

        addProperty(returnObj, keyError, errorIsNotConnected);
        addProperty(returnObj, keyMessage, logIsNotConnected);

        addDevice(returnObj, device);

        callbackContext.error(returnObj);

        return true;
    }

    private static boolean wasConnected(String address, CallbackContext callbackContext) {
        HashMap<Object, Object> connection = connections.get(address);
        if (connection != null) {
            BluetoothGatt peripheral = (BluetoothGatt) connection.get(keyPeripheral);
            BluetoothDevice device = peripheral.getDevice();

            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, keyError, errorConnect);
            addProperty(returnObj, keyMessage, logPreviouslyConnected);

            addDevice(returnObj, device);

            callbackContext.error(returnObj);

            return true;
        }
        return false;
    }

    private static HashMap<Object, Object> wasNeverConnected(String address, CallbackContext callbackContext) {
        HashMap<Object, Object> connection = connections.get(address);
        if (connection != null) {
            return connection;
        }

        JSONObject returnObj = new JSONObject();

        addProperty(returnObj, keyError, errorNeverConnected);
        addProperty(returnObj, keyMessage, logNeverConnected);
        addProperty(returnObj, keyAddress, address);

        callbackContext.error(returnObj);

        return null;
    }

    private static void addDevice(JSONObject returnObj, BluetoothDevice device) {
        addProperty(returnObj, keyAddress, device.getAddress());
        addProperty(returnObj, keyName, device.getName());
    }

    private static void addService(JSONObject returnObj, BluetoothGattService service) {
        addProperty(returnObj, keyService, formatUuid(service.getUuid()));
    }

    private static void addCharacteristic(JSONObject returnObj, BluetoothGattCharacteristic characteristic) {
        addService(returnObj, characteristic.getService());
        addProperty(returnObj, keyCharacteristic, formatUuid(characteristic.getUuid()));
    }

    private static void addDescriptor(JSONObject returnObj, BluetoothGattDescriptor descriptor) {
        addCharacteristic(returnObj, descriptor.getCharacteristic());
        addProperty(returnObj, keyDescriptor, formatUuid(descriptor.getUuid()));
    }

    //General Helpers
    static void addProperty(JSONObject obj, String key, Object value) {
        //Believe exception only occurs when adding duplicate keys, so just ignore it
        try {
            if (value == null) {
                obj.put(key, JSONObject.NULL);
            } else {
                obj.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void addPropertyBytes(JSONObject obj, String key, byte[] bytes) {
        String string = Base64.encodeToString(bytes, Base64.NO_WRAP);

        addProperty(obj, key, string);
    }

    private static JSONObject getArgsObject(JSONArray args) {
        if (args.length() == 1) {
            try {
                return args.getJSONObject(0);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    private static byte[] getPropertyBytes(JSONObject obj, String key) {
        String string = obj.optString(key, null);

        if (string == null) {
            return null;
        }

        byte[] bytes = Base64.decode(string, Base64.NO_WRAP);

        if (bytes == null || bytes.length == 0) {
            return null;
        }

        return bytes;
    }

    private static String getAddress(JSONObject obj) {
        //Get the address string from arguments
        String address = obj.optString(keyAddress, null);

        if (address == null) {
            return null;
        }

        //Validate address format
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            return null;
        }

        return address;
    }

    private static boolean getRequest(JSONObject obj) {
        return obj.optBoolean(keyRequest, false);
    }

    private static boolean getStatusReceiver(JSONObject obj) {
        return obj.optBoolean(keyStatusReceiver, true);
    }

    private static String getAdapterName(JSONObject obj) {
        return obj.optString(keyName, "Biba");
    }

    private static JSONObject getProperties(BluetoothGattCharacteristic characteristic) {
        int properties = characteristic.getProperties();

        JSONObject propertiesObject = new JSONObject();

        if ((properties & BluetoothGattCharacteristic.PROPERTY_BROADCAST) == BluetoothGattCharacteristic.PROPERTY_BROADCAST) {
            addProperty(propertiesObject, propertyBroadcast, true);
        }

        if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) == BluetoothGattCharacteristic.PROPERTY_READ) {
            addProperty(propertiesObject, propertyRead, true);
        }

        if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) {
            addProperty(propertiesObject, propertyWriteWithoutResponse, true);
        }

        if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE) == BluetoothGattCharacteristic.PROPERTY_WRITE) {
            addProperty(propertiesObject, propertyWrite, true);
        }

        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
            addProperty(propertiesObject, propertyNotify, true);
        }

        if ((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) == BluetoothGattCharacteristic.PROPERTY_INDICATE) {
            addProperty(propertiesObject, propertyIndicate, true);
        }

        if ((properties & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) == BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) {
            addProperty(propertiesObject, propertyAuthenticatedSignedWrites, true);
        }

        if ((properties & BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) == BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) {
            addProperty(propertiesObject, propertyExtendedProperties, true);
        }

        if ((properties & 0x100) == 0x100) {
            addProperty(propertiesObject, propertyNotifyEncryptionRequired, true);
        }

        if ((properties & 0x200) == 0x200) {
            addProperty(propertiesObject, propertyIndicateEncryptionRequired, true);
        }

        return propertiesObject;
    }

    private static JSONObject getPermissions(BluetoothGattCharacteristic characteristic) {
        int permissions = characteristic.getPermissions();

        JSONObject permissionsObject = new JSONObject();

        if ((permissions & BluetoothGattCharacteristic.PERMISSION_READ) == BluetoothGattCharacteristic.PERMISSION_READ) {
            addProperty(permissionsObject, permissionRead, true);
        }

        if ((permissions & BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED) == BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED) {
            addProperty(permissionsObject, permissionReadEncrypted, true);
        }

        if ((permissions & BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM) == BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM) {
            addProperty(permissionsObject, permissionReadEncryptedMITM, true);
        }

        if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE) == BluetoothGattCharacteristic.PERMISSION_WRITE) {
            addProperty(permissionsObject, permissionWrite, true);
        }

        if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED) == BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED) {
            addProperty(permissionsObject, permissionWriteEncrypted, true);
        }

        if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM) == BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM) {
            addProperty(permissionsObject, permissionWriteEncryptedMITM, true);
        }

        if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED) == BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED) {
            addProperty(permissionsObject, permissionWriteSigned, true);
        }

        if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM) == BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM) {
            addProperty(permissionsObject, permissionWriteSignedMITM, true);
        }

        return permissionsObject;
    }

    private static JSONObject getPermissions(BluetoothGattDescriptor descriptor) {
        int permissions = descriptor.getPermissions();

        JSONObject permissionsObject = new JSONObject();

        if ((permissions & BluetoothGattDescriptor.PERMISSION_READ) == BluetoothGattDescriptor.PERMISSION_READ) {
            addProperty(permissionsObject, permissionRead, true);
        }

        if ((permissions & BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED) == BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED) {
            addProperty(permissionsObject, permissionReadEncrypted, true);
        }

        if ((permissions & BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED_MITM) == BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED_MITM) {
            addProperty(permissionsObject, permissionReadEncryptedMITM, true);
        }

        if ((permissions & BluetoothGattDescriptor.PERMISSION_WRITE) == BluetoothGattDescriptor.PERMISSION_WRITE) {
            addProperty(permissionsObject, permissionWrite, true);
        }

        if ((permissions & BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED) == BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED) {
            addProperty(permissionsObject, permissionWriteEncrypted, true);
        }

        if ((permissions & BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED_MITM) == BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED_MITM) {
            addProperty(permissionsObject, permissionWriteEncryptedMITM, true);
        }

        if ((permissions & BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED) == BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED) {
            addProperty(permissionsObject, permissionWriteSigned, true);
        }

        if ((permissions & BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED_MITM) == BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED_MITM) {
            addProperty(permissionsObject, permissionWriteSignedMITM, true);
        }

        return permissionsObject;
    }

    private BluetoothGattServerCallback bluetoothGattServerCallback = new BluetoothGattServerCallback() {
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            if (callbackContextInitializeAction == null) {
                return;
            }

            JSONObject returnObj = new JSONObject();

            addDevice(returnObj, device);
            addCharacteristic(returnObj, characteristic);

            addProperty(returnObj, "status", "readRequested");
            addProperty(returnObj, "requestId", requestId);
            addProperty(returnObj, "offset", offset);

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
            pluginResult.setKeepCallback(true);
            callbackContextInitializeAction.sendPluginResult(pluginResult);
        }

        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            if (callbackContextInitializeAction == null) {
                return;
            }

            JSONObject returnObj = new JSONObject();

            addDevice(returnObj, device);
            addCharacteristic(returnObj, characteristic);

            addProperty(returnObj, "status", "writeRequested");
            addProperty(returnObj, "requestId", requestId);
            addProperty(returnObj, "offset", offset);
            addPropertyBytes(returnObj, "value", value);

            addProperty(returnObj, "preparedWrite", preparedWrite);
            addProperty(returnObj, "responseNeeded", responseNeeded);

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
            pluginResult.setKeepCallback(true);
            callbackContextInitializeAction.sendPluginResult(pluginResult);
        }

        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            if (callbackContextInitializeAction == null) {
                return;
            }

            JSONObject returnObj = new JSONObject();

            addDevice(returnObj, device);

            if (newState == BluetoothGatt.STATE_CONNECTED) {
                addProperty(returnObj, "status", "connected");
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                addProperty(returnObj, "status", "disconnected");
            }

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
            pluginResult.setKeepCallback(true);
            callbackContextInitializeAction.sendPluginResult(pluginResult);
        }

        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            if (callbackContextInitializeAction == null) {
                return;
            }

            JSONObject returnObj = new JSONObject();

            addDevice(returnObj, device);
            addDescriptor(returnObj, descriptor);

            addProperty(returnObj, "status", "readRequested");
            addProperty(returnObj, "requestId", requestId);
            addProperty(returnObj, "offset", offset);

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
            pluginResult.setKeepCallback(true);
            callbackContextInitializeAction.sendPluginResult(pluginResult);
        }

        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            if (callbackContextInitializeAction == null) {
                return;
            }

            if (descriptor.getUuid().equals(clientConfigurationDescriptorUuid)) {
                JSONObject returnObj = new JSONObject();

                addDevice(returnObj, device);
                addCharacteristic(returnObj, descriptor.getCharacteristic());

                if (Arrays.equals(value, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)) {
                    addProperty(returnObj, "status", "unsubscribed");
                } else {
                    addProperty(returnObj, "status", "subscribed");
                }

                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
                pluginResult.setKeepCallback(true);
                callbackContextInitializeAction.sendPluginResult(pluginResult);

                gattServer.sendResponse(device, requestId, 0, offset, value);

                return;
            }

            JSONObject returnObj = new JSONObject();

            addDevice(returnObj, device);
            addDescriptor(returnObj, descriptor);

            addProperty(returnObj, "status", "writeRequested");
            addProperty(returnObj, "requestId", requestId);
            addProperty(returnObj, "offset", offset);
            addPropertyBytes(returnObj, "value", value);

            addProperty(returnObj, "preparedWrite", preparedWrite);
            addProperty(returnObj, "responseNeeded", responseNeeded);

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
            pluginResult.setKeepCallback(true);
            callbackContextInitializeAction.sendPluginResult(pluginResult);
        }

        //TODO implement this later
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            //Log.d("Ble", "execute write");
        }

        public void onMtuChanged(BluetoothDevice device, int mtu) {
            if (callbackContextInitializeAction == null) {
                return;
            }

            JSONObject returnObj = new JSONObject();

            addDevice(returnObj, device);
            addProperty(returnObj, "status", "mtuChanged");
            addProperty(returnObj, "mtu", mtu);

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
            pluginResult.setKeepCallback(true);
            callbackContextInitializeAction.sendPluginResult(pluginResult);
        }

        public void onNotificationSent(BluetoothDevice device, int status) {
            if (callbackContextInitializeAction == null) {
                return;
            }

            JSONObject returnObj = new JSONObject();

            addDevice(returnObj, device);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                addProperty(returnObj, "status", "notificationSent");
            } else {
                addProperty(returnObj, "error", "notificationSent");
                addProperty(returnObj, "message", "Unable to send notification");
            }

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
            pluginResult.setKeepCallback(true);
            callbackContextInitializeAction.sendPluginResult(pluginResult);
        }

        public void onServiceAdded(int status, BluetoothGattService service) {
            if (callbackContextAddServiceAction == null) {
                return;
            }

            JSONObject returnObj = new JSONObject();

            addService(returnObj, service);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                addProperty(returnObj, "status", "serviceAdded");
                callbackContextAddServiceAction.success(returnObj);
            } else {
                addProperty(returnObj, "error", "service");
                addProperty(returnObj, "message", "Unable to add service");
                callbackContextAddServiceAction.error(returnObj);
            }
        }
    };
}
