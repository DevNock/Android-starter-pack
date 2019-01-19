package com.devnock.basearchitecture.base.activity;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;

public abstract class BaseDataBindingActivity<T extends ViewDataBinding> extends BaseActivity {

    private T binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, getLayoutResId());
        binding.setLifecycleOwner(this);
    }

    protected abstract int getLayoutResId();

    protected T getBinding() {
        return binding;
    }

}
