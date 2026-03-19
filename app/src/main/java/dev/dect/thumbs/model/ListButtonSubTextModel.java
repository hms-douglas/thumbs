package dev.dect.thumbs.model;

import androidx.annotation.Nullable;

public class ListButtonSubTextModel {
    public interface OnListButtonSubText {
        void onButtonClicked(ListButtonSubTextModel listButtonSubTextModel, int pos);
    }

    private final int ID_TITLE;

    private String VALUE,
                   DISABLED_MESSAGE;

    private OnListButtonSubText LISTENER;

    private boolean IS_ENABLED,
                    IS_MESSAGE_TEXT,
                    IS_VISIBLE,
                    IS_LAST_FROM_GROUP;

    public ListButtonSubTextModel(int title, @Nullable String value, @Nullable OnListButtonSubText onListButtonSubText, boolean lastFromGroup) {
        this.ID_TITLE = title;
        this.VALUE = value;
        this.IS_LAST_FROM_GROUP = lastFromGroup;
        this.LISTENER = onListButtonSubText;
        this.IS_ENABLED = onListButtonSubText != null;
        this.IS_MESSAGE_TEXT = false;
        this.IS_VISIBLE = true;
    }

    public ListButtonSubTextModel(int title, String value, boolean messageText, OnListButtonSubText onListButtonSubText, boolean lastFromGroup) {
        this.ID_TITLE = title;
        this.VALUE = value;
        this.IS_LAST_FROM_GROUP = lastFromGroup;
        this.LISTENER = onListButtonSubText;
        this.IS_ENABLED = true;
        this.IS_MESSAGE_TEXT = messageText;
        this.IS_VISIBLE = true;
    }

    public int getIdTitle() {
        return ID_TITLE;
    }

    public String getValue() {
        return VALUE;
    }

    public void setValue(String v) {
        this.VALUE = v;
    }

    public void setValue(int v) {
        this.VALUE = String.valueOf(v);
    }

    public boolean isLastItemFromGroup(){
        return IS_LAST_FROM_GROUP;
    }

    public OnListButtonSubText getListener() {
        return LISTENER;
    }

    public boolean isEnabled() {
        return IS_ENABLED;
    }

    public boolean isVisible() {
        return IS_VISIBLE;
    }

    public void setVisible(boolean isVisible) {
        IS_VISIBLE = isVisible;
    }

    public void setDisabledMessage(String message) {
        DISABLED_MESSAGE = message;
    }

    public String getDisabledMessage() {
        return DISABLED_MESSAGE;
    }

    public boolean isIsMessageText() {
        return IS_MESSAGE_TEXT;
    }

    public void setListener(OnListButtonSubText listener) {
        LISTENER = listener;

        IS_ENABLED = true;

        IS_MESSAGE_TEXT = false;
    }

    public void setIsLastItemFromGroup(boolean isLastFromGroup) {
        IS_LAST_FROM_GROUP = isLastFromGroup;
    }
}
