package dev.dect.thumbs.model;

public class ListSwitchModel {
    public interface OnListSwitchModel {
        void onChange(ListSwitchModel listSwitchModel, boolean b, int pos);
    }

    public static final int NO_TEXT = -1;

    private final int ID_TITLE;

    private int ID_SUB_TITLE;

    private boolean IS_ENABLED,
                    IS_VISIBLE;

    private final boolean IS_LAST_FROM_GROUP;

    private final OnListSwitchModel LISTENER;

    public ListSwitchModel(int title, int subTitle, boolean b, OnListSwitchModel listener, boolean lastFromGroup) {
        this.ID_TITLE = title;
        this.ID_SUB_TITLE = subTitle;
        this.IS_ENABLED = b;
        this.IS_LAST_FROM_GROUP = lastFromGroup;
        this.LISTENER = listener;
        this.IS_VISIBLE = true;
    }

    public int getIdTitle() {
        return ID_TITLE;
    }

    public int getIdSubTitle() {
        return ID_SUB_TITLE;
    }

    public void setIdSubTitle(int idSubTitle) {
        ID_SUB_TITLE = idSubTitle;
    }

    public boolean hasSubTitle() {
        return this.getIdSubTitle() != NO_TEXT;
    }

    public boolean isEnabled() {
        return IS_ENABLED;
    }

    public void setEnabled(boolean b) {
        this.IS_ENABLED = b;
    }

    public boolean isVisible() {
        return IS_VISIBLE;
    }

    public void setIsVisible(boolean isVisible) {
        IS_VISIBLE = isVisible;
    }

    public boolean isLastItemFromGroup(){
        return IS_LAST_FROM_GROUP;
    }

    public OnListSwitchModel getListener() {
        return LISTENER;
    }
}
