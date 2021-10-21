package com.example.mitacapidemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.mitac.api.libs.MitacAPI;
import com.mitac.api.libs.engine.LedService;

public class LedServiceActivity extends AppCompatActivity implements View.OnClickListener {
    private MitacAPI mitacAPI;

    private Button bt_test;
    private RadioGroup ledGroup;
    private RadioGroup irGroup;
    private LinearLayout ledLayout;
    private LinearLayout irLayout;

    private Spinner spinner;
    private int apiIndex = 0;
    private int ledApiIndex = 0;
    private int irApiIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_service);

        mitacAPI = MitacAPI.getInstance();

        ledLayout = findViewById(R.id.ll_led);
        irLayout = findViewById(R.id.ll_ir);
        bt_test = findViewById(R.id.bt_test);
        bt_test.setOnClickListener(this);

        ledGroup = findViewById(R.id.led_group);
        ledGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("led","checkedId: " + checkedId);
                ledApiIndex = checkedId;

            }
        });

        irGroup = findViewById(R.id.ir_group);
        irGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("led","checkedId: " + checkedId);
                irApiIndex = checkedId;
            }
        });

        String[] portArrayStrings = new String[]{"controlLed","setIRLedOn"};
        ArrayAdapter<String> portAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_text_item, portArrayStrings);
        portAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(spinnerListener);
        spinner.setAdapter(portAdapter);
    }

    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            switch (position) {
                case 0:
                    apiIndex = 0;
                    ledLayout.setVisibility(View.VISIBLE);
                    irLayout.setVisibility(View.GONE);
                    break;
                case 1:
                    apiIndex = 1;
                    ledLayout.setVisibility(View.GONE);
                    irLayout.setVisibility(View.VISIBLE);
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
                        switch (ledApiIndex) {
                            case R.id.rb_RED_ON_NORMAL:
                                mitacAPI.getLedService().controlLed(this, LedService.LedCommand.RED_ON_NORMAL);
                                break;
                            case R.id.rb_RED_OFF:
                                mitacAPI.getLedService().controlLed(this, LedService.LedCommand.RED_OFF);
                                break;
                            case R.id.rb_RED_BLINK_SHORT:
                                mitacAPI.getLedService().controlLed(this, LedService.LedCommand.RED_BLINK_SHORT);
                                break;
                            case R.id.rb_RED_BLINK_LOOP:
                                mitacAPI.getLedService().controlLed(this, LedService.LedCommand.RED_BLINK_LOOP);
                                break;
                            case R.id.rb_GREEN_ON_NORMAL:
                                mitacAPI.getLedService().controlLed(this, LedService.LedCommand.GREEN_ON_NORMAL);
                                break;
                            case R.id.rb_GREEN_OFF:
                                mitacAPI.getLedService().controlLed(this, LedService.LedCommand.GREEN_OFF);
                                break;
                            case R.id.rb_GREEN_BLINK_SHORT:
                                mitacAPI.getLedService().controlLed(this, LedService.LedCommand.GREEN_BLINK_SHORT);
                                break;
                            case R.id.rb_GREEN_BLINK_LOOP:
                                mitacAPI.getLedService().controlLed(this, LedService.LedCommand.GREEN_BLINK_LOOP);
                                break;
                            case R.id.rb_BLUE_ON_NORMAL:
                                mitacAPI.getLedService().controlLed(this, LedService.LedCommand.BLUE_ON_NORMAL);
                                break;
                            case R.id.rb_BLUE_OFF:
                                mitacAPI.getLedService().controlLed(this, LedService.LedCommand.BLUE_OFF);
                                break;
                            case R.id.rb_BLUE_BLINK_SHORT:
                                mitacAPI.getLedService().controlLed(this, LedService.LedCommand.BLUE_BLINK_SHORT);
                                break;
                            case R.id.rb_BLUE_BLINK_LOOP:
                                mitacAPI.getLedService().controlLed(this, LedService.LedCommand.BLUE_BLINK_LOOP);
                                break;
                            case R.id.rb_ALL_OFF:
                                mitacAPI.getLedService().controlLed(this, LedService.LedCommand.ALL_OFF);
                                break;
                        }
                        break;
                    case 1:
                        switch (irApiIndex) {
                            case R.id.rb_IR_ON:
                                mitacAPI.getLedService().setIRLedOn(this,true);
                                break;
                            case R.id.rb_IR_OFF:
                                mitacAPI.getLedService().setIRLedOn(this,false);
                                break;
                        }
                        break;
                }
                break;
        }
    }

}