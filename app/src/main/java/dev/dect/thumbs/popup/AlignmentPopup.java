package dev.dect.thumbs.popup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Objects;

import dev.dect.thumbs.R;
import dev.dect.thumbs.utils.InterfaceUtils;

@SuppressLint("InflateParams")
public class AlignmentPopup extends Dialog {
    public static final int TOP = 0,
                            CENTER = 1,
                            BOTTOM = 2,
                            LEFT = 3,
                            RIGHT = 4;

    public interface OnAlignmentPopupListener {
        void onFontPicked(int vertical, int horizontal);
    }

    private final LinearLayout POPUP_VIEW;

    private final ConstraintLayout POPUP_CONTAINER;

    private final OnAlignmentPopupListener LISTENER;

    private final int INITIAL_VERTICAL,
                      INITIAL_HORIZONTAL;

    private final ImageButton TOP_LEFT,
                              TOP_CENTER,
                              TOP_RIGHT,
                              CENTER_LEFT,
                              CENTER_CENTER,
                              CENTER_RIGHT,
                              BOTTOM_LEFT,
                              BOTTOM_CENTER,
                              BOTTOM_RIGHT;

    public AlignmentPopup(Context ctx, int vertical, int horizontal, OnAlignmentPopupListener l) {
        super(ctx, R.style.Theme_Thumbs_Translucent_Popup);

        final View view = ((LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_picker_alignment, null);

        this.POPUP_CONTAINER = view.findViewById(R.id.popupContainer);
        this.POPUP_VIEW = view.findViewById(R.id.popup);
        this.LISTENER = l;
        this.INITIAL_HORIZONTAL = horizontal;
        this.INITIAL_VERTICAL = vertical;

        this.TOP_LEFT = view.findViewById(R.id.topLeft);
        this.TOP_RIGHT = view.findViewById(R.id.topRight);
        this.TOP_CENTER = view.findViewById(R.id.topCenter);
        this.CENTER_LEFT = view.findViewById(R.id.centerLeft);
        this.CENTER_RIGHT = view.findViewById(R.id.centerRight);
        this.CENTER_CENTER = view.findViewById(R.id.centerCenter);
        this.BOTTOM_LEFT = view.findViewById(R.id.bottomLeft);
        this.BOTTOM_RIGHT = view.findViewById(R.id.bottomRight);
        this.BOTTOM_CENTER = view.findViewById(R.id.bottomCenter);

        POPUP_CONTAINER.setOnClickListener((v) -> this.dismissWithAnimation());

        TOP_LEFT.setOnClickListener((v) -> set(TOP, LEFT, true));
        TOP_RIGHT.setOnClickListener((v) -> set(TOP, RIGHT, true));
        TOP_CENTER.setOnClickListener((v) -> set(TOP, CENTER, true));
        CENTER_LEFT.setOnClickListener((v) -> set(CENTER, LEFT, true));
        CENTER_RIGHT.setOnClickListener((v) -> set(CENTER, RIGHT, true));
        CENTER_CENTER.setOnClickListener((v) -> set(CENTER, CENTER, true));
        BOTTOM_LEFT.setOnClickListener((v) -> set(BOTTOM, LEFT, true));
        BOTTOM_RIGHT.setOnClickListener((v) -> set(BOTTOM, RIGHT, true));
        BOTTOM_CENTER.setOnClickListener((v) -> set(BOTTOM, CENTER, true));

        final AppCompatButton btnMain = view.findViewById(R.id.popupBtnMain);

        btnMain.setOnClickListener((v) -> this.dismissWithAnimation());

        set(vertical, horizontal, false);

        this.setContentView(view);

        setOnShowListener(dialogInterface -> InterfaceUtils.Popup.setMaxHeight(ctx, POPUP_VIEW));

        Objects.requireNonNull(getWindow()).setStatusBarColor(ctx.getColor(R.color.popup_background_transparent));
    }

    private void set(int vertical, int horizontal, boolean userInput) {
        clear();

        if(vertical == TOP) {
            if(horizontal == RIGHT) {
                TOP_RIGHT.setImageResource(R.drawable.icon_alignment_on);
            } else if(horizontal == CENTER) {
                TOP_CENTER.setImageResource(R.drawable.icon_alignment_on);
            } else {
                TOP_LEFT.setImageResource(R.drawable.icon_alignment_on);
            }
        } else if(vertical == CENTER) {
            if(horizontal == RIGHT) {
                CENTER_RIGHT.setImageResource(R.drawable.icon_alignment_on);
            } else if(horizontal == CENTER) {
                CENTER_CENTER.setImageResource(R.drawable.icon_alignment_on);
            } else {
                CENTER_LEFT.setImageResource(R.drawable.icon_alignment_on);
            }
        } else {
            if(horizontal == RIGHT) {
                BOTTOM_RIGHT.setImageResource(R.drawable.icon_alignment_on);
            } else if(horizontal == CENTER) {
                BOTTOM_CENTER.setImageResource(R.drawable.icon_alignment_on);
            } else {
                BOTTOM_LEFT.setImageResource(R.drawable.icon_alignment_on);
            }
        }

        if(userInput) {
            if(vertical != INITIAL_VERTICAL || horizontal != INITIAL_HORIZONTAL) {
                LISTENER.onFontPicked(vertical, horizontal);
            }

            dismissWithAnimation();
        }
    }

    private void clear() {
        TOP_LEFT.setImageResource(R.drawable.icon_alignment_off);
        TOP_RIGHT.setImageResource(R.drawable.icon_alignment_off);
        TOP_CENTER.setImageResource(R.drawable.icon_alignment_off);
        BOTTOM_LEFT.setImageResource(R.drawable.icon_alignment_off);
        BOTTOM_RIGHT.setImageResource(R.drawable.icon_alignment_off);
        BOTTOM_CENTER.setImageResource(R.drawable.icon_alignment_off);
        CENTER_LEFT.setImageResource(R.drawable.icon_alignment_off);
        CENTER_RIGHT.setImageResource(R.drawable.icon_alignment_off);
        CENTER_CENTER.setImageResource(R.drawable.icon_alignment_off);
    }

    public void dismissWithAnimation() {
        InterfaceUtils.Popup.callOutAnimation(this, POPUP_CONTAINER, POPUP_VIEW);
    }

    public static String getName(Context ctx, int v, int h) {
        String name;

        if(v == CENTER) {
            name = ctx.getString(R.string.popup_alignment_center);

            if(h == CENTER) {
                return name;
            }
        } else {
            switch(v) {
                case TOP:
                    name = ctx.getString(R.string.popup_alignment_top);
                    break;

                case BOTTOM:
                    name = ctx.getString(R.string.popup_alignment_bottom);
                    break;

                default:
                    name = "?";
                    break;
            }
        }

        name += " - ";

        switch(h) {
            case LEFT:
                name += ctx.getString(R.string.popup_alignment_left);
                break;

            case CENTER:
                name += ctx.getString(R.string.popup_alignment_center);
                break;

            case RIGHT:
                name += ctx.getString(R.string.popup_alignment_right);
                break;

            default:
                name += "?";
                break;
        }

        return name;
    }
}
