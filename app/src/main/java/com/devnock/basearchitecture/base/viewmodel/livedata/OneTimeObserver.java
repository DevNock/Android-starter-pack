package com.devnock.basearchitecture.base.viewmodel.livedata;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class OneTimeObserver<T> implements Observer<T> {
	private final LiveData<T> liveData;

	protected OneTimeObserver(LiveData<T> liveData) {
		this.liveData = liveData;
	}

	@Override
	public final void onChanged(@Nullable T value) {
	    if(value != null) {
            handle(value);
            liveData.removeObserver(this);
        }
	}

	protected abstract void handle(@NonNull T value);
}
