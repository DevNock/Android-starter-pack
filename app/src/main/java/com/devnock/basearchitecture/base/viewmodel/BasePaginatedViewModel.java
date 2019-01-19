package com.devnock.basearchitecture.base.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;

import com.devnock.basearchitecture.base.repository.BasePaginatedRepository;
import com.devnock.basearchitecture.model.LoadingState;
import com.devnock.basearchitecture.widget.PaginatedListLoadingStateView;

public class BasePaginatedViewModel<ActionT extends IBaseViewAction, DataT> extends BaseViewModel<ActionT>
        implements PaginatedListLoadingStateView.OnRetryListener {

    protected BasePaginatedRepository<DataT> repository;
    private MediatorLiveData<Boolean> hasItems;
    private Observer<Boolean> onNeedReauthorizeChangedObserver;


    public BasePaginatedViewModel(BasePaginatedRepository<DataT> repository) {
        this.repository = repository;
        hasItems = new MediatorLiveData<>();
        hasItems.addSource(getData(), state -> updateHasItems());
        hasItems.addSource(getLoadingState(), state -> updateHasItems());
        setupObservers();
    }

    private void setupObservers() {
        repository.getNeedReauthorize().observeForever(getOnNeedReauthorizeChangedObserver());
    }

    private Observer<Boolean> getOnNeedReauthorizeChangedObserver() {
        if (onNeedReauthorizeChangedObserver == null) {
            onNeedReauthorizeChangedObserver = this::onNeedReauthorizeChanged;
        }
        return onNeedReauthorizeChangedObserver;
    }

    private void onNeedReauthorizeChanged(Boolean needReauthorize) {
        if (needReauthorize) {
            postAction(IBaseViewAction::showReAuthorizationScreen);
        }
    }

    public LiveData<PagedList<DataT>> getData() {
        return repository.getData();
    }

    public LiveData<LoadingState> getLoadingState() {
        return repository.getLoadingState();
    }

    public LiveData<Boolean> getHasItems() {
        return hasItems;
    }

    public MutableLiveData<Boolean> getNeedReauthorize() {
        return repository.getNeedReauthorize();
    }

    public void onRetry() {
        repository.onRetry();
    }

    public void onRefresh() {
        repository.onRefresh();
    }

    public MutableLiveData<Integer> getNeedUpdateItemId() {
        return repository.getNeedUpdateItemId();
    }

    private void updateHasItems() {
        boolean value = true;
        if (getLoadingState().getValue() != null && getLoadingState().getValue() == LoadingState.COMPLETED) {
            value = getData().getValue() != null && !getData().getValue().isEmpty();
        }

        hasItems.setValue(value);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.onViewModelCleared();
        repository.getNeedReauthorize().removeObserver(getOnNeedReauthorizeChangedObserver());
    }

}
