package dev.dect.thumbs.model;

import dev.dect.thumbs.utils.ConverterUtils;

public class ListButtonColorModel {
    public interface OnListButtonColorModel {
        void onColorPicked(String color);
    }

    private final int ID_TITLE;

    private final boolean IS_LAST_FROM_GROUP,
                          HAS_ALPHA;

    private final OnListButtonColorModel LISTENER;

    private String COLOR;

    public ListButtonColorModel(int title, String color, OnListButtonColorModel onListButtonSubText, boolean lastFromGroup) {
        this(title, color, false, onListButtonSubText, lastFromGroup);
    }

    public ListButtonColorModel(int title, String color, boolean alpha, OnListButtonColorModel onListButtonSubText, boolean lastFromGroup) {
        this.ID_TITLE = title;
        this.COLOR = color;
        this.IS_LAST_FROM_GROUP = lastFromGroup;
        this.LISTENER = onListButtonSubText;
        this.HAS_ALPHA = alpha;
    }

    public int getIdTitle() {
        return ID_TITLE;
    }

    public void setColor(String color) {
        this.COLOR = color;
    }

    public String getColor() {
        return COLOR;
    }

    public int getColorInt() {
        return ConverterUtils.Color.hexColorToInt(COLOR);
    }

    public boolean isLastItemFromGroup(){
        return IS_LAST_FROM_GROUP;
    }

    public OnListButtonColorModel getListener() {
        return LISTENER;
    }

    public boolean hasAlpha() {
        return HAS_ALPHA;
    }
}
