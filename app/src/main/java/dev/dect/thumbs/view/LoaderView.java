package dev.dect.thumbs.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import dev.dect.thumbs.R;

public class LoaderView extends AppCompatImageView {
    private AnimatedVectorDrawableCompat LOADER;

    public LoaderView(Context context) {
        super(context);

        init();
    }

    public LoaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public LoaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @Override
    protected void onAttachedToWindow(){
        super.onAttachedToWindow();

        startAnimation();
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();

        stopAnimation();
    }

    @Override
    public void setVisibility(int visibility) {
        if(visibility == VISIBLE) {
            startAnimation();
        } else {
            stopAnimation();
        }

        super.setVisibility(visibility);
    }

    private void init() {
        LOADER = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.loader);

        setBackground(LOADER);
    }

    private void startAnimation() {
        LOADER.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                LOADER.start();
            }
        });

        LOADER.start();
    }

    private void stopAnimation() {
        LOADER.stop();

        LOADER.clearAnimationCallbacks();
    }
}
