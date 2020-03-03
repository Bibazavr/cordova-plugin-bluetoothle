package com.microline.bluetoothle;

import org.json.JSONArray;
import org.json.JSONException;


public class AutoStartParams {
    ///--------------------------------
    // BIBA параметры Для запуска с автостарта
    ///--------------------------------
    static JSONArray init = null;
    static JSONArray service = null;
    static JSONArray params_advertising = null;

    static {
        try {
            init = new JSONArray("[{ " +
                    "\"request\": \"true\"," +
                    "\"statusReceiver\": \"true\"," +
                    "\"restoreKey\": \"ZONT\"" +
                    "}]");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            params_advertising = new JSONArray("[{ " +
                    "\"service\": \"6E400001-B5A3-F393-E0A9-E50E24DCCA9E\"," +  //Android
                    "\"name\": \"Biba\"" +
                    "}]");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            service = new JSONArray("[{ " +
                    "\"service\":\"6E400001-B5A3-F393-E0A9-E50E24DCCA9E\"," +
                    "\"characteristics\": [" +
                    "{" +
                    "\"uuid\": \"6E400002-B5A3-F393-E0A9-E50E24DCCA9E\"," +
                    "\"permissions\":" + "{ " +
                    "\"read\": \"true\", \"write\":\"true\",\"readEncryptionRequired\":\"true\", \"writeEncryptionRequired\":\"true\"" +
                    "}," +
                    "\"properties\": {" +
                    "\"read\": \"true\"," +
                    "\"writeWithoutResponse\":\"true\"," +
                    "\"write\":\"true\"," +
                    "\"notify\":\"true\"," +
                    "\"indicate\":\"true\"," +
                    "\"authenticatedSignedWrites\":\"true\"," +
                    "\"notifyEncryptionRequired\":\"true\"," +
                    "\"indicateEncryptionRequired\":\"true\"" +
                    "}" +
                    "}," +
                    "{" +
                    "\"uuid\": \"6E400003-B5A3-F393-E0A9-E50E24DCCA9E\"," +
                    "\"permissions\":" + "{ " +
                    "\"read\": \"true\", \"write\":\"true\",\"readEncryptionRequired\":\"true\", \"writeEncryptionRequired\":\"true\"" +
                    "}," +
                    "\"properties\": {" +
                    "\"read\": \"true\"," +
                    "\"writeWithoutResponse\":\"true\"," +
                    "\"write\":\"true\"," +
                    "\"notify\":\"true\"," +
                    "\"indicate\":\"true\"," +
                    "\"authenticatedSignedWrites\":\"true\"," +
                    "\"notifyEncryptionRequired\":\"true\"," +
                    "\"indicateEncryptionRequired\":\"true\"" +
                    "}" +
                    "}" +
                    "]" +
                    "}]");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    ///--------------------------------
}