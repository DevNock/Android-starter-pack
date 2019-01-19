package com.devnock.basearchitecture.base.fragment;

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
import com.devnock.basearchitecture.base.activity.BaseActivity;
import com.devnock.basearchitecture.base.activity.BaseViewModelActivity;
import com.devnock.basearchitecture.base.interfaces.OnBackPressedListener;
import com.devnock.basearchitecture.base.interfaces.OnPermissionRequestListener;
import com.devnock.basearchitecture.base.viewmodel.Action;
import com.devnock.basearchitecture.base.viewmodel.BaseViewModel;
import com.devnock.basearchitecture.base.viewmodel.IBaseViewAction;
import com.devnock.basearchitecture.utils.LiveDataUtils;
import com.devnock.basearchitecture.utils.UserUtils;
import com.devnock.basearchitecture.utils.network.ApiUtils;

import okhttp3.ResponseBody;

public abstract class BaseViewModelFragment<ActionT extends IBaseViewAction, ViewModelT extends BaseViewModel<ActionT>,
        DataBindingT extends ViewDataBinding> extends BaseDataBindingFragment<DataBindingT> implements IBaseViewAction,
        OnBackPressedListener {

    protected final String TAG = this.getClass().getSimpleName();
    private ViewModelT viewModel;
    private ActionT viewActionHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = createViewModel();
        viewActionHandler = createViewActionHandler();

        if (savedInstanceState != null) {
            viewModel.onRestoreInstanceState(savedInstanceState);
        }

        setupViewActionObserver();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getBinding().setVariable(BR.viewModel, getViewModel());
        if (viewModel != null) {
            viewModel.init(savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (viewModel != null) {
            viewModel.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.onResume();
        }
    }

    @Override
    public void onStop() {
        if (viewModel != null) {
            viewModel.onStop();
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        if (viewModel != null) {
            viewModel.onPause();
        }
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (viewModel != null) {
            viewModel.onSaveInstanceState(outState);
        }
    }

    protected ViewModelT getViewModel() {
        return viewModel;
    }

    protected abstract ViewModelT createViewModel();

    protected abstract ActionT createViewActionHandler();

    @Override
    public void showAPIError(Throwable throwable) {
        showMessage(ApiUtils.getApiErrorMessage(getActivity(), throwable));
    }

    @Override
    public void showAPIError(ResponseBody responseBody) {
        showMessage(ApiUtils.getApiErrorMessage(getActivity(), responseBody));
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
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
        if (getParentFragment() != null) {
            getParentFragment().getChildFragmentManager().popBackStack();
        } else {
            if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getActivity().getSupportFragmentManager().popBackStack();
            } else {
                ((BaseActivity) getActivity()).forceBackPressed();
            }
        }
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
        getViewModel().getLoadingLiveData().observe(this, blockedLoadingObserver);
    }

    @Override
    public void checkPermission(String[] permissions, OnPermissionRequestListener onPermissionRequestListener) {
        ((BaseViewModelActivity) getActivity()).checkPermission(permissions, onPermissionRequestListener);
    }

    public <T> void rewriteObserver(LiveData<T> liveData, @NonNull Observer<T> observer) {
        liveData.removeObservers(this);
        liveData.observe(this, observer);
    }

    @Override
    public void showReAuthorizationScreen() {
        UserUtils.showReAuthorizationScreen(getActivity());
    }

    @Override
    public void onBackPressed() {
        getViewModel().onBackBtnClicked();
    }

}
