package com.devnock.basearchitecture.base.viewmodel;

public interface Action<T> {
    void handleAction(T consumer);
}
