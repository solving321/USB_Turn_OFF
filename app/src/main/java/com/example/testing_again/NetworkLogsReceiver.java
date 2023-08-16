package com.example.testing_again;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.app.admin.NetworkEvent;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;


// This class records DNS requests and network connections to a logfile.
// See more on this feature here:
// https://developer.android.com/work/dpc/logging

public class NetworkLogsReceiver {

    @TargetApi(Build.VERSION_CODES.O)
    public static void onNetworkLogsAvailable(Context context, ComponentName admin, long batchToken, int networkLogsCount) {
        Log.i(Constants.TAG, format("onNetworkLogsAvailable(), batchToken: %s, event count: %s", batchToken, networkLogsCount));

        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        List<NetworkEvent> events = null;
        try {
            events = dpm.retrieveNetworkLogs(admin, batchToken);
        } catch (SecurityException e) {
            Log.e(Constants.TAG, format("Exception while retrieving network logs batch with batchToken: %s", batchToken), e);
        }

        if (events == null) {
            Log.e(Constants.TAG, format("Failed to retrieve network logs batch with batchToken: %s", batchToken));
            return;
        }

        ArrayList<String> loggedEvents = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            for (NetworkEvent event : events) {
                loggedEvents.add(event.toString());
            }
        } else {
            events.forEach(event -> loggedEvents.add(event.toString()));
        }

        // Asynchronously write logs to file:
        // /sdcard/Android/data/com.browserstack.deviceowner/files/network_logs_1_1666200461607.txt
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Date timestamp = new Date();
            String filename = format("network_logs_%s_%s.txt", batchToken, timestamp.getTime());
            File file = new File(context.getExternalFilesDir(null), filename);
            try (OutputStream os = new FileOutputStream(file)) {
                for (String event : loggedEvents) {
                    os.write((event + "\n").getBytes());
                }
                Log.d(Constants.TAG, format("Saved network logs to file: %s", filename));
            } catch (IOException e) {
                Log.e(Constants.TAG, "Failed saving network events to file", e);
            }
        });
    }
}
