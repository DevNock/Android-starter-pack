package com.devnock.basearchitecture.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.devnock.basearchitecture.R;
import com.devnock.basearchitecture.base.interfaces.OnPermissionRequestListener;

import java.util.ArrayList;
import java.util.List;

public class PermissionRequester {

    private static final int REQUEST_CODE_PERMISSION = 99;
    private static final String TAG = PermissionRequester.class.getSimpleName();

    private final Activity activity;
    private String[] arrayPermissions;
    private OnPermissionRequestListener listener;

    public PermissionRequester(Activity activity) {
        this.activity = activity;
    }

    public static boolean isNeedHandlePermissions() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public void tryRequestPermission(String[] arrayPermissions, OnPermissionRequestListener listener) {
        this.arrayPermissions = arrayPermissions;
        if (isNeedHandlePermissions()) {
            this.listener = listener;
            updatePermissionState();
        } else {
            listener.onPermissionGranted();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void updatePermissionState() {
        List<String> permissions = getDeniedPermissionForAndroidM();
        if (permissions.size() != 0) {
            activity.requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_CODE_PERMISSION);
        } else {
            listener.onPermissionGranted();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private List<String> getDeniedPermissionForAndroidM() {
        List<String> permissions = new ArrayList<>();
        for (int i = 0; i < arrayPermissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, arrayPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(arrayPermissions[i]);
            }
        }
        return permissions;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION: {
                List<String> deniedPermissions = new ArrayList<>();
                for (int i = 0; i < permissions.length; i++) {
                    Log.d(TAG, "Permission receive" + permissions[i]);
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Permission Granted: " + permissions[i]);
                    } else {
                        Log.d(TAG, "Permission Denied: " + permissions[i]);
                        deniedPermissions.add(permissions[i]);
                    }
                }
                if (deniedPermissions.isEmpty()) {
                    listener.onPermissionGranted();
                } else {
                    checkDeniedPermissions(deniedPermissions);
                    listener.onPermissionDenied();
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }

    private void checkDeniedPermissions(List<String> deniedPermissions) {
        if (!getNeverAskAgainPermission(deniedPermissions).isEmpty()) {
            showStoragePermissionRationaleDialog();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showStoragePermissionRationaleDialog() {
        new AlertDialog.Builder(activity)
                .setMessage(activity.getString(R.string.never_ask_for_write_permission_rationale))
                .setPositiveButton(activity.getString(R.string.allow_button),
                        (dialog, which) -> {
                            openAppSettings();
                            listener.onPermissionDenied();
                        })
                .setNegativeButton(activity.getString(R.string.i_dont_want_button),
                        (dialog, which) -> listener.onPermissionDenied())
                .create()
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private List<String> getNeverAskAgainPermission(List<String> deniedPermissions) {
        List<String> neverAskAgainPermission = new ArrayList<>();
        for (String deniedPermission : deniedPermissions) {
            if (!activity.shouldShowRequestPermissionRationale(deniedPermission)) {
                neverAskAgainPermission.add(deniedPermission);
            }
        }
        return neverAskAgainPermission;
    }

    public static boolean isWritePermissionGranted(Context context) {
        return isPermissionGranted(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static boolean isPermissionGranted(Context context, String permission) {
        if (isNeedHandlePermissions()) {
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
}
