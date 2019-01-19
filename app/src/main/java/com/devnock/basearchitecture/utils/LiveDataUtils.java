package com.devnock.basearchitecture.utils;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Supplier;

public final class LiveDataUtils {

    private LiveDataUtils() {
    }

    public static <T> void observeNonNull(LifecycleOwner owner, LiveData<T> liveData, Consumer<T> observer) {
        liveData.observe(owner, value -> {
            if (value != null) {
                observer.accept(value);
            }
        });
    }

    public static <T> LiveData<PagedList<T>> createLivePagedList(Supplier<DataSource<Integer, T>> dataSourceCreator,
                                                                 int pageSize, int prefetchDistance) {
        final DataSource.Factory<Integer, T> dataSourceFactory = new DataSource.Factory<Integer, T>() {
            @Override
            public DataSource<Integer, T> create() {
                return dataSourceCreator.get();
            }
        };
        final PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(pageSize)
                .setPageSize(pageSize)
                .setPrefetchDistance(prefetchDistance)
                .setEnablePlaceholders(false)
                .build();
        return new LivePagedListBuilder<>(dataSourceFactory, config).build();
    }
}
