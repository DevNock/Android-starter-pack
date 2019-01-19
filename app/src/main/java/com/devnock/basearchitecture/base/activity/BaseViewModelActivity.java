package com.devnock.basearchitecture.base.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.devnock.basearchitecture.BR;
import com.devnock.basearchitecture.R;
import com.devnock.basearchitecture.base.interfaces.OnPermissionRequestListener;
import com.devnock.basearchitecture.base.viewmodel.Action;
import com.devnock.basearchitecture.base.viewmodel.BaseViewModel;
import com.devnock.basearchitecture.base.viewmodel.IBaseViewAction;
import com.devnock.basearchitecture.utils.LiveDataUtils;
import com.devnock.basearchitecture.utils.PermissionRequester;
import com.devnock.basearchitecture.utils.UserUtils;
import com.devnock.basearchitecture.utils.network.ApiUtils;

import okhttp3.ResponseBody;

public abstract class BaseViewModelActivity<ActionT extends IBaseViewAction, ViewModelT extends BaseViewModel<ActionT>,
        DataBindingT extends ViewDataBinding> extends BaseDataBindingActivity<DataBindingT> implements IBaseViewAction {

    private ViewModelT viewModel;
    private ActionT viewActionHandler;
    private PermissionRequester permissionRequester;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = createViewModel();
        viewActionHandler = createViewActionHandler();

        getBinding().setVariable(BR.viewModel, getViewModel());
        setupViewActionObserver();

        if (viewModel != null) {
            viewModel.init(savedInstanceState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (viewModel != null) {
            viewModel.onStart();
        }
    }

    @Override
    protected void onStop() {
        if (viewModel != null) {
            viewModel.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (viewModel != null) {
            viewModel.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (viewModel != null) {
            viewModel.onRestoreInstanceState(savedInstanceState);
        }
    }

    protected abstract ViewModelT createViewModel();

    protected abstract ActionT createViewActionHandler();

    protected ViewModelT getViewModel() {
        return viewModel;
    }

    @Override
    public void showAPIError(Throwable throwable) {
        showMessage(ApiUtils.getApiErrorMessage(this, throwable));
    }

    @Override
    public void showAPIError(ResponseBody responseBody) {
        showMessage(ApiUtils.getApiErrorMessage(this, responseBody));
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showMessage(int resId) {
        showMessage(getString(resId));
    }

    @Override
    public void showUnknownError() {
        showMessage(R.string.unknown_error);
    }

    @Override
    public void finishScreen() {
        finish();
    }

    private void setupViewActionObserver() {
        if (viewModel != null) {
            LiveDataUtils.observeNonNull(this, viewModel.getViewActionLiveData(), event -> {
                Action<ActionT> action = event.getContentIfNotHandled();
                if (action != null) {
                    action.handleAction(viewActionHandler);
                }
            });
        }
    }

    protected void subscribeForLoading() {
        getViewModel().getLoadingLiveData().observe(this, loadingObserver);
    }

    @Override
    public void checkPermission(String[] permissions, OnPermissionRequestListener onPermissionRequestListener) {
        permissionRequester = new PermissionRequester(this);
        permissionRequester.tryRequestPermission(permissions, onPermissionRequestListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissionRequester == null || !permissionRequester.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void showReAuthorizationScreen() {
        UserUtils.showReAuthorizationScreen(this);
    }

    public <T> void rewriteObserver(LiveData<T> liveData, @NonNull Observer<T> observer) {
        liveData.removeObservers(this);
        liveData.observe(this, observer);
    }
}
