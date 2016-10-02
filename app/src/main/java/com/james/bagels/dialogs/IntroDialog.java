package com.james.bagels.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDialog;

import com.james.bagels.BuildConfig;
import com.james.bagels.R;
import com.james.bagels.activities.MainActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class IntroDialog extends AppCompatDialog {

    public IntroDialog(Context context) {
        super(context, R.style.DialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_intro);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.start)
    public void start() {
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putInt(MainActivity.KEY_FIRST_TIME, BuildConfig.VERSION_CODE).apply();
        if (isShowing()) dismiss();
    }
}
