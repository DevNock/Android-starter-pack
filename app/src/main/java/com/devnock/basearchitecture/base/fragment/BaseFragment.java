package com.devnock.basearchitecture.base.fragment;

import android.arch.lifecycle.Observer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.devnock.basearchitecture.widget.LoadingDialog;

public class BaseFragment extends Fragment {

    protected final String TAG = getClass().getSimpleName();

    private LoadingDialog loadingDialog;

    @Override
    public void onStop() {
        super.onStop();
        closeLoadingDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        String title = getTitle();
        if (title != null)
            getActivity().setTitle(title);
    }

    protected void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.show(requireContext());
        }
    }

    protected void hideLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.hide();
            closeLoadingDialog();
        }
    }

    private void closeLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    protected String getTitle() {
        return null;
    }

    protected void showFragment(int contentFrame, Fragment fragment) {
        showFragment(contentFrame, fragment, false);
    }

    protected void showFragment(int contentFrame, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getCanonicalName());
        }
        transaction.replace(contentFrame, fragment);
        transaction.commit();
        getChildFragmentManager().executePendingTransactions();
    }

    protected Observer<Boolean> blockedLoadingObserver = value -> {
        if (value != null && value) {
            showLoadingDialog();
        } else {
            hideLoadingDialog();
        }
    };
}
