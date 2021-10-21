package com.example.mitacapidemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.mitac.api.libs.MitacAPI;
import com.mitac.api.libs.engine.BatteryService;
import com.mitac.api.libs.engine.SerialPortService;

public class BatteryServiceActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "BatteryServiceActivity";
    private MitacAPI mitacAPI;

    private Button bt_test;

    private LinearLayout enableLayout;
    private RadioGroup enableGroup;
    private int enableRadioGroupCheckedId = R.id.rb_enable;

    private LinearLayout inputLayout;
    private TextView inputHint;
    private TextView inputText;

    private LinearLayout inputLayout2;
    private TextView inputHint2;
    private TextView inputText2;

    private TextView resultInfo;
    private EditText resultInfo2;
    private Spinner spinner;
    private int apiIndex = 0;

    private boolean wakeUpListenerRegistered = false;
    private boolean gpioListenerRegistered = false;
    private boolean batteryListenerRegistered = false;
    private boolean accListenerRegistered = false;
    private boolean emergencyKeyListenerRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_service);

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

        enableGroup = findViewById(R.id.enable_group);
        enableGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                enableRadioGroupCheckedId = checkedId;

            }
        });

        String[] portArrayStrings = new String[]{"registerWakeUpSourceListener","unregisterWakeUpSourceListener","registerEmergencyKeyListener",
                "unregisterEmergencyKeyListener","isBatteryLow","isBatteryCut", "registerBatteryStatusListener",
                "unregisterBatteryStatusListener","isAccOn","registerAccChangeListener","unregisterAccChangeListener","sendHeartBeat",
                "registerGPIOListener","unregisterGPIOListener","getOutputGPIOSignal","setOutputGPIOSignalHigh","setOutputGPIOSignalLow"};
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
            inputText.setText("");
            inputText2.setText("");
            resultInfo.setVisibility(View.GONE);
            resultInfo.setText("");
            resultInfo2.setVisibility(View.GONE);
            resultInfo2.setText("");

            switch (position) {
                case 0:
                case 2:
                case 6:
                case 9:
                case 12:
                    resultInfo2.setVisibility(View.VISIBLE);
                    break;
                case 4:
                case 5:
                case 8:
                case 14:
                case 15:
                case 16:
                    resultInfo.setVisibility(View.VISIBLE);
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
                        if(!wakeUpListenerRegistered) {
                            wakeUpListenerRegistered = true;
                            mitacAPI.getBatteryService().registerWakeUpSourceListener(this, new BatteryService.WakeUpSourceListener() {
                                @Override
                                public void onCanWake() {
                                    Log.d(TAG, "registerWakeUpSourceListener() onCanWake()");
                                    if(apiIndex == 0) {
                                        resultInfo2.append("onCanWake");
                                        resultInfo2.append("\n");
                                    }
                                }

                                @Override
                                public void onSusWake() {
                                    Log.d(TAG, "registerWakeUpSourceListener() onSusWake()");
                                    if(apiIndex == 0) {
                                        resultInfo2.append("onSusWake");
                                        resultInfo2.append("\n");
                                    }
                                }

                                @Override
                                public void onGsensorWake() {
                                    Log.d(TAG, "registerWakeUpSourceListener() onGsensorWake()");
                                    if(apiIndex == 0) {
                                        resultInfo2.append("onGsensorWake");
                                        resultInfo2.append("\n");
                                    }
                                }
                            });
                        }
                        break;
                    case 1:
                        if(wakeUpListenerRegistered) {
                            mitacAPI.getBatteryService().unregisterWakeUpSourceListener(this);
                            wakeUpListenerRegistered = false;
                        }
                        break;
                    case 2:
                        if(!emergencyKeyListenerRegistered) {
                            emergencyKeyListenerRegistered = true;
                            mitacAPI.getBatteryService().registerEmergencyKeyListener(this, new BatteryService.EmergencyKeyListener() {
                                @Override
                                public void onPressed() {
                                    Log.d(TAG, "registerEmergencyKeyListener() onPressed()");
                                    if(apiIndex == 2) {
                                        resultInfo2.append("EmergencyKey onPressed");
                                        resultInfo2.append("\n");
                                    }
                                }

                                @Override
                                public void onReleased() {
                                    Log.d(TAG, "registerEmergencyKeyListener() onReleased()");
                                    if(apiIndex == 2) {
                                        resultInfo2.append("EmergencyKey onReleased");
                                        resultInfo2.append("\n");
                                    }
                                }
                            });
                        }
                        break;
                    case 3:
                        if(emergencyKeyListenerRegistered) {
                            mitacAPI.getBatteryService().unregisterEmergencyKeyListener(this);
                            emergencyKeyListenerRegistered = false;
                        }
                        break;
                    case 4:
                        boolean isBatteryLowResult = mitacAPI.getBatteryService().isBatteryLow(this);
                        resultInfo.setText("isBatteryLow:"+isBatteryLowResult);
                        break;
                    case 5:
                        boolean isBatteryCutResult = mitacAPI.getBatteryService().isBatteryCut(this);
                        resultInfo.setText("isBatteryCut:"+isBatteryCutResult);
                        break;
                    case 6:
                        if(!batteryListenerRegistered) {
                            batteryListenerRegistered = true;
                            mitacAPI.getBatteryService().registerBatteryStatusListener(this, new BatteryService.BatteryStatusListener() {
                                @Override
                                public void onBatteryCut() {
                                    Log.d(TAG, "registerBatteryStatusListener() onBatteryCut()");
                                    if(apiIndex == 6) {
                                        resultInfo2.append("onBatteryCut");
                                        resultInfo2.append("\n");
                                    }
                                }

                                @Override
                                public void onBatteryLow() {
                                    Log.d(TAG, "registerBatteryStatusListener() onBatteryLow()");
                                    if(apiIndex == 6) {
                                        resultInfo2.append("onBatteryLow");
                                        resultInfo2.append("\n");
                                    }
                                }
                            });
                        }
                        break;
                    case 7:
                        if(batteryListenerRegistered) {
                            mitacAPI.getBatteryService().unregisterBatteryStatusListener(this);
                            batteryListenerRegistered = false;
                        }
                        break;
                    case 8:
                        boolean isAccOnResult = mitacAPI.getBatteryService().isAccOn(this);
                        resultInfo.setText("isAccOn:"+isAccOnResult);
                        break;
                    case 9:
                        if(!accListenerRegistered) {
                            accListenerRegistered = true;
                            mitacAPI.getBatteryService().registerAccChangeListener(this, new BatteryService.AccChangeListener() {

                                @Override
                                public void onAccOn() {
                                    Log.d(TAG, "registerAccChangeListener() onAccOn()");
                                    if(apiIndex == 9) {
                                        resultInfo2.append("onAccOn");
                                        resultInfo2.append("\n");
                                    }
                                }

                                @Override
                                public void onAccOff() {
                                    Log.d(TAG, "registerAccChangeListener() onAccOff()");
                                    if(apiIndex == 9) {
                                        resultInfo2.append("onAccOff");
                                        resultInfo2.append("\n");
                                    }
                                }
                            });
                        }
                        break;
                    case 10:
                        if(accListenerRegistered) {
                            mitacAPI.getBatteryService().unregisterAccChangeListener(this);
                            accListenerRegistered = false;
                        }
                        break;
                    case 11:
                        mitacAPI.getBatteryService().sendHeartBeat(this);
                        break;
                    case 12:
                        if(!gpioListenerRegistered) {
                            gpioListenerRegistered = true;
                            mitacAPI.getBatteryService().registerGPIOListener(this, new BatteryService.GPIOListener() {
                                @Override
                                public void onCoverDetect(String value) {
                                    Log.d(TAG, "registerGPIOListener() onCoverDetect():"+value);
                                    if(apiIndex == 12) {
                                        resultInfo2.append("onCoverDetect");
                                        resultInfo2.append("\n");
                                    }
                                }

                                @Override
                                public void onPwrbdGpio0(String value) {
                                    Log.d(TAG, "registerGPIOListener() onPwrbdGpio0():"+value);
                                    if(apiIndex == 12) {
                                        resultInfo2.append("onPwrbdGpio0");
                                        resultInfo2.append("\n");
                                    }
                                }

                                @Override
                                public void onPwrbdGpio1(String value) {
                                    Log.d(TAG, "registerGPIOListener() onPwrbdGpio1():"+value);
                                    if(apiIndex == 12) {
                                        resultInfo2.append("onPwrbdGpio1");
                                        resultInfo2.append("\n");
                                    }
                                }

                                @Override
                                public void onPwrbdGpio2(String value) {
                                    Log.d(TAG, "registerGPIOListener() onPwrbdGpio2():"+value);
                                    if(apiIndex == 12) {
                                        resultInfo2.append("onPwrbdGpio2");
                                        resultInfo2.append("\n");
                                    }
                                }
                            });
                        }
                        break;
                    case 13:
                        if(gpioListenerRegistered) {
                            mitacAPI.getBatteryService().unregisterGPIOListener(this);
                            gpioListenerRegistered = false;
                        }
                        break;
                    case 14:
                        int gpioSignal = mitacAPI.getBatteryService().getOutputGPIOSignal(this);
                        if(gpioSignal == 1) {
                            resultInfo.setText("getOutputGPIOSignal: High");
                        } else if(gpioSignal == 0) {
                            resultInfo.setText("getOutputGPIOSignal: Low");
                        } else {
                            resultInfo.setText("getOutputGPIOSignal: Fail");
                        }
                        break;
                    case 15:
                        boolean setOutputGPIOSignalHighResult = mitacAPI.getBatteryService().setOutputGPIOSignalHigh(this);
                        resultInfo.setText("setOutputGPIOSignalHigh Result:"+setOutputGPIOSignalHighResult);
                        break;
                    case 16:
                        boolean setOutputGPIOSignalLowResult = mitacAPI.getBatteryService().setOutputGPIOSignalLow(this);
                        resultInfo.setText("setOutputGPIOSignalLowResult Result:"+setOutputGPIOSignalLowResult);
                        break;
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(wakeUpListenerRegistered) {
            mitacAPI.getBatteryService().unregisterWakeUpSourceListener(this);
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
    }

}