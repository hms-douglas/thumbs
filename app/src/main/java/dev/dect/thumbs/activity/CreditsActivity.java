package dev.dect.thumbs.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import dev.dect.thumbs.R;
import dev.dect.thumbs.data.Constants;
import dev.dect.thumbs.utils.InterfaceUtils;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(null);

        InterfaceUtils.StatusBar.updateColor(this);

        setContentView(R.layout.activity_credits);

        initListeners();
    }

    private void initListeners() {
        findViewById(R.id.btnBack).setOnClickListener((v) -> finish());

        findViewById(R.id.btnLicenseCCBY3).setOnClickListener((v) -> openLicenseUrlText(Constants.Url.License.CC_BY_3));
        findViewById(R.id.btnLicenseMit).setOnClickListener((v) -> openLicenseUrlText(Constants.Url.License.MIT));
        findViewById(R.id.btnLicenseOpenFont).setOnClickListener((v) -> openLicenseUrlText(Constants.Url.License.OPEN_FONT));
        findViewById(R.id.btnLicenseUbuntuFont).setOnClickListener((v) -> openLicenseUrlText(Constants.Url.License.UBUNTU_FONT));
    }

    private void openLicenseUrlText(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
