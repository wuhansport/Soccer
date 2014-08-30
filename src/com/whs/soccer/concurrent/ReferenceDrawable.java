package com.whs.soccer.concurrent;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

/**
 * Class ReferenceDrawable
 * @author antoniochen
 */
public class ReferenceDrawable extends BitmapDrawable {
    private final AtomicInteger mRefCount = new AtomicInteger();

    public ReferenceDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
    }

    public ReferenceDrawable(Resources res, String filepath) {
        super(res, filepath);
    }

    public ReferenceDrawable(Resources res, InputStream is) {
        super(res, is);
    }

    @Override
    public void draw(Canvas canvas) {
        if (isBitmapValid()) {
            super.draw(canvas);
        }
    }

    /**
     * Atomically increments by one the current reference count.
     * @see #release()
     * @see #referenceCount()
     */
    public void addRef() {
        mRefCount.incrementAndGet();
    }

    /**
     * Atomically decrements by one the current reference count.
     * If the reference count <= 0, the bitmap associated with
     * this object will be recycle.
     * @see #addRef()
     * @see #referenceCount()
     */
    public void release() {
        if (mRefCount.decrementAndGet() <= 0 && isBitmapValid()) {
            Log.d(getClass().getName(), "The ReferenceDrawable was released");
            getBitmap().recycle();
        }
    }

    /**
     * Returns the current reference count.
     * @return The current reference count.
     * @see #addRef()
     * @see #release()
     */
    public int referenceCount() {
        return mRefCount.get();
    }

    /**
     * Checks the bitmap associated with this object is valid.
     * @return <tt>true</tt> if the bitmap is valid, <tt>false</tt> otherwise.
     */
    public boolean isBitmapValid() {
        synchronized (this) {
            final Bitmap bitmap = getBitmap();
            return (bitmap != null && !bitmap.isRecycled());
        }
    }

    /**
     * Returns the number of bytes used to store the bitmap's
     * pixels associated with this object.
     * @return The number of bytes.
     */
    public int getByteCount() {
        return (isBitmapValid() ? getBitmap().getByteCount() : 0);
    }
}
