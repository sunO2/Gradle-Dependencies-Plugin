//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hezhihu.testdemo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

public class SafeDialog extends AppCompatDialog {
    private OnDismissListener onDismissListener;

    public SafeDialog(Context context) {
        super(context);
    }

    public SafeDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected SafeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void dismiss() {
        super.dismiss();
    }

    public void show() {
        try {
            super.show();
        } catch (Exception var2) {
            if (this.onDismissListener != null) {
                this.onDismissListener.onDismiss(this);
            }
        }
    }

    public void setOnDismissListener(OnDismissListener listener) {
        super.setOnDismissListener(listener);
        this.onDismissListener = listener;
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(@NonNull AccessibilityEvent event) {
        event.setClassName(getClass().getName());
        event.setPackageName(getContext().getPackageName());
        return true;
    }

    public OnDismissListener getOnDismissListener() {
        return this.onDismissListener;
    }
}
