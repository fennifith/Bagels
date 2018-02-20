package com.james.bagels.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.james.bagels.data.Bagel;
import com.james.bagels.utils.ImageUtils;

public class BagelImageView extends AppCompatImageView {

    private Paint imagePaint;
    private Paint imageBlurredPaint;

    private float blurredAlpha;
    private Bagel bagel;
    private Bitmap image, blurredImage;

    private Handler handler;
    private Runnable runnable;

    public BagelImageView(Context context) {
        this(context, null, 0);
    }

    public BagelImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BagelImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        imagePaint = new Paint();
        imagePaint.setAntiAlias(true);
        imagePaint.setFilterBitmap(true);
        imagePaint.setDither(true);
        imagePaint.setAlpha(0);

        imageBlurredPaint = new Paint();
        imageBlurredPaint.setAntiAlias(true);
        imageBlurredPaint.setFilterBitmap(true);
        imageBlurredPaint.setDither(true);
        imageBlurredPaint.setAlpha(0);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                setBlurred(true);
            }
        };
    }

    public void setBagel(Bagel bagel) {
        this.bagel = bagel;

        Glide.with(getContext()).load(BagelImageView.this.bagel.location).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                image = ImageUtils.drawableToBitmap(resource);
                postInvalidate();

                new Thread() {
                    @Override
                    public void run() {
                        blurredImage = ImageUtils.blurBitmap(getContext(), image);
                        setBlurred(false);
                    }
                }.start();
            }
        });
    }

    public void setBlurred(boolean isBlurred) {
        handler.removeCallbacks(runnable);
        blurredAlpha = isBlurred ? 255 : 0;
        if (!isBlurred)
            handler.postDelayed(runnable, 5000);

        postInvalidate();
    }

    public boolean isBlurred() {
        return blurredAlpha > 0;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int currentBlurredAlpha = imageBlurredPaint.getAlpha();
        if (blurredAlpha != currentBlurredAlpha && blurredImage != null) {
            float newAlpha = ((float) currentBlurredAlpha * 5 + blurredAlpha) / 6;
            currentBlurredAlpha = blurredAlpha > currentBlurredAlpha ? Math.round(newAlpha + .5f) : (int) (newAlpha - .5f);
            imageBlurredPaint.setAlpha(currentBlurredAlpha);
        }

        if (image != null) {
            int currentAlpha = imagePaint.getAlpha();
            if (currentAlpha < 255)
                imagePaint.setAlpha(Math.round((((float) currentAlpha * 5 + 255) / 6) + .5f));

            float scale = (float) canvas.getWidth() / image.getWidth();
            float xOffset = 0, yOffset = 0;
            if (image.getHeight() * scale < canvas.getHeight()) {
                scale = (float) canvas.getHeight() / image.getHeight();
                xOffset = (canvas.getWidth() / 2) - (image.getWidth() * scale / 2);
            } else yOffset = (canvas.getHeight() / 2) - (image.getHeight() * scale / 2);

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            matrix.postTranslate(xOffset, yOffset);
            canvas.drawBitmap(image, matrix, imagePaint);

            if (currentBlurredAlpha > 0 && blurredImage != null)
                canvas.drawBitmap(blurredImage, matrix, imageBlurredPaint);

            if (currentAlpha < 255 || currentBlurredAlpha != blurredAlpha)
                postInvalidate();
        }
    }
}
