package dev.dect.thumbs.popup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Objects;

import dev.dect.thumbs.R;
import dev.dect.thumbs.utils.ConverterUtils;
import dev.dect.thumbs.utils.InterfaceUtils;
import dev.dect.thumbs.utils.Utils;

@SuppressLint({"InflateParams", "ClickableViewAccessibility"})
public class ColorPickerPopup extends Dialog {
    private static final String TAG = ColorPickerPopup.class.getSimpleName();

    public interface OnColorPickerPopup {
        void onColorPicked(String color);
    }

    private final LinearLayout POPUP_VIEW;

    private final ConstraintLayout POPUP_CONTAINER;

    private final ImageView PICKER,
                            HUE,
                            TRANSPARENCY,
                            COLOR;

    private final TextView TEXT_HEX,
                           TEXT_RED,
                           TEXT_GREEN,
                           TEXT_BLUE,
                           TEXT_ALPHA;

    private final Context CONTEXT;

    private final int[] HUE_COLORS = new int[360];

    private final float[] HSV = new float[3];

    private final boolean SHOW_ALPHA;

    private Bitmap PICKER_BITMAP,
                   HUE_BITMAP,
                   TRANSPARENCY_BITMAP;

    private final int HUE_HEIGHT,
                      TRANSPARENCY_HEIGHT;

    private int PICKER_SIZE,
                SCROLLER_SIZE,
                TRANSPARENCY_255;

    private String HEX_COLOR;

    public ColorPickerPopup(Context ctx, String color, boolean showAlpha, OnColorPickerPopup l) {
        this(ctx, color, null, showAlpha, l);
    }

    /** @noinspection CodeBlock2Expr*/ //palette must have up to 7 colors
    public ColorPickerPopup(Context ctx, String color, @Nullable String[] palette, boolean showAlpha, OnColorPickerPopup l) {
        super(ctx, R.style.Theme_Thumbs_Translucent_Popup);

        final View view = ((LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_picker_color, null);

        this.CONTEXT = ctx;
        this.POPUP_CONTAINER = view.findViewById(R.id.popupContainer);
        this.POPUP_VIEW = view.findViewById(R.id.popup);
        this.PICKER = view.findViewById(R.id.picker);
        this.HUE = view.findViewById(R.id.hue);
        this.HEX_COLOR = color;
        this.HUE_HEIGHT = CONTEXT.getResources().getDimensionPixelSize(R.dimen.color_picker_hue_height);
        this.TRANSPARENCY = view.findViewById(R.id.transparency);
        this.TRANSPARENCY_255 = Integer.valueOf(color.substring(7), 16);
        this.TRANSPARENCY_HEIGHT = CONTEXT.getResources().getDimensionPixelSize(R.dimen.color_picker_transparency_height);
        this.COLOR = view.findViewById(R.id.color);
        this.SHOW_ALPHA = showAlpha;

        this.TEXT_HEX = view.findViewById(R.id.hex);
        this.TEXT_RED = view.findViewById(R.id.red);
        this.TEXT_GREEN = view.findViewById(R.id.green);
        this.TEXT_BLUE = view.findViewById(R.id.blue);
        this.TEXT_ALPHA = view.findViewById(R.id.alpha);

        if(showAlpha) {
            TRANSPARENCY.setOnTouchListener((view1, event) -> {
                final float x = event.getX() > SCROLLER_SIZE ? SCROLLER_SIZE : (event.getX() < 0 ? 0 : event.getX());

                TRANSPARENCY_255 = (int) ((255 * x) / SCROLLER_SIZE);

                drawTransparency();
                drawTransparencySelector();

                colorPicked();

                return true;
            });
        } else {
            TRANSPARENCY.setVisibility(View.GONE);

            view.findViewById(R.id.alphaLabelContainer).setVisibility(View.GONE);

            ((LinearLayout) view.findViewById(R.id.info)).setWeightSum(6);

            TRANSPARENCY_255 = 255;
        }

        Color.colorToHSV(Color.parseColor(color.substring(0, 7)), HSV);

        POPUP_CONTAINER.setOnClickListener((v) -> this.dismissWithAnimation());

        view.findViewById(R.id.popupBtnYes).setOnClickListener((v) -> {
            l.onColorPicked(HEX_COLOR);
            this.dismissWithAnimation();
        });

        view.findViewById(R.id.popupBtnNo).setOnClickListener((v) -> this.dismissWithAnimation());

        setOnShowListener(dialogInterface -> InterfaceUtils.Popup.setMaxHeight(ctx, POPUP_VIEW));

        generateHueColors();

        PICKER.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                PICKER_SIZE = PICKER.getWidth();

                SCROLLER_SIZE = HUE.getWidth();

                drawHue();
                drawHueSelector();
                drawSpectrum();
                drawSpectrumSelector();
                drawTransparency();
                drawTransparencySelector();

                colorPicked();

                PICKER.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        PICKER.setOnTouchListener((view1, event) -> {
            final float x = event.getX() > PICKER_SIZE ? PICKER_SIZE : (event.getX() < 0 ? 0 : event.getX()),
                        y = event.getY() > PICKER_SIZE ? PICKER_SIZE : (event.getY() < 0 ? 0 : event.getY());

            setHsvFromSpectrumPos(x, y);
            drawSpectrum();
            drawSpectrumSelector(x, y);
            drawTransparency();
            drawTransparencySelector();

            colorPicked();

            return true;
        });

        HUE.setOnTouchListener((view1, event) -> {
            final float x = event.getX() > SCROLLER_SIZE ? SCROLLER_SIZE : (event.getX() < 0 ? 0 : event.getX());

            HSV[0] = (360 * x) / SCROLLER_SIZE;

            drawHue();
            drawHueSelector();
            drawSpectrum();
            drawSpectrumSelector();
            drawTransparency();
            drawTransparencySelector();

            colorPicked();

            return true;
        });

        TEXT_HEX.setOnClickListener((v) -> {
            new InputPopup.Text(
                ctx,
                R.string.color_picker_hex,
                HEX_COLOR.substring(0, showAlpha ? 9 : 7),
                R.string.popup_btn_set,
                new InputPopup.OnInputPopup() {
                    @Override
                    public void onStringInputSet(String input) {
                        if(!input.isBlank()) {
                            if(!input.startsWith("#")) {
                                input = "#" + input;
                            }

                            try {
                                Color.colorToHSV(Color.parseColor(input.substring(0, 7)), HSV);

                                if(showAlpha) {
                                    if(input.length() > 7) {
                                        TRANSPARENCY_255 = Integer.valueOf(input.substring(7), 16);
                                    } else {
                                        TRANSPARENCY_255 = 255;
                                    }

                                    drawTransparency();
                                    drawTransparencySelector();
                                }

                                drawHue();
                                drawHueSelector();
                                drawSpectrum();
                                drawSpectrumSelector();

                                colorPicked();
                            } catch (Exception e) {
                                Log.e(TAG, "onStringInputSet: " + e.getMessage());

                                Toast.makeText(ctx, R.string.toast_error_generic, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                R.string.popup_btn_cancel,
                null,
                true,
                false,
                false
            ).show();
        });

        if(palette == null) {
            view.findViewById(R.id.palette).setVisibility(View.GONE);
        } else {
            final LinearLayout paletteContainer = view.findViewById(R.id.palette);

            final int paletteL = Math.min(palette.length, 7);

            for(int i = 0; i < paletteL; i++) {
                final String c = palette[i];

                final View btn = paletteContainer.getChildAt(i * 2);

                btn.setBackgroundTintList(ColorStateList.valueOf(ConverterUtils.Color.hexColorToInt(c)));

                btn.setOnClickListener((v) -> {
                    Color.colorToHSV(Color.parseColor(c.substring(0, 7)), HSV);

                    drawHue();
                    drawHueSelector();
                    drawSpectrum();
                    drawSpectrumSelector();

                    colorPicked();
                });
            }

            if(paletteL < 7) {
                paletteContainer.getChildAt((paletteL * 2) - 1).setVisibility(View.GONE);

                for(int i = paletteL; i < 7; i++) {
                    final int index = i * 2;

                    paletteContainer.getChildAt(index).setVisibility(View.GONE);

                    if(i < 6) {
                        paletteContainer.getChildAt(index + 1).setVisibility(View.GONE);
                    }
                }
            }
        }

        this.setContentView(view);

        Objects.requireNonNull(getWindow()).setStatusBarColor(ctx.getColor(R.color.popup_background_transparent));
    }

    public void dismissWithAnimation() {
        InterfaceUtils.Popup.callOutAnimation(this, POPUP_CONTAINER, POPUP_VIEW);
    }

    private void generateHueColors() {
        final float[] hsv = new float[]{0,1,1};

        for(int i = 0; i < 360; i++) {
            hsv[0] = i;

            HUE_COLORS[i] = Color.HSVToColor(hsv);
        }
    }

    private void drawTransparency() {
        TRANSPARENCY_BITMAP = Bitmap.createBitmap(SCROLLER_SIZE, TRANSPARENCY_HEIGHT, Bitmap.Config.ARGB_8888);

        final Canvas canvasTransparency = new Canvas(TRANSPARENCY_BITMAP);

        Utils.drawTransparencyBackgroundOnCanvas(canvasTransparency);

        final Paint paintTransparency = new Paint();

        paintTransparency.setShader(new LinearGradient(0f, 0f, SCROLLER_SIZE, 0f, Color.TRANSPARENT, Color.HSVToColor(HSV), Shader.TileMode.MIRROR));

        canvasTransparency.drawRect(new RectF(0, 0, SCROLLER_SIZE, TRANSPARENCY_HEIGHT), paintTransparency);

        TRANSPARENCY.setImageBitmap(TRANSPARENCY_BITMAP);
    }

    private void drawTransparencySelector() {
        final float x = (SCROLLER_SIZE * TRANSPARENCY_255) / 255f;

        final Canvas canvasSelector = new Canvas(TRANSPARENCY_BITMAP);

        final Paint paintSelector = new Paint();

        final float r = TRANSPARENCY_HEIGHT / 2f;

        paintSelector.setStyle(Paint.Style.FILL);
        paintSelector.setColor(Color.WHITE);

        canvasSelector.drawCircle(x, r, r - 3, paintSelector);

        paintSelector.setColor(Color.WHITE);
        paintSelector.setStrokeWidth(4);
        paintSelector.setStyle(Paint.Style.STROKE);

        canvasSelector.drawCircle(x, r, r - 3, paintSelector);

        TRANSPARENCY.setImageBitmap(TRANSPARENCY_BITMAP);
    }

    private void drawHue() {
        HUE_BITMAP = Bitmap.createBitmap(SCROLLER_SIZE, HUE_HEIGHT, Bitmap.Config.ARGB_8888);

        final Canvas canvasHue = new Canvas(HUE_BITMAP);

        final Paint paintHue = new Paint();

        paintHue.setShader(new LinearGradient(0f, 0f, SCROLLER_SIZE, 0f, HUE_COLORS, null, Shader.TileMode.MIRROR));

        canvasHue.drawRect(new RectF(0, 0, SCROLLER_SIZE, HUE_HEIGHT), paintHue);

        HUE.setImageBitmap(HUE_BITMAP);
    }

    private void drawHueSelector() {
        final float x = (SCROLLER_SIZE * HSV[0]) / 360;

        final Canvas canvasSelector = new Canvas(HUE_BITMAP);

        final Paint paintSelector = new Paint();

        final float r = HUE_HEIGHT / 2f;

        paintSelector.setStyle(Paint.Style.FILL);
        paintSelector.setColor(Color.HSVToColor(new float[]{HSV[0], 1, 1}));

        canvasSelector.drawCircle(x, r, r - 3, paintSelector);

        paintSelector.setColor(Color.WHITE);
        paintSelector.setStrokeWidth(4);
        paintSelector.setStyle(Paint.Style.STROKE);

        canvasSelector.drawCircle(x, r, r - 3, paintSelector);

        HUE.setImageBitmap(HUE_BITMAP);
    }

    private void drawSpectrum() {
        PICKER_BITMAP = Bitmap.createBitmap(PICKER_SIZE, PICKER_SIZE, Bitmap.Config.ARGB_8888);

        final Canvas canvasPicker = new Canvas(PICKER_BITMAP);

        final Paint paintPicker = new Paint();

        paintPicker.setShader(new LinearGradient(0, 0, PICKER_SIZE, 0, Color.WHITE, Color.HSVToColor(new float[]{HSV[0], 1, 1}), Shader.TileMode.CLAMP));

        canvasPicker.drawRect(new RectF(0, 0, PICKER_SIZE, PICKER_SIZE), paintPicker);

        paintPicker.setShader(new LinearGradient(0, 0, 0, PICKER_SIZE, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP));

        canvasPicker.drawRect(new RectF(0, 0, PICKER_SIZE, PICKER_SIZE), paintPicker);

        PICKER.setImageBitmap(PICKER_BITMAP);
    }

    private void drawSpectrumSelector() {
        drawSpectrumSelector(HSV[1] * PICKER_SIZE, PICKER_SIZE - (HSV[2] * PICKER_SIZE));
    }

    private void drawSpectrumSelector(float x, float y) {
        final Canvas canvasSelector = new Canvas(PICKER_BITMAP);

        final Paint paintSelector = new Paint();

        final float r = CONTEXT.getResources().getDimensionPixelSize(R.dimen.color_picker_selector) / 2f;

        paintSelector.setStyle(Paint.Style.FILL);
        paintSelector.setColor(Color.HSVToColor(HSV));

        canvasSelector.drawCircle(x, y, r, paintSelector);

        paintSelector.setColor(Color.WHITE);
        paintSelector.setStrokeWidth(4);
        paintSelector.setStyle(Paint.Style.STROKE);

        canvasSelector.drawCircle(x, y, r, paintSelector);

        PICKER.setImageBitmap(PICKER_BITMAP);
    }

    private void drawColor() {
        final Bitmap colorBitmap = Bitmap.createBitmap(COLOR.getWidth(), COLOR.getHeight(), Bitmap.Config.ARGB_8888);

        final Canvas canvasColor = new Canvas(colorBitmap);

        Utils.drawTransparencyBackgroundOnCanvas(canvasColor);

        final Paint paintColor = new Paint();

        paintColor.setStyle(Paint.Style.FILL);
        paintColor.setColor(ConverterUtils.Color.hexColorToInt(HEX_COLOR));

        canvasColor.drawRect(new RectF(0, 0, canvasColor.getWidth(), canvasColor.getHeight()), paintColor);

        COLOR.setImageBitmap(colorBitmap);
    }

    private void setHsvFromSpectrumPos(float x, float y) {
        HSV[1] = x / PICKER_SIZE;
        HSV[2] = 1 - (y / PICKER_SIZE);
    }

    private void colorPicked() {
        final int color = Color.HSVToColor(HSV);

        HEX_COLOR = ConverterUtils.Color.intColorToHex(color, TRANSPARENCY_255);

        if(SHOW_ALPHA) {
            TEXT_ALPHA.setText(String.valueOf(TRANSPARENCY_255));
            TEXT_HEX.setText(HEX_COLOR);
        } else {
            TEXT_HEX.setText(HEX_COLOR.substring(0, 7));
        }

        TEXT_RED.setText(String.valueOf(Color.red(color)));
        TEXT_GREEN.setText(String.valueOf(Color.green(color)));
        TEXT_BLUE.setText(String.valueOf(Color.blue(color)));

        drawColor();
    }
}
