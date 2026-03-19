package dev.dect.thumbs.popup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import dev.dect.thumbs.R;
import dev.dect.thumbs.adapter.PickerFontAdapter;
import dev.dect.thumbs.model.FontModel;
import dev.dect.thumbs.utils.InterfaceUtils;

@SuppressLint("InflateParams")
public class PickerFontPopup extends Dialog {
    public interface OnPickerPopup {
        void onFontPicked(FontModel font);
    }

    private final LinearLayout POPUP_VIEW;

    private final ConstraintLayout POPUP_CONTAINER;

    public PickerFontPopup(Context ctx, FontModel initialFont, OnPickerPopup l) {
        super(ctx, R.style.Theme_Thumbs_Translucent_Popup);

        final View view = ((LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_picker_font, null);

        this.POPUP_CONTAINER = view.findViewById(R.id.popupContainer);
        this.POPUP_VIEW = view.findViewById(R.id.popup);

        POPUP_CONTAINER.setOnClickListener((v) -> this.dismissWithAnimation());

        final AppCompatButton btnMain = view.findViewById(R.id.popupBtnMain);

        btnMain.setOnClickListener((v) -> this.dismissWithAnimation());

        final ArrayList<FontModel> fonts = FontModel.getFontsFromAsset();

        final PickerFontAdapter pickerFontAdapter = new PickerFontAdapter(fonts, initialFont);

        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setAdapter(pickerFontAdapter);

        recyclerView.setOnClickListener((v) -> {
            if(pickerFontAdapter.getFontSelected().getId() != initialFont.getId()) {
                l.onFontPicked(pickerFontAdapter.getFontSelected());
            }

            this.dismissWithAnimation();
        });

        this.setContentView(view);

        setOnShowListener(dialogInterface -> InterfaceUtils.Popup.setMaxHeight(ctx, POPUP_VIEW));

        Objects.requireNonNull(getWindow()).setStatusBarColor(ctx.getColor(R.color.popup_background_transparent));
    }

    public void dismissWithAnimation() {
        InterfaceUtils.Popup.callOutAnimation(this, POPUP_CONTAINER, POPUP_VIEW);
    }
}
