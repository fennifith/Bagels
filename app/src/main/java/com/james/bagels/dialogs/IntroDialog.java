package com.james.bagels.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatDialog;

import com.james.bagels.BuildConfig;
import com.james.bagels.R;
import com.james.bagels.activities.MainActivity;
import com.james.bagels.utils.ImageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IntroDialog extends AppCompatDialog {

    @BindView(R.id.startFab)
    FloatingActionButton startFab;

    public IntroDialog(Context context) {
        super(context, R.style.DialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_intro);
        ButterKnife.bind(this);

        startFab.setImageDrawable(ImageUtils.tintDrawable(ImageUtils.getVectorDrawable(getContext(), R.drawable.ic_arrow_forward), Color.BLACK));
    }

    @OnClick(R.id.start)
    public void start() {
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putInt(MainActivity.KEY_FIRST_TIME, BuildConfig.VERSION_CODE).apply();
        if (isShowing()) dismiss();
    }
}
