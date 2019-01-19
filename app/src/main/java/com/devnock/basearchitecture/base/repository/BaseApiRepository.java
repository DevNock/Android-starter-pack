package com.devnock.basearchitecture.base.repository;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.devnock.basearchitecture.utils.network.ApiClient;
import com.devnock.basearchitecture.utils.network.ApiUtils;
import com.devnock.basearchitecture.utils.network.RequestInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public abstract class BaseApiRepository extends BaseRepository {

    public interface CompleteListener<T> {
        void onCompleted(T result);

        void onError(Throwable throwable);
    }

    private final ApiClient apiClient;
    protected MutableLiveData<Boolean> needReauthorize = new MutableLiveData<>();

    protected BaseApiRepository() {
        apiClient = ApiUtils.createApiClient();
    }

    protected final <T> T createApiService(Class<T> serviceClass) {
        return apiClient.createService(serviceClass);
    }

    protected String getAuthHeader() {
        return ApiUtils.getAuthenticationHeader();
    }

    public <S> S getDefaultService(Class<S> serviceClass, String baseUrl) {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new RequestInterceptor());
        retrofitBuilder.client(builder.build());

        return retrofitBuilder.build().create(serviceClass);

    }

    public static void logError(Throwable throwable, @Nullable CompleteListener completeListener) {
        throwable.printStackTrace();
        Crashlytics.logException(throwable);
        if (completeListener != null) {
            completeListener.onError(throwable);
        }
    }

    public void onViewModelCleared() {
    }

    protected void checkForReauthorize(Throwable throwable) {
        needReauthorize.postValue(ApiUtils.needReauthorize(throwable));
    }

    public MutableLiveData<Boolean> getNeedReauthorize() {
        return needReauthorize;
    }

}
