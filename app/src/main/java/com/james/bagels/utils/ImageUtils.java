package com.james.bagels.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;

public class ImageUtils {

    public static Drawable getVectorDrawable(Context context, int resId) {
        VectorDrawableCompat drawable;
        try {
            drawable = VectorDrawableCompat.create(context.getResources(), resId, context.getTheme());
        } catch (Exception e) {
            e.printStackTrace();
            return new ColorDrawable(Color.TRANSPARENT);
        }

        if (drawable != null) {
            Drawable icon = DrawableCompat.wrap(drawable.getCurrent());
            DrawableCompat.setTint(icon, Color.WHITE);
            return icon;
        } else {
            Log.wtf(context.getClass().getName(), "Can't get a vector drawable.");
            return new ColorDrawable(Color.TRANSPARENT);
        }
    }

    @Nullable
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) drawable = new ColorDrawable(Color.TRANSPARENT);
        if (drawable instanceof BitmapDrawable) return ((BitmapDrawable) drawable).getBitmap();

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError e) {
            return null;
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Nullable
    public static Bitmap blurBitmap(Context context, Bitmap bitmap) {
        if (context == null || bitmap == null) return null;

        Bitmap blurredBitmap;
        try {
            blurredBitmap = Bitmap.createBitmap(bitmap);
        } catch (OutOfMemoryError e) {
            return null;
        }

        RenderScript renderScript = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(renderScript, bitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(renderScript, input.getType());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));

        script.setInput(input);
        script.setRadius(20);

        script.forEach(output);
        output.copyTo(blurredBitmap);

        return blurredBitmap;
    }

    public static Drawable tintDrawable(Drawable source, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DrawableCompat.setTint(source, color);
        } else source.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        return source;
    }

    public static Bitmap tintBitmap(Bitmap source, @ColorInt int color) {
        Bitmap bitmap = Bitmap.createBitmap(source);

        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        new Canvas(bitmap).drawBitmap(source, new Matrix(), paint);

        return bitmap;
    }
}
