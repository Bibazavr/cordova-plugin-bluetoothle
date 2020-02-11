package com.randdusing.bluetoothle;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.NotificationChannel;

import androidx.core.app.NotificationCompat;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;
// Biba imports
import static com.randdusing.bluetoothle.AutoStartParams.init;
import static com.randdusing.bluetoothle.AutoStartParams.service;
import static com.randdusing.bluetoothle.AutoStartParams.params_advertising;

import static com.randdusing.bluetoothle.BluetoothLePlugin.argsAddServiceAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.argsBondAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.argsInitializeAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.argsIsBondedAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.argsNotifyAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.argsRemoveAllServiceAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.argsRemoveServiceAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.argsRespondAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.argsStartAdvertisingAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.argsStopAdvertisingAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.argsUnBondAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.argsDisconnectAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextAddServiceAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextBondAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextGetAdapterInfoAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextInitializeAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextIsAdvertisingAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextIsBondedAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextNotifyAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextRemoveAllServiceAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextRemoveServiceAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextRespondAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextStartAdvertisingAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextStopAdvertisingAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextUnBondAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextEnableAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextDisableAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.callbackContextDisconnectAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.stringAddServiceAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.stringBondAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.stringGetAdapterInfoAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.stringInitializeAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.stringIsAdvertisingAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.stringIsBondedAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.stringNotifyAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.stringRemoveAllServiceAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.stringRemoveServiceAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.stringRespondAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.stringStartAdvertisingAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.stringStopAdvertisingAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.stringUnBondAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.stringEnableAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.stringDisableAction;
import static com.randdusing.bluetoothle.BluetoothLePlugin.stringDisconnectAction;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;

import android.os.IBinder;
import android.os.Handler;
import android.os.Build;
import android.os.ParcelUuid;

import android.util.Base64;
import android.widget.Toast;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private static String TAG = "BIBA";
    private static final int ID_SERVICE = 300;

    //API 21+ Scan and Advertise Callbacks
    private static ScanCallback scanCallback = null;
    private static AdvertiseCallback advertiseCallback = null;

    //Initialization related variables
    private static final int REQUEST_BT_ENABLE = 59627; /*Random integer*/
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 59628;
    private static final int REQUEST_LOCATION_SOURCE_SETTINGS = 59629;
    private static BluetoothAdapter bluetoothAdapter;
    private static boolean isReceiverRegistered = false;
    private static boolean isBondReceiverRegistered = false;
    private static boolean isAutoStart = false;

    //General callback variables
    private static BluetoothGattServer gattServer;
    private static boolean isAdvertising = false;

    //Store connections and all their callbacks
    private static HashMap<Object, HashMap<Object, Object>> connections;

    //Store bonds
    private static HashMap<String, CallbackContext> bonds = new HashMap<String, CallbackContext>();


    //Object keys
    private static final String keyStatus = "status";
    private static final String keyError = "error";
    private static final String keyMessage = "message";
    private static final String keyRequest = "request";
    private static final String keyStatusReceiver = "statusReceiver";
    private static final String keyName = "name";
    private static final String keyAddress = "address";
    private static final String keyRssi = "rssi";
    private static final String keyScanMode = "scanMode";
    private static final String keyMatchMode = "matchMode";
    private static final String keyMatchNum = "matchNum";
    private static final String keyCallbackType = "callbackType";
    private static final String keyAdvertisement = "advertisement";
    private static final String keyUuid = "uuid";
    private static final String keyService = "service";
    private static final String keyServices = "services";
    private static final String keyCharacteristic = "characteristic";
    private static final String keyCharacteristics = "characteristics";
    private static final String keyProperties = "properties";
    private static final String keyPermissions = "permissions";
    private static final String keyDescriptor = "descriptor";
    private static final String keyDescriptors = "descriptors";
    private static final String keyValue = "value";
    private static final String keyType = "type";
    private static final String keyIsInitialized = "isInitialized";
    private static final String keyIsEnabled = "isEnabled";
    private static final String keyIsScanning = "isScanning";
    private static final String keyIsBonded = "isBonded";
    private static final String keyIsConnected = "isConnected";
    private static final String keyIsDiscovered = "isDiscovered";
    private static final String keyIsDiscoverable = "isDiscoverable";
    private static final String keyPeripheral = "peripheral";
    private static final String keyState = "state";
    private static final String keyDiscoveredState = "discoveredState";
    private static final String keyConnectionPriority = "connectionPriority";
    private static final String keyMtu = "mtu";

    //Write Types
    private static final String writeTypeNoResponse = "noResponse";

    //Status Types
    private static final String statusEnabled = "enabled";
    private static final String statusDisabled = "disabled";
    private static final String statusScanStarted = "scanStarted";
    private static final String statusScanStopped = "scanStopped";
    private static final String statusScanResult = "scanResult";
    private static final String statusBonded = "bonded";
    private static final String statusBonding = "bonding";
    private static final String statusUnbonded = "unbonded";
    private static final String statusConnected = "connected";
    private static final String statusDisconnected = "disconnected";
    private static final String statusClosed = "closed";
    private static final String statusDiscovered = "discovered";
    private static final String statusRead = "read";
    private static final String statusSubscribed = "subscribed";
    private static final String statusSubscribedResult = "subscribedResult";
    private static final String statusUnsubscribed = "unsubscribed";
    private static final String statusWritten = "written";
    private static final String statusReadDescriptor = "readDescriptor";
    private static final String statusWrittenDescriptor = "writtenDescriptor";
    private static final String statusRssi = "rssi";
    private static final String statusConnectionPriorityRequested = "connectionPriorityRequested";
    private static final String statusMtu = "mtu";

    //Properties
    private static final String propertyBroadcast = "broadcast";
    private static final String propertyRead = "read";
    private static final String propertyWriteWithoutResponse = "writeWithoutResponse";
    private static final String propertyWrite = "write";
    private static final String propertyNotify = "notify";
    private static final String propertyIndicate = "indicate";
    private static final String propertyAuthenticatedSignedWrites = "authenticatedSignedWrites";
    private static final String propertyExtendedProperties = "extendedProperties";
    private static final String propertyNotifyEncryptionRequired = "notifyEncryptionRequired";
    private static final String propertyIndicateEncryptionRequired = "indicateEncryptionRequired";
    private static final String propertyConnectionPriorityHigh = "high";
    private static final String propertyConnectionPriorityLow = "low";
    private static final String propertyConnectionPriorityBalanced = "balanced";

    //Permissions
    private static final String permissionRead = "read";
    private static final String permissionReadEncrypted = "readEncrypted";
    private static final String permissionReadEncryptedMITM = "readEncryptedMITM";
    private static final String permissionWrite = "write";
    private static final String permissionWriteEncrypted = "writeEncrypted";
    private static final String permissionWriteEncryptedMITM = "writeEncryptedMITM";
    private static final String permissionWriteSigned = "writeSigned";
    private static final String permissionWriteSignedMITM = "writeSignedMITM";

    //Error Types
    private static final String errorInitialize = "initialize";
    private static final String errorEnable = "enable";
    private static final String errorDisable = "disable";
    private static final String errorArguments = "arguments";
    private static final String errorStartScan = "startScan";
    private static final String errorStopScan = "stopScan";
    private static final String errorBond = "bond";
    private static final String errorUnbond = "unbond";
    private static final String errorConnect = "connect";
    private static final String errorReconnect = "reconnect";
    private static final String errorDiscover = "discover";
    private static final String errorServices = "services";
    private static final String errorCharacteristics = "characteristics";
    private static final String errorDescriptors = "descriptors";
    private static final String errorRead = "read";
    private static final String errorSubscription = "subscription";
    private static final String errorWrite = "write";
    private static final String errorReadDescriptor = "readDescriptor";
    private static final String errorWriteDescriptor = "writeDescriptor";
    private static final String errorRssi = "rssi";
    private static final String errorNeverConnected = "neverConnected";
    private static final String errorIsNotDisconnected = "isNotDisconnected";
    private static final String errorIsNotConnected = "isNotConnected";
    private static final String errorIsDisconnected = "isDisconnected";
    private static final String errorService = "service";
    private static final String errorCharacteristic = "characteristic";
    private static final String errorDescriptor = "descriptor";
    private static final String errorRequestConnectionPriority = "requestConnectPriority";
    private static final String errorMtu = "mtu";

    //Error Messages
    //Initialization
    private static final String logNotEnabled = "Bluetooth not enabled";
    private static final String logNotDisabled = "Bluetooth not disabled";
    private static final String logNotInit = "Bluetooth not initialized";
    private static final String logOperationUnsupported = "Operation unsupported";
    //Scanning
    private static final String logAlreadyScanning = "Scanning already in progress";
    private static final String logScanStartFail = "Scan failed to start";
    private static final String logNotScanning = "Not scanning";
    //Bonding
    private static final String logBonded = "Device already bonded";
    private static final String logBonding = "Device already bonding";
    private static final String logUnbonded = "Device already unbonded";
    private static final String logBondFail = "Device failed to bond on return";
    private static final String logUnbondFail = "Device failed to unbond on return";
    //Connection
    private static final String logPreviouslyConnected = "Device previously connected, reconnect or close for new device";
    private static final String logConnectFail = "Connection failed";
    private static final String logNeverConnected = "Never connected to device";
    private static final String logIsNotConnected = "Device isn't connected";
    private static final String logIsNotDisconnected = "Device isn't disconnected";
    private static final String logIsDisconnected = "Device is disconnected";
    private static final String logNoAddress = "No device address";
    private static final String logNoDevice = "Device not found";
    private static final String logReconnectFail = "Reconnection to device failed";
    //Discovery
    private static final String logAlreadyDiscovering = "Already discovering device";
    private static final String logDiscoveryFail = "Unable to discover device";
    //Read/write
    private static final String logNoArgObj = "Argument object not found";
    private static final String logNoService = "Service not found";
    private static final String logNoCharacteristic = "Characteristic not found";
    private static final String logNoDescriptor = "Descriptor not found";
    private static final String logReadFail = "Unable to read";
    private static final String logReadFailReturn = "Unable to read on return";
    private static final String logSubscribeFail = "Unable to subscribe";
    private static final String logSubscribeAlready = "Already subscribed";
    private static final String logUnsubscribeFail = "Unable to unsubscribe";
    private static final String logUnsubscribeAlready = "Already unsubscribed";
    private static final String logWriteFail = "Unable to write";
    private static final String logWriteFailReturn = "Unable to write on return";
    private static final String logWriteValueNotFound = "Write value not found";
    private static final String logWriteValueNotSet = "Write value not set";
    private static final String logReadDescriptorFail = "Unable to read descriptor";
    private static final String logReadDescriptorFailReturn = "Unable to read descriptor on return";
    private static final String logWriteDescriptorNotAllowed = "Unable to write client configuration descriptor";
    private static final String logWriteDescriptorFail = "Unable to write descriptor";
    private static final String logWriteDescriptorValueNotFound = "Write descriptor value not found";
    private static final String logWriteDescriptorValueNotSet = "Write descriptor value not set";
    private static final String logWriteDescriptorFailReturn = "Descriptor not written on return";
    private static final String logRssiFail = "Unable to read RSSI";
    private static final String logRssiFailReturn = "Unable to read RSSI on return";
    //Request Connection Priority
    private static final String logRequestConnectionPriorityNull = "Request connection priority not set";
    private static final String logRequestConnectionPriorityInvalid = "Request connection priority is invalid";
    private static final String logRequestConnectionPriorityFailed = "Request connection priority failed";
    //MTU
    private static final String logMtuFail = "Unable to set MTU";
    private static final String logMtuFailReturn = "Unable to set MTU on return";

    private static final String logRequiresAPI21 = "Requires API level 21";

    private static final String operationConnect = "connect";
    private static final String operationDiscover = "discover";
    private static final String operationRssi = "rssi";
    private static final String operationRead = "read";
    private static final String operationSubscribe = "subscribe";
    private static final String operationUnsubscribe = "unsubscribe";
    private static final String operationWrite = "write";
    private static final String operationMtu = "mtu";

    private static final String baseUuidStart = "0000";
    private static final String baseUuidEnd = "-0000-1000-8000-00805F9B34FB";

    //Client Configuration UUID for notifying/indicating
    private final UUID clientConfigurationDescriptorUuid = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show();
        Log.e("BIBA", "onCreate");

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
    private String createNotificationChannel(NotificationManager notificationManager) {
        String channelId = "6969";
        String channelName = "BIBA";
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
        Log.e("BIBA", "onStartCommand");


        String action = intent != null ? intent.getAction() : null;
        if (null == action) {
            Toast.makeText(this, "ZONT Метка запустилась.", Toast.LENGTH_LONG).show();
            Log.e("BIBA", "AutoAction");
            isAutoStart = true;

            Log.e("BIBA", "InitializeAction");
            initialize(init, isAutoStart);
            createAdvertiseCallback(isAutoStart);

            Log.e("BIBA", "AddServiceAction");
            addServiceAction(service, isAutoStart);

            Log.e("BIBA", "StartAdvertisingAction");
            startAdvertisingAction(params_advertising, isAutoStart);


        } else if (stringEnableAction.equals(action)) {
            Log.e("BIBA", "stringEnableAction");
            Log.e(TAG, String.format("%s", callbackContextEnableAction));
            enableAction(callbackContextEnableAction);
        } else if (stringDisableAction.equals(action)) {
            Log.e("BIBA", "stringDisableAction");
            Log.e(TAG, String.format("%s", callbackContextDisableAction));
            disableAction(callbackContextDisableAction);
        } else if (stringDisconnectAction.equals(action)) {
            Log.e("BIBA", "stringDisconnectAction");
            Log.e(TAG, String.valueOf(argsDisconnectAction));
            Log.e(TAG, String.format("%s", callbackContextDisconnectAction));
            disconnectAction(argsDisconnectAction, callbackContextDisconnectAction);
        } else if (stringInitializeAction.equals(action)) {
            Log.e("BIBA", "stringInitializeAction");
            Log.e(TAG, String.valueOf(argsInitializeAction));
            Log.e(TAG, String.format("%s", callbackContextInitializeAction));
            initialize(argsInitializeAction, isAutoStart, callbackContextInitializeAction);
            createAdvertiseCallback(isAutoStart);
        } else if (stringAddServiceAction.equals(action)) {
            Log.e("BIBA", "stringAddServiceAction");
            Log.e(TAG, String.valueOf(argsAddServiceAction));
            Log.e(TAG, String.format("%s", callbackContextAddServiceAction));
            addServiceAction(argsAddServiceAction, isAutoStart, callbackContextAddServiceAction);
        } else if (stringStartAdvertisingAction.equals(action)) {
            Log.e("BIBA", "stringStartAdvertisingAction");
            Log.e(TAG, String.valueOf(argsStartAdvertisingAction));
            Log.e(TAG, String.format("%s", callbackContextStartAdvertisingAction));
            startAdvertisingAction(argsStartAdvertisingAction, isAutoStart, callbackContextStartAdvertisingAction);
        } else if (stringRespondAction.equals(action)) {
            Log.e("BIBA", "stringRespondAction");
            Log.e(TAG, String.valueOf(argsRespondAction));
            Log.e(TAG, String.format("%s", callbackContextRespondAction));
            respondAction(argsRespondAction, callbackContextRespondAction);
        } else if (stringNotifyAction.equals(action)) {
            Log.e("BIBA", "stringNotifyAction");
            Log.e(TAG, String.valueOf(argsNotifyAction));
            Log.e(TAG, String.format("%s", callbackContextNotifyAction));
            notifyAction(argsNotifyAction, callbackContextNotifyAction);
        } else if (stringRemoveServiceAction.equals(action)) {
            Log.e("BIBA", "stringRemoveServiceAction");
            Log.e(TAG, String.valueOf(argsRemoveServiceAction));
            Log.e(TAG, String.format("%s", callbackContextRemoveServiceAction));
            removeServiceAction(argsRemoveServiceAction, callbackContextRemoveServiceAction);
        } else if (stringRemoveAllServiceAction.equals(action)) {
            Log.e("BIBA", "stringRemoveAllServiceAction");
            Log.e(TAG, String.valueOf(argsRemoveAllServiceAction));
            Log.e(TAG, String.format("%s", callbackContextRemoveAllServiceAction));
            removeAllServicesAction(argsRemoveAllServiceAction, callbackContextRemoveAllServiceAction);
        } else if (stringStopAdvertisingAction.equals(action)) {
            Log.e("BIBA", "stringStopAdvertisingAction");
            Log.e(TAG, String.valueOf(argsStopAdvertisingAction));
            Log.e(TAG, String.format("%s", callbackContextStopAdvertisingAction));
            stopAdvertisingAction(argsStopAdvertisingAction, callbackContextStopAdvertisingAction);
        } else if (stringIsAdvertisingAction.equals(action)) {
            Log.e("BIBA", "stringIsAdvertisingAction");
            Log.e(TAG, String.format("%s", callbackContextIsAdvertisingAction));
            isAdvertisingAction(callbackContextIsAdvertisingAction);
        } else if (stringGetAdapterInfoAction.equals(action)) {
            Log.e("BIBA", "stringGetAdapterInfoAction");
            Log.e(TAG, String.format("%s", callbackContextGetAdapterInfoAction));
            getAdapterInfoAction(callbackContextGetAdapterInfoAction);
        } else if (stringBondAction.equals(action)) {
            Log.e("BIBA", "stringBondAction");
            Log.e(TAG, String.valueOf(argsBondAction));
            Log.e(TAG, String.format("%s", callbackContextBondAction));
            bondAction(argsBondAction, callbackContextBondAction);
        } else if (stringUnBondAction.equals(action)) {
            Log.e("BIBA", "stringUnBondAction");
            Log.e(TAG, String.valueOf(argsUnBondAction));
            Log.e(TAG, String.format("%s", callbackContextUnBondAction));
            unbondAction(argsUnBondAction, callbackContextUnBondAction);
        } else if (stringIsBondedAction.equals(action)) {
            Log.e("BIBA", "stringIsBondedAction");
            Log.e(TAG, String.valueOf(argsIsBondedAction));
            Log.e(TAG, String.format("%s", callbackContextIsBondedAction));
            isBondedAction(argsIsBondedAction, callbackContextIsBondedAction);
        }

//        initialize(argsInitializeAction, callbackContextService);
//        initializePeripheral(callbackContextService);
//        addServiceAction(argsService, callbackContextService);
//        startAdvertisingAction(callbackContextService);
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

    private void disconnectAction(JSONArray args, CallbackContext callbackContext) {
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

        HashMap<Object, Object> connection = wasNeverConnected(address, callbackContext);
        if (connection == null) {
            return;
        }

        BluetoothGatt bluetoothGatt = (BluetoothGatt) connection.get(keyPeripheral);
        BluetoothDevice device = bluetoothGatt.getDevice();

        if (isDisconnected(connection, device, callbackContext)) {
            return;
        }

        int state = Integer.valueOf(connection.get(keyState).toString());

        JSONObject returnObj = new JSONObject();

        //Return disconnecting status and keep callback
        addDevice(returnObj, device);

        //If it's connecting, cancel attempt and return disconnect
        if (state == BluetoothProfile.STATE_CONNECTING) {
            addProperty(returnObj, keyStatus, statusDisconnected);
            connection.put(keyState, BluetoothProfile.STATE_DISCONNECTED);

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);

            connection.remove(operationConnect);
        } else {
            //Very unlikely that this is DISCONNECTING
            connection.put(operationConnect, callbackContext);
        }

        bluetoothGatt.disconnect();
    }

    private void initialize(JSONArray args, Boolean isAutoStart, CallbackContext... callbackContextInitializeAction) {
        //Save init callback
        JSONObject returnObj;
        if (bluetoothAdapter != null) {
            returnObj = new JSONObject();
            PluginResult pluginResult;

            if (bluetoothAdapter.isEnabled()) {
                addProperty(returnObj, keyStatus, statusEnabled);

                pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
                pluginResult.setKeepCallback(true);
                callbackContextInitializeAction[0].sendPluginResult(pluginResult);
            } else {
                addProperty(returnObj, keyStatus, statusDisabled);
                addProperty(returnObj, keyMessage, logNotEnabled);

                pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
                pluginResult.setKeepCallback(true);
                callbackContextInitializeAction[0].sendPluginResult(pluginResult);
            }
            return;
        }

        JSONObject obj = getArgsObject(args);
        if (obj != null && getStatusReceiver(obj)) {
            //Add a receiver to pick up when Bluetooth state changes
            this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            isReceiverRegistered = true;
        }

        //Get Bluetooth adapter via Bluetooth Manager
        BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();


        connections = new HashMap<Object, HashMap<Object, Object>>();

        returnObj = new JSONObject();

        //If it's already enabled,
        if (bluetoothAdapter.isEnabled() && !isAutoStart) {
            addProperty(returnObj, keyStatus, statusEnabled);
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
            pluginResult.setKeepCallback(true);
            callbackContextInitializeAction[0].sendPluginResult(pluginResult);
        }

//        boolean request = false;
//        if (obj != null) {
//            request = getRequest(obj);
//        }
//
//        //Request user to enable Bluetooth надо сделать невидимое активити и там сделать реквест
//        if (request) {
//            //Request Bluetooth to be enabled
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            cordova.startActivityForResult(this, enableBtIntent, REQUEST_BT_ENABLE);
//        } else {
//            //No request, so send back not enabled
//            addProperty(returnObj, keyStatus, statusDisabled);
//            addProperty(returnObj, keyMessage, logNotEnabled);
//            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
//            pluginResult.setKeepCallback(true);
//            initCallbackContext.sendPluginResult(pluginResult);
//        }


        /// ---------------------------------------------------------------
        //  Инициализацию bluetooth объеденил с initializePeripheralAction
        /// ---------------------------------------------------------------

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP && !isAutoStart) {
            returnObj = new JSONObject();
            addProperty(returnObj, "error", "initializePeripheral");
            addProperty(returnObj, "message", logOperationUnsupported);

            callbackContextInitializeAction[0].error(returnObj);
            return;
        }


        //Re-opening Gatt server seems to cause some issues
        if (gattServer == null) {
            gattServer = bluetoothManager.openGattServer(this.getApplicationContext(), bluetoothGattServerCallback);
        }
        if (!isAutoStart) {
            returnObj = new JSONObject();
            addProperty(returnObj, keyStatus, statusEnabled);

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
            pluginResult.setKeepCallback(true);
            callbackContextInitializeAction[0].sendPluginResult(pluginResult);
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

        if (isAutoStart) {
            return;
        }

        if (result) {
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, "service", uuid.toString());
            addProperty(returnObj, "status", "serviceAdded");

            callbackContext[0].success(returnObj);
        } else {
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, "service", uuid.toString());
            addProperty(returnObj, "error", "service");
            addProperty(returnObj, "message", "Failed to add service");

            callbackContext[0].error(returnObj);
        }
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

    private void startAdvertisingAction(JSONArray args, Boolean isAutoStart, CallbackContext... callbackContext) {
        JSONObject obj = getArgsObject(args);
        if (!isAutoStart && isNotArgsObject(obj, callbackContext[0])) {
            return;
        }
//        String name = getAdapterName(obj);
        //set adapter name
        bluetoothAdapter.setName(getAdapterName(obj));

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

        dataBuilder.setIncludeDeviceName(obj.optBoolean("includeDeviceName", true));

        dataBuilder.setIncludeTxPowerLevel(obj.optBoolean("includeTxPowerLevel", true));

        AdvertiseData advertiseData = dataBuilder.build();

        advertiser.startAdvertising(advertiseSettings, advertiseData, advertiseCallback);
    }

    private void stopAdvertisingAction(JSONArray args, CallbackContext callbackContext) {
        BluetoothLeAdvertiser advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (advertiser == null || !bluetoothAdapter.isMultipleAdvertisementSupported()) {
            JSONObject returnObj = new JSONObject();

            addProperty(returnObj, "error", "startAdvertising");
            addProperty(returnObj, "message", "Advertising isn't supported");

            callbackContext.error(returnObj);
            return;
        }

        advertiser.stopAdvertising(advertiseCallback);

        JSONObject returnObj = new JSONObject();
        addProperty(returnObj, "status", "advertisingStopped");
        callbackContext.success(returnObj);
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
        if (!result) {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, "error", "notify");
            addProperty(returnObj, "message", "Failed to notify");
            callbackContext.error(returnObj);
        }
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
            Log.d("BLE", e.getMessage());
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

    private void createAdvertiseCallback(Boolean isAutoStart) {
        advertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartFailure(int errorCode) {
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
            if (callbackContextInitializeAction == null && !isAutoStart) {
                return;
            }

            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                JSONObject returnObj = new JSONObject();
                PluginResult pluginResult;

                switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                    case BluetoothAdapter.STATE_OFF:
                        if (!isAutoStart) {
                            addProperty(returnObj, keyStatus, statusDisabled);
                            addProperty(returnObj, keyMessage, logNotEnabled);

                            connections = new HashMap<Object, HashMap<Object, Object>>();

                            pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
                            pluginResult.setKeepCallback(true);
                            callbackContextInitializeAction.sendPluginResult(pluginResult);
                        } else {
                            android.widget.Toast.makeText(context, "Включите Bluetooth, чтобы ZONT Метка заработала", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case BluetoothAdapter.STATE_ON:
                        if (isAutoStart) {
                            startAdvertisingAction(params_advertising, isAutoStart);
                            android.widget.Toast.makeText(context, "ZONT Метка снова активна", Toast.LENGTH_SHORT).show();
                        } else {
                            addProperty(returnObj, keyStatus, statusEnabled);

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

    private static boolean isNotService(BluetoothGattService service, BluetoothDevice device, CallbackContext callbackContext) {
        if (service != null) {
            return false;
        }

        JSONObject returnObj = new JSONObject();

        addProperty(returnObj, keyError, errorService);
        addProperty(returnObj, keyMessage, logNoService);

        addDevice(returnObj, device);

        callbackContext.error(returnObj);

        return true;
    }

    private static boolean isNotCharacteristic(BluetoothGattCharacteristic characteristic, BluetoothDevice device, CallbackContext callbackContext) {
        if (characteristic != null) {
            return false;
        }

        JSONObject returnObj = new JSONObject();

        addProperty(returnObj, keyError, errorCharacteristic);
        addProperty(returnObj, keyMessage, logNoCharacteristic);

        addDevice(returnObj, device);

        callbackContext.error(returnObj);

        return true;
    }

    private static boolean isNotDescriptor(BluetoothGattDescriptor descriptor, BluetoothDevice device, CallbackContext callbackContext) {
        if (descriptor != null) {
            return false;
        }

        JSONObject returnObj = new JSONObject();

        addProperty(returnObj, keyError, errorDescriptor);
        addProperty(returnObj, keyMessage, logNoDescriptor);

        addDevice(returnObj, device);

        callbackContext.error(returnObj);

        return true;
    }

    private static boolean isNotDisconnected(HashMap<Object, Object> connection, BluetoothDevice device, CallbackContext callbackContext) {
        int state = Integer.valueOf(connection.get(keyState).toString());

        //Determine whether the device is currently connected including connecting and disconnecting
        //Certain actions like connect and reconnect can only be done while completely disconnected
        if (state == BluetoothProfile.STATE_DISCONNECTED) {
            return false;
        }

        JSONObject returnObj = new JSONObject();

        addProperty(returnObj, keyError, errorIsNotDisconnected);
        addProperty(returnObj, keyMessage, logIsNotDisconnected);

        addDevice(returnObj, device);

        callbackContext.error(returnObj);

        return true;
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
    private static void addProperty(JSONObject obj, String key, Object value) {
        //Believe exception only occurs when adding duplicate keys, so just ignore it
        try {
            if (value == null) {
                obj.put(key, JSONObject.NULL);
            } else {
                obj.put(key, value);
            }
        } catch (JSONException e) {
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

    private static UUID[] getServiceUuids(JSONObject obj) {
        if (obj == null) {
            return new UUID[]{};
        }

        JSONArray array = obj.optJSONArray(keyServices);

        if (array == null) {
            return new UUID[]{};
        }

        //Create temporary array list for building array of UUIDs
        ArrayList<UUID> arrayList = new ArrayList<UUID>();

        //Iterate through the UUID strings
        for (int i = 0; i < array.length(); i++) {
            String value = array.optString(i, null);

            if (value == null) {
                continue;
            }

            if (value.length() == 4) {
                value = baseUuidStart + value + baseUuidEnd;
            }

            //Try converting string to UUID and add to list
            try {
                UUID uuid = UUID.fromString(value);
                arrayList.add(uuid);
            } catch (Exception ex) {
            }
        }

        UUID[] uuids = new UUID[arrayList.size()];
        uuids = arrayList.toArray(uuids);
        return uuids;
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

    private static int getWriteType(JSONObject obj) {
        String writeType = obj.optString(keyType, null);

        if (writeType == null || !writeType.equals(writeTypeNoResponse)) {
            return BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
        }
        return BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;
    }

    private static int getMtu(JSONObject obj) {
        int mtu = obj.optInt(keyMtu);

        if (mtu == 0) {
            return 23;
        }

        return mtu;
    }

    private static JSONObject getDiscovery(BluetoothGatt bluetoothGatt) {
        JSONObject deviceObject = new JSONObject();

        BluetoothDevice device = bluetoothGatt.getDevice();

        addProperty(deviceObject, keyStatus, statusDiscovered);

        addDevice(deviceObject, device);

        JSONArray servicesArray = new JSONArray();

        List<BluetoothGattService> services = bluetoothGatt.getServices();

        for (BluetoothGattService service : services) {
            JSONObject serviceObject = new JSONObject();

            addProperty(serviceObject, keyUuid, formatUuid(service.getUuid()));

            JSONArray characteristicsArray = new JSONArray();

            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();

            for (BluetoothGattCharacteristic characteristic : characteristics) {
                JSONObject characteristicObject = new JSONObject();

                addProperty(characteristicObject, keyUuid, formatUuid(characteristic.getUuid()));
                addProperty(characteristicObject, keyProperties, getProperties(characteristic));
                addProperty(characteristicObject, keyPermissions, getPermissions(characteristic));

                JSONArray descriptorsArray = new JSONArray();

                List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();

                for (BluetoothGattDescriptor descriptor : descriptors) {
                    JSONObject descriptorObject = new JSONObject();

                    addProperty(descriptorObject, keyUuid, formatUuid(descriptor.getUuid()));
                    addProperty(descriptorObject, keyPermissions, getPermissions(descriptor));

                    descriptorsArray.put(descriptorObject);
                }

                addProperty(characteristicObject, keyDescriptors, descriptorsArray);

                characteristicsArray.put(characteristicObject);
            }

            addProperty(serviceObject, keyCharacteristics, characteristicsArray);

            servicesArray.put(serviceObject);
        }

        addProperty(deviceObject, keyServices, servicesArray);

        return deviceObject;
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
            } else {
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
            //Log.d("BLE", "execute write");
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
