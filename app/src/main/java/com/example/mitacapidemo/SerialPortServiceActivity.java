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
import com.mitac.api.libs.engine.NetService;
import com.mitac.api.libs.engine.SerialPortService;

public class SerialPortServiceActivity extends AppCompatActivity implements View.OnClickListener {
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

    private EditText resultInfo;
    private Spinner spinner;
    private int apiIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port_service);

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

        enableGroup = findViewById(R.id.enable_group);
        enableGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                enableRadioGroupCheckedId = checkedId;

            }
        });

        String[] portArrayStrings = new String[]{"openCanSerialPort","readCanSerialPort","writeCanSerialPort","closeCanSerialPort",
                "openRS232SerialPort","readRS232SerialPort","writeRS232SerialPort","closeRS232SerialPort","getAvailableSerialports"};
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

            switch (position) {
                case 0:
                case 4:
                    inputLayout.setVisibility(View.VISIBLE);
                    inputHint.setText("speed:");
                    inputText.setText("115200");
                    break;
                case 2:
                case 6:
                    inputLayout.setVisibility(View.VISIBLE);
                    inputHint.setText("message:");
                    inputText.setText("testMessage");
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
                        int speed = Integer.parseInt(inputText.getText().toString());
                        mitacAPI.getSerialPortService().openCanSerialPort(this, speed, new SerialPortService.SerialPortResultListener() {
                            @Override
                            public void onSuccess() {
                               // Log.d(TAG,"openCanSerialPort onSuccess");
                                resultInfo.append("openCanSerialPort success\n");
                            }

                            @Override
                            public void onFail() {
                               // Log.d(TAG,"openCanSerialPort onFail");
                                resultInfo.append("openCanSerialPort fail\n");
                            }
                        });
                        break;
                    case 1:
                        mitacAPI.getSerialPortService().readCanSerialPort(this, new SerialPortService.SerialPortMessageListener() {
                            @Override
                            public void onMessage(String s, String s1, byte[] bytes) {
                               // Log.d(TAG,"readCanSerialPort port: "+s + " message:"+s1);
                                resultInfo.append("readCanSerialPort port:"+s+" message:"+s1 +"\n");
                            }
                        });
                        break;
                    case 2:
                        String message = inputText.getText().toString();
                        mitacAPI.getSerialPortService().writeCanSerialPort(this, message, new SerialPortService.SerialPortResultListener() {
                            @Override
                            public void onSuccess() {
                                //Log.d(TAG,"writeCanSerialPort onSuccess");
                                resultInfo.append("writeCanSerialPort success\n");
                            }

                            @Override
                            public void onFail() {
                                //Log.d(TAG,"writeCanSerialPort onFail");
                                resultInfo.append("writeCanSerialPort fail\n");
                            }
                        });
                        break;
                    case 3:
                        mitacAPI.getSerialPortService().closeCanSerialPort(this, new SerialPortService.SerialPortResultListener() {
                            @Override
                            public void onSuccess() {
                                //Log.d(TAG,"closeCanSerialPort onSuccess");
                                resultInfo.append("closeCanSerialPort success\n");
                            }

                            @Override
                            public void onFail() {
                                //Log.d(TAG,"closeCanSerialPort onFail");
                                resultInfo.append("closeCanSerialPort fail\n");
                            }
                        });
                        break;
                    case 4:
                        int speed2 = Integer.parseInt(inputText.getText().toString());
                        mitacAPI.getSerialPortService().openRS232SerialPort(this, speed2, new SerialPortService.SerialPortResultListener() {
                            @Override
                            public void onSuccess() {
                                // Log.d(TAG,"openRS232SerialPort onSuccess");
                                resultInfo.append("openRS232SerialPort success\n");
                            }

                            @Override
                            public void onFail() {
                                // Log.d(TAG,"openRS232SerialPort onFail");
                                resultInfo.append("openRS232SerialPort fail\n");
                            }
                        });
                        break;
                    case 5:
                        mitacAPI.getSerialPortService().readRS232SerialPort(this, new SerialPortService.SerialPortMessageListener() {
                            @Override
                            public void onMessage(String s, String s1, byte[] bytes) {
                                // Log.d(TAG,"readRS232SerialPort port: "+s + " message:"+s1);
                                resultInfo.append("readRS232SerialPort port:"+s+" message:"+s1 +"\n");
                            }
                        });
                        break;
                    case 6:
                        String message2 = inputText.getText().toString();
                        mitacAPI.getSerialPortService().writeRS232SerialPort(this, message2, new SerialPortService.SerialPortResultListener() {
                            @Override
                            public void onSuccess() {
                                //Log.d(TAG,"writeRS232SerialPort onSuccess");
                                resultInfo.append("writeRS232SerialPort success\n");
                            }

                            @Override
                            public void onFail() {
                                //Log.d(TAG,"writeRS232SerialPort onFail");
                                resultInfo.append("writeRS232SerialPort fail\n");
                            }
                        });
                        break;
                    case 7:
                        mitacAPI.getSerialPortService().closeRS232SerialPort(this, new SerialPortService.SerialPortResultListener() {
                            @Override
                            public void onSuccess() {
                                //Log.d(TAG,"closeRS232SerialPort onSuccess");
                                resultInfo.append("closeRS232SerialPort success\n");
                            }

                            @Override
                            public void onFail() {
                                //Log.d(TAG,"closeRS232SerialPort onFail");
                                resultInfo.append("closeRS232SerialPort fail\n");
                            }
                        });
                        break;
                    case 8:
                        String[] ports = mitacAPI.getSerialPortService().getAvailableSerialports(this);
                        for(String port: ports) {
                            resultInfo.append("getAvailableSerialports :"+port +"\n");
                        }
                        break;

                }
                break;
        }
    }

}