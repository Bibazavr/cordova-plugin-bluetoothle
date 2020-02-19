let bluetoothleName = "BluetoothLePlugin";
let bluetoothle = {
    connectedDevices: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, bluetoothleName, "connectedDevices", []);
    },
    initialize: function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, bluetoothleName, "initialize", [params]);
    },
    enable: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, bluetoothleName, "enable", []);
    },
    disable: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, bluetoothleName, "disable", []);
    },
    getAdapterInfo: function (successCallback) {
        cordova.exec(successCallback, successCallback, bluetoothleName, "getAdapterInfo", []);
    },
    bond: function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, bluetoothleName, "bond", [params]);
    },
    unbond: function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, bluetoothleName, "unbond", [params]);
    },
    isBonded: function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, bluetoothleName, "isBonded", [params]);
    },
    disconnect: function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, bluetoothleName, "disconnect", [params]);
    },
    addService: function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, bluetoothleName, "addService", [params]);
    },
    removeService: function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, bluetoothleName, "removeService", [params]);
    },
    removeAllServices: function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, bluetoothleName, "removeAllServices", [params]);
    },
    startAdvertising: function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, bluetoothleName, "startAdvertising", [params]);
    },
    stopAdvertising: function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, bluetoothleName, "stopAdvertising", [params]);
    },
    isAdvertising: function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, bluetoothleName, "isAdvertising", []);
    },
    respond: function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, bluetoothleName, "respond", [params]);
    },
    notify: function (successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, bluetoothleName, "notify", [params]);
    },
    encodedStringToBytes: function (string) {
        let data = atob(string);
        let bytes = new Uint8Array(data.length);
        for (let i = 0; i < bytes.length; i++) {
            bytes[i] = data.charCodeAt(i);
        }
        return bytes;
    },
    bytesToEncodedString: function (bytes) {
        return btoa(String.fromCharCode.apply(null, bytes));
    },
    stringToBytes: function (string) {
        let bytes = new ArrayBuffer(string.length * 2);
        let bytesUint16 = new Uint16Array(bytes);
        for (let i = 0; i < string.length; i++) {
            bytesUint16[i] = string.charCodeAt(i);
        }
        return new Uint8Array(bytesUint16);
    },
    bytesToString: function (bytes) {
        return String.fromCharCode.apply(null, new Uint16Array(bytes));
    },
    bytesToHex: function (bytes) {
        let string = [];
        for (let i = 0; i < bytes.length; i++) {
            string.push("0x" + ("0" + (bytes[i].toString(16))).substr(-2).toUpperCase());
        }
        return string.join(" ");
    },
    SCAN_MODE_OPPORTUNISTIC: -1,
    SCAN_MODE_LOW_POWER: 0,
    SCAN_MODE_BALANCED: 1,
    SCAN_MODE_LOW_LATENCY: 2,
    MATCH_NUM_ONE_ADVERTISEMENT: 1,
    MATCH_NUM_FEW_ADVERTISEMENT: 2,
    MATCH_NUM_MAX_ADVERTISEMENT: 3,
    MATCH_MODE_AGGRESSIVE: 1,
    MATCH_MODE_STICKY: 2,
    CALLBACK_TYPE_ALL_MATCHES: 1,
    CALLBACK_TYPE_FIRST_MATCH: 2,
    CALLBACK_TYPE_MATCH_LOST: 4,
};
module.exports = bluetoothle;
