package com.james.bagels.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.james.bagels.R;
import com.james.bagels.data.Bagel;
import com.james.bagels.views.BagelImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BagelFragment extends Fragment {

    public static final String EXTRA_BAGEL = "com.james.bagels.EXTRA_BAGEL";

    @BindView(R.id.imageView)
    BagelImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bagel, container, false);
        ButterKnife.bind(this, v);

        Bagel bagel = getArguments().getParcelable(EXTRA_BAGEL);
        imageView.setBagel(bagel);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imageView.setBlurred(false);
                        break;
                }

                return false;
            }
        });

        return v;
    }
}
