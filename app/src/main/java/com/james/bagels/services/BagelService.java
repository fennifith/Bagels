package com.james.bagels.services;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.james.bagels.data.Bagel;
import com.james.bagels.utils.ImageUtils;

public class BagelService extends WallpaperService {

    public static final String WALLPAPER_KEY = "com.james.bagels.WALLPAPER_KEY";
    public static final String ACTION_UPDATE = "com.james.bagels.ACTION_UPDATE";

    private BagelEngine bagelEngine;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals(ACTION_UPDATE))
            bagelEngine.loadDrawables();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public Engine onCreateEngine() {
        bagelEngine = new BagelEngine();
        return bagelEngine;
    }

    public class BagelEngine extends Engine {

        private Handler handler;
        private Runnable runnable;

        private ValueAnimator animator;
        private boolean isBlurred;

        private Bagel bagel;
        private Bitmap bitmap, blurredBitmap;
        private Paint paint, blurredPaint;

        private Integer width, height;

        private boolean isVisible;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    setBlurred(true);
                }
            };

            paint = new Paint();
            paint.setAntiAlias(true);
            blurredPaint = new Paint();
            blurredPaint.setAntiAlias(true);
        }

        public void loadDrawables() {
            bitmap = null;
            blurredBitmap = null;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BagelService.this);
            if (prefs.contains(WALLPAPER_KEY)) {
                bagel = new Bagel(prefs.getString(WALLPAPER_KEY, null));

                Glide.with(BagelService.this).load(bagel.location).into(new SimpleTarget<Drawable>(width, height) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        bitmap = ImageUtils.drawableToBitmap(resource);
                        if (isVisible) draw(getSurfaceHolder(), null);

                        new Thread() {
                            @Override
                            public void run() {
                                blurredBitmap = ImageUtils.blurBitmap(BagelService.this, bitmap);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        handler.postDelayed(runnable, 5000);
                                    }
                                });
                            }
                        }.start();
                    }
                });
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.isVisible = visible;
            super.onVisibilityChanged(visible);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            isVisible = true;
            super.onSurfaceCreated(holder);

            draw(holder, null);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            isVisible = false;
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onSurfaceRedrawNeeded(SurfaceHolder holder) {
            super.onSurfaceRedrawNeeded(holder);
            draw(holder, null);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            this.width = width;
            this.height = height;
            loadDrawables();

            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            if (animator != null && animator.isRunning()) return;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                setBlurred(false);
            }
        }

        private void setBlurred(boolean isBlurred) {
            if (this.isBlurred == isBlurred || (animator != null && animator.isRunning())) return;
            else this.isBlurred = isBlurred;

            animator = ValueAnimator.ofInt(isBlurred ? 0 : 255, isBlurred ? 255 : 0);
            animator.setDuration(500);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    draw(getSurfaceHolder(), (int) animation.getAnimatedValue());
                }
            });
            animator.start();

            handler.removeCallbacks(runnable);
            if (!isBlurred) handler.postDelayed(runnable, 5000);
        }

        private void draw(SurfaceHolder holder, @Nullable Integer blurredAlpha) {
            if (bitmap == null) return;

            Canvas canvas;
            try {
                canvas = holder.lockCanvas();
                if (canvas == null) return;
            } catch (Exception e) {
                return;
            }

            canvas.drawBitmap(bitmap, 0, 0, paint);

            if (blurredAlpha != null && blurredAlpha > 0 && blurredBitmap != null) {
                blurredPaint.setAlpha(blurredAlpha);
                canvas.drawBitmap(blurredBitmap, 0, 0, blurredPaint);
            }

            holder.unlockCanvasAndPost(canvas);
        }
    }
}
