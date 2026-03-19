package dev.dect.thumbs.popup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import java.util.Objects;

import dev.dect.thumbs.R;

@SuppressLint("InflateParams")
public class FullLoadingPopup extends Dialog {
    public interface OnLoadingPopup {
        void onUserDismiss();
    }

    private boolean userDismiss = true;

    public FullLoadingPopup(Context ctx) {
        this(ctx, null);
    }

    public FullLoadingPopup(Context ctx, OnLoadingPopup listener) {
        super(ctx, R.style.Theme_Thumbs_Translucent_Popup);

        final View view = ((LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_loading_full, null);

        if(listener == null) {
            this.setCancelable(false);
            this.setDismissible(false);
        } else {
            this.setOnDismissListener((dialogInterface) -> {
                if(userDismiss) {
                    listener.onUserDismiss();
                }
            });
        }

        this.setContentView(view);

        Objects.requireNonNull(getWindow()).setStatusBarColor(ctx.getColor(R.color.popup_background_transparent));
    }

    public void setDismissible(boolean b) {
        this.setCancelable(b);
    }

    public void dismissByCode() {
        userDismiss = false;

        super.dismiss();
    }
}
