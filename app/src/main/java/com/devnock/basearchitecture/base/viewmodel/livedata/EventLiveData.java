package com.devnock.basearchitecture.base.viewmodel.livedata;

import android.arch.lifecycle.MutableLiveData;

public class EventLiveData<T> extends MutableLiveData<Event<T>> {

    @Override
    public void setValue(Event<T> value) {
//        if(hasActiveObservers()) {
            super.setValue(value);
//        }
    }

}
