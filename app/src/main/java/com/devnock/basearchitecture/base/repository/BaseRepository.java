package com.devnock.basearchitecture.base.repository;

public abstract class BaseRepository {

    protected final String TAG = getClass().getSimpleName();

    public abstract void clear();
}
