package com.james.bagels.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.Toast;

import com.james.bagels.utils.ImageUtils;

public class BagelImageView extends AppCompatImageView {

    private boolean isBlurred;
    private Drawable drawable, blurredDrawable;

    public BagelImageView(Context context) {
        super(context);
    }

    public BagelImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BagelImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
        new Thread() {
            @Override
            public void run() {
                final Bitmap bitmap = ImageUtils.blurBitmap(getContext(), ImageUtils.drawableToBitmap(BagelImageView.this.drawable));
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap != null) blurredDrawable = new BitmapDrawable(getResources(), bitmap);
                    }
                });
            }
        }.start();
    }

    public void setBlurred(boolean isBlurred) {
        if (!this.isBlurred) drawable = getDrawable();
        if (this.isBlurred != isBlurred && drawable != null && blurredDrawable != null) {
            TransitionDrawable transitionDrawable;
            if (!isBlurred) {
                transitionDrawable = new TransitionDrawable(new Drawable[]{blurredDrawable, drawable});
            } else {
                transitionDrawable = new TransitionDrawable(new Drawable[]{drawable, blurredDrawable});
            }

            setImageDrawable(transitionDrawable);
            transitionDrawable.startTransition(500);
            this.isBlurred = isBlurred;
        }
    }

    public boolean isBlurred() {
        return isBlurred;
    }
}
