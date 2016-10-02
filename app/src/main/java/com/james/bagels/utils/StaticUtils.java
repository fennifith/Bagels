package com.james.bagels.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;

public class StaticUtils {

    public static boolean isServiceRunning(Context context, Class service) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningService : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.getName().equals(runningService.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
