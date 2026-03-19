package dev.dect.thumbs.generator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import dev.dect.thumbs.data.ThumbSettings;
import dev.dect.thumbs.popup.AlignmentPopup;
import dev.dect.thumbs.utils.AppFilesUtils;
import dev.dect.thumbs.utils.ConverterUtils;
import dev.dect.thumbs.utils.FormatterUtils;
import dev.dect.thumbs.utils.MathUtils;

public class ThumbGenerator {
    public interface OnThumbGenerator {
        default void onSampleCompleted(Bitmap bitmap) {}

        default void onCompleted() {}

        default void onProgress() {}

        void onError(boolean fatal);
    }

    private static final String TAG = ThumbGenerator.class.getSimpleName();

    private final Context CONTEXT;

    private final ThumbSettings SETTINGS;

    private final OnThumbGenerator LISTENER;

    private final MediaMetadataRetriever MEDIA_RETRIEVER;

    private final Handler HANDLER;

    private final int[] VIDEO_WH = new int[2],
                        THUMBNAIL_WH = new int[2];

    private final boolean IS_GENERATING_SAMPLE;

    private long VIDEO_DURATION,
                 VIDEO_SIZE;

    private String VIDEO_NAME;

    private int VIDEO_FPS;

    public ThumbGenerator(Context ctx, ThumbSettings thumbSettings, Uri videoUri, OnThumbGenerator l) {
        this.CONTEXT = ctx;
        this.SETTINGS = thumbSettings;
        this.LISTENER = l;
        this.MEDIA_RETRIEVER = new MediaMetadataRetriever();

        this.HANDLER = new Handler(Looper.getMainLooper());

        if(videoUri == null) {
            this.IS_GENERATING_SAMPLE = true;

            videoUri = Uri.fromFile(AppFilesUtils.getCachedSampleFile(ctx));
        } else {
            this.IS_GENERATING_SAMPLE = false;
        }

        MEDIA_RETRIEVER.setDataSource(CONTEXT, videoUri);

        loadFixedData(videoUri);
    }

    @SuppressLint("Range")
    private void loadFixedData(Uri videoUri) {
        VIDEO_DURATION = Long.parseLong(Objects.requireNonNull(MEDIA_RETRIEVER.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));

        VIDEO_WH[0] = Integer.parseInt(Objects.requireNonNull(MEDIA_RETRIEVER.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)));
        VIDEO_WH[1] = Integer.parseInt(Objects.requireNonNull(MEDIA_RETRIEVER.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)));

        try {
            if(IS_GENERATING_SAMPLE) {
                final File file = AppFilesUtils.getCachedSampleFile(CONTEXT);

                VIDEO_NAME = file.getName();
                VIDEO_SIZE = file.length();
            } else {
                final Cursor cursor = CONTEXT.getContentResolver().query(videoUri, null, null, null, null);

                Objects.requireNonNull(cursor).moveToFirst();

                VIDEO_NAME = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                VIDEO_SIZE = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));

                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "loadFixedData - name and size: " + e.getMessage());

            VIDEO_NAME = "?";
            VIDEO_SIZE = 0;

            HANDLER.post(() -> LISTENER.onError(false));
        }

        try {
            final MediaExtractor mediaExtractor = new MediaExtractor();

            mediaExtractor.setDataSource(CONTEXT, videoUri, null);

            for(int i = 0; i < mediaExtractor.getTrackCount(); ++i) {
                final MediaFormat format = mediaExtractor.getTrackFormat(i);

                if(Objects.requireNonNull(format.getString(MediaFormat.KEY_MIME)).startsWith("video/")) {
                    if(format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                        VIDEO_FPS = format.getInteger(MediaFormat.KEY_FRAME_RATE);

                        break;
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "loadFixedData - fps: " + e.getMessage());

            VIDEO_FPS = -1;

            HANDLER.post(() -> LISTENER.onError(false));
        }
    }

    private int[] calculateAndGetImageWH() {
        final int[] wh = new int[2];

        wh[0] = SETTINGS.getMarginInPx() * 2;
        wh[1] = SETTINGS.getMarginInPx() * 2;

        wh[0] += SETTINGS.getColumnNumber() * THUMBNAIL_WH[0];
        wh[1] += SETTINGS.getRowNumber() * THUMBNAIL_WH[1];

        wh[0] += (SETTINGS.getColumnNumber() - 1)  * SETTINGS.getSpaceBetweenThumbnailsInPx();
        wh[1] += (SETTINGS.getRowNumber() - 1)  * SETTINGS.getSpaceBetweenThumbnailsInPx();

        wh[1] += calculateAndGetHeaderLineAndFullHeight()[1];

        return wh;
    }

    private int[] calculateAndGetHeaderLineAndFullHeight() {
        int lines = 0;

        if(SETTINGS.isToShowNameInTitle()) {
            lines++;
        }

        if(SETTINGS.isToShowResolutionInTitle() || SETTINGS.isToShowFramesInTitle()) {
            lines++;
        }

        if(SETTINGS.isToShowDurationInTitle()) {
            lines++;
        }

        if(SETTINGS.isToShowSizeInTitle()) {
            lines++;
        }

        if(lines == 0) {
            return new int[] {0, 0};
        }

        final Paint headerPaint = getHeaderPaint();

        final Rect bounds = new Rect();

        headerPaint.getTextBounds("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", 0, 36, bounds);

        final int fullHeight = SETTINGS.getMarginInPx() //space between the header and the thumbnails, like a bottom margin
                               + (lines * bounds.height()) //height each line occupies
                               + ((lines - 1) * SETTINGS.getTitleSpaceBetweenLinesInPx());

        return new int[] {bounds.height(), fullHeight};
    }

    private int[] calculateAndGetIncrementBy() {
        return new int[] {
            THUMBNAIL_WH[0] + SETTINGS.getSpaceBetweenThumbnailsInPx(),
            THUMBNAIL_WH[1] + SETTINGS.getSpaceBetweenThumbnailsInPx()
        };
    }

    private int[] calculateAndGetTimestampXYHelper(Paint paint) {
        final int[] xy = new int[2];

        if(SETTINGS.getTimestampHorizontalPosition() == AlignmentPopup.LEFT) {
            xy[0] = SETTINGS.getTimestampMarginInPx();
        } else if(SETTINGS.getTimestampHorizontalPosition() == AlignmentPopup.CENTER) {
            xy[0] = THUMBNAIL_WH[0] / 2;
        } else {
            xy[0] = THUMBNAIL_WH[0] - SETTINGS.getTimestampMarginInPx();
        }

        final Rect bounds = new Rect();

        paint.getTextBounds("0123456789:", 0, 11, bounds);

        if(SETTINGS.getTimestampVerticalPosition() == AlignmentPopup.TOP) {
            xy[1] = SETTINGS.getTimestampMarginInPx() + bounds.height();
        } else if(SETTINGS.getTimestampVerticalPosition() == AlignmentPopup.CENTER) {
            xy[1] = (THUMBNAIL_WH[1] / 2) + (bounds.height() / 2);
        } else {
            xy[1] = THUMBNAIL_WH[1] - SETTINGS.getTimestampMarginInPx();
        }

        return xy;
    }

    private void calculateThumbnailSize() {
        MathUtils.getScaledSize(VIDEO_WH, THUMBNAIL_WH, SETTINGS.getThumbnailMaxSize());

        if(THUMBNAIL_WH[0] <= 0) {
            THUMBNAIL_WH[0] = 1;
        }

        if(THUMBNAIL_WH[1] <= 0) {
            THUMBNAIL_WH[1] = 1;
        }
    }

    private Paint getHeaderPaint() {
        final Paint paint = new Paint();

        paint.setColor(ConverterUtils.Color.hexColorToInt(SETTINGS.getTitleColor()));
        paint.setTextSize(SETTINGS.getTitleSize());
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setAntiAlias(true);

        paint.setTypeface(
            Typeface.createFromAsset(
                CONTEXT.getAssets(),
                SETTINGS.getFont().getPath()
            )
        );

        return paint;
    }

    private Paint getThumbnailShadowPaint() {
        final int color = ConverterUtils.Color.hexColorToInt(SETTINGS.getThumbnailShadowColor());

        final Paint paint = new Paint();

        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        paint.setShadowLayer(4f, 1f, 1f, color);

        return paint;
    }

    private Paint getTimestampPaint() {
        final Paint paint = new Paint();

        paint.setColor(ConverterUtils.Color.hexColorToInt(SETTINGS.getTimestampTextColor()));
        paint.setTextSize(SETTINGS.getTimestampSize());
        paint.setShadowLayer(2f, 1f, 1f, ConverterUtils.Color.hexColorToInt(SETTINGS.getTimestampTextShadowColor()));
        paint.setAntiAlias(true);

        if(SETTINGS.getTimestampHorizontalPosition() == AlignmentPopup.LEFT) {
            paint.setTextAlign(Paint.Align.LEFT);
        } else if(SETTINGS.getTimestampHorizontalPosition() == AlignmentPopup.CENTER) {
            paint.setTextAlign(Paint.Align.CENTER);
        } else {
            paint.setTextAlign(Paint.Align.RIGHT);
        }

        paint.setTypeface(
            Typeface.createFromAsset(
                CONTEXT.getAssets(),
                SETTINGS.getFont().getPath()
            )
        );

        return paint;
    }

    public void generate() {
        calculateThumbnailSize();

        final int[] imageWH = calculateAndGetImageWH();

        final Bitmap bitmap = Bitmap.createBitmap(imageWH[0], imageWH[1], Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(bitmap);

        canvas.drawColor(ConverterUtils.Color.hexColorToInt(SETTINGS.getBackgroundColor()));

        final Paint timestampPaint = getTimestampPaint();

        final int numberOfThumbs = SETTINGS.getRowNumber() * SETTINGS.getColumnNumber(),
                  lastHelper = numberOfThumbs - 2;

        final long timeStepInMicro = TimeUnit.MILLISECONDS.toMicros(VIDEO_DURATION / (numberOfThumbs - 1));

        final int[] xy = new int[] {SETTINGS.getMarginInPx(), SETTINGS.getMarginInPx()},
                    xyIncrementBy = calculateAndGetIncrementBy(),
                    currentColumn = new int[] {0},
                    xyTimestamp = calculateAndGetTimestampXYHelper(timestampPaint);

        drawHeader(canvas, xy);

        final long[] currentTimestampInMicro = new long[] {TimeUnit.MILLISECONDS.toMicros(1000)};

        final int precision = SETTINGS.isToUseHighPrecision() ? MediaMetadataRetriever.OPTION_CLOSEST : MediaMetadataRetriever.OPTION_CLOSEST_SYNC;

        final Paint paintThumbnailShadow = getThumbnailShadowPaint();

        for(int i = 0; i < numberOfThumbs; i++) {
            final Bitmap thumbnail = MEDIA_RETRIEVER.getScaledFrameAtTime(
                currentTimestampInMicro[0],
                precision,
                THUMBNAIL_WH[0],
                THUMBNAIL_WH[1]
            );

            if(thumbnail != null) {
                drawThumbnail(canvas, thumbnail, xy, paintThumbnailShadow);

                drawTimestamp(canvas, xy, xyTimestamp, currentTimestampInMicro[0], timestampPaint);

                nextTimestamp(i, lastHelper, timeStepInMicro, currentTimestampInMicro);

                nextXY(currentColumn, xy, xyIncrementBy);
            }

            HANDLER.post(LISTENER::onProgress);
        }

        if(IS_GENERATING_SAMPLE) {
            HANDLER.post(() -> LISTENER.onSampleCompleted(bitmap));
        } else {
            if(createOutputFile(bitmap)) {
                HANDLER.post(LISTENER::onCompleted);
            } else {
                HANDLER.post(() -> LISTENER.onError(true));
            }
        }
    }

    private void drawThumbnail(Canvas canvas, Bitmap thumbnail, int[] xy, Paint paintThumbnailShadow) {
        if(SETTINGS.isToDrawThumbnailShadow()) {
            canvas.drawRect(
                xy[0],
                xy[1],
                xy[0] + THUMBNAIL_WH[0],
                xy[1] + THUMBNAIL_WH[1],
                paintThumbnailShadow
            );
        }

        canvas.drawBitmap(thumbnail, xy[0], xy[1], null);
    }

    private void drawTimestamp(Canvas canvas, int[] xy, int[] xyTimestamp, long timestampInMicro, Paint paint) {
        canvas.drawText(
            (SETTINGS.isToUseHighPrecision() ? "" : "~ ") + FormatterUtils.DateTime.timeInMicro(timestampInMicro),
            xy[0] + xyTimestamp[0],
            xy[1] + xyTimestamp[1],
            paint
        );
    }

    private void drawHeader(Canvas canvas, int[] xy) {
        final int lineHeight = calculateAndGetHeaderLineAndFullHeight()[0];

        final Paint paint = getHeaderPaint();

        final ArrayList<String> lines = new ArrayList<>();

        if(SETTINGS.isToShowNameInTitle()) {
            lines.add(SETTINGS.getNameText() + ": " + VIDEO_NAME);
        }

        if(SETTINGS.isToShowResolutionInTitle() || SETTINGS.isToShowFramesInTitle()) {
            String s = null;

            if(SETTINGS.isToShowResolutionInTitle()) {
                s = SETTINGS.getResolutionText() + ": " + VIDEO_WH[0] + "x" + VIDEO_WH[1];
            }

            if(SETTINGS.isToShowFramesInTitle()) {
                s = (s == null ? "" : s + "    ") + SETTINGS.getFramesText() + ": " + VIDEO_FPS;
            }

            lines.add(s);
        }

        if(SETTINGS.isToShowDurationInTitle()) {
            lines.add(SETTINGS.getDurationText() + ": " + FormatterUtils.DateTime.timeInMillis(VIDEO_DURATION));
        }

        if(SETTINGS.isToShowSizeInTitle()) {
            lines.add(SETTINGS.getSizeText() + ": " + FormatterUtils.File.toReadableSize(VIDEO_SIZE));
        }

        if(!lines.isEmpty()) {
            boolean isFirstLine = true;

            for(String line : lines) {
                xy[1] += lineHeight;

                if(isFirstLine) {
                    isFirstLine = false;
                } else {
                    xy[1] += SETTINGS.getTitleSpaceBetweenLinesInPx();
                }

                canvas.drawText(line, xy[0], xy[1], paint);
            }

            xy[1] += SETTINGS.getMarginInPx();
        }
    }

    private void nextTimestamp(int i , int last, long timeStepInMicro, long[] currentTimestampInMicro) {
        if(i == last) {
            currentTimestampInMicro[0] = TimeUnit.MILLISECONDS.toMicros(VIDEO_DURATION - 1000);
        } else {
            currentTimestampInMicro[0] += timeStepInMicro;
        }
    }

    private void nextXY(int[] currentColumn, int[] xy, int[] xyIncrementBy) {
        ++currentColumn[0];

        if(currentColumn[0] >= SETTINGS.getColumnNumber()) {
            xy[0] = SETTINGS.getMarginInPx();
            xy[1] += xyIncrementBy[1];

            currentColumn[0] = 0;
        } else {
            xy[0] += xyIncrementBy[0];
        }
    }

    private boolean createOutputFile(Bitmap bitmap) {
        String fileName;

        if(SETTINGS.isToUseFileNameToSave()) {
            fileName = VIDEO_NAME.substring(0, VIDEO_NAME.lastIndexOf("."));
        } else {
            fileName = UUID.randomUUID().toString().replaceAll("-", "_");
        }

        final File file = AppFilesUtils.createFile(CONTEXT, fileName, "png");

        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

            fileOutputStream.flush();

            fileOutputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "createOutputFile: " + e.getMessage());

            return false;
        }

        AppFilesUtils.notifyMediaScanner(CONTEXT, file);

        return true;
    }
}
