package com.devnock.basearchitecture.base.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseDataBindingFragment<T extends ViewDataBinding> extends BaseFragment {

	private T binding;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(fillLayoutInflater(inflater), getLayoutResId(), container, false);
		binding.setLifecycleOwner(this);
		return binding.getRoot();
	}

	protected LayoutInflater fillLayoutInflater(LayoutInflater inflater) {
		return inflater;
	}

	protected abstract int getLayoutResId();

	protected T getBinding() {
		return binding;
	}
}
