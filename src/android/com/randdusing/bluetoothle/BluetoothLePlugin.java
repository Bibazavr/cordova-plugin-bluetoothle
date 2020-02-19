package com.randdusing.bluetoothle;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import android.content.Context;
import android.content.Intent;
import android.os.Build;


import org.json.JSONArray;
import org.json.JSONException;


import static com.randdusing.bluetoothle.Actions.*;

public class BluetoothLePlugin extends CordovaPlugin {
    public BluetoothLePlugin() {
    }

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        //Execute the specified action
        if ("initialize".equals(action)) {
            initializeAction(args, callbackContext);
        } else if ("enable".equals(action)) {
            enableAction(callbackContext);
        } else if ("getAdapterInfo".equals(action)) {
            getAdapterInfoAction(callbackContext);
        } else if ("disable".equals(action)) {
            disableAction(callbackContext);
        } else if ("bond".equals(action)) {
            bondAction(args, callbackContext);
        } else if ("unbond".equals(action)) {
            unbondAction(args, callbackContext);
        } else if ("disconnect".equals(action)) {
            disconnectAction(args, callbackContext);
        } else if ("isBonded".equals(action)) {
            isBondedAction(args, callbackContext);
        } else if ("addService".equals(action)) {
            addServiceAction(args, callbackContext);
        } else if ("removeService".equals(action)) {
            removeServiceAction(args, callbackContext);
        } else if ("removeAllServices".equals(action)) {
            removeAllServicesAction(args, callbackContext);
        } else if ("startAdvertising".equals(action)) {
            startAdvertisingAction(args, callbackContext);
        } else if ("stopAdvertising".equals(action)) {
            stopAdvertisingAction(callbackContext);
        } else if ("isAdvertising".equals(action)) {
            isAdvertisingAction(callbackContext);
        } else if ("respond".equals(action)) {
            respondAction(args, callbackContext);
        } else if ("notify".equals(action)) {
            notifyAction(args, callbackContext);
        } else {
            return false;
        }
        return true;
    }


    static CallbackContext callbackContextInitializeAction;
    static JSONArray argsInitializeAction;

    private void initializeAction(JSONArray args, CallbackContext callbackContext) {
        callbackContextInitializeAction = callbackContext;
        argsInitializeAction = args;

        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, Background.class);
        intent.setAction(stringInitializeAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }


    static CallbackContext callbackContextAddServiceAction;
    static JSONArray argsAddServiceAction;

    private void addServiceAction(JSONArray args, CallbackContext callbackContext) {
        callbackContextAddServiceAction = callbackContext;
        argsAddServiceAction = args;

        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, Background.class);
        intent.setAction(stringAddServiceAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }


    static CallbackContext callbackContextRemoveServiceAction;
    static JSONArray argsRemoveServiceAction;

    private void removeServiceAction(JSONArray args, CallbackContext callbackContext) {
        callbackContextRemoveServiceAction = callbackContext;
        argsRemoveServiceAction = args;

        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, Background.class);
        intent.setAction(stringRemoveServiceAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }


    static CallbackContext callbackContextRemoveAllServiceAction;
    static JSONArray argsRemoveAllServiceAction;

    private void removeAllServicesAction(JSONArray args, CallbackContext callbackContext) {
        callbackContextRemoveAllServiceAction = callbackContext;
        argsRemoveAllServiceAction = args;

        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, Background.class);
        intent.setAction(stringRemoveAllServiceAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }


    static CallbackContext callbackContextStartAdvertisingAction;
    static JSONArray argsStartAdvertisingAction;

    private void startAdvertisingAction(JSONArray args, CallbackContext callbackContext) {
        callbackContextStartAdvertisingAction = callbackContext;
        argsStartAdvertisingAction = args;

        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, Background.class);
        intent.setAction(stringStartAdvertisingAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }


    static CallbackContext callbackContextStopAdvertisingAction;

    private void stopAdvertisingAction(CallbackContext callbackContext) {
        callbackContextStopAdvertisingAction = callbackContext;

        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, Background.class);
        intent.setAction(stringStopAdvertisingAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }


    static CallbackContext callbackContextIsAdvertisingAction;

    private void isAdvertisingAction(CallbackContext callbackContext) {
        callbackContextIsAdvertisingAction = callbackContext;

        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, Background.class);
        intent.setAction(stringIsAdvertisingAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }


    static CallbackContext callbackContextRespondAction;
    static JSONArray argsRespondAction;

    private void respondAction(JSONArray args, CallbackContext callbackContext) {
        callbackContextRespondAction = callbackContext;
        argsRespondAction = args;

        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, Background.class);
        intent.setAction(stringRespondAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }


    static CallbackContext callbackContextNotifyAction;
    static JSONArray argsNotifyAction;

    private void notifyAction(JSONArray args, CallbackContext callbackContext) {
        callbackContextNotifyAction = callbackContext;
        argsNotifyAction = args;

        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, Background.class);
        intent.setAction(stringNotifyAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }


    static CallbackContext callbackContextGetAdapterInfoAction;

    /**
     * Retrieves a minimal set of adapter details
     * (address, name, initialized state, enabled state, scanning state, discoverable state)
     */
    private void getAdapterInfoAction(CallbackContext callbackContext) {
        callbackContextGetAdapterInfoAction = callbackContext;

        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, Background.class);
        intent.setAction(stringGetAdapterInfoAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }


    static CallbackContext callbackContextEnableAction;

    private void enableAction(CallbackContext callbackContext) {
        callbackContextEnableAction = callbackContext;

        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, Background.class);
        intent.setAction(stringEnableAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    static CallbackContext callbackContextDisableAction;

    private void disableAction(CallbackContext callbackContext) {
        callbackContextDisableAction = callbackContext;

        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, Background.class);
        intent.setAction(stringDisableAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }


    static CallbackContext callbackContextBondAction;
    static JSONArray argsBondAction;

    private void bondAction(JSONArray args, CallbackContext callbackContext) {
        callbackContextBondAction = callbackContext;
        argsBondAction = args;

        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, Background.class);
        intent.setAction(stringBondAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }


    static CallbackContext callbackContextUnBondAction;
    static JSONArray argsUnBondAction;

    private void unbondAction(JSONArray args, CallbackContext callbackContext) {
        callbackContextUnBondAction = callbackContext;
        argsUnBondAction = args;

        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, Background.class);
        intent.setAction(stringUnBondAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }


    static CallbackContext callbackContextDisconnectAction;
    static JSONArray argsDisconnectAction;

    private void disconnectAction(JSONArray args, CallbackContext callbackContext) {
        callbackContextDisconnectAction = callbackContext;
        argsDisconnectAction = args;

        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, Background.class);
        intent.setAction(stringDisconnectAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }


    static CallbackContext callbackContextIsBondedAction;
    static JSONArray argsIsBondedAction;

    private void isBondedAction(JSONArray args, CallbackContext callbackContext) {
        callbackContextIsBondedAction = callbackContext;
        argsIsBondedAction = args;

        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, Background.class);
        intent.setAction(stringIsBondedAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
