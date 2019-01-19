package com.devnock.basearchitecture.utils.network;

import android.content.Context;
import android.support.annotation.Nullable;

import com.devnock.basearchitecture.R;
import com.devnock.basearchitecture.application.Preferences;
import com.devnock.basearchitecture.utils.entity.ServerErrorEntity;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.File;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.HttpException;

public class ApiUtils {

    public static final int NO_INTERNET_ERROR_CODE = 999;
    public static final int UNKNOWN_ERROR_CODE = 998;
    public static final int NEED_RE_AUTHORIZATION_ERROR_CODE = 401;

    public static ApiClient createApiClient() {
        return new ApiClient();
    }


    public static int getApiErrorCode(Throwable throwable) {
        if (throwable instanceof HttpException) {
            return getApiErrorCode(((HttpException) throwable).response().errorBody());
        } else if (throwable instanceof UnknownHostException) {
            return NO_INTERNET_ERROR_CODE;
        }
        return UNKNOWN_ERROR_CODE;
    }

    public static int getRequestErrorCode(Throwable throwable) {
        int errorCode = UNKNOWN_ERROR_CODE;
        if (throwable instanceof HttpException) {
            errorCode = ((HttpException) throwable).response().code();
        } else if (throwable instanceof UnknownHostException) {
            errorCode = NO_INTERNET_ERROR_CODE;
        }
        return errorCode;
    }

    public static String getApiErrorMessage(Context context, Throwable throwable) {
        if (throwable instanceof HttpException) {
            return getApiErrorMessage(context, ((HttpException) throwable).response().errorBody());
        } else if (throwable instanceof UnknownHostException) {
            return context.getString(R.string.no_internet_error);
        } else if (throwable instanceof SocketTimeoutException || throwable instanceof TimeoutException) {
            return context.getString(R.string.time_out_exception);
        }

        return throwable.getMessage();
    }

    public static int getApiErrorCode(ResponseBody errorBody) {
        ServerErrorEntity serverErrorEntity = parseApiError(errorBody);
        return serverErrorEntity != null ? serverErrorEntity.getErrorCode() : UNKNOWN_ERROR_CODE;
    }

    public static String getApiErrorMessage(Context context, ResponseBody errorBody) {
        ServerErrorEntity serverErrorEntity = parseApiError(errorBody);
        return serverErrorEntity != null ? serverErrorEntity.getMessage() : context.getString(R.string.unknown_error);
    }

    public static ServerErrorEntity getServerErrorEntity(Throwable throwable) {
        if (throwable instanceof HttpException) {
            return parseApiError(((HttpException) throwable).response().errorBody());
        } else {
            return null;
        }
    }

    public static String getAuthenticationHeader() {
        return Preferences.getAuthToken();
    }

    public static MultipartBody.Part createBodyForImageLoading(File file) {
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/formData"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        return body;
    }

    public static ServerErrorEntity parseApiError(Throwable throwable) {
        if (throwable instanceof HttpException) {
            ServerErrorEntity entity = parseApiError(((HttpException) throwable).response().errorBody());
            return entity;
        } else {
            return null;
        }
    }

    public static ServerErrorEntity parseApiError(ResponseBody responseBody) {
        try {
            return new Gson().fromJson(cloneResponseBody(responseBody).charStream(), ServerErrorEntity.class);
        } catch (JsonParseException e) {
            return null;
        }
    }

    private static ResponseBody cloneResponseBody(ResponseBody responseBody) {
        final Buffer bufferClone = responseBody.source().buffer().clone();
        return ResponseBody.create(responseBody.contentType(), responseBody.contentLength(), bufferClone);
    }

    public static boolean needReauthorize(Throwable throwable) {
        if (ApiUtils.getRequestErrorCode(throwable) == ApiUtils.NEED_RE_AUTHORIZATION_ERROR_CODE) {
            return true;
        } else {
            ServerErrorEntity serverErrorEntity = parseApiError(throwable);
            return needReauthorize(serverErrorEntity);
        }
    }

    private static boolean needReauthorize(@Nullable ServerErrorEntity serverErrorEntity) {
        if (serverErrorEntity != null
                && serverErrorEntity.getErrorCode() == ServerErrorEntity.INVALID_TOKEN) {
            return true;
        }
        return false;
    }

    public static boolean needReauthorize(ResponseBody responseBody) {
        return needReauthorize(parseApiError(responseBody));
    }
}
