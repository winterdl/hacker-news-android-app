package com.marcelljee.hackernews.factory;

import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.marcelljee.hackernews.R;

@SuppressWarnings("WeakerAccess")
public class SnackbarFactory {

    public static Snackbar createRetrieveErrorSnackbar(View view, View.OnClickListener listener) {
        return createSnackBar(view, R.string.message_error_retrieve_data, R.string.action_retry, listener);
    }

    public static Snackbar createSnackBar(View view, @StringRes
            int messageResId, @StringRes int actionResId, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(view, messageResId, Snackbar.LENGTH_SHORT);
        if (actionResId > 0 && listener != null) snackbar.setAction(actionResId, listener);

        return snackbar;
    }
}
