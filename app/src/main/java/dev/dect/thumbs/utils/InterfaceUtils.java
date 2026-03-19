package dev.dect.thumbs.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import dev.dect.thumbs.R;

public class InterfaceUtils {
    public static class Keyboard {
        public static void requestShowForInput(EditText editText) {
            editText.requestFocus();

            editText.postDelayed(() -> {
                InputMethodManager keyboard = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                keyboard.showSoftInput(editText, 0);

                editText.setSelection(editText.getText().toString().length());
            }, 100);
        }
    }

    public static class Popup {
        public static void setMaxHeight(Context ctx, LinearLayout view) {
            final int screenHeight = ctx.getSystemService(WindowManager.class).getCurrentWindowMetrics().getBounds().height(),
                      maxHeightPercentage = ctx.getResources().getInteger(R.integer.popup_max_height_percentage),
                      maxHeight = (screenHeight * maxHeightPercentage) / 100;

            if(view.getHeight() > maxHeight) {
                final ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();

                params.height = maxHeight;

                view.setLayoutParams(params);
            }
        }

        public static void callOutAnimation(Dialog dialog, ConstraintLayout container, View view) {
            final Animation popupAnimationOut = AnimationUtils.loadAnimation(dialog.getContext(), R.anim.popup_out),
                            popupContainerOut = AnimationUtils.loadAnimation(dialog.getContext(), R.anim.popup_background_out);

            popupAnimationOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    dialog.dismiss();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            container.startAnimation(popupContainerOut);
            view.startAnimation(popupAnimationOut);
        }
    }

    public static class StatusBar {
        public static void updateColor(Activity activity) {
            final boolean isLightMode = activity.getResources().getConfiguration().isNightModeActive();

            new WindowInsetsControllerCompat(activity.getWindow(), activity.getWindow().getDecorView()).setAppearanceLightStatusBars(!isLightMode);
        }
    }
}
