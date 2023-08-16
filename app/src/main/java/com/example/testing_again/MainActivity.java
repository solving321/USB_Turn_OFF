package com.example.testing_again;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mAdminComponentName;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminComponentName = new ComponentName(this, DeviceAdminReceiver.class); // Use your DeviceAdminReceiver class
        mContext = this;

        Button helloButton = findViewById(R.id.helloButton);
        helloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Hello Alert")
                        .setMessage("Turning off USB Debugging")
                        .setPositiveButton("OK", null)
                        .show();

                toggleUsbDataSignaling();
            }
        });
    }

    private void toggleUsbDataSignaling() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Hello Alert")
                .setMessage("USB Debugging Turned Off")
                .setPositiveButton("OK", null)
                .show();

        toggleADB();
    }

    private void toggleADB() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDevicePolicyManager.setGlobalSetting(mAdminComponentName, Settings.Global.ADB_ENABLED, "0"); // Set to "0" to disable ADB
            if (mContext != null) {
                Log.i(Constants.TAG, "Writing ADB toggle to file");
                writeADBToggle();
            }
        } else {
            Log.i(Constants.TAG, "Failed to disable ADB, unsupported SDK version: " + Build.VERSION.SDK_INT);
        }
    }

    private void writeADBToggle() {
        File path = mContext.getExternalFilesDir(null);
        if (path != null) {
            File file = new File(path, "adb_toggle.txt");
            Integer count = 0;
            if (file.exists()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    count = Integer.parseInt(br.readLine());
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                FileWriter myWriter = new FileWriter(file);
                myWriter.write(String.valueOf(count + 1));
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
