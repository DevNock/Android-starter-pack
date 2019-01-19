package com.devnock.basearchitecture.base.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.devnock.basearchitecture.application.App;
import com.devnock.basearchitecture.base.repository.BaseApiRepository;
import com.devnock.basearchitecture.base.viewmodel.livedata.Event;
import com.devnock.basearchitecture.base.viewmodel.livedata.EventLiveData;
import com.devnock.basearchitecture.utils.network.ApiUtils;

import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public class BaseViewModel<ActionT extends IBaseViewAction> extends ViewModel {

    protected final String TAG = getClass().getSimpleName();

    private EventLiveData<Action<ActionT>> viewActionLiveData = new EventLiveData<>();
    private MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();

    {
        loadingLiveData.setValue(Boolean.FALSE);
    }

    public BaseViewModel(){}

    public void init(Bundle savedInstanceState) {
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public void onStart() {
    }

    public void onResume() {
    }

    public void onStop() {
    }

    public void onPause() {

    }

    public EventLiveData<Action<ActionT>> getViewActionLiveData() {
        return viewActionLiveData;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    protected void postAction(Action<ActionT> action) {
        viewActionLiveData.setValue(Event.from(action));
    }

    protected void showLoading() {
        loadingLiveData.setValue(Boolean.TRUE);
    }

    protected void hideLoading() {
        loadingLiveData.setValue(Boolean.FALSE);
    }

    protected BaseApiRepository.CompleteListener<Response<Void>> getDefaultVoidListener(@Nullable Action<Void> action) {
        return new BaseApiRepository.CompleteListener<Response<Void>>() {
            @Override
            public void onCompleted(Response<Void> result) {
                hideLoading();
                if (result.isSuccessful()) {
                    if (action != null) {
                        action.handleAction(null);
                    }
                } else {
                    postAction(consumer -> consumer.showAPIError(result.errorBody()));
                    checkForReAuthorization(new HttpException(result));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                handleServerError(throwable);
            }
        };
    }


    protected void handleServerError(Throwable throwable) {
        hideLoading();
        postAction(consumer -> consumer.showAPIError(throwable));
        checkForReAuthorization(throwable);
    }

    protected void handleServerError(ResponseBody responseBody) {
        hideLoading();
        postAction(consumer -> consumer.showAPIError(responseBody));
        checkForReAuthorization(responseBody);
    }

    protected void checkForReAuthorization(Throwable throwable) {
        if (ApiUtils.needReauthorize(throwable)) {
            showReAuthorizationScreen();
        }
    }

    private void checkForReAuthorization(ResponseBody responseBody) {
        if (ApiUtils.needReauthorize(responseBody)) {
            showReAuthorizationScreen();
        }
    }


    protected BaseApiRepository.CompleteListener<Response<Void>> getDefaultActionListener(Action<ActionT> action) {
        return getDefaultVoidListener(consumer -> postAction(action));
    }

    protected void logError(Response<Void> result) {
        String error = "Response body error: " + ApiUtils.getApiErrorMessage(App.getContext(), result.errorBody());
        Log.w(TAG, error);
        Crashlytics.log(TAG + " " + error);
    }

    protected void logError(Exception e) {
        Crashlytics.logException(e);
        e.printStackTrace();
    }

    public Context getContext() {
        return App.getContext();
    }

    public void onBackBtnClicked() {
        finishScreen();
    }

    protected void showReAuthorizationScreen() {
        postAction(IBaseViewAction::showReAuthorizationScreen);
    }

    protected void showUnknownError() {
        postAction(consumer -> consumer.showUnknownError());
    }

    public void onCloseAccepted() {
        finishScreen();
    }

    protected void finishScreen() {
        postAction(IBaseViewAction::finishScreen);

    }
}
