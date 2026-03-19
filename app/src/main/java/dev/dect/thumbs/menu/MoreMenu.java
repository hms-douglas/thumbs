package dev.dect.thumbs.menu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.PopupMenu;

import dev.dect.thumbs.R;
import dev.dect.thumbs.activity.CreditsActivity;
import dev.dect.thumbs.data.AppSettings;
import dev.dect.thumbs.data.Constants;
import dev.dect.thumbs.utils.ExternalActivityUtils;

public class MoreMenu {
    public interface OnMainMenu {
        void onPreviewChanged();
    }

    private final PopupMenu POPUP_MENU;

    private final Menu MENU;

    public MoreMenu(View anchor, OnMainMenu l) {
        final Context ctx = anchor.getContext();

        POPUP_MENU = new PopupMenu(ctx, anchor, Gravity.END, 0, R.style.Theme_Thumbs_PopupMenu);

        MENU = POPUP_MENU.getMenu();

        MENU.setGroupDividerEnabled(true);

        POPUP_MENU.getMenuInflater().inflate(R.menu.menu_more, MENU);

        POPUP_MENU.setOnMenuItemClickListener((item) -> {
            final int idClicked = item.getItemId();

            if(idClicked == R.id.preview) {
                l.onPreviewChanged();
            } else if(idClicked == R.id.github) {
                ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Url.App.REPOSITORY)));
            } else if(idClicked == R.id.info) {
                ExternalActivityUtils.requestSettings(ctx);
            } else if(idClicked == R.id.credits) {
                ctx.startActivity(new Intent(ctx, CreditsActivity.class));
            }

            return true;
        });

        POPUP_MENU.setForceShowIcon(true);
    }

    public void show(AppSettings appSettings) {
        MENU.findItem(R.id.preview).setTitle(appSettings.isToShowPreview() ? R.string.setting_app_preview_disable : R.string.setting_app_preview_enable);

        POPUP_MENU.show();
    }
}
