package com.james.bagels.utils;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.james.bagels.R;
import com.james.bagels.views.BagelImageView;

public class BagelPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View page, float position) {
        page.setTranslationX(page.getWidth() * -(position / 2));

        View imageView = page.findViewById(R.id.imageView);

        if (position <= -1.0f || position >= 1.0f) page.setAlpha(0.0f);
        else if (position == 0.0f) page.setAlpha(1.0f);
        else {
            page.setAlpha(1.0f - Math.abs(position));
            if (imageView != null && imageView instanceof BagelImageView)
                ((BagelImageView) imageView).setBlurred(false);
        }
    }
}
