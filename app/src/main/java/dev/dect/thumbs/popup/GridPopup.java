package dev.dect.thumbs.popup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Objects;

import dev.dect.thumbs.R;
import dev.dect.thumbs.utils.InterfaceUtils;
import dev.dect.thumbs.view.GridPicker;

@SuppressLint("InflateParams")
public class GridPopup extends Dialog {
    public interface OnGridPopup {
        void onGridSet(int rows, int columns);
    }

    public static final int NO_TEXT = -1;

    private final LinearLayout POPUP_VIEW;

    private final ConstraintLayout POPUP_CONTAINER;

    public GridPopup(Context ctx, int title, int rows, int columns, int btnYesText, OnGridPopup btnYes, int btnNoText, @Nullable Runnable btnNo, boolean dismissible, boolean dismissibleCallsNo) {
        super(ctx, dev.dect.thumbs.R.style.Theme_Thumbs_Translucent_Popup);

        final View view = ((LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_grid, null);

        if(title == NO_TEXT) {
            view.findViewById(R.id.popupTitle).setVisibility(View.GONE);
        } else {
            ((TextView) view.findViewById(R.id.popupTitle)).setText(title);
        }

        final GridPicker rowsPicker = view.findViewById(R.id.rows),
                         columnsPicker = view.findViewById(R.id.columns);

        rowsPicker.setMinValue(1);
        rowsPicker.setMaxValue(10);
        rowsPicker.setValue(rows);

        columnsPicker.setMinValue(1);
        columnsPicker.setMaxValue(10);
        columnsPicker.setValue(columns);

        final AppCompatButton buttonYes = view.findViewById(R.id.popupBtnYes);

        buttonYes.setText(btnYesText);

        buttonYes.setOnClickListener((v) -> {
            this.dismissWithAnimation();

            if((rowsPicker.getValue() != 1 || columnsPicker.getValue() != 1) && (rowsPicker.getValue() != rows || columnsPicker.getValue() != columns)) {
                btnYes.onGridSet(rowsPicker.getValue(), columnsPicker.getValue());
            }
        });

        if(btnNoText == NO_TEXT) {
            view.findViewById(R.id.popupBtnNo).setVisibility(View.GONE);

            buttonYes.setAllCaps(true);
        } else {
            final AppCompatButton buttonNo = view.findViewById(R.id.popupBtnNo);

            buttonNo.setText(btnNoText);

            buttonNo.setOnClickListener((v) -> {
                this.dismissWithAnimation();

                if(btnNo != null) {
                    btnNo.run();
                }
            });
        }

        this.POPUP_CONTAINER = view.findViewById(R.id.popupContainer);
        this.POPUP_VIEW = view.findViewById(R.id.popup);

        if(dismissible) {
            POPUP_CONTAINER.setOnClickListener((v) -> {
                if(dismissibleCallsNo) {
                    if(btnNo != null) {
                        btnNo.run();
                    }
                } else {
                    this.dismissWithAnimation();
                }
            });
        }

        setOnShowListener(dialogInterface -> InterfaceUtils.Popup.setMaxHeight(ctx, POPUP_VIEW));

        this.setContentView(view);

        Objects.requireNonNull(getWindow()).setStatusBarColor(ctx.getColor(R.color.popup_background_transparent));
    }

    public void dismissWithAnimation() {
        InterfaceUtils.Popup.callOutAnimation(this, POPUP_CONTAINER, POPUP_VIEW);
    }
}
