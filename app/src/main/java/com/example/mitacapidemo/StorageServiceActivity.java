package com.example.mitacapidemo;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.format.Formatter;
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

import androidx.appcompat.app.AppCompatActivity;

import com.mitac.api.libs.MitacAPI;
import com.mitac.api.libs.engine.StorageService;
import com.mitac.api.libs.engine.SystemService;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class StorageServiceActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "StorageServiceActivity";
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

    private boolean brokenSdcardListenerRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_service);

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

        String[] portArrayStrings = new String[]{"registerBrokenSdcardListener","unregisterBrokenSdcardListener", "formatSdCard", "isSDCardMounted", "getSdCardPartitionsNum","addSdCardPartition"
                , "addSdCardPartitionAllUnallocated", "deleteSdCardAllPartitions",
                "deleteSdCardPartition","formatSdCardPartition","getSdCardTotalSize", "getSdCardPartitionSize","getSdcardVolumeInfo"};
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
            resultInfo.setVisibility(View.VISIBLE);
            resultInfo.setText("");
            resultInfo2.setVisibility(View.GONE);
            resultInfo2.setText("");
            if(brokenSdcardListenerRegistered) {
                mitacAPI.getStorageService().unregisterBrokenSdcardListener(StorageServiceActivity.this);
                brokenSdcardListenerRegistered = false;
            }

            switch (position) {
                case 2:
                    inputLayout.setVisibility(View.VISIBLE);
                    inputHint.setText("new label(Not necessary):");
                    inputText.setText("");
                    break;
                case 5:
                    inputLayout.setVisibility(View.VISIBLE);
                    inputHint.setText("partition size(M):");
                    inputText.setText("200");
                    break;
                case 9:
                    inputLayout.setVisibility(View.VISIBLE);
                    inputHint.setText("partition id():");
                    inputText.setText("");
                    inputLayout2.setVisibility(View.VISIBLE);
                    inputHint2.setText("new label(Not necessary):");
                    inputText2.setText("");
                    break;
                case 8:
                case 11:
                    inputLayout.setVisibility(View.VISIBLE);
                    inputHint.setText("partition id():");
                    inputText.setText("");
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
            resultInfo.setText(text);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_test:
                switch (apiIndex) {
                    case 0:
                        if(!brokenSdcardListenerRegistered) {
                            brokenSdcardListenerRegistered = true;
                            mitacAPI.getStorageService().registerBrokenSdcardListener(this, new StorageService.BrokenSdcardListener() {
                                @Override
                                public void sdCardUnplug() {
                                    if(apiIndex == 0) {
                                        resultInfo.append("registerBrokenSdcardListener: sdCardUnplug");
                                    }
                                }

                                @Override
                                public void brokenSdcardPlugin() {
                                    if(apiIndex == 0) {
                                        resultInfo.append("registerBrokenSdcardListener: brokenSdcardPlugin");
                                    }
                                }

                                @Override
                                public void fileSystemUnsupported() {
                                    if(apiIndex == 0) {
                                        resultInfo.append("registerBrokenSdcardListener: fileSystemUnsupported");
                                    }
                                }
                            });
                        }
                        break;
                    case 1:
                        if(brokenSdcardListenerRegistered) {
                            mitacAPI.getStorageService().unregisterBrokenSdcardListener(this);
                            brokenSdcardListenerRegistered = false;
                        }
                        break;
                    case 2:
                        resultInfo.setVisibility(View.VISIBLE);
                        resultInfo.setText("SdCard is formatting,please wait result..");
                        String newLabel = inputText.getText().toString();
                        mitacAPI.getStorageService().formatSdCard(this, newLabel, new StorageService.FormatSdCardResultListener() {
                            @Override
                            public void onResult(boolean success) {
                                if(apiIndex == 2) {
                                    resultInfo.setText("formatSdCard format result: "+success);
                                }
                            }
                        });
                        break;
                    case 3:
                        boolean isSDCardMountedResult = mitacAPI.getStorageService().isSDCardMounted(this);
                        resultInfo.setText("isSDCardMounted: "+isSDCardMountedResult);
                        break;
                    case 4:
                        int getSdCardPartitionsNumResult = mitacAPI.getStorageService().getSdCardPartitionsNum(this);
                        resultInfo.setText("getSdCardPartitionsNum: "+getSdCardPartitionsNumResult);
                        break;
                    case 5:
                        resultInfo.setText("Creating partition,please wait result..");
                        int partitionSize = Integer.parseInt(inputText.getText().toString());
                        mitacAPI.getStorageService().addSdCardPartition(this, partitionSize, new StorageService.AddSdCardPartitionListener() {
                            @Override
                            public void onResult(int partitionId) {
                                Log.d(TAG,"addSdCardPartition: "+partitionId);
                                Message msg = new Message();
                                msg.obj = "addSdCardPartition: " + partitionId;
                                handler.sendMessage(msg);
                            }
                        });
                        break;
                    case 6:
                        resultInfo.setText("Creating partition,please wait result..");
                        mitacAPI.getStorageService().addSdCardPartitionAllUnallocated(this, new StorageService.AddSdCardPartitionListener() {
                            @Override
                            public void onResult(int partitionId) {
                                Log.d(TAG,"addSdCardPartitionAllUnallocated: "+partitionId);
                                Message msg = new Message();
                                msg.obj = "addSdCardPartitionAllUnallocated: " + partitionId;
                                handler.sendMessage(msg);
                            }
                        });
                        break;
                    case 7:
                        resultInfo.setText("Deleting partition,please wait result..");
                        mitacAPI.getStorageService().deleteSdCardAllPartitions(this, new StorageService.DeleteSdCardPartitionListener() {
                            @Override
                            public void onResult(boolean success) {
                                Log.d(TAG,"deleteSdCardAllPartitions: "+success);
                                Message msg = new Message();
                                msg.obj = "deleteSdCardAllPartitions result:" + success;
                                handler.sendMessage(msg);
                            }
                        });
                        break;
                    case 8:
                        resultInfo.setText("Deleting partition,please wait result..");
                        int partitionId = Integer.parseInt(inputText.getText().toString());
                        mitacAPI.getStorageService().deleteSdCardPartition(this, partitionId, new StorageService.DeleteSdCardPartitionListener() {
                            @Override
                            public void onResult(boolean success) {
                                Log.d(TAG,"deleteSdCardPartition: "+success);
                                Message msg = new Message();
                                msg.obj = "deleteSdCardPartitions result:" + success;
                                handler.sendMessage(msg);
                            }
                        });
                        break;
                    case 9:
                        resultInfo.setText("SdCard is formatting,please wait result..");
                        int partitionId2 = Integer.parseInt(inputText.getText().toString());
                        String partitionnewLabel = inputText2.getText().toString();
                        mitacAPI.getStorageService().formatSdCardPartition(this, partitionId2, partitionnewLabel, new StorageService.FormatSdCardResultListener() {
                            @Override
                            public void onResult(boolean result) {
                                Log.d(TAG,"formatSdCardPartition: " + result);
                                resultInfo.setText("formatSdCardPartition:" + result);
                            }
                        });
                        break;
                    case 10:
                        int getSdCardTotalSizeResult = mitacAPI.getStorageService().getSdCardTotalSize(this);
                        Log.d(TAG,"getSdCardTotalSize: " + getSdCardTotalSizeResult);
                        resultInfo.setText("getSdCardTotalSize:" + getSdCardTotalSizeResult);
                        break;
                    case 11:
                        int partitionId3 = Integer.parseInt(inputText.getText().toString());
                        int getSdCardPartitionSizeResult = mitacAPI.getStorageService().getSdCardPartitionSize(this, partitionId3);
                        Log.d(TAG,"getSdCardPartitionSize: " + getSdCardPartitionSizeResult);
                        resultInfo.setText("getSdCardPartitionSize:" + getSdCardPartitionSizeResult);
                        break;
                    case 12:
                        List<StorageService.MitacSdcardVolumeInfo> volumeInfoList= mitacAPI.getStorageService().getSdcardVolumeInfoList(this);
                        if(volumeInfoList.size()==0) {
                            Log.d(TAG,"getSdcardVolumeInfo size()==0" );
                            resultInfo.setText("getSdcardVolumeInfo size 0");
                        } else {
                            for(StorageService.MitacSdcardVolumeInfo info:volumeInfoList) {
                                Log.d(TAG,"volumeInfoList: " + info.getId() + " "+ info.getMountState() + " "+ info.getPartitionNum()+ " "+ info.getUuid()+ " "+ info.getLabel());
                                resultInfo.append("volumeInfoList- Id:" + info.getId() + " MountState:"+ info.getMountState() + " Partition Num:"+ info.getPartitionNum()+ " UUID:"+ info.getUuid()+ " Label:"+ info.getLabel() + "\n");
                            }
                        }
                        break;
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(brokenSdcardListenerRegistered) {
            mitacAPI.getStorageService().unregisterBrokenSdcardListener(this);
        }
    }
}