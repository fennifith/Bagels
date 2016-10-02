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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.james.bagels.data.Bagel;
import com.james.bagels.utils.ImageUtils;

public class BagelImageView extends AppCompatImageView {

    private boolean isBlurred;
    private Bagel bagel;
    private Drawable drawable, blurredDrawable;

    private Handler handler;
    private Runnable runnable;

    public BagelImageView(Context context) {
        super(context);
    }

    public BagelImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BagelImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBagel(Bagel bagel) {
        this.bagel = bagel;
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                setBlurred(false);
            }
        };

        Glide.with(getContext()).load(BagelImageView.this.bagel.location).centerCrop().into(new GlideDrawableImageViewTarget(this) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                super.onResourceReady(resource, animation);
                drawable = resource;

                new Thread() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = ImageUtils.blurBitmap(getContext(), ImageUtils.drawableToBitmap(drawable));
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (bitmap != null) {
                                    blurredDrawable = new BitmapDrawable(getResources(), bitmap);
                                    handler.postDelayed(runnable, 5000);
                                }
                            }
                        });
                    }
                }.start();
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                e.printStackTrace();
            }
        });
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
