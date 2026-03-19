package dev.dect.thumbs.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;

import dev.dect.thumbs.R;


public class Utils {
    public static void copyToClipboard(Context ctx, String s) {
        final ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);

        if(clipboard != null) {
            final ClipData clip = ClipData.newPlainText(ctx.getString(R.string.word_text), s);

            clipboard.setPrimaryClip(clip);
        }
    }

    public static void drawTransparencyBackgroundOnCanvas(Canvas canvas) {
        final float height = canvas.getHeight(),
                width = canvas.getWidth();

        final Paint paint = new Paint();

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#FDFDFD"));

        canvas.drawRect(new RectF(0, 0, width, height), paint);

        paint.setColor(Color.parseColor("#808080"));

        final float size = 16.5f,
                amountLines = height / size;

        for(float i = 0; i < amountLines; i++) {
            final float startXat = i % 2 == 0 ? 0 : size;

            for(float j = 0; j < ((width - startXat) / size); j += 2) {
                final float x = (size * j) + startXat,
                        y = size * i;

                canvas.drawRect(new RectF(x, y, x + size, y + size), paint);
            }
        }
    }

    public static void drawColorSampleOnImageView(ImageView imageView, int color) {
        final int colorSize = 100;

        final Bitmap bitmap = Bitmap.createBitmap(colorSize, colorSize, Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(bitmap);

        Utils.drawTransparencyBackgroundOnCanvas(canvas);

        final Paint paintColor = new Paint();

        paintColor.setStyle(Paint.Style.FILL);
        paintColor.setColor(color);

        canvas.drawRect(new RectF(0, 0, colorSize, colorSize), paintColor);

        final Bitmap cuttedBitmap = Bitmap.createBitmap(colorSize, colorSize, Bitmap.Config.ARGB_8888);

        final Canvas canvasCut = new Canvas(cuttedBitmap);

        final Rect rect = new Rect(0, 0, colorSize, colorSize);

        final Paint paintCut = new Paint();

        paintCut.setAntiAlias(true);

        canvasCut.drawARGB(0, 0, 0, 0);

        canvasCut.drawCircle(colorSize / 2f, colorSize / 2f, colorSize / 2f, paintCut);

        paintCut.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvasCut.drawBitmap(bitmap, rect, rect, paintCut);

        imageView.setImageBitmap(cuttedBitmap);
    }
}
