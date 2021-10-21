package com.example.mitacapidemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.mitac.api.libs.MitacAPI;
import com.mitac.api.libs.engine.NetService;
import com.mitac.api.libs.engine.SystemService;

public class NetServiceActivity extends AppCompatActivity implements View.OnClickListener {
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
    private Spinner spinner;
    private int apiIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_service);

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

        String[] portArrayStrings = new String[]{"startSoftAp","stopSoftAp","getSoftApSetting","setSoftApSetting",
                "isWifiApEnabled","getSoftApNumClients","setWifiDirectOn","setWifiDirectOff","setWifiDirectBootAutoStart",
                "setWifiDirectPassphrase","setWifiDirectShowDialog","deleteWifiDirectGroup","setHotspotTrafficShared"};
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
            resultInfo.setVisibility(View.GONE);
            resultInfo.setText("");
            inputLayout.setVisibility(View.GONE);
            inputLayout2.setVisibility(View.GONE);

            switch (position) {
                case 8:
                case 10:
                case 12:
                    enableLayout.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    inputLayout.setVisibility(View.VISIBLE);
                    inputLayout2.setVisibility(View.VISIBLE);
                    inputHint.setText("ssid:");
                    inputHint2.setText("password:");
                    inputText.setText("test");
                    inputText2.setText("12345678");
                    break;
                case 6:
                    inputLayout.setVisibility(View.VISIBLE);
                    inputHint.setText("device name:");
                    inputText.setText("test");
                    break;
                case 9:
                    inputLayout.setVisibility(View.VISIBLE);
                    inputHint.setText("passphrase:");
                    inputText.setText("12345678");
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
                        mitacAPI.getNetService().startSoftAp(this);
                        break;
                    case 1:
                        mitacAPI.getNetService().stopSoftAp(this);
                        break;
                    case 2:
                        resultInfo.setVisibility(View.VISIBLE);
                        mitacAPI.getNetService().getSoftApSetting(this, new NetService.SoftApSettingListener() {
                            @Override
                            public void onResult(String ssid, String password) {
                                resultInfo.setText("");
                                resultInfo.setText("getSelfDiagnosisList ssid: "+ssid+" password:"+password);
                            }
                        });
                        break;
                    case 3:
                        String ssid = inputText.getText().toString();
                        String password = inputText2.getText().toString();
                        mitacAPI.getNetService().setSoftApSetting(this, ssid, password);
                        break;
                    case 4:
                        boolean isWifiApEnabledResult = mitacAPI.getNetService().isWifiApEnabled(this);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("isWifiApEnabled result: " + isWifiApEnabledResult);
                        break;
                    case 5:
                        int getSoftApNumClientsResult = mitacAPI.getNetService().getSoftApNumClients(this);
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("getSoftApNumClients result: " + getSoftApNumClientsResult);
                        break;
                    case 6:
                        String deviceName = inputText.getText().toString();
                        mitacAPI.getNetService().setWifiDirectOn(this, deviceName);
                        break;
                    case 7:
                        mitacAPI.getNetService().setWifiDirectOff(this);
                        break;
                    case 8:
                        switch (enableRadioGroupCheckedId) {
                            case R.id.rb_enable:
                                mitacAPI.getNetService().setWifiDirectBootAutoStart(this, true);
                                break;
                            case R.id.rb_disable:
                                mitacAPI.getNetService().setWifiDirectBootAutoStart(this, false);
                                break;
                        }
                        break;
                    case 9:
                        String passphrase = inputText.getText().toString();
                        mitacAPI.getNetService().setWifiDirectPassphrase(this, passphrase);
                        break;
                    case 10:
                        switch (enableRadioGroupCheckedId) {
                            case R.id.rb_enable:
                                mitacAPI.getNetService().setWifiDirectShowDialog(this, true);
                                break;
                            case R.id.rb_disable:
                                mitacAPI.getNetService().setWifiDirectShowDialog(this, false);
                                break;
                        }
                        break;
                    case 11:
                        mitacAPI.getNetService().deleteWifiDirectGroup(this);
                        break;
                    case 12:
                        switch (enableRadioGroupCheckedId) {
                            case R.id.rb_enable:
                                mitacAPI.getNetService().setHotspotTrafficShared(this, true);
                                break;
                            case R.id.rb_disable:
                                mitacAPI.getNetService().setHotspotTrafficShared(this, false);
                                break;
                        }
                        break;
                }
                break;
        }
    }
}