package com.randdusing.bluetoothle;

import java.util.UUID;

public class Constants {
    //Initialization related variables
    static final int REQUEST_BT_ENABLE = 59627; /*Random integer*/
    static final int REQUEST_ACCESS_COARSE_LOCATION = 59628;
    static final int REQUEST_LOCATION_SOURCE_SETTINGS = 59629;

    //Object keys
    static final String keyStatus = "status";
    static final String keyError = "error";
    static final String keyMessage = "message";
    static final String keyRequest = "request";
    static final String keyStatusReceiver = "statusReceiver";
    static final String keyName = "name";
    static final String keyAddress = "address";
    static final String keyRssi = "rssi";
    static final String keyScanMode = "scanMode";
    static final String keyMatchMode = "matchMode";
    static final String keyMatchNum = "matchNum";
    static final String keyCallbackType = "callbackType";
    static final String keyAdvertisement = "advertisement";
    static final String keyUuid = "uuid";
    static final String keyService = "service";
    static final String keyServices = "services";
    static final String keyCharacteristic = "characteristic";
    static final String keyCharacteristics = "characteristics";
    static final String keyProperties = "properties";
    static final String keyPermissions = "permissions";
    static final String keyDescriptor = "descriptor";
    static final String keyDescriptors = "descriptors";
    static final String keyValue = "value";
    static final String keyType = "type";
    static final String keyIsInitialized = "isInitialized";
    static final String keyIsEnabled = "isEnabled";
    static final String keyIsScanning = "isScanning";
    static final String keyIsBonded = "isBonded";
    static final String keyIsConnected = "isConnected";
    static final String keyIsDiscovered = "isDiscovered";
    static final String keyIsDiscoverable = "isDiscoverable";
    static final String keyPeripheral = "peripheral";
    static final String keyState = "state";
    static final String keyDiscoveredState = "discoveredState";
    static final String keyConnectionPriority = "connectionPriority";
    static final String keyMtu = "mtu";

    //Write Types
    static final String writeTypeNoResponse = "noResponse";

    //Status Types
    static final String statusEnabled = "enabled";
    static final String statusDisabled = "disabled";
    static final String statusScanStarted = "scanStarted";
    static final String statusScanStopped = "scanStopped";
    static final String statusScanResult = "scanResult";
    static final String statusBonded = "bonded";
    static final String statusBonding = "bonding";
    static final String statusUnbonded = "unbonded";
    static final String statusConnected = "connected";
    static final String statusDisconnected = "disconnected";
    static final String statusClosed = "closed";
    static final String statusDiscovered = "discovered";
    static final String statusRead = "read";
    static final String statusSubscribed = "subscribed";
    static final String statusSubscribedResult = "subscribedResult";
    static final String statusUnsubscribed = "unsubscribed";
    static final String statusWritten = "written";
    static final String statusReadDescriptor = "readDescriptor";
    static final String statusWrittenDescriptor = "writtenDescriptor";
    static final String statusRssi = "rssi";
    static final String statusConnectionPriorityRequested = "connectionPriorityRequested";
    static final String statusMtu = "mtu";

    //Properties
    static final String propertyBroadcast = "broadcast";
    static final String propertyRead = "read";
    static final String propertyWriteWithoutResponse = "writeWithoutResponse";
    static final String propertyWrite = "write";
    static final String propertyNotify = "notify";
    static final String propertyIndicate = "indicate";
    static final String propertyAuthenticatedSignedWrites = "authenticatedSignedWrites";
    static final String propertyExtendedProperties = "extendedProperties";
    static final String propertyNotifyEncryptionRequired = "notifyEncryptionRequired";
    static final String propertyIndicateEncryptionRequired = "indicateEncryptionRequired";
    static final String propertyConnectionPriorityHigh = "high";
    static final String propertyConnectionPriorityLow = "low";
    static final String propertyConnectionPriorityBalanced = "balanced";

    //Permissions
    static final String permissionRead = "read";
    static final String permissionReadEncrypted = "readEncrypted";
    static final String permissionReadEncryptedMITM = "readEncryptedMITM";
    static final String permissionWrite = "write";
    static final String permissionWriteEncrypted = "writeEncrypted";
    static final String permissionWriteEncryptedMITM = "writeEncryptedMITM";
    static final String permissionWriteSigned = "writeSigned";
    static final String permissionWriteSignedMITM = "writeSignedMITM";

    //Error Types
    static final String errorInitialize = "initialize";
    static final String errorEnable = "enable";
    static final String errorDisable = "disable";
    static final String errorArguments = "arguments";
    static final String errorStartScan = "startScan";
    static final String errorStopScan = "stopScan";
    static final String errorBond = "bond";
    static final String errorUnbond = "unbond";
    static final String errorConnect = "connect";
    static final String errorReconnect = "reconnect";
    static final String errorDiscover = "discover";
    static final String errorServices = "services";
    static final String errorCharacteristics = "characteristics";
    static final String errorDescriptors = "descriptors";
    static final String errorRead = "read";
    static final String errorSubscription = "subscription";
    static final String errorWrite = "write";
    static final String errorReadDescriptor = "readDescriptor";
    static final String errorWriteDescriptor = "writeDescriptor";
    static final String errorRssi = "rssi";
    static final String errorNeverConnected = "neverConnected";
    static final String errorIsNotDisconnected = "isNotDisconnected";
    static final String errorIsNotConnected = "isNotConnected";
    static final String errorIsDisconnected = "isDisconnected";
    static final String errorService = "service";
    static final String errorCharacteristic = "characteristic";
    static final String errorDescriptor = "descriptor";
    static final String errorRequestConnectionPriority = "requestConnectPriority";
    static final String errorMtu = "mtu";

    //Error Messages
    //Initialization
    static final String logNotEnabled = "Bluetooth not enabled";
    static final String logNotDisabled = "Bluetooth not disabled";
    static final String logNotInit = "Bluetooth not initialized";
    static final String logOperationUnsupported = "Operation unsupported";
    //Scanning
    static final String logAlreadyScanning = "Scanning already in progress";
    static final String logScanStartFail = "Scan failed to start";
    static final String logNotScanning = "Not scanning";
    //Bonding
    static final String logBonded = "Device already bonded";
    static final String logBonding = "Device already bonding";
    static final String logUnbonded = "Device already unbonded";
    static final String logBondFail = "Device failed to bond on return";
    static final String logUnbondFail = "Device failed to unbond on return";
    //Connection
    static final String logPreviouslyConnected = "Device previously connected, reconnect or close for new device";
    static final String logConnectFail = "Connection failed";
    static final String logNeverConnected = "Never connected to device";
    static final String logIsNotConnected = "Device isn't connected";
    static final String logIsNotDisconnected = "Device isn't disconnected";
    static final String logIsDisconnected = "Device is disconnected";
    static final String logNoAddress = "No device address";
    static final String logNoDevice = "Device not found";
    static final String logReconnectFail = "Reconnection to device failed";
    //Discovery
    static final String logAlreadyDiscovering = "Already discovering device";
    static final String logDiscoveryFail = "Unable to discover device";
    //Read/write
    static final String logNoArgObj = "Argument object not found";
    static final String logNoService = "Service not found";
    static final String logNoCharacteristic = "Characteristic not found";
    static final String logNoDescriptor = "Descriptor not found";
    static final String logReadFail = "Unable to read";
    static final String logReadFailReturn = "Unable to read on return";
    static final String logSubscribeFail = "Unable to subscribe";
    static final String logSubscribeAlready = "Already subscribed";
    static final String logUnsubscribeFail = "Unable to unsubscribe";
    static final String logUnsubscribeAlready = "Already unsubscribed";
    static final String logWriteFail = "Unable to write";
    static final String logWriteFailReturn = "Unable to write on return";
    static final String logWriteValueNotFound = "Write value not found";
    static final String logWriteValueNotSet = "Write value not set";
    static final String logReadDescriptorFail = "Unable to read descriptor";
    static final String logReadDescriptorFailReturn = "Unable to read descriptor on return";
    static final String logWriteDescriptorNotAllowed = "Unable to write client configuration descriptor";
    static final String logWriteDescriptorFail = "Unable to write descriptor";
    static final String logWriteDescriptorValueNotFound = "Write descriptor value not found";
    static final String logWriteDescriptorValueNotSet = "Write descriptor value not set";
    static final String logWriteDescriptorFailReturn = "Descriptor not written on return";
    static final String logRssiFail = "Unable to read RSSI";
    static final String logRssiFailReturn = "Unable to read RSSI on return";
    //Request Connection Priority
    static final String logRequestConnectionPriorityNull = "Request connection priority not set";
    static final String logRequestConnectionPriorityInvalid = "Request connection priority is invalid";
    static final String logRequestConnectionPriorityFailed = "Request connection priority failed";
    //MTU
    static final String logMtuFail = "Unable to set MTU";
    static final String logMtuFailReturn = "Unable to set MTU on return";

    static final String logRequiresAPI21 = "Requires API level 21";

    static final String operationConnect = "connect";
    static final String operationDiscover = "discover";
    static final String operationRssi = "rssi";
    static final String operationRead = "read";
    static final String operationSubscribe = "subscribe";
    static final String operationUnsubscribe = "unsubscribe";
    static final String operationWrite = "write";
    static final String operationMtu = "mtu";

    static final String baseUuidStart = "0000";
    static final String baseUuidEnd = "-0000-1000-8000-00805F9B34FB";

    //Client Configuration UUID for notifying/indicating
    static final UUID clientConfigurationDescriptorUuid = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");
}


