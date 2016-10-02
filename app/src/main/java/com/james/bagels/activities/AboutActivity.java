package com.james.bagels.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Toast;

import com.james.bagels.R;
import com.james.bagels.dialogs.LicenseDialog;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class AboutActivity extends AppCompatActivity {

    private static final String URL_JAMES = "https://theandroidmaster.github.io/";
    private static final String URL_JARED = "https://plus.google.com/108101726709784843314";
    private static final String URL_STORE_PREFIX = "https://play.google.com/store/apps/details?id=";
    private static final String URL_GITHUB = "https://github.com/TheAndroidMaster/Bagels";

    private String[] bagelStrings;
    private Random random;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        bagelStrings = getResources().getStringArray(R.array.messages);
        random = new Random();
    }

    @OnClick({R.id.james, R.id.jared, R.id.store, R.id.github, R.id.libraries})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.james:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_JAMES)));
                break;
            case R.id.jared:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_JARED)));
                break;
            case R.id.store:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_PREFIX + getPackageName())));
                break;
            case R.id.github:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_GITHUB)));
                break;
            case R.id.libraries:
                new LicenseDialog(this).show();
        }
    }

    @OnLongClick(R.id.icon)
    public boolean onLongClick(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        Toast.makeText(this, bagelStrings[random.nextInt(bagelStrings.length)], Toast.LENGTH_SHORT).show();
        return true;
    }
}
