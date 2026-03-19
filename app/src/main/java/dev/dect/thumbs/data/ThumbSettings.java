package dev.dect.thumbs.data;

import android.content.Context;
import android.content.SharedPreferences;

import dev.dect.thumbs.R;
import dev.dect.thumbs.model.FontModel;
import dev.dect.thumbs.popup.AlignmentPopup;
import dev.dect.thumbs.utils.ConverterUtils;

public class ThumbSettings {
    private final SharedPreferences.Editor EDITOR;

    private boolean SHOW_NAME_IN_TITLE,
                    SHOW_SIZE_IN_TITLE,
                    SHOW_RESOLUTION_IN_TITLE,
                    SHOW_FRAMES_IN_TITLE,
                    SHOW_DURATION_IN_TITLE,
                    USE_FILE_NAME_TO_SAVE,
                    USE_HIGH_PRECISION,
                    DRAW_THUMBNAIL_SHADOW;

    private String NAME_TEXT,
                   SIZE_TEXT,
                   RESOLUTION_TEXT,
                   FRAMES_TEXT,
                   DURATION_TEXT,
                   BACKGROUND_COLOR,
                   TIMESTAMP_TEXT_COLOR,
                   TIMESTAMP_TEXT_SHADOW_COLOR,
                   TITLE_COLOR,
                   THUMBNAIL_SHADOW_COLOR;

    private int MARGIN_IN_PX,
                COLUMN_NUMBER,
                ROW_NUMBER,
                THUMBNAIL_SPACE_BETWEEN_IN_PX,
                TIMESTAMP_VERTICAL_POSITION,
                TIMESTAMP_HORIZONTAL_POSITION,
                TIMESTAMP_SIZE,
                THUMBNAIL_MAX_SIZE,
                TIMESTAMP_MARGIN_IN_PX,
                TITLE_SIZE,
                TITLE_SPACE_BETWEEN_LINES_IN_PX;

    private FontModel FONT;

    public ThumbSettings(Context ctx) {
        final SharedPreferences sp = ctx.getSharedPreferences(SP.NAME, Context.MODE_PRIVATE);

        BACKGROUND_COLOR = sp.getString(SP.Keys.BACKGROUND_COLOR, "#EFEFEFFF");
        MARGIN_IN_PX = sp.getInt(SP.Keys.MARGIN_IN_PX, 15);
        ROW_NUMBER = sp.getInt(SP.Keys.ROW_NUMBER, 4);
        COLUMN_NUMBER = sp.getInt(SP.Keys.COLUMN_NUMBER, 4);

        THUMBNAIL_MAX_SIZE = sp.getInt(SP.Keys.THUMBNAIL_MAX_SIZE, 432);
        THUMBNAIL_SPACE_BETWEEN_IN_PX = sp.getInt(SP.Keys.THUMBNAIL_SPACE_BETWEEN_IN_PX, 15);
        THUMBNAIL_SHADOW_COLOR = sp.getString(SP.Keys.THUMBNAIL_SHADOW_COLOR, "#000000BE");

        TIMESTAMP_VERTICAL_POSITION = sp.getInt(SP.Keys.TIMESTAMP_VERTICAL_POSITION, AlignmentPopup.BOTTOM);
        TIMESTAMP_HORIZONTAL_POSITION = sp.getInt(SP.Keys.TIMESTAMP_HORIZONTAL_POSITION, AlignmentPopup.RIGHT);
        TIMESTAMP_SIZE = sp.getInt(SP.Keys.TIMESTAMP_SIZE, 20);
        TIMESTAMP_MARGIN_IN_PX = sp.getInt(SP.Keys.TIMESTAMP_MARGIN, 15);
        TIMESTAMP_TEXT_COLOR = sp.getString(SP.Keys.TIMESTAMP_TEXT_COLOR, "#F9F9F9FF");
        TIMESTAMP_TEXT_SHADOW_COLOR = sp.getString(SP.Keys.TIMESTAMP_TEXT_SHADOW_COLOR, "#000000FF");

        SHOW_NAME_IN_TITLE = sp.getBoolean(SP.Keys.SHOW_NAME_IN_TITLE, true);
        SHOW_RESOLUTION_IN_TITLE = sp.getBoolean(SP.Keys.SHOW_RESOLUTION_IN_TITLE, true);
        SHOW_FRAMES_IN_TITLE = sp.getBoolean(SP.Keys.SHOW_FRAMES_IN_TITLE, true);
        SHOW_DURATION_IN_TITLE = sp.getBoolean(SP.Keys.SHOW_DURATION_IN_TITLE, true);
        SHOW_SIZE_IN_TITLE = sp.getBoolean(SP.Keys.SHOW_SIZE_IN_TITLE, true);

        NAME_TEXT = sp.getString(SP.Keys.NAME_TEXT, ctx.getString(R.string.title_name));
        RESOLUTION_TEXT = sp.getString(SP.Keys.RESOLUTION_TEXT, ctx.getString(R.string.title_resolution));
        DURATION_TEXT = sp.getString(SP.Keys.DURATION_TEXT, ctx.getString(R.string.title_duration));
        FRAMES_TEXT = sp.getString(SP.Keys.FRAMES_TEXT, ctx.getString(R.string.title_frames));
        SIZE_TEXT = sp.getString(SP.Keys.SIZE_TEXT, ctx.getString(R.string.title_size));

        TITLE_COLOR = sp.getString(SP.Keys.TITLE_COLOR, "#000000FF");
        TITLE_SIZE = sp.getInt(SP.Keys.TITLE_SIZE, 25);
        TITLE_SPACE_BETWEEN_LINES_IN_PX = sp.getInt(SP.Keys.TITLE_SPACE_BETWEEN_LINES_IN_PX, 8);

        USE_FILE_NAME_TO_SAVE = sp.getBoolean(SP.Keys.USE_FILE_NAME_TO_SAVE, true);

        USE_HIGH_PRECISION = sp.getBoolean(SP.Keys.USE_HIGH_PRECISION, true);

        FONT = FontModel.getFontFromId(
            sp.getInt(
                SP.Keys.FONT_ID,
                FontModel.getRobotoFromAsset().getId()
            )
        );

        updateIsToDrawShadow();

        EDITOR = sp.edit();
    }

    public boolean isToShowNameInTitle() {
        return SHOW_NAME_IN_TITLE;
    }

    public void setShowNameInTitle(boolean showNameInTitle) {
        SHOW_NAME_IN_TITLE = showNameInTitle;

        put(SP.Keys.SHOW_NAME_IN_TITLE, showNameInTitle);
    }

    public boolean isToShowSizeInTitle() {
        return SHOW_SIZE_IN_TITLE;
    }

    public void setShowSizeInTitle(boolean showSizeInTitle) {
        SHOW_SIZE_IN_TITLE = showSizeInTitle;

        put(SP.Keys.SHOW_SIZE_IN_TITLE, showSizeInTitle);
    }

    public boolean isToShowResolutionInTitle() {
        return SHOW_RESOLUTION_IN_TITLE;
    }

    public void setShowResolutionInTitle(boolean showResolutionInTitle) {
        SHOW_RESOLUTION_IN_TITLE = showResolutionInTitle;

        put(SP.Keys.SHOW_RESOLUTION_IN_TITLE, showResolutionInTitle);
    }

    public boolean isToShowFramesInTitle() {
        return SHOW_FRAMES_IN_TITLE;
    }

    public void setShowFramesInTitle(boolean showFramesInTitle) {
        SHOW_FRAMES_IN_TITLE = showFramesInTitle;

        put(SP.Keys.SHOW_FRAMES_IN_TITLE, showFramesInTitle);
    }

    public boolean isToShowDurationInTitle() {
        return SHOW_DURATION_IN_TITLE;
    }

    public void setShowDurationInTitle(boolean showDurationInTitle) {
        SHOW_DURATION_IN_TITLE = showDurationInTitle;

        put(SP.Keys.SHOW_DURATION_IN_TITLE, showDurationInTitle);
    }

    public boolean isToUseFileNameToSave() {
        return USE_FILE_NAME_TO_SAVE;
    }

    public void setUseFileNameToSave(boolean useFileNameToSave) {
        USE_FILE_NAME_TO_SAVE = useFileNameToSave;

        put(SP.Keys.USE_FILE_NAME_TO_SAVE, useFileNameToSave);
    }

    public boolean isToUseHighPrecision() {
        return USE_HIGH_PRECISION;
    }

    public void setUseHighPrecision(boolean useHighPrecision) {
        USE_HIGH_PRECISION = useHighPrecision;

        put(SP.Keys.USE_HIGH_PRECISION, useHighPrecision);
    }

    public String getNameText() {
        return NAME_TEXT;
    }

    public void setNameText(String nameText) {
        NAME_TEXT = nameText;

        put(SP.Keys.NAME_TEXT, nameText);
    }

    public String getSizeText() {
        return SIZE_TEXT;
    }

    public void setSizeText(String sizeText) {
        SIZE_TEXT = sizeText;

        put(SP.Keys.SIZE_TEXT, sizeText);
    }

    public String getResolutionText() {
        return RESOLUTION_TEXT;
    }

    public void setResolutionText(String resolutionText) {
        RESOLUTION_TEXT = resolutionText;

        put(SP.Keys.RESOLUTION_TEXT, resolutionText);
    }

    public String getFramesText() {
        return FRAMES_TEXT;
    }

    public void setFramesText(String framesText) {
        FRAMES_TEXT = framesText;

        put(SP.Keys.FRAMES_TEXT, framesText);
    }

    public String getBackgroundColor() {
        return BACKGROUND_COLOR;
    }

    public void setBackgroundColor(String backgroundColor) {
        BACKGROUND_COLOR = backgroundColor;

        put(SP.Keys.BACKGROUND_COLOR, backgroundColor);
    }

    public String getTitleColor() {
        return TITLE_COLOR;
    }

    public void setTitleColor(String titleColor) {
        TITLE_COLOR = titleColor;

        put(SP.Keys.TITLE_COLOR, titleColor);
    }

    public int getTitleSize() {
        return TITLE_SIZE;
    }

    public void setTitleSize(int titleSize) {
        TITLE_SIZE = titleSize;

        put(SP.Keys.TITLE_SIZE, titleSize);
    }

    public int getTitleSpaceBetweenLinesInPx() {
        return TITLE_SPACE_BETWEEN_LINES_IN_PX;
    }

    public void setTitleSpaceBetweenLinesInPx(int titleSpaceBetweenLinesInPx) {
        TITLE_SPACE_BETWEEN_LINES_IN_PX = titleSpaceBetweenLinesInPx;

        put(SP.Keys.TITLE_SPACE_BETWEEN_LINES_IN_PX, titleSpaceBetweenLinesInPx);
    }

    public String getTimestampTextShadowColor() {
        return TIMESTAMP_TEXT_SHADOW_COLOR;
    }

    public void setTimestampTextShadowColor(String timestampTextShadowColor) {
        TIMESTAMP_TEXT_SHADOW_COLOR = timestampTextShadowColor;

        put(SP.Keys.TIMESTAMP_TEXT_SHADOW_COLOR, timestampTextShadowColor);
    }
    public String getTimestampTextColor() {
        return TIMESTAMP_TEXT_COLOR;
    }

    public void setTimestampTextColor(String timestampTextColor) {
        TIMESTAMP_TEXT_COLOR = timestampTextColor;

        put(SP.Keys.TIMESTAMP_TEXT_COLOR, timestampTextColor);
    }

    public String getDurationText() {
        return DURATION_TEXT;
    }

    public void setDurationText(String durationText) {
        DURATION_TEXT = durationText;

        put(SP.Keys.DURATION_TEXT, durationText);
    }

    public int getMarginInPx() {
        return MARGIN_IN_PX;
    }

    public void setMarginInPx(int margin) {
        MARGIN_IN_PX = margin;

        put(SP.Keys.MARGIN_IN_PX, margin);
    }

    public int getColumnNumber() {
        return COLUMN_NUMBER;
    }

    public void setColumnNumber(int columnNumber) {
        COLUMN_NUMBER = columnNumber;

        put(SP.Keys.COLUMN_NUMBER, columnNumber);
    }

    public int getRowNumber() {
        return ROW_NUMBER;
    }

    public void setRowNumber(int rowNumber) {
        ROW_NUMBER = rowNumber;

        put(SP.Keys.ROW_NUMBER, rowNumber);
    }

    public int getSpaceBetweenThumbnailsInPx() {
        return THUMBNAIL_SPACE_BETWEEN_IN_PX;
    }

    public void setSpaceBetweenThumbnailsInPx(int spaceBetweenThumbnails) {
        THUMBNAIL_SPACE_BETWEEN_IN_PX = spaceBetweenThumbnails;

        updateIsToDrawShadow();

        put(SP.Keys.THUMBNAIL_SPACE_BETWEEN_IN_PX, spaceBetweenThumbnails);
    }

    public int getTimestampVerticalPosition() {
        return TIMESTAMP_VERTICAL_POSITION;
    }

    public void setTimestampVerticalPosition(int timestampPosition) {
        TIMESTAMP_VERTICAL_POSITION = timestampPosition;

        put(SP.Keys.TIMESTAMP_VERTICAL_POSITION, timestampPosition);
    }

    public int getTimestampHorizontalPosition() {
        return TIMESTAMP_HORIZONTAL_POSITION;
    }

    public void setTimestampHorizontalPosition(int timestampPosition) {
        TIMESTAMP_HORIZONTAL_POSITION = timestampPosition;

        put(SP.Keys.TIMESTAMP_HORIZONTAL_POSITION, timestampPosition);
    }

    public String getTimestampPositionName(Context ctx) {
        return AlignmentPopup.getName(ctx, TIMESTAMP_VERTICAL_POSITION, TIMESTAMP_HORIZONTAL_POSITION);
    }

    public int getTimestampSize() {
        return TIMESTAMP_SIZE;
    }

    public void setTimestampSize(int timestampSize) {
        TIMESTAMP_SIZE = timestampSize;

        put(SP.Keys.TIMESTAMP_SIZE, timestampSize);
    }

    public int getTimestampMarginInPx() {
        return TIMESTAMP_MARGIN_IN_PX;
    }

    public void setTimestampMarginInPx(int timestampMargin) {
        TIMESTAMP_MARGIN_IN_PX = timestampMargin;
    }

    public int getThumbnailMaxSize() {
        return THUMBNAIL_MAX_SIZE;
    }

    public void setThumbnailMaxSize(int thumbnailMaxSize) {
        THUMBNAIL_MAX_SIZE = thumbnailMaxSize;

        put(SP.Keys.THUMBNAIL_MAX_SIZE, thumbnailMaxSize);
    }

    public String getThumbnailShadowColor() {
        return THUMBNAIL_SHADOW_COLOR;
    }

    public void setThumbnailShadowColor(String thumbnailShadowColor) {
        THUMBNAIL_SHADOW_COLOR = thumbnailShadowColor;

        updateIsToDrawShadow();

        put(SP.Keys.THUMBNAIL_SHADOW_COLOR, thumbnailShadowColor);
    }

    public boolean isToDrawThumbnailShadow() {
        return DRAW_THUMBNAIL_SHADOW;
    }

    public FontModel getFont() {
        return FONT;
    }

    public void setFont(FontModel font) {
        FONT = font;

        put(SP.Keys.FONT_ID, FONT.getId());
    }

    private void updateIsToDrawShadow() {
        DRAW_THUMBNAIL_SHADOW = (ConverterUtils.Color.hexColorToArgb(THUMBNAIL_SHADOW_COLOR)[0] != 0) && (THUMBNAIL_SPACE_BETWEEN_IN_PX > 4);
    }

    private void put(String key, int i) {
        EDITOR.putInt(key, i).apply();
    }

    private void put(String key, String s) {
        EDITOR.putString(key, s).apply();
    }

    private void put(String key, boolean b) {
        EDITOR.putBoolean(key, b).apply();
    }
}
