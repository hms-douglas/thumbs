package dev.dect.thumbs.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import dev.dect.thumbs.R;

public class ExternalActivityUtils {
    public static final String TAG = ExternalActivityUtils.class.getSimpleName();

    public static void requestSettings(Context ctx) {
        try {
            ctx.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + ctx.getPackageName())));
        } catch (Exception e) {
            Log.e(TAG, "requestSettings: " + e.getMessage());

            Toast.makeText(ctx, ctx.getString(R.string.toast_error_generic), Toast.LENGTH_SHORT).show();
        }
    }
}
