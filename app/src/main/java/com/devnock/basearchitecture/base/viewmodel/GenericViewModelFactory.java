package com.devnock.basearchitecture.base.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

//TODO rethink of factory design, maybe all in one for all viewmodels
public abstract class GenericViewModelFactory<ViewModelT extends BaseViewModel> implements ViewModelProvider.Factory {

    @NonNull
    @Override
    public final <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        ViewModelT instance = createInstance();
        if(!modelClass.isInstance(instance)) {
            throw new IllegalArgumentException(
                    "Factory can create only " + instance.getClass().getCanonicalName() + " instances");
        }
        return (T) instance;
    }

    protected abstract ViewModelT createInstance();
}
