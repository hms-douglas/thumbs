package dev.dect.thumbs.data;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettings {
    private final SharedPreferences.Editor EDITOR;

    private boolean SHOW_PREVIEW;

    public AppSettings(Context ctx) {
        final SharedPreferences sp = ctx.getSharedPreferences(SP.NAME, Context.MODE_PRIVATE);

        SHOW_PREVIEW = sp.getBoolean(SP.Keys.SHOW_PREVIEW, true);

        EDITOR = sp.edit();
    }

    public boolean isToShowPreview() {
        return SHOW_PREVIEW;
    }

    public void setShowPreview(boolean showPreview) {
        SHOW_PREVIEW = showPreview;

        put(SP.Keys.SHOW_PREVIEW, showPreview);
    }

    /** @noinspection SameParameterValue*/
    private void put(String key, boolean b) {
        EDITOR.putBoolean(key, b).apply();
    }
}
