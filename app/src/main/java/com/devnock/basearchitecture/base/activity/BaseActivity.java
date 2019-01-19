package com.devnock.basearchitecture.base.activity;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.devnock.basearchitecture.R;
import com.devnock.basearchitecture.base.interfaces.OnBackPressedListener;
import com.devnock.basearchitecture.widget.LoadingDialog;

public class BaseActivity extends AppCompatActivity {

    protected final String TAG = getClass().getSimpleName();

    private LoadingDialog loadingDialog;

    @Override
    protected void onStop() {
        super.onStop();
        closeLoadingDialog();
    }

    protected void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.show(this);
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

    /**
     * This is equivalent to calling {@link #showFragment(int, Fragment, boolean)}
     * with default container R.id.container
     *
     * @param fragment       - fragment to show
     * @param addToBackStack - whether this fragment should be added to back stack or not
     */
    protected void showFragment(Fragment fragment, boolean addToBackStack) {
        showFragment(R.id.container, fragment, addToBackStack);
    }

    /**
     * This is equivalent to calling {@link #showFragment(int, Fragment, boolean)}
     * with false set for the addToBackStack parameter.
     *
     * @param contentFrame - container res id to show fragment in
     * @param fragment     - fragment to show
     */
    public void showFragment(int contentFrame, Fragment fragment) {
        showFragment(contentFrame, fragment, false);
    }

    /**
     * Shows fragment in specific contentFrame
     *
     * @param contentFrame   - container res id to show fragment in
     * @param fragment       - fragment to show
     * @param addToBackStack - whether this fragment should be added to back stack or not
     */
    protected void showFragment(int contentFrame, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getCanonicalName());
        }
        transaction.replace(contentFrame, fragment);
        transaction.commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    protected void popBackStack(Class popFragmentClass, boolean inclusive) {
        getSupportFragmentManager().popBackStack(popFragmentClass != null
                        ? popFragmentClass.getCanonicalName() : null,
                inclusive ? FragmentManager.POP_BACK_STACK_INCLUSIVE : 0);
    }

    protected void showFragmentWithRightToLeftAnimation(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);

        }
        transaction.commit();
    }

    protected void openActivityCleared(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment == null) {
            forceBackPressed();
        } else if (currentFragment.getChildFragmentManager().getFragments() == null || currentFragment.getChildFragmentManager().getFragments().isEmpty()) {
            checkBackPressedListener(currentFragment);
        } else {
            checkBackPressedListener(currentFragment.getChildFragmentManager().getFragments().get(currentFragment.getChildFragmentManager().getFragments().size() - 1));
        }
    }

    private void checkBackPressedListener(Fragment fragment) {
        if (fragment != null && fragment instanceof OnBackPressedListener) {
            ((OnBackPressedListener) fragment).onBackPressed();
        } else {
            forceBackPressed();
        }
    }

    public void forceBackPressed() {
        super.onBackPressed();
    }

    protected Observer<Boolean> loadingObserver = value -> {
        if (value != null && value) {
            showLoadingDialog();
        } else {
            hideLoadingDialog();
        }
    };


}
