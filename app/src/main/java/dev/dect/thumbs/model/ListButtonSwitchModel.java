package dev.dect.thumbs.model;

public class ListButtonSwitchModel {
    public interface OnListButtonSwitchModel {
        void onButtonClicked(ListButtonSwitchModel listButtonSubText, int pos);

        void onChange(boolean b);
    }

    private final int ID_TITLE;

    private String VALUE;

    private boolean IS_ENABLED;

    private final boolean IS_LAST_FROM_GROUP;

    private final OnListButtonSwitchModel LISTENER;

    public ListButtonSwitchModel(int title, String value, OnListButtonSwitchModel l, boolean b, boolean lastFromGroup) {
        this.ID_TITLE = title;
        this.VALUE = value;
        this.IS_LAST_FROM_GROUP = lastFromGroup;
        this.LISTENER = l;
        this.IS_ENABLED = b;
    }

    public ListButtonSwitchModel(int title, int value, OnListButtonSwitchModel l, boolean b, boolean lastFromGroup) {
        this.ID_TITLE = title;
        this.VALUE = String.valueOf(value);
        this.IS_LAST_FROM_GROUP = lastFromGroup;
        this.LISTENER = l;
        this.IS_ENABLED = b;
    }

    public ListButtonSwitchModel(int title, float value, OnListButtonSwitchModel l, boolean b, boolean lastFromGroup) {
        this.ID_TITLE = title;
        this.VALUE = String.valueOf(value);
        this.IS_LAST_FROM_GROUP = lastFromGroup;
        this.LISTENER = l;
        this.IS_ENABLED = b;
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

    public void setValue(float v) {
        this.VALUE = String.valueOf(v);
    }

    public boolean isLastItemFromGroup(){
        return IS_LAST_FROM_GROUP;
    }

    public boolean isEnabled() {
        return IS_ENABLED;
    }

    public void setEnabled(boolean b) {
        this.IS_ENABLED = b;
    }

    public OnListButtonSwitchModel getListener() {
        return LISTENER;
    }
}
