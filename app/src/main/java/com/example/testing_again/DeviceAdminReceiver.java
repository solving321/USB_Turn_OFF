package com.example.testing_again;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

// Receives the ACTION_DEVICE_ADMIN_ENABLED broadcast intent, granting
// the app device owner abilities. This class can also catch other intents related to
// administrator settings and react to them.
// Publishing this receiver requires BIND_DEVICE_ADMIN permission in AndroidManifest.

public class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {

    // Returns an identifier for the device admin component
    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), DeviceAdminReceiver.class);
    }

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        Log.d(Constants.TAG, "Device Owner Enabled");
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onNetworkLogsAvailable(Context context, Intent intent, long batchToken, int networkLogsCount) {
        Log.i(Constants.TAG, "DeviceAdminReceiver: received network logs available broadcast");
        NetworkLogsReceiver.onNetworkLogsAvailable(context, getComponentName(context), batchToken, networkLogsCount);
    }
}
