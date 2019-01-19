package com.devnock.basearchitecture.base.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Consumer;
import com.devnock.basearchitecture.model.LoadingState;
import com.devnock.basearchitecture.utils.Constants;
import com.devnock.basearchitecture.utils.LiveDataUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class BasePaginatedRepository<DataT> extends BaseApiRepository {

    protected final MutableLiveData<LoadingState> loadingState = new MutableLiveData<>();

    protected Optional<Runnable> retryCallback = Optional.empty();

    protected LiveData<PagedList<DataT>> data;
    private LinkedHashMap<String, DataT> backedData;
    protected MutableLiveData<Integer> needUpdateItemId = new MutableLiveData<>();

    public BasePaginatedRepository() {
        super();
        initData();
    }

    protected void initData() {
        backedData = new LinkedHashMap<>();
        data = LiveDataUtils.createLivePagedList(BasePaginatedRepository.BaseDataSource::new,
                getPageSize(), getPrefetchDistance());
        loadingState.setValue(LoadingState.COMPLETED);
    }

    protected int getPageSize() {
        return Constants.DEFAULT_PAGE_SIZE;
    }

    protected int getPrefetchDistance() {
        return Constants.DEFAULT_PREFETCH_DISTANCE;
    }

    public void onRetry() {
        if (loadingState.getValue() == LoadingState.FAILED) {
            retryCallback.ifPresent(Runnable::run);
            retryCallback = Optional.empty();
        }
    }

    public LiveData<PagedList<DataT>> getData() {
        return data;
    }

    public LiveData<LoadingState> getLoadingState() {
        return loadingState;
    }

    public void onRefresh() {
        refresh(true);
    }

    @Override
    public void clear() {
        backedData.clear();
        if (data.getValue() != null) {
            data.getValue().getDataSource().invalidate();
        }
    }

    public class BaseDataSource extends PositionalDataSource<DataT> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<DataT> callback) {
            BasePaginatedRepository.this.loadInitial(params, callback);
        }

        @Override
        public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<DataT> callback) {
            BasePaginatedRepository.this.loadRange(params, callback);
        }
    }

    protected void loadInitial(PositionalDataSource.LoadInitialParams params,
                               PositionalDataSource.LoadInitialCallback<DataT> callback) {
        if (backedData.isEmpty()) {
            loadData(0, params.requestedLoadSize, data -> callback.onResult(data, 0),
                    () -> loadInitial(params, callback));
        } else {
            callback.onResult(getBackedDataList(), 0);
        }
    }

    protected void loadRange(@NonNull PositionalDataSource.LoadRangeParams params,
                             @NonNull PositionalDataSource.LoadRangeCallback<DataT> callback) {
        loadData(params.startPosition, params.loadSize, callback::onResult, () -> loadRange(params, callback));
    }


    protected void notifyBackedDataChange() {
        refresh(false);
    }

    protected void updateLoadingState(@Nullable Runnable retryCallback) {
        loadingState.postValue((retryCallback == null) ? LoadingState.COMPLETED : LoadingState.FAILED);
        this.retryCallback = Optional.ofNullable(retryCallback);
    }

    private void loadData(int offset, int limit, Consumer<List<DataT>> onSuccess, Runnable retryCallback) {
        loadingState.postValue(offset == 0 ? LoadingState.REFRESHING : LoadingState.LOADING);
        loadData(offset, limit, new CompleteListener<List<DataT>>() {
            @Override
            public void onCompleted(List<DataT> result) {
                updateLoadingState(null);
                onSuccess.accept(result);
                addBackedData(result);
            }

            @Override
            public void onError(Throwable throwable) {
                updateLoadingState(retryCallback);
                checkForReauthorize(throwable);
            }
        });
    }

    protected abstract void loadData(int offset, int limit, CompleteListener<List<DataT>> completeListener);

    private void refresh(boolean removeBackedData) {
        if (removeBackedData) {
            backedData.clear();
        }
        if (data.getValue() != null) {
            data.getValue().getDataSource().invalidate();
        }
    }

    protected void addBackedData(List<DataT> newData) {
        for (DataT item : newData) {
            backedData.put(getItemId(item), item);
        }
    }

    protected void updateBackedData(Collection<DataT> newData) {
        backedData = new LinkedHashMap<>();
        for (DataT item : newData) {
            backedData.put(getItemId(item), item);
        }
    }

    protected String getItemId(DataT item) {
        return String.valueOf(item.hashCode());
    }

    protected LinkedHashMap<String, DataT> getBackedData() {
        return backedData;
    }

    protected LinkedList<DataT> getBackedDataList() {
        return new LinkedList<>(backedData.values());
    }

    public MutableLiveData<Integer> getNeedUpdateItemId() {
        return needUpdateItemId;
    }

}
