package com.devnock.basearchitecture.base.viewmodel;

import com.devnock.basearchitecture.base.interfaces.OnPermissionRequestListener;

import okhttp3.ResponseBody;

public interface IBaseViewAction {
    void showAPIError(Throwable throwable);

    void showAPIError(ResponseBody errorBody);

    void showMessage(String msg);

    void showMessage(int resId);

    void showUnknownError();

    void finishScreen();

    void checkPermission(String[] permissions, OnPermissionRequestListener onPermissionRequestListener);

    void showReAuthorizationScreen();
}
