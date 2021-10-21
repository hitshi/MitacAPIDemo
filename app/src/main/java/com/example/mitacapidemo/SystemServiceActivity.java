package com.example.mitacapidemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.mitac.api.libs.MitacAPI;
import com.mitac.api.libs.engine.LedService;
import com.mitac.api.libs.engine.SystemService;

import java.lang.reflect.Method;

public class SystemServiceActivity extends AppCompatActivity implements View.OnClickListener {
    private MitacAPI mitacAPI;

    private Button bt_test;

    private LinearLayout enableLayout;
    private RadioGroup enableGroup;
    private int enableRadioGroupCheckedId = R.id.rb_enable;

    private LinearLayout usbModeLayout;
    private RadioGroup usbModeGroup;
    private int usbModeRadioGroupCheckedId = 0;

    private LinearLayout inputLayout;
    private TextView inputHint;
    private TextView inputText;

    private TextView resultInfo;
    private Spinner spinner;
    private int apiIndex = 0;

    private boolean selfDiagnosisListenerRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_service);

        mitacAPI = MitacAPI.getInstance();

        resultInfo = findViewById(R.id.tv_result);
        enableLayout = findViewById(R.id.ll_enable);
        usbModeLayout = findViewById(R.id.ll_usb_mode);

        inputLayout = findViewById(R.id.ll_textview);
        inputHint = findViewById(R.id.input_hint);
        inputText = findViewById(R.id.input_text);

        bt_test = findViewById(R.id.bt_test);
        bt_test.setOnClickListener(this);

        enableGroup = findViewById(R.id.enable_group);
        enableGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                enableRadioGroupCheckedId = checkedId;

            }
        });

        usbModeGroup = findViewById(R.id.usb_mode_group);
        usbModeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                usbModeRadioGroupCheckedId = checkedId;

            }
        });

        String[] portArrayStrings = new String[]{"getBaseImageVersion","getRegionImageVersion","getSerialNumber", "doFactoryReset","updateRegionOtaImage","updateBaseOtaImage",
                "setAdbDebugEnable","setAdbWifiDebugEnable","getCpuTemperature","setLogServiceEnabled","getSelfDiagnosisList",
                "registerSelfDiagnosisListener","unRegisterSelfDiagnosisListener",
                "rebootDevice","suspendDevice","shutdownDevice","setCameraPowerOff","setCameraPowerOn","setGsensorThreshold","setSensorWakeUp","setGsensorWakeupDuration"
                ,"enableNfc","disableNfc","setAirplaneMode","setGsmEnable","isGsmEnable","setVpnInterventionAutoConfirm",
                "setUsbMode","setUsbFileTransportEnable","enableUsbHost","restartUsbHost","isAccSupported","setAccSupported"};
        ArrayAdapter<String> portAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_text_item, portArrayStrings);
        portAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(spinnerListener);
        spinner.setAdapter(portAdapter);
    }

    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            apiIndex = position;
            enableLayout.setVisibility(View.GONE);
            usbModeLayout.setVisibility(View.GONE);
            resultInfo.setVisibility(View.GONE);
            inputLayout.setVisibility(View.GONE);
            resultInfo.setText("");

            switch (position) {
                case 4:
                    resultInfo.setVisibility(View.VISIBLE);
                    resultInfo.setText("Update region OTA package, please put OTA packages into internal storage root director or external " +
                            "SD card root director, region package name should be like \"RGIMG.*_OTA.zip\".");
                    break;
                case 5:
                    resultInfo.setVisibility(View.VISIBLE);
                    resultInfo.setText("Auto update base OTA  package, please put OTA packages into internal storage root director or external " +
                            "SD card root director, base package name should be like \"*OTA.zip\"");
                    break;
                case 18:
                    inputLayout.setVisibility(View.VISIBLE);
                    inputHint.setText("Threshold value:");
                    inputText.setText("2000");
                    break;
                case 20:
                    inputLayout.setVisibility(View.VISIBLE);
                    inputHint.setText("Duration value (0,1,2,3):");
                    inputText.setText("0");
                    break;
                case 6:
                case 7:
                case 9:
                case 19:
                case 23:
                case 24:
                case 26:
                case 28:
                case 32:
                    enableLayout.setVisibility(View.VISIBLE);
                    break;
                case 27:
                    usbModeLayout.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    @Override
    public void onClick(View view) {
        resultInfo.setVisibility(View.GONE);

        switch (view.getId()) {
            case R.id.bt_test:
                switch (apiIndex) {
                    case 0:
                        String baseImageVersionValue = mitacAPI.getSystemService().getBaseImageVersion(this);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("BaseImageVersion: "+baseImageVersionValue);
                        break;
                    case 1:
                        String regionImageVersionValue = mitacAPI.getSystemService().getRegionImageVersion(this);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("RegionImageVersion: "+regionImageVersionValue);
                        break;
                    case 2:
                        String result = mitacAPI.getSystemService().getSerialNumber(this);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("SerialNumber: "+result);
                        break;
                    case 3:
                        mitacAPI.getSystemService().doFactoryReset(this);
                        break;
                    case 4:
                        mitacAPI.getSystemService().updateRegionOtaImage(this, true, new SystemService.UpdateOtaErrorListener() {
                            @Override
                            public void onError(int errorCode) {
                                if(apiIndex == 2) {
                                    resultInfo.setVisibility(View.VISIBLE);
                                    resultInfo.setText("updateRegionOtaImage errorCode: "+errorCode +
                                            "\n1: CHECK error. Battery is low, not allow to update.\n" +
                                            "2: LOOKUP_FILE error. Didn't find newer Region image package.\n" +
                                            "4: VERIFY error. Verify package fail.\n" +
                                            "8: INSTALL error. Install package fail.");
                                }
                            }
                        });
                        break;
                    case 5:
                        mitacAPI.getSystemService().updateBaseOtaImage(this, new SystemService.UpdateOtaErrorListener() {
                            @Override
                            public void onError(int errorCode) {
                                if(apiIndex == 3) {
                                    resultInfo.setVisibility(View.VISIBLE);
                                    resultInfo.setText("updateBaseOtaImage errorCode: "+errorCode +
                                            "\n1: CHECK error. Battery is low, not allow to update.\n" +
                                            "2: LOOKUP_FILE error. Didn't find newer base image package.\n" +
                                            "4: VERIFY error. Verify package fail.\n" +
                                            "8: INSTALL error. Install package fail.");
                                }
                            }
                        });
                        break;
                    case 6:
                        switch (enableRadioGroupCheckedId) {
                            case R.id.rb_enable:
                                mitacAPI.getSystemService().setAdbDebugEnable(this, true);
                                break;
                            case R.id.rb_disable:
                                mitacAPI.getSystemService().setAdbDebugEnable(this, false);
                                break;
                        }
                        break;
                    case 7:
                        switch (enableRadioGroupCheckedId) {
                            case R.id.rb_enable:
                                mitacAPI.getSystemService().setAdbWifiDebugEnable(this, true);
                                break;
                            case R.id.rb_disable:
                                mitacAPI.getSystemService().setAdbWifiDebugEnable(this, false);
                                break;
                        }
                        break;
                    case 8:
                        float temperature = mitacAPI.getSystemService().getCpuTemperature(this);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("getCpuTemperature: "+temperature);
                        break;
                    case 9:
                        switch (enableRadioGroupCheckedId) {
                            case R.id.rb_enable:
                                mitacAPI.getSystemService().setLogServiceEnabled(this, true);
                                break;
                            case R.id.rb_disable:
                                mitacAPI.getSystemService().setLogServiceEnabled(this, false);
                                break;
                        }
                        break;
                    case 10:
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("");
                        String[] selfDiagnosisList = mitacAPI.getSystemService().getSelfDiagnosisList(this);
                        if(selfDiagnosisList != null) {
                            for(String str: selfDiagnosisList) {
                                resultInfo.append("getSelfDiagnosisList: "+str);
                            }
                        }
                        break;
                    case 11:
                        if(!selfDiagnosisListenerRegistered) {
                            selfDiagnosisListenerRegistered = true;
                            resultInfo.setVisibility(View.VISIBLE);
                            mitacAPI.getSystemService().registerSelfDiagnosisListener(this, new SystemService.SelfDiagnosisResultListener() {
                                @Override
                                public void onResult(String[] selfDiagnosisList) {
                                    resultInfo.setText("");
                                    for(String str: selfDiagnosisList) {
                                        resultInfo.append("getSelfDiagnosisList: "+str);
                                    }
                                }
                            });
                        }
                        break;
                    case 12:
                        if(selfDiagnosisListenerRegistered) {
                            mitacAPI.getSystemService().unRegisterSelfDiagnosisListener(this);
                            selfDiagnosisListenerRegistered = false;
                        }
                        break;
                    case 13:
                        mitacAPI.getSystemService().rebootDevice(this);
                        break;
                    case 14:
                        mitacAPI.getSystemService().suspendDevice(this);
                        break;
                    case 15:
                        mitacAPI.getSystemService().shutdownDevice(this);
                        break;
                    case 16:
                        boolean cameraPowerOffResult = mitacAPI.getSystemService().setCameraPowerOff(this);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("setCameraPowerOff result: "+cameraPowerOffResult);
                        break;
                    case 17:
                        boolean cameraPowerOnResult = mitacAPI.getSystemService().setCameraPowerOn(this);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("setCameraPowerOn result: "+cameraPowerOnResult);
                        break;
                    case 18:
                        int thresholdValue = Integer.parseInt(inputText.getText().toString());
                        boolean setGsensorThresholdResult = mitacAPI.getSystemService().setGsensorThreshold(this, thresholdValue);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("setGsensorThreshold result: "+setGsensorThresholdResult);
                        break;
                    case 19:
                        boolean setSensorWakeUpResult = false;
                        switch (enableRadioGroupCheckedId) {
                            case R.id.rb_enable:
                                setSensorWakeUpResult = mitacAPI.getSystemService().setSensorWakeUp(this, true);
                                break;
                            case R.id.rb_disable:
                                setSensorWakeUpResult = mitacAPI.getSystemService().setSensorWakeUp(this, false);
                                break;
                        }
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("setSensorWakeUp result: "+setSensorWakeUpResult);
                        break;
                    case 20:
                        int durationValue = Integer.parseInt(inputText.getText().toString());
                        boolean setGsensorWakeupDurationResult = mitacAPI.getSystemService().setGsensorWakeupDuration(this, durationValue);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("setGsensorWakeupDuration result: "+setGsensorWakeupDurationResult);
                        break;
                    case 21:
                        boolean enableNfcResult = mitacAPI.getSystemService().enableNfc(this);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("enableNfc result: "+enableNfcResult);
                        break;
                    case 22:
                        boolean disableNfcResult = mitacAPI.getSystemService().disableNfc(this);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("disableNfc result: "+disableNfcResult);
                        break;
                    case 23:
                        switch (enableRadioGroupCheckedId) {
                            case R.id.rb_enable:
                                mitacAPI.getSystemService().setAirplaneMode(this, true);
                                break;
                            case R.id.rb_disable:
                                mitacAPI.getSystemService().setAirplaneMode(this, false);
                                break;
                        }
                        break;
                    case 24:
                        switch (enableRadioGroupCheckedId) {
                            case R.id.rb_enable:
                                mitacAPI.getSystemService().setGsmEnable(this, true);
                                break;
                            case R.id.rb_disable:
                                mitacAPI.getSystemService().setGsmEnable(this, false);
                                break;
                        }

                        break;
                    case 25:
                        int isGsmEnableResult = mitacAPI.getSystemService().isGsmEnable(this);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("isGsmEnable result: "+isGsmEnableResult+"\n 1:enabled; 0:disabled; -1:not support GSM");
                        break;
                    case 26:
                        switch (enableRadioGroupCheckedId) {
                            case R.id.rb_enable:
                                mitacAPI.getSystemService().setVpnInterventionAutoConfirm(this, true);
                                break;
                            case R.id.rb_disable:
                                mitacAPI.getSystemService().setVpnInterventionAutoConfirm(this, false);
                                break;
                        }
                        break;
                    case 27:
                        switch (enableRadioGroupCheckedId) {
                            case R.id.rb_usb_mode_none:
                            default:
                                mitacAPI.getSystemService().setUsbMode(this, SystemService.UsbMode.NONE);
                                break;
                            case R.id.rb_usb_mode_mtp:
                                mitacAPI.getSystemService().setUsbMode(this, SystemService.UsbMode.MTP);
                                break;
                            case R.id.rb_usb_mode_rndis:
                                mitacAPI.getSystemService().setUsbMode(this, SystemService.UsbMode.RNDIS);
                                break;
                            case R.id.rb_usb_mode_midi:
                                mitacAPI.getSystemService().setUsbMode(this, SystemService.UsbMode.MIDI);
                                break;
                            case R.id.rb_usb_mode_ptp:
                                mitacAPI.getSystemService().setUsbMode(this, SystemService.UsbMode.PTP);
                                break;
                        }
                        break;
                    case 28:
                        switch (enableRadioGroupCheckedId) {
                            case R.id.rb_enable:
                                mitacAPI.getSystemService().setUsbFileTransportEnable(this, true);
                                break;
                            case R.id.rb_disable:
                                mitacAPI.getSystemService().setUsbFileTransportEnable(this, false);
                                break;
                        }
                        break;
                    case 29:
                        int enableUsbHostResult = mitacAPI.getSystemService().enableUsbHost(this, true);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("enableUsbHost result: "+enableUsbHostResult);
                        break;
                    case 30:
                        int restartUsbHostResult = mitacAPI.getSystemService().restartUsbHost(this);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("restartUsbHost result: "+restartUsbHostResult);
                        break;
                    case 31:
                        boolean isAccSupportedResult = mitacAPI.getSystemService().isAccSupported(this);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("isAccSupportedResult result: "+isAccSupportedResult);
                        break;
                    case 32:
                        switch (enableRadioGroupCheckedId) {
                            case R.id.rb_enable:
                                mitacAPI.getSystemService().setAccSupported(this, true);
                                break;
                            case R.id.rb_disable:
                                mitacAPI.getSystemService().setAccSupported(this, false);
                                break;
                        }
                        break;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(selfDiagnosisListenerRegistered) {
            mitacAPI.getSystemService().unRegisterSelfDiagnosisListener(this);
        }
    }
}