package com.devnock.basearchitecture.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.devnock.basearchitecture.R;
import com.devnock.basearchitecture.model.LoadingState;
import com.devnock.basearchitecture.utils.ViewUtils;

// TODO: Layout
public class PaginatedListLoadingStateView extends FrameLayout {
    private ViewHolder viewHolder;
    private OnRetryListener onRetryListener;

    public PaginatedListLoadingStateView(@NonNull Context context) {
        super(context);

        initView();
    }

    public PaginatedListLoadingStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    public PaginatedListLoadingStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PaginatedListLoadingStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                                         int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initView();
    }

    public void setOnRetryListener(OnRetryListener onRetryListener) {
        this.onRetryListener = onRetryListener;
    }

    public void bind(LoadingState state, int failedLoadingTextResId) {
        ViewUtils.setVisible(viewHolder.loadingState, state == LoadingState.LOADING || state == LoadingState.REFRESHING);
        ViewUtils.setVisible(viewHolder.failedState, state == LoadingState.FAILED);
        viewHolder.failedReasonTV.setText(failedLoadingTextResId);

    }

    private void initView() {
        inflate(getContext(), R.layout.view_paginated_list_loading_state, this);
        viewHolder = new ViewHolder(this);

        viewHolder.retry.setOnClickListener(v -> {
            if (onRetryListener != null) {
                onRetryListener.onRetry();
            }
        });
        findViewById(R.id.root_view).setOnClickListener(v -> {
            if (onRetryListener != null) {
                onRetryListener.onRetry();
            }
        });
    }

    public interface OnRetryListener {
        void onRetry();
    }

    private static class ViewHolder {
        private final View loadingState;
        private final View failedState;
        private final Button retry;
        private final TextView failedReasonTV;

        private ViewHolder(View parent) {
            loadingState = parent.findViewById(R.id.loadingState);
            failedState = parent.findViewById(R.id.failedState);
            retry = parent.findViewById(R.id.retry);
            failedReasonTV = parent.findViewById(R.id.failed_reason_tv);
        }
    }
}
