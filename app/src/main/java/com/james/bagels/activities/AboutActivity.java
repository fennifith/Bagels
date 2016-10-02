package com.james.bagels.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.LinearLayout;

import com.james.bagels.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {

    private static final String URL_JAMES = "https://theandroidmaster.github.io/";
    private static final String URL_JARED = "https://plus.google.com/108101726709784843314";
    private static final String URL_STORE_PREFIX = "https://play.google.com/store/apps/details?id=";
    private static final String URL_GITHUB = "https://github.com/TheAndroidMaster/Bagels";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.james, R.id.jared, R.id.store, R.id.github})
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
        }
    }
}
