package com.example.mitacapidemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.mitac.api.libs.MitacAPI;
import com.mitac.api.libs.engine.GpsService;
import com.mitac.api.libs.engine.McuService;

public class McuServiceActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "McuServiceActivity";
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
    private LinearLayout inputLayout3;
    private TextView inputHint3;
    private TextView inputText3;
    private LinearLayout inputLayout4;
    private TextView inputHint4;
    private TextView inputText4;
    private LinearLayout inputLayout5;
    private TextView inputHint5;
    private TextView inputText5;

    private TextView resultInfo;
    private EditText resultInfo2;
    private Spinner spinner;
    private int apiIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcu_service);

        mitacAPI = MitacAPI.getInstance();

        resultInfo = findViewById(R.id.tv_result);
        resultInfo2 = findViewById(R.id.tv_result2);
        enableLayout = findViewById(R.id.ll_enable);

        bt_test = findViewById(R.id.bt_test);
        bt_test.setOnClickListener(this);

        inputLayout = findViewById(R.id.ll_textview);
        inputHint = findViewById(R.id.input_hint);
        inputText = findViewById(R.id.input_text);
        inputLayout2 = findViewById(R.id.ll_textview2);
        inputHint2 = findViewById(R.id.input_hint2);
        inputText2 = findViewById(R.id.input_text2);
        inputLayout3 = findViewById(R.id.ll_textview3);
        inputHint3 = findViewById(R.id.input_hint3);
        inputText3 = findViewById(R.id.input_text3);
        inputLayout4 = findViewById(R.id.ll_textview4);
        inputHint4 = findViewById(R.id.input_hint4);
        inputText4 = findViewById(R.id.input_text4);
        inputLayout5 = findViewById(R.id.ll_textview5);
        inputHint5 = findViewById(R.id.input_hint5);
        inputText5 = findViewById(R.id.input_text5);

        radioButton1 = findViewById(R.id.rb_enable);
        radioButton2 = findViewById(R.id.rb_disable);

        enableGroup = findViewById(R.id.enable_group);
        enableGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                enableRadioGroupCheckedId = checkedId;

            }
        });

        String[] portArrayStrings = new String[]{"getPowerMcuFirmwareVersion","getPowerMcuBootloaderVersion","updatePowerMcuFirmware","getBatteryVoltage",
                "getAccOffDebounceTime","setAccOffDebounceTime","getMainBoardTemperature","getVehicleVoltageLevel","setAllBatteryParams","set12VBatteryParams","set24VBatteryParams","setPowerMcuAllParams"
                ,"getPowerMcuAllParams","getCanMcuFirmwareInfo","updateCanMcuFirmware","resetCanMcu","isCanWakeUpSupported"};
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
            inputLayout.setVisibility(View.GONE);
            inputLayout2.setVisibility(View.GONE);
            inputLayout3.setVisibility(View.GONE);
            inputLayout4.setVisibility(View.GONE);
            inputLayout5.setVisibility(View.GONE);
            inputText.setText("");
            inputText2.setText("");
            inputText3.setText("");
            inputText4.setText("");
            inputText5.setText("");
            resultInfo.setVisibility(View.VISIBLE);
            resultInfo.setText("");
            resultInfo2.setVisibility(View.GONE);
            resultInfo2.setText("");

            switch (position) {
                case 2:
                    resultInfo2.setVisibility(View.VISIBLE);
                    resultInfo.setText("Please put Power MCU firmware .bin file that name starts with N664_Power_MCU_FW under the root directory of internal storage to test.");
                    break;
                case 5:
                    inputLayout.setVisibility(View.VISIBLE);
                    inputHint.setText("Acc off debounce time:");
                    inputText.setText("3000");
                    break;
                case 14:
                    resultInfo.setText("Please put CAN MCU firmware .bin file under the root directory of internal storage to test.");
                    inputLayout.setVisibility(View.VISIBLE);
                    inputHint.setText("Update file path:");
                    inputText.setText("/data/media/0/N664_CAN_MCU_FW_R01.1.0515.bin");
                    resultInfo2.setVisibility(View.VISIBLE);
                    break;
                case 8:
                    inputLayout.setVisibility(View.VISIBLE);
                    inputHint.setText("12v Battery LowVoltage:");
                    inputText.setText("10.5");
                    inputLayout2.setVisibility(View.VISIBLE);
                    inputHint2.setText("12v Battery Cut Voltage:");
                    inputText2.setText("7.5");
                    inputLayout3.setVisibility(View.VISIBLE);
                    inputHint3.setText("24v Battery Low Voltage:");
                    inputText3.setText("23");
                    inputLayout4.setVisibility(View.VISIBLE);
                    inputHint4.setText("24v Battery Cut Voltage:");
                    inputText4.setText("18");
                    break;
                case 9:
                    inputLayout.setVisibility(View.VISIBLE);
                    inputHint.setText("12v Battery Low Voltage:");
                    inputText.setText("10.5");
                    inputLayout2.setVisibility(View.VISIBLE);
                    inputHint2.setText("12v Battery Cut Voltage:");
                    inputText2.setText("7.5");
                    break;
                case 10:
                    inputLayout.setVisibility(View.VISIBLE);
                    inputHint.setText("24v Battery Low Voltage:");
                    inputText.setText("23");
                    inputLayout2.setVisibility(View.VISIBLE);
                    inputHint2.setText("24v Battery Cut Voltage:");
                    inputText2.setText("18");
                    break;
                case 11:
                    inputLayout.setVisibility(View.VISIBLE);
                    inputHint.setText("12v Battery LowVoltage:");
                    inputText.setText("10.5");
                    inputLayout2.setVisibility(View.VISIBLE);
                    inputHint2.setText("12v Battery Cut Voltage:");
                    inputText2.setText("7.5");
                    inputLayout3.setVisibility(View.VISIBLE);
                    inputHint3.setText("24v Battery Low Voltage:");
                    inputText3.setText("23");
                    inputLayout4.setVisibility(View.VISIBLE);
                    inputHint4.setText("24v Battery Cut Voltage:");
                    inputText4.setText("18");
                    inputLayout5.setVisibility(View.VISIBLE);
                    inputHint5.setText("Acc off debounce time:");
                    inputText5.setText("3000");
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String text = msg.obj.toString();
            if(apiIndex == 2 || apiIndex == 14) {
                resultInfo2.setText(text);
            } else {
                resultInfo.setText(text);
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_test:
                switch (apiIndex) {
                    case 0:
                        mitacAPI.getMcuService().getPowerMcuFirmwareVersion(this, new McuService.McuCmdResultListener(){
                            @Override
                            public void onResult(boolean execResult, String info) {
                                Log.d(TAG, "getPowerMcuFirmwareVersion(): execResult:"+execResult +" ,resultInfo:"+info);
                                if(apiIndex == 0) {
                                    Message msg = new Message();
                                    msg.obj = "Execute Result:"+execResult +"\nPowerMcuFirmwareVersion:"+info;
                                    handler.sendMessage(msg);
                                }
                            }
                        });
                        break;
                    case 1:
                        mitacAPI.getMcuService().getPowerMcuBootloaderVersion(this, new McuService.McuCmdResultListener(){
                            @Override
                            public void onResult(boolean execResult, String info) {
                                Log.d(TAG, "getPowerMcuBootloaderVersion(): execResult:"+execResult +" ,resultInfo:"+info);
                                if(apiIndex == 1) {
                                    Message msg = new Message();
                                    msg.obj = "Execute Result:"+execResult +"\nPowerMcuBootloaderVersion:"+info;
                                    handler.sendMessage(msg);
                                }
                            }
                        });
                        break;
                    case 2:
                        resultInfo2.setText("");
                        mitacAPI.getMcuService().updatePowerMcuFirmware(this, McuService.UpgradeMcuPath.INTERNAL_STORAGE, new McuService.McuUpgradeListener() {
                            @Override
                            public void onResult(boolean updateResult, String info) {
                                Log.d(TAG,"autoUpgradeMcu: "+updateResult + " s:"+info);
                                if(apiIndex == 2) {
                                    Message msg = new Message();
                                    msg.obj = "Update Result:"+updateResult +"\n\nUpdate Info:"+info;
                                    handler.sendMessage(msg);
                                }
                            }
                        });
                        break;
                    case 3:
                        float getBatteryVoltageResult = mitacAPI.getMcuService().getBatteryVoltage(this);
                        if(apiIndex == 3) {
                            Message msg = new Message();
                            msg.obj = "getBatteryVoltage:" + getBatteryVoltageResult;
                            handler.sendMessage(msg);
                        }
                        break;
                    case 4:
                        mitacAPI.getMcuService().getAccOffDebounceTime(this, new McuService.McuCmdResultListener() {
                            @Override
                            public void onResult(boolean execResult, String info) {
                                Log.d(TAG, "getAccOffDebounceTime(): execResult:"+execResult +" ,resultInfo:"+info);
                                if(apiIndex == 4) {
                                    Message msg = new Message();
                                    msg.obj = "Execute Result:"+execResult +"\nAccOffDebounceTime:"+info;
                                    handler.sendMessage(msg);
                                }
                            }
                        });
                        break;
                    case 5:
                        int accOffDebounceTime = Integer.parseInt(inputText.getText().toString());
                        mitacAPI.getMcuService().setAccOffDebounceTime(this,accOffDebounceTime, new McuService.McuCmdResultListener() {
                            @Override
                            public void onResult(boolean execResult, String info) {
                                Log.d(TAG, "setAccOffDebounceTime:" + execResult);
                                Log.d(TAG, "setAccOffDebounceTime:" + info);
                                if(apiIndex == 5) {
                                    Message msg = new Message();
                                    msg.obj = "Execute Result:"+execResult +"\nExecute Info:"+info;
                                    handler.sendMessage(msg);
                                }
                            }
                        });
                        break;
                    case 6:
                        mitacAPI.getMcuService().getMainBoardTemperature(this, new McuService.McuCmdResultListener(){
                            @Override
                            public void onResult(boolean execResult, String info) {
                                Log.d(TAG, "getMainBoardTemperature(): execResult:"+execResult +" ,resultInfo:"+info);
                                if(apiIndex == 6) {
                                    Message msg = new Message();
                                    msg.obj = "Execute Result:"+execResult +"\nMainBoardTemperature:"+info;
                                    handler.sendMessage(msg);
                                }
                            }
                        });
                        break;
                    case 7:
                        mitacAPI.getMcuService().getVehicleVoltageLevel(this, new McuService.McuCmdResultListener() {
                            @Override
                            public void onResult(boolean execResult, String info) {
                                Log.d(TAG, "getVoltageLevel:" + execResult);
                                Log.d(TAG, "getVoltageLevel:" + info);
                                if(apiIndex == 7) {
                                    Message msg = new Message();
                                    msg.obj = "Execute Result:"+execResult +"\nVoltageLevel:"+info;
                                    handler.sendMessage(msg);
                                }
                            }
                        });
                        break;
                    case 8:
                        double battLowVoltage12v = Double.parseDouble(inputText.getText().toString());
                        double battCutVoltage12v = Double.parseDouble(inputText2.getText().toString());
                        double battLowVoltage24v = Double.parseDouble(inputText3.getText().toString());
                        double battCutVoltage24v = Double.parseDouble(inputText4.getText().toString());
                        mitacAPI.getMcuService().setAllBatteryParams(this,battLowVoltage12v,battCutVoltage12v, battLowVoltage24v,battCutVoltage24v
                                , new McuService.McuCmdResultListener() {
                                    @Override
                                    public void onResult(boolean execResult, String info) {
                                        Log.d(TAG, "setAllBatteryParams:" + execResult);
                                        Log.d(TAG, "setAllBatteryParams:" + info);
                                        if(apiIndex == 8) {
                                            Message msg = new Message();
                                            msg.obj = "setAllBatteryParams Result:"+execResult;
                                            handler.sendMessage(msg);
                                        }
                                    }
                                });
                        break;
                    case 9:
                        double battLowVoltage12v2 = Double.parseDouble(inputText.getText().toString());
                        double battCutVoltage12v2 = Double.parseDouble(inputText2.getText().toString());
                        mitacAPI.getMcuService().set12VBatteryParams(this,battLowVoltage12v2,battCutVoltage12v2
                                , new McuService.McuCmdResultListener() {
                                    @Override
                                    public void onResult(boolean execResult, String info) {
                                        Log.d(TAG, "set12VBatteryParams:" + execResult);
                                        Log.d(TAG, "set12VBatteryParams:" + info);
                                        if(apiIndex == 9) {
                                            Message msg = new Message();
                                            msg.obj = "set12VBatteryParams Result:"+execResult;
                                            handler.sendMessage(msg);
                                        }
                                    }
                                });
                        break;
                    case 10:
                        double battLowVoltage24v2 = Double.parseDouble(inputText.getText().toString());
                        double battCutVoltage24v2 = Double.parseDouble(inputText2.getText().toString());
                        mitacAPI.getMcuService().set24VBatteryParams(this,battLowVoltage24v2,battCutVoltage24v2
                                , new McuService.McuCmdResultListener() {
                                    @Override
                                    public void onResult(boolean execResult, String info) {
                                        Log.d(TAG, "set24VBatteryParams:" + execResult);
                                        Log.d(TAG, "set24VBatteryParams:" + info);
                                        if(apiIndex == 10) {
                                            Message msg = new Message();
                                            msg.obj = "set24VBatteryParams Result:"+execResult;
                                            handler.sendMessage(msg);
                                        }
                                    }
                                });
                        break;
                    case 11:
                        double battLowVoltage12v3 = Double.parseDouble(inputText.getText().toString());
                        double battCutVoltage12v3 = Double.parseDouble(inputText2.getText().toString());
                        double battLowVoltage24v3 = Double.parseDouble(inputText3.getText().toString());
                        double battCutVoltage24v3 = Double.parseDouble(inputText4.getText().toString());
                        int accOffDebounceTime3 = Integer.parseInt(inputText5.getText().toString());
                        mitacAPI.getMcuService().setPowerMcuAllParams(this,battLowVoltage12v3,battCutVoltage12v3,
                                battLowVoltage24v3,battCutVoltage24v3,accOffDebounceTime3, new McuService.McuCmdResultListener() {
                                    @Override
                                    public void onResult(boolean execResult, String info) {
                                        Log.d(TAG, "setAllMcuParams:" + execResult);
                                        Log.d(TAG, "setAllMcuParams:" + info);
                                        if(apiIndex == 11) {
                                            Message msg = new Message();
                                            msg.obj = "setPowerMcuAllParams Result:"+execResult;
                                            handler.sendMessage(msg);
                                        }
                                    }
                                });
                        break;
                    case 12:
                        mitacAPI.getMcuService().getPowerMcuAllParams(this, new McuService.McuCmdResultListener() {
                            @Override
                            public void onResult(boolean execResult, String info) {
                                Log.d(TAG, "getAllMcuParams:" + execResult);
                                Log.d(TAG, "getAllMcuParams:" + info);
                                if(apiIndex == 12) {
                                    Message msg = new Message();
                                    msg.obj = "Execute Result:"+execResult +"\nPowerMcuAllParams:"+info;
                                    handler.sendMessage(msg);
                                }
                            }
                        });
                        break;
                    case 13:
                        mitacAPI.getMcuService().getCanMcuFirmwareInfo(this, new McuService.McuCmdResultListener(){
                            @Override
                            public void onResult(boolean execResult, String info) {
                                Log.d(TAG, "getCanMcuFirmwareInfo(): execResult:"+execResult +" ,resultInfo:"+info);
                                if(apiIndex == 13) {
                                    Message msg = new Message();
                                    msg.obj = "Execute Result:"+execResult +"\nCanMcuFirmwareInfo:"+info;
                                    handler.sendMessage(msg);
                                }
                            }
                        });
                        break;
                    case 14:
                        String filePath = inputText.getText().toString();
                        resultInfo2.setText("");
                        mitacAPI.getMcuService().updateCanMcuFirmware(this, filePath, new McuService.McuUpgradeListener() {
                            @Override
                            public void onResult(boolean upgradeResult, String upgradeInfo) {
                                Log.d(TAG, "testUpdateCanMcuFirmware(): execResult:"+upgradeResult +" ,resultInfo:"+upgradeInfo);
                                if(apiIndex == 14) {
                                    Message msg = new Message();
                                    msg.obj = "Update Result:"+upgradeResult +"\n\nUpdate Info:"+upgradeInfo;
                                    handler.sendMessage(msg);
                                }
                            }
                        });
                        break;
                    case 15:
                        mitacAPI.getMcuService().resetCanMcu(this, new McuService.ResetCanMcuListener() {
                            @Override
                            public void onResult(boolean resetResult) {
                                Log.d(TAG,"resetCanMcu:  "+ resetResult);
                                if(apiIndex == 15) {
                                    Message msg = new Message();
                                    msg.obj = "resetCanMcu Result:"+resetResult;
                                    handler.sendMessage(msg);
                                }
                            }
                        });
                        break;
                    case 16:
                        boolean isCanWakeUpSupportedResult = mitacAPI.getMcuService().isCanWakeUpSupported(this);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("isCanWakeUpSupported: "+isCanWakeUpSupportedResult);
                        break;
                }
        }
    }
}