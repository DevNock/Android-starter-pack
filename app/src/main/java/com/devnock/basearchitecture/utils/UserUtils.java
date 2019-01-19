package com.devnock.basearchitecture.utils;

import android.app.Activity;
import android.content.Intent;

import com.devnock.basearchitecture.application.Preferences;

public class UserUtils {

    public static void showReAuthorizationScreen(Activity activity) {
        if (Preferences.isUserAuthorize()) {
            Preferences.logOut();
            // TODO:
//            openActivityCleared(activity, something);
        }
    }

    private static void openActivityCleared(Activity activity, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
