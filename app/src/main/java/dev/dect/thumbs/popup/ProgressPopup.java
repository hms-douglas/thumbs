package dev.dect.thumbs.popup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Objects;

import dev.dect.thumbs.R;
import dev.dect.thumbs.utils.InterfaceUtils;

@SuppressLint({"InflateParams", "SetTextI18n"})
public class ProgressPopup extends Dialog {
    private final ConstraintLayout POPUP_VIEW,
                                   POPUP_CONTAINER;

    private final ProgressBar PROGRESS_BAR;

    private final TextView PROGRESS;

    public ProgressPopup(Context ctx, int title) {
        super(ctx, R.style.Theme_Thumbs_Translucent_Popup);

        final View view = ((LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_progress, null);

        ((TextView) view.findViewById(R.id.popupTitle)).setText(title);

        this.POPUP_CONTAINER = view.findViewById(R.id.popupContainer);
        this.POPUP_VIEW = view.findViewById(R.id.popup);
        this.PROGRESS = view.findViewById(R.id.popupProgress);
        this.PROGRESS_BAR = view.findViewById(R.id.popupProgressBar);

        PROGRESS_BAR.setMin(0);
        PROGRESS_BAR.setMax(100);
        PROGRESS_BAR.setProgress(0);

        PROGRESS.setText("0%");

        this.setContentView(view);

        this.setCancelable(false);

        Objects.requireNonNull(getWindow()).setStatusBarColor(ctx.getColor(R.color.popup_background_transparent));
    }

    public void setMax(int max) {
        this.PROGRESS_BAR.setMax(max);
    }

    public void setValue(int value) {
        PROGRESS_BAR.setProgress(value, true);

        PROGRESS.setText(((value * 100) / this.PROGRESS_BAR.getMax()) + "%");
    }

    public void dismissWithAnimation() {
        InterfaceUtils.Popup.callOutAnimation(this, POPUP_CONTAINER, POPUP_VIEW);
    }
}
