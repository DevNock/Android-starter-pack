package com.devnock.basearchitecture.screen.splash;

import android.arch.lifecycle.ViewModel;
import android.content.Intent;

import com.devnock.basearchitecture.base.viewmodel.BaseViewModel;
import com.devnock.basearchitecture.base.viewmodel.IBaseViewAction;

public class SplashViewModel extends BaseViewModel<SplashViewModel.SplashViewAction> {

    public interface SplashViewAction extends IBaseViewAction {

    }

    public SplashViewModel(Intent intent) {
        super();
        parseIntent(intent);
    }

    private void parseIntent(Intent intent) {

    }

}
