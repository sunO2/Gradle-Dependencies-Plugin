//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hezhihu.testdemo.dialog;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.hezhihu.testdemo.R;

import java.util.ArrayList;
import java.util.List;

public class ActionSheet extends SafeDialog {
    ActionSheet(Context context, int themeResId) {
        super(context, themeResId);
    }

    private static class ActionSheetMenu {
        String funName;
        public View.OnClickListener listener;

        ActionSheetMenu(String funName, View.OnClickListener listener) {
            this.funName = funName;
            this.listener = listener;
        }
    }

    public static class Builder {
        private Context context;
        private ActionSheet.Params params = new ActionSheet.Params();

        public Builder(Context context) {
            this.context = context;
        }

        public ActionSheet.Builder setCanCancel(boolean canCancel) {
            this.params.canCancel = canCancel;
            return this;
        }

        public ActionSheet.Builder setShadow(boolean shadow) {
            this.params.shadow = shadow;
            return this;
        }

        public ActionSheet.Builder setTitle(String title) {
            this.params.menuTitle = title;
            return this;
        }

        public ActionSheet.Builder addMenu(String text, View.OnClickListener listener) {
            ActionSheet.ActionSheetMenu bm = new ActionSheet.ActionSheetMenu(text, listener);
            this.params.menuList.add(bm);
            return this;
        }

        public ActionSheet.Builder addMenu(int textId, View.OnClickListener listener) {
            return this.addMenu(this.context.getString(textId), listener);
        }

        public ActionSheet.Builder setCancelListener(View.OnClickListener cancelListener) {
            this.params.cancelListener = cancelListener;
            return this;
        }

        public ActionSheet.Builder setCancelText(int resId) {
            this.params.cancelText = this.context.getString(resId);
            return this;
        }

        public ActionSheet.Builder setCancelText(String text) {
            this.params.cancelText = text;
            return this;
        }

        private ActionSheet createBaseDialog() {
            ActionSheet dialog = new ActionSheet(this.context, 0);
            Window window = dialog.getWindow();
//            if (window != null) {
//                window.getDecorView().setPadding(0, 0, 0, 0);
//                LayoutParams lp = window.getAttributes();
//                lp.width = -1;
//                lp.height = -2;
//                window.setAttributes(lp);
//                window.setGravity(80);
//            }
//
//            dialog.setCanceledOnTouchOutside(this.params.canCancel);
//            dialog.setCancelable(this.params.canCancel);
            window.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            return dialog;
        }

        public ActionSheet create() {
            final ActionSheet dialog = this.createBaseDialog();
            View view = LayoutInflater.from(this.context).inflate(R.layout.actionsheet_layout, (ViewGroup)null);
            TextView btnCancel = (TextView)view.findViewById(R.id.btn_cancel);
            ViewGroup layContainer = (ViewGroup)view.findViewById(R.id.lay_container);
            android.view.ViewGroup.LayoutParams lpItem = new android.view.ViewGroup.LayoutParams(-1, -2);
            android.view.ViewGroup.LayoutParams lpDivider = new android.view.ViewGroup.LayoutParams(-1, 1);
            int dip1 = (int)(1.0F * this.context.getResources().getDisplayMetrics().density + 0.5F);
            int spacing = dip1 * 12;
            boolean hasTitle = !TextUtils.isEmpty(this.params.menuTitle);
            if (hasTitle) {
                TextView tTitle = new TextView(this.context);
                tTitle.setLayoutParams(lpItem);
                tTitle.setGravity(17);
                tTitle.setTextColor(-7368817);
                tTitle.setText(this.params.menuTitle);
                tTitle.setTextSize(17.0F);
                tTitle.setPadding(0, spacing, 0, spacing);
                tTitle.setBackgroundResource(R.drawable.actionsheet_select_item_top);
                layContainer.addView(tTitle);
                View viewDivider = new View(this.context);
                viewDivider.setLayoutParams(lpDivider);
                viewDivider.setBackgroundColor(-3222826);
                layContainer.addView(viewDivider);
            }

            for(int i = 0; i < this.params.menuList.size(); ++i) {
                ActionSheet.ActionSheetMenu bottomMenu = (ActionSheet.ActionSheetMenu)this.params.menuList.get(i);
                TextView bbm = new TextView(this.context);
                bbm.setLayoutParams(lpItem);
                int backgroundResId = R.drawable.actionsheet_select_item_center;
                if (this.params.menuList.size() > 1) {
                    if (i == 0) {
                        if (hasTitle) {
                            backgroundResId = R.drawable.actionsheet_select_item_center;
                        } else {
                            backgroundResId = R.drawable.actionsheet_select_item_top;
                        }
                    } else if (i == this.params.menuList.size() - 1) {
                        backgroundResId = R.drawable.actionsheet_select_item_bottom;
                    }
                } else if (this.params.menuList.size() == 1) {
                    backgroundResId = R.drawable.actionsheet_select_item_singleton;
                }

                bbm.setBackgroundResource(backgroundResId);
                bbm.setPadding(0, spacing, 0, spacing);
                bbm.setGravity(17);
                bbm.setFocusable(false);
                bbm.setText(bottomMenu.funName);
                bbm.setTextColor(-16745729);
                bbm.setTextSize(19.0F);
                bbm.setOnClickListener(bottomMenu.listener);
                layContainer.addView(bbm);
                if (i != this.params.menuList.size() - 1) {
                    View viewDivider = new View(this.context);
                    viewDivider.setLayoutParams(lpDivider);
                    viewDivider.setBackgroundColor(-3222826);
                    layContainer.addView(viewDivider);
                }
            }

            if (!TextUtils.isEmpty(this.params.cancelText)) {
                btnCancel.setText(this.params.cancelText);
            }

            if (this.params.cancelListener != null) {
                btnCancel.setOnClickListener(this.params.cancelListener);
            } else {
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

            dialog.setContentView(view);
//            dialog.setCanceledOnTouchOutside(this.params.canCancel);
//            dialog.setCancelable(this.params.canCancel);
            return dialog;
        }

        public ActionSheet createGrayDialog() {
            final ActionSheet dialog = this.createBaseDialog();
            View view = LayoutInflater.from(this.context).inflate(R.layout.actionsheet_gray_layout, (ViewGroup)null);
            TextView btnCancel = (TextView)view.findViewById(R.id.btn_cancel);
            ViewGroup layContainer = (ViewGroup)view.findViewById(R.id.lay_container);
            android.view.ViewGroup.LayoutParams lpItem = new android.view.ViewGroup.LayoutParams(-1, 150);
            android.view.ViewGroup.LayoutParams lpDivider = new android.view.ViewGroup.LayoutParams(-1, 1);

            for(int i = 0; i < this.params.menuList.size(); ++i) {
                ActionSheet.ActionSheetMenu bottomMenu = (ActionSheet.ActionSheetMenu)this.params.menuList.get(i);
                TextView bbm = new TextView(this.context);
                bbm.setLayoutParams(lpItem);
                bbm.setBackgroundColor(Color.parseColor("#ffffff"));
                bbm.setGravity(17);
                bbm.setText(bottomMenu.funName);
                bbm.setTextColor(Color.parseColor("#000000"));
                bbm.setTextSize(17.0F);
                bbm.setOnClickListener(bottomMenu.listener);
                layContainer.addView(bbm);
                if (i != this.params.menuList.size() - 1) {
                    View viewDivider = new View(this.context);
                    viewDivider.setLayoutParams(lpDivider);
                    viewDivider.setBackgroundColor(-3222826);
                    layContainer.addView(viewDivider);
                }
            }

            if (!TextUtils.isEmpty(this.params.cancelText)) {
                btnCancel.setText(this.params.cancelText);
            }

            if (this.params.cancelListener != null) {
                btnCancel.setOnClickListener(this.params.cancelListener);
            } else {
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

            dialog.setContentView(view);
            return dialog;
        }
    }

    static class Params {
        private final List<ActionSheet.ActionSheetMenu> menuList = new ArrayList();
        private View.OnClickListener cancelListener;
        private String menuTitle;
        private String cancelText;
        private boolean canCancel = true;
        private boolean shadow = true;

        Params() {
        }
    }
}
