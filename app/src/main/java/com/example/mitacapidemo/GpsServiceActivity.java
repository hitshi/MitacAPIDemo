package com.example.mitacapidemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.mitac.api.libs.MitacAPI;
import com.mitac.api.libs.engine.BatteryService;
import com.mitac.api.libs.engine.GpsService;

import java.util.ArrayList;
import java.util.List;

public class GpsServiceActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "GpsServiceActivity";
    private MitacAPI mitacAPI;

    private Button bt_test;

    private LinearLayout enableLayout;
    private RadioGroup enableGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private int enableRadioGroupCheckedId = R.id.rb_enable;

    private LinearLayout inputLayout;
    private TextView inputHint;
    private TextView inputText;

    private LinearLayout inputLayout2;
    private TextView inputHint2;
    private TextView inputText2;

    private TextView resultInfo;
    private Spinner spinner;
    private int apiIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_service);

        mitacAPI = MitacAPI.getInstance();

        resultInfo = findViewById(R.id.tv_result);
        enableLayout = findViewById(R.id.ll_enable);

        bt_test = findViewById(R.id.bt_test);
        bt_test.setOnClickListener(this);

        inputLayout = findViewById(R.id.ll_textview);
        inputHint = findViewById(R.id.input_hint);
        inputText = findViewById(R.id.input_text);
        inputLayout2 = findViewById(R.id.ll_textview2);
        inputHint2 = findViewById(R.id.input_hint2);
        inputText2 = findViewById(R.id.input_text2);

        radioButton1 = findViewById(R.id.rb_enable);
        radioButton2 = findViewById(R.id.rb_disable);

        enableGroup = findViewById(R.id.enable_group);
        enableGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                enableRadioGroupCheckedId = checkedId;

            }
        });

        String[] portArrayStrings = new String[]{"isDrEnabled","setDrEnabled","setSpeedMode","getSpeedMode",
                "sendCommand","getVehicleReverseSignal","getDrStatus","getAutoAlignmentStatus","getWheelTickResolution",
                "getGPSFirmwareVersion","upgradeGpsFirmware","allowGpsUseWhenAccOff"};
        ArrayAdapter<String> portAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_text_item, portArrayStrings);
        portAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(spinnerListener);
        spinner.setAdapter(portAdapter);

        checkLocationPermission();
    }

    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            apiIndex = position;
            enableLayout.setVisibility(View.GONE);
            inputLayout.setVisibility(View.GONE);
            inputLayout2.setVisibility(View.GONE);
            inputText.setText("");
            inputText2.setText("");
            resultInfo.setVisibility(View.VISIBLE);
            resultInfo.setText("");

            switch (position) {
                case 1:
                case 11:
                    enableLayout.setVisibility(View.VISIBLE);
                    radioButton1.setText("Enable");
                    radioButton2.setText("Disable");
                    radioButton1.setChecked(true);
                    break;
                case 2:
                    enableLayout.setVisibility(View.VISIBLE);
                    radioButton1.setText("Source Type");
                    radioButton2.setText("Sink Type");
                    radioButton1.setChecked(true);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_test:
                switch (apiIndex) {
                    case 0:
                        int isDrEnabledResult = mitacAPI.getUBloxGpsService().isDrEnabled(this);
                        if(isDrEnabledResult == 1) {
                            resultInfo.setText("isDrEnabled: Enabled");
                        } else if(isDrEnabledResult == 0) {
                            resultInfo.setText("isDrEnabled: Disabled");
                        } else {
                            resultInfo.setText("isDrEnabled: Not valid");
                        }
                        break;
                    case 1:
                        boolean setDrEnabledResult = false;
                        switch (enableRadioGroupCheckedId) {
                            case R.id.rb_enable:
                                setDrEnabledResult = mitacAPI.getUBloxGpsService().setDrEnabled(this, true);
                                break;
                            case R.id.rb_disable:
                                setDrEnabledResult = mitacAPI.getUBloxGpsService().setDrEnabled(this, false);
                                break;
                        }
                        resultInfo.setText("setDrEnabled Result:" + setDrEnabledResult);
                        break;
                    case 2:
                        boolean setSpeedModeResult = false;
                        switch (enableRadioGroupCheckedId) {
                            case R.id.rb_enable:
                                setSpeedModeResult = mitacAPI.getUBloxGpsService().setSpeedMode(this, 0);
                                break;
                            case R.id.rb_disable:
                                setSpeedModeResult = mitacAPI.getUBloxGpsService().setSpeedMode(this, 1);
                                break;
                        }
                        resultInfo.setText("setSpeedMode Result:" + setSpeedModeResult);
                        break;
                    case 3:
                        int getSpeedModeResult = mitacAPI.getUBloxGpsService().getSpeedMode(this);
                        if(getSpeedModeResult == 1) {
                            resultInfo.setText("getSpeedMode: Sink type");
                        } else if(getSpeedModeResult == 0) {
                            resultInfo.setText("getSpeedMode: Source type");
                        } else {
                            resultInfo.setText("getSpeedMode: Failed");
                        }
                        break;
                    case 4:
                        byte[] command= {(byte)0x06,(byte)0x56,(byte)0x0c,(byte)0x00,(byte)0x00,(byte)0x89, (byte)0xa8,
                                (byte)0x20,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};
                        mitacAPI.getUBloxGpsService().sendCommand(this, command);
                        resultInfo.setText("has send a test command");
                        break;
                    case 5:
                        mitacAPI.getUBloxGpsService().getVehicleReverseSignal(this, new GpsService.ReverseSignalListener() {
                            @Override
                            public void onReceiveMessage(int reverseSignal, long speedPulseNumbers) {
                                Log.d(TAG, "getVehicleReverseSignal(): reverseSignal:"+reverseSignal +" ,speedPulseNumbers:"+speedPulseNumbers);
                                if(apiIndex == 5) {
                                    resultInfo.setText("getVehicleReverseSignal: reverseSignal:"+reverseSignal +" ,speedPulseNumbers:"+speedPulseNumbers);
                                }
                            }
                        });
                        break;
                    case 6:
                        mitacAPI.getUBloxGpsService().getDrStatus(this, new GpsService.DrStatusListener() {
                            @Override
                            public void onReceiveMessage(int DrMode) {
                                Log.d(TAG, "getDrStatus(): DrMode:"+DrMode );
                                if(apiIndex == 6) {
                                    resultInfo.setText("getDrStatus: DrMode:"+DrMode );
                                }
                            }
                        });
                        break;
                    case 7:
                        mitacAPI.getUBloxGpsService().getAutoAlignmentStatus(this, new GpsService.AutoAlignmentStatusListener() {
                            @Override
                            public void onReceiveMessage(int imuErrorCode, int status, boolean autoAlignmentRunning, double roll, double pitch, double yaw) {
                                Log.d(TAG, "getAutoAlignmentStatus(): imuErrorCode:"+imuErrorCode + ",status:"+status
                                        +",autoAlignmentRunning:"+autoAlignmentRunning +",roll:"+roll + ",pitch:"+pitch +",yaw:"+yaw);
                                if(apiIndex == 7) {
                                    resultInfo.setText("getAutoAlignmentStatus: imuErrorCode:"+imuErrorCode + ",status:"+status
                                            +",autoAlignmentRunning:"+autoAlignmentRunning +",roll:"+roll + ",pitch:"+pitch +",yaw:"+yaw);
                                }
                            }
                        });
                        break;
                    case 8:
                        mitacAPI.getUBloxGpsService().getWheelTickResolution(this, new GpsService.WheelTickResolutionListener() {
                            @Override
                            public void onReceiveMessage(double wheelTickResolution) {
                                Log.d(TAG, "getWheelTickResolution(): wheelTickResolution:"+wheelTickResolution );
                                if(apiIndex == 8) {
                                    resultInfo.setText("getWheelTickResolution: wheelTickResolution:"+wheelTickResolution );
                                }
                            }
                        });
                        break;
                    case 9:
                        String getGPSFirmwareVersionResult = mitacAPI.getUBloxGpsService().getGPSFirmwareVersion(this);
                        resultInfo.setText("getGPSFirmwareVersion:" + getGPSFirmwareVersionResult );
                        break;
                    case 10:
                        mitacAPI.getUBloxGpsService().upgradeGpsFirmware(this, new GpsService.UpgradeFirmwareResultListener() {
                            @Override
                            public void onResult(boolean upgradeResult, String upgradeInfo) {
                                Log.d(TAG, "upgradeResult:" +upgradeResult + " upgradeInfo:"+upgradeInfo);
                                if(apiIndex == 10) {
                                    resultInfo.setText("upgradeResult:" +upgradeResult + " upgradeInfo:"+upgradeInfo);
                                }
                            }
                        });
                        break;
                    case 11:
                        boolean allowGpsUseWhenAccOffResult = false;
                        switch (enableRadioGroupCheckedId) {
                            case R.id.rb_enable:
                                allowGpsUseWhenAccOffResult = mitacAPI.getUBloxGpsService().allowGpsUseWhenAccOff(this, true);
                                break;
                            case R.id.rb_disable:
                                allowGpsUseWhenAccOffResult = mitacAPI.getUBloxGpsService().allowGpsUseWhenAccOff(this, false);
                                break;
                        }
                        resultInfo.setText("allowGpsUseWhenAccOff Result:" +allowGpsUseWhenAccOffResult);
                        break;
                }
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

                } else {
                    // Permission Denied
                    Log.d(TAG, "Permission Denied");
                    finish();
                }
                break;
        }
    }
}