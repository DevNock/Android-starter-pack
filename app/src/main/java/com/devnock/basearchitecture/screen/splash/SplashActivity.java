package com.devnock.basearchitecture.screen.splash;

import android.arch.lifecycle.ViewModelProviders;

import com.devnock.basearchitecture.R;
import com.devnock.basearchitecture.base.activity.BaseViewModelActivity;
import com.devnock.basearchitecture.base.viewmodel.GenericViewModelFactory;
import com.devnock.basearchitecture.databinding.ActivitySplashBinding;

public class SplashActivity extends BaseViewModelActivity<SplashViewModel.SplashViewAction, SplashViewModel, ActivitySplashBinding> implements SplashViewModel.SplashViewAction {
    @Override
    protected SplashViewModel createViewModel() {
        return ViewModelProviders.of(this, new GenericViewModelFactory<SplashViewModel>() {

            @Override
            protected SplashViewModel createInstance() {
                return new SplashViewModel(getIntent());
            }
        }).get(SplashViewModel.class);
    }

    @Override
    protected SplashViewModel.SplashViewAction createViewActionHandler() {
        return this;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_splash;
    }
}
