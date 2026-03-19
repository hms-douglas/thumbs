package dev.dect.thumbs.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.NumberPicker;

import dev.dect.thumbs.R;

@SuppressLint("ClickableViewAccessibility")
public class GridPicker extends NumberPicker {
    public GridPicker(Context context) {
        super(context);

        setInitialStyle();

        setMinMax();
    }

    public GridPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        setInitialStyle();

        setMinMax();
    }

    public GridPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setInitialStyle();

        setMinMax();
    }

    private void setMinMax() {
        this.setMinValue(0);
        this.setMaxValue(59);
    }

    private void setInitialStyle() {
        this.setTextColor(this.getContext().getColor(R.color.popup_grid_picker_font_not_focused));
        this.setTextSize(this.getContext().getResources().getDimension(R.dimen.popup_grid_picker_font));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            this.setTextColor(this.getContext().getColor(R.color.popup_grid_picker_font_focused));
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> this.setTextColor(this.getContext().getColor(R.color.popup_grid_picker_font_not_focused)), 800);
        }

        return super.onTouchEvent(event);
    }
}
