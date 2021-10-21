package com.example.mitacapidemo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mitac.api.libs.MitacAPI;
import com.mitac.api.libs.engine.BatteryService;
import com.mitac.api.libs.engine.GpsService;
import com.mitac.api.libs.engine.LedService;
import com.mitac.api.libs.engine.McuService;
import com.mitac.api.libs.engine.NetService;
import com.mitac.api.libs.engine.SerialPortService;
import com.mitac.api.libs.engine.StorageService;
import com.mitac.api.libs.engine.SystemService;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MitacAPItest";
    private MitacAPI mitacAPI;
    private Button led_bt;
    private Button mcu_bt;
    private Button system_bt;
    private Button battery_bt;
    private Button gps_bt;
    private Button net_bt;
    private Button serialport_bt;
    private Button storage_bt;

    private boolean wakeUpListenerRegistered = false;
    private boolean gpioListenerRegistered = false;
    private boolean selfDiagnosisListenerRegistered = false;
    private boolean batteryListenerRegistered = false;
    private boolean accListenerRegistered = false;
    private boolean emergencyKeyListenerRegistered = false;
    private boolean brokenSdcardListenerRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        led_bt = findViewById(R.id.led_bt);
        mcu_bt = findViewById(R.id.mcu_bt2);
        system_bt = findViewById(R.id.system_bt3);
        battery_bt = findViewById(R.id.battery_bt4);
        gps_bt = findViewById(R.id.gps_bt5);
        net_bt = findViewById(R.id.net_bt6);
        serialport_bt = findViewById(R.id.serialport_bt7);
        storage_bt = findViewById(R.id.storage_bt8);

        led_bt.setOnClickListener(this);
        mcu_bt.setOnClickListener(this);
        system_bt.setOnClickListener(this);
        battery_bt.setOnClickListener(this);
        gps_bt.setOnClickListener(this);
        net_bt.setOnClickListener(this);
        serialport_bt.setOnClickListener(this);
        storage_bt.setOnClickListener(this);

        //init MitacAPI
        mitacAPI = MitacAPI.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.led_bt:
                Intent ledIntent =new Intent(MainActivity.this,LedServiceActivity.class);
                startActivity(ledIntent);
                break;
            case R.id.mcu_bt2:
                Intent mcuIntent =new Intent(MainActivity.this,McuServiceActivity.class);
                startActivity(mcuIntent);
                break;
            case R.id.system_bt3:
                Intent systemIntent =new Intent(MainActivity.this,SystemServiceActivity.class);
                startActivity(systemIntent);
                break;
            case R.id.battery_bt4:
                Intent batteryIntent =new Intent(MainActivity.this,BatteryServiceActivity.class);
                startActivity(batteryIntent);
                break;
            case R.id.gps_bt5:
                Intent gpsIntent =new Intent(MainActivity.this,GpsServiceActivity.class);
                startActivity(gpsIntent);
                break;
            case R.id.net_bt6:
                Intent netIntent =new Intent(MainActivity.this,NetServiceActivity.class);
                startActivity(netIntent);
                break;
            case R.id.serialport_bt7:
                Intent serialportIntent =new Intent(MainActivity.this,SerialPortServiceActivity.class);
                startActivity(serialportIntent);
                break;
            case R.id.storage_bt8:
                Intent storageIntent =new Intent(MainActivity.this,StorageServiceActivity.class);
                startActivity(storageIntent);
                break;
            default:

                break;
        }
    }

    /**
     * Dynamic access location permission.
     */
    private boolean checkLocationPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsList = new ArrayList<String>();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (permissionsList.size() > 0) {
                ActivityCompat.requestPermissions((Activity) this, permissionsList.toArray(new String[permissionsList.size()]),
                        1);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG,"Permission Success");
                    // Permission Success
                    testUbloxGpsServiceGetFunctions();
                } else {
                    // Permission Denied
                    Log.d(TAG, "Permission Denied");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(wakeUpListenerRegistered) {
            mitacAPI.getBatteryService().unregisterWakeUpSourceListener(this);
        }
        if(selfDiagnosisListenerRegistered) {
            mitacAPI.getSystemService().unRegisterSelfDiagnosisListener(this);
        }
        if(accListenerRegistered) {
            mitacAPI.getBatteryService().unregisterAccChangeListener(this);
        }
        if(gpioListenerRegistered) {
            mitacAPI.getBatteryService().unregisterGPIOListener(this);
        }
        if(emergencyKeyListenerRegistered) {
            mitacAPI.getBatteryService().unregisterEmergencyKeyListener(this);
        }
        if(batteryListenerRegistered) {
            mitacAPI.getBatteryService().unregisterBatteryStatusListener(this);
        }
        if(brokenSdcardListenerRegistered) {
            mitacAPI.getStorageService().unregisterBrokenSdcardListener(this);
        }
    }

    public void registerWakeUpSourceListener() {
        wakeUpListenerRegistered = true;
        mitacAPI.getBatteryService().registerWakeUpSourceListener(this, new BatteryService.WakeUpSourceListener() {
            @Override
            public void onCanWake() {
                Log.d(TAG, "registerWakeUpSourceListener() onCanWake()");
            }

            @Override
            public void onSusWake() {
                Log.d(TAG, "registerWakeUpSourceListener() onSusWake()");
            }

            @Override
            public void onGsensorWake() {
                Log.d(TAG, "registerWakeUpSourceListener() onGsensorWake()");
            }
        });
    }

    public void registerAccChangeListener() {
        accListenerRegistered = true;
        mitacAPI.getBatteryService().registerAccChangeListener(this, new BatteryService.AccChangeListener() {

            @Override
            public void onAccOn() {
                Log.d(TAG, "registerAccChangeListener() onAccOn()");
            }

            @Override
            public void onAccOff() {
                Log.d(TAG, "registerAccChangeListener() onAccOff()");
            }
        });
    }

    public void registerBatteryStatusListener() {
        batteryListenerRegistered = true;
        mitacAPI.getBatteryService().registerBatteryStatusListener(this, new BatteryService.BatteryStatusListener() {
            @Override
            public void onBatteryCut() {
                Log.d(TAG, "registerBatteryStatusListener() onBatteryCut()");
            }

            @Override
            public void onBatteryLow() {
                Log.d(TAG, "registerBatteryStatusListener() onBatteryLow()");
            }
        });
    }

    public void registerGPIOListener() {
        gpioListenerRegistered = true;
        mitacAPI.getBatteryService().registerGPIOListener(this, new BatteryService.GPIOListener() {
            @Override
            public void onCoverDetect(String value) {
                Log.d(TAG, "registerGPIOListener() onCoverDetect():"+value);
            }

            @Override
            public void onPwrbdGpio0(String value) {
                Log.d(TAG, "registerGPIOListener() onPwrbdGpio0():"+value);
            }

            @Override
            public void onPwrbdGpio1(String value) {
                Log.d(TAG, "registerGPIOListener() onPwrbdGpio1():"+value);
            }

            @Override
            public void onPwrbdGpio2(String value) {
                Log.d(TAG, "registerGPIOListener() onPwrbdGpio2():"+value);
            }
        });
    }

    public void registerEmergencyKeyListener() {
        emergencyKeyListenerRegistered = true;
        mitacAPI.getBatteryService().registerEmergencyKeyListener(this, new BatteryService.EmergencyKeyListener() {
            @Override
            public void onPressed() {
                Log.d(TAG, "registerEmergencyKeyListener() onPressed()");
            }

            @Override
            public void onReleased() {
                Log.d(TAG, "registerEmergencyKeyListener() onReleased()");
            }
        });
    }

    public void registerBrokenSdcardListener() {
        brokenSdcardListenerRegistered = true;
        mitacAPI.getStorageService().registerBrokenSdcardListener(this, new StorageService.BrokenSdcardListener() {
            @Override
            public void sdCardUnplug() {
                Log.d(TAG, "registerBrokenSdcardListener() sdCardUnplug()");
            }

            @Override
            public void brokenSdcardPlugin() {
                Log.d(TAG, "registerBrokenSdcardListener() brokenSdcardPlugin()");
            }

            @Override
            public void fileSystemUnsupported() {
                Log.d(TAG, "registerBrokenSdcardListener() fileSystemUnsupported()");
            }
        });
    }

    public boolean isAccOn() {
        return mitacAPI.getBatteryService().isAccOn(this);
    }

    public boolean isBatteryCut() {
        return mitacAPI.getBatteryService().isBatteryCut(this);
    }

    public boolean isBatteryLow() {
        return mitacAPI.getBatteryService().isBatteryLow(this);
    }

    public int testEestartUsbHost() {
        return mitacAPI.getSystemService().restartUsbHost(this);
    }

    public boolean testAllowGpsUseWhenAccOff(boolean enable) {
        return mitacAPI.getUBloxGpsService().allowGpsUseWhenAccOff(this, enable);
    }

    public int testEnableUsbHost(boolean enable) {
        return mitacAPI.getSystemService().enableUsbHost(this, enable);
    }

    public void testSendHeartBeat() {
        mitacAPI.getBatteryService().sendHeartBeat(this);
    }

    public void testSuspendDevice() {
        mitacAPI.getSystemService().suspendDevice(this);
    }

    public void testRebootDevice() {
        mitacAPI.getSystemService().rebootDevice(this);
    }

    public void testShutdownDevice() {
        mitacAPI.getSystemService().shutdownDevice(this);
    }

    public void testDoFactoryReset() {
        mitacAPI.getSystemService().doFactoryReset(this);
    }

    public void testOpenCanSerialPort() {
        mitacAPI.getSerialPortService().openCanSerialPort(this, 115200, new SerialPortService.SerialPortResultListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"openCanSerialPort onSuccess");
            }

            @Override
            public void onFail() {
                Log.d(TAG,"openCanSerialPort onFail");
            }
        });
    }

    public void testCloseCanSerialPort() {
        mitacAPI.getSerialPortService().closeCanSerialPort(this, new SerialPortService.SerialPortResultListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"closeCanSerialPort onSuccess");
            }

            @Override
            public void onFail() {
                Log.d(TAG,"closeCanSerialPort onFail");
            }
        });
    }

    public void testReadCanSerialPort() {
        mitacAPI.getSerialPortService().readCanSerialPort(this, new SerialPortService.SerialPortMessageListener() {
            @Override
            public void onMessage(String s, String s1, byte[] bytes) {
                Log.d(TAG,"readCanSerialPort port: "+s + " message:"+s1);
            }
        });
    }

    public void testWriteCanSerialPort() {
        mitacAPI.getSerialPortService().writeCanSerialPort(this,"test", new SerialPortService.SerialPortResultListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"writeCanSerialPort onSuccess");
            }

            @Override
            public void onFail() {
                Log.d(TAG,"writeCanSerialPort onFail");
            }
        });
    }

    public String[] testGetAvailableSerialPort() {
        return mitacAPI.getSerialPortService().getAvailableSerialports(this);
    }

    public void testsetVpnInterventionAutoConfir(boolean autoConfirm) {
        mitacAPI.getSystemService().setVpnInterventionAutoConfirm(this,autoConfirm);
    }

    public void testUpdateBaseOtaImage() {
        mitacAPI.getSystemService().updateBaseOtaImage(this, new SystemService.UpdateOtaErrorListener() {
            @Override
            public void onError(int i) {
                Log.d(TAG,"updateBaseOtaImage: "+i);
            }
        });
    }

    public void testUpdateRegionOtaImage() {
        mitacAPI.getSystemService().updateRegionOtaImage(this, true, new SystemService.UpdateOtaErrorListener() {
            @Override
            public void onError(int i) {
                Log.d(TAG,"updateRegionOtaImage: "+i);
            }
        });
    }

    public void testUpgradeGpsFirmware() {
        mitacAPI.getUBloxGpsService().upgradeGpsFirmware(this, new GpsService.UpgradeFirmwareResultListener() {
            @Override
            public void onResult(boolean upgradeResult, String upgradeInfo) {
                Log.d(TAG, "upgradeResult:" +upgradeResult + " upgradeInfo:"+upgradeInfo);
            }
        });
    }

    public void testSetLogServiceEnabled(boolean enable) {
        mitacAPI.getSystemService().setLogServiceEnabled(this, enable);
    }

    public void testSetAdbDebugEnable() {
        mitacAPI.getSystemService().setAdbDebugEnable(this, true);
    }

    public void testUpdatePowerMcuFirmware() {
        mitacAPI.getMcuService().updatePowerMcuFirmware(this, McuService.UpgradeMcuPath.VENDOR_FIRMWARE_DIR, new McuService.McuUpgradeListener() {
            @Override
            public void onResult(boolean b, String s) {
                Log.d(TAG,"autoUpgradeMcu: "+b + " s:"+s);
            }
        });
    }

    public void testGetVehicleVoltageLevel() {
        mitacAPI.getMcuService().getVehicleVoltageLevel(this, new McuService.McuCmdResultListener() {
            @Override
            public void onResult(boolean execResult, String resultInfo) {
                Log.d(TAG, "getVoltageLevel:" + execResult);
                Log.d(TAG, "getVoltageLevel:" + resultInfo);
                if(execResult) {
                    if(resultInfo.equalsIgnoreCase("12v")) {

                    } else if(resultInfo.equalsIgnoreCase("24v")) {

                    }
                }
            }
        });
    }

    public void testSetAccOffDebounceTime() {
        mitacAPI.getMcuService().setAccOffDebounceTime(this,3000, new McuService.McuCmdResultListener() {
            @Override
            public void onResult(boolean execResult, String resultInfo) {
                Log.d(TAG, "setAccOffDebounceTime:" + execResult);
                Log.d(TAG, "setAccOffDebounceTime:" + resultInfo);
            }
        });
    }

    public void testSet12VBatteryParams() {
        mitacAPI.getMcuService().set12VBatteryParams(this,10.5,7.5
                , new McuService.McuCmdResultListener() {
                    @Override
                    public void onResult(boolean execResult, String resultInfo) {
                        Log.d(TAG, "set12VBatteryParams:" + execResult);
                        Log.d(TAG, "set12VBatteryParams:" + resultInfo);
                    }
        });
    }

    public void testSet24VBatteryParams() {
        mitacAPI.getMcuService().set24VBatteryParams(this,23,18
                , new McuService.McuCmdResultListener() {
                    @Override
                    public void onResult(boolean execResult, String resultInfo) {
                        Log.d(TAG, "set12VBatteryParams:" + execResult);
                        Log.d(TAG, "set12VBatteryParams:" + resultInfo);
                    }
        });
    }

    public void testSetAllVBatteryParams() {
        mitacAPI.getMcuService().setAllBatteryParams(this,10.5,7.5, 23,18
                , new McuService.McuCmdResultListener() {
                    @Override
                    public void onResult(boolean execResult, String resultInfo) {
                        Log.d(TAG, "setAllBatteryParams:" + execResult);
                        Log.d(TAG, "setAllBatteryParams:" + resultInfo);
                    }
                });
    }

    public void testSetAdbWifiDebugEnable() {
        mitacAPI.getSystemService().setAdbWifiDebugEnable(this, true);
    }

    public void testSetCameraPowerOn() {
        Log.d(TAG, "testSetCameraPowerOn:" + mitacAPI.getSystemService().setCameraPowerOn(this));
    }

    public void testSetCameraPowerOff() {
        Log.d(TAG, "testSetCameraPowerOn:" + mitacAPI.getSystemService().setCameraPowerOff(this));
    }

    public void testFormatSdCard() {
        mitacAPI.getStorageService().formatSdCard(this, "ddd",new StorageService.FormatSdCardResultListener() {
            @Override
            public void onResult(boolean success) {
                Log.d(TAG, "testFormatSdCard(): success:"+success);
            }
        });
    }

    public void registerSelfDiagnosisListener() {
        selfDiagnosisListenerRegistered = true;
        mitacAPI.getSystemService().registerSelfDiagnosisListener(this, new SystemService.SelfDiagnosisResultListener() {
            @Override
            public void onResult(String[] selfDiagnosisList) {
               for(String str: selfDiagnosisList) {
                   Log.d(TAG, "registerSelfDiagnosisListener(): selfDiagnosisList:"+str);
               }
            }
        });
    }

    public void testUpdateCanMcuFirmware() {
        mitacAPI.getMcuService().updateCanMcuFirmware(this, "/data/media/0/N664_CAN_MCU_FW_R01.1.0515.bin", new McuService.McuUpgradeListener() {
            @Override
            public void onResult(boolean upgradeResult, String upgradeInfo) {
                Log.d(TAG, "testUpdateCanMcuFirmware(): execResult:"+upgradeResult +" ,resultInfo:"+upgradeInfo);
            }
        });
    }

    public void testgetAllMcuParams() {
        mitacAPI.getMcuService().getPowerMcuAllParams(this, new McuService.McuCmdResultListener() {
            @Override
            public void onResult(boolean execResult, String resultInfo) {
                Log.d(TAG, "getAllMcuParams:" + execResult);
                Log.d(TAG, "getAllMcuParams:" + resultInfo);
            }
        });
    }

    public void testsetAllMcuParams() {
        mitacAPI.getMcuService().setPowerMcuAllParams(this,10.5,7.5,
                23,18,3000, new McuService.McuCmdResultListener() {
                    @Override
                    public void onResult(boolean execResult, String resultInfo) {
                        Log.d(TAG, "setAllMcuParams:" + execResult);
                        Log.d(TAG, "setAllMcuParams:" + resultInfo);
                    }
                });
    }

    /*public void testUpdatePowerMcuFirmware() {
        mitacAPI.getMcuService().updatePowerMcuFirmware(this, "/data/media/0/N664_Power_MCU_FW_R06.1.0812.bin", new McuService.McuCmdResultListener() {
            @Override
            public void onResult(boolean execResult, String resultInfo) {
                Log.d(TAG, "testUpdatePowerMcuFirmware(): execResult:"+execResult +" ,resultInfo:"+resultInfo);
            }
        });
    }*/

    /*public void testUpdatePowerMcuBootloader() {
        mitacAPI.getMcuService().updatePowerMcuBootloader(this, "/data/media/0/N664_Power_MCU_FW_R06.1.0812.bin", new McuService.McuCmdResultListener() {
            @Override
            public void onResult(boolean execResult, String resultInfo) {
                Log.d(TAG, "testUpdatePowerMcuBootloader(): execResult:"+execResult +" ,resultInfo:"+resultInfo);
            }
        });
    }*/

    public void testSetSoftApSetting() {
        mitacAPI.getNetService().setSoftApSetting(this, "ssssss", "cccccccc");
    }

    public void testStartSoftAp() {
        mitacAPI.getNetService().startSoftAp(this, "aaaaa", "bbbbbbbb");
    }

    public void testStopSoftAp() {
        mitacAPI.getNetService().stopSoftAp(this);
    }

    public void testLedControlLed() {
        mitacAPI.getLedService().controlLed(this, LedService.LedCommand.BLUE_BLINK_LOOP);
    }

    public void testLedControlLedOff() {
        mitacAPI.getLedService().controlLed(this, LedService.LedCommand.ALL_OFF);
    }

    public void testSetDrEnabled() {
        mitacAPI.getUBloxGpsService().setDrEnabled(this, true);
    }

    public boolean testSetSpeedMode() {
        return mitacAPI.getUBloxGpsService().setSpeedMode(this, 1);
    }


    public void testUBloxSendCommand() {
        byte[] command= {(byte)0x06,(byte)0x56,(byte)0x0c,(byte)0x00,(byte)0x00,(byte)0x89, (byte)0xa8,
                (byte)0x20,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};
        mitacAPI.getUBloxGpsService().sendCommand(this, command);
    }

    public void testSetGsensorThreshold() {
        Log.d(TAG, "testSetGsensorThreshold: " + mitacAPI.getSystemService().setGsensorThreshold(this, 100));
    }

    public void testDeviceInfo() {
        Log.d(TAG, "getDeviceInfo() start");

        Log.d(TAG, "getCpuTemperature():" + mitacAPI.getSystemService().getCpuTemperature(this));
        Log.d(TAG, "getSerialNumber():" + mitacAPI.getSystemService().getSerialNumber(this));

        Log.d(TAG, "isWifiApEnabled():" + mitacAPI.getNetService().isWifiApEnabled(this));
        Log.d(TAG, "getSoftApNumClients():" + mitacAPI.getNetService().getSoftApNumClients(this));

        String[] selfDiagnosisList = mitacAPI.getSystemService().getSelfDiagnosisList(this);
        if(selfDiagnosisList != null) {
            for(String str: selfDiagnosisList) {
                Log.d(TAG, "getSelfDiagnosisList():"+str);
            }
        }

        mitacAPI.getNetService().getSoftApSetting(this, new NetService.SoftApSettingListener() {
            @Override
            public void onResult(String ssid, String password) {
                Log.d(TAG, "getSoftApSetting(): ssid:"+ssid +" ,password:"+password);
            }
        });



    }

    public void testOpenNfc() {
        mitacAPI.getSystemService().enableNfc(this);
    }

    public void testCloseNfc() {
        mitacAPI.getSystemService().disableNfc(this);
    }

    public void testSetAirplaneMode(boolean enable) {
        mitacAPI.getSystemService().setAirplaneMode(this, enable);
    }

    public void testSetUsbMode(int mode) {
        mitacAPI.getSystemService().setUsbMode(this, mode);
    }

    public void testSetIRLedOn() {
        mitacAPI.getLedService().setIRLedOn(this, true);
    }

    public void testSetIRLedOff() {
        mitacAPI.getLedService().setIRLedOn(this, false);
    }

    public void testMcuServiceGetFunctions() {
        mitacAPI.getMcuService().getAccOffDebounceTime(this, new McuService.McuCmdResultListener() {

            @Override
            public void onResult(boolean execResult, String resultInfo) {
                Log.d(TAG, "getAccOffDebounceTime(): execResult:"+execResult +" ,resultInfo:"+resultInfo);
            }
        });

        mitacAPI.getMcuService().getCanMcuFirmwareInfo(this, new McuService.McuCmdResultListener(){
            @Override
            public void onResult(boolean execResult, String resultInfo) {
                Log.d(TAG, "getCanMcuFirmwareInfo(): execResult:"+execResult +" ,resultInfo:"+resultInfo);
                if(execResult) {
                    //todo
                } else {
                    //todo
                }
            }
        });

        mitacAPI.getMcuService().getMainBoardTemperature(this, new McuService.McuCmdResultListener(){
            @Override
            public void onResult(boolean execResult, String resultInfo) {
                Log.d(TAG, "getMainBoardTemperature(): execResult:"+execResult +" ,resultInfo:"+resultInfo);
                if(execResult) {
                    //todo
                } else {
                    //todo
                }
            }
        });

        mitacAPI.getMcuService().getPowerMcuFirmwareVersion(this, new McuService.McuCmdResultListener(){
            @Override
            public void onResult(boolean execResult, String resultInfo) {
                Log.d(TAG, "getPowerMcuFirmwareVersion(): execResult:"+execResult +" ,resultInfo:"+resultInfo);
                if(execResult) {
                    //todo
                } else {
                    //todo
                }
            }
        });

        mitacAPI.getMcuService().getPowerMcuBootloaderVersion(this, new McuService.McuCmdResultListener(){
            @Override
            public void onResult(boolean execResult, String resultInfo) {
                Log.d(TAG, "getPowerMcuBootloaderVersion(): execResult:"+execResult +" ,resultInfo:"+resultInfo);
                if(execResult) {
                    //todo
                } else {
                    //todo
                }
            }
        });
    }

    public void testUbloxGpsServiceGetFunctions() {
        //need permission android.permission.ACCESS_FINE_LOCATION
        mitacAPI.getUBloxGpsService().getAutoAlignmentStatus(this, new GpsService.AutoAlignmentStatusListener() {
            @Override
            public void onReceiveMessage(int imuErrorCode, int status, boolean autoAlignmentRunning, double roll, double pitch, double yaw) {
                Log.d(TAG, "getAutoAlignmentStatus(): imuErrorCode:"+imuErrorCode + ",status:"+status
                        +",autoAlignmentRunning:"+autoAlignmentRunning +",roll:"+roll + ",pitch:"+pitch +",yaw:"+yaw);
            }
        });

        mitacAPI.getUBloxGpsService().getDrStatus(this, new GpsService.DrStatusListener() {
            @Override
            public void onReceiveMessage(int DrMode) {
                Log.d(TAG, "getDrStatus(): DrMode:"+DrMode );
            }
        });

        Log.d(TAG, "getGPSFWVersion():" + mitacAPI.getUBloxGpsService().getGPSFirmwareVersion(this));

        Log.d(TAG, "getSpeedMode():" + mitacAPI.getUBloxGpsService().getSpeedMode(this));

        mitacAPI.getUBloxGpsService().getVehicleReverseSignal(this, new GpsService.ReverseSignalListener() {
            @Override
            public void onReceiveMessage(int reverseSignal, long speedPulseNumbers) {
                Log.d(TAG, "getVehicleReverseSignal(): reverseSignal:"+reverseSignal +" ,speedPulseNumbers:"+speedPulseNumbers);
            }
        });

        mitacAPI.getUBloxGpsService().getWheelTickResolution(this, new GpsService.WheelTickResolutionListener() {
            @Override
            public void onReceiveMessage(double wheelTickResolution) {
                Log.d(TAG, "GetWheelTickResolution(): wheelTickResolution:"+wheelTickResolution );
            }
        });

        Log.d(TAG, "isDrEnabled():" + mitacAPI.getUBloxGpsService().isDrEnabled(this));
    }

}
