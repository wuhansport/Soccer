package com.whs.soccer.concurrent;

import java.util.Map.Entry;
import java.util.Set;

import android.app.ActivityManager;
import android.content.Context;
import android.util.LruCache;

/**
 * Class ReferenceDrawablePool
 * @author antoniochen
 */
public final class ReferenceDrawablePool<Key> {
    private final ReferenceDrawableCache<Key> mDrawableCache;

    /**
     * Constructor
     * @param maxSize The maximum number of bytes
     * of the {@link ReferenceDrawable}s in this pool.
     * @see #ReferenceDrawablePool(Context, float)
     */
    public ReferenceDrawablePool(int maxSize) {
        mDrawableCache = new ReferenceDrawableCache<Key>(maxSize);
    }

    /**
     * Constructor
     * @param context The <tt>Context</tt>.
     * @param scaleMemory The scale memory, expressed as a percentage
     * of this application memory of the current device.
     * @see #ReferenceDrawablePool(int)
     */
    public ReferenceDrawablePool(Context context, float scaleMemory) {
        final ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        mDrawableCache = new ReferenceDrawableCache<Key>((int)(am.getMemoryClass() * 1024 * 1024 * scaleMemory));
    }

    /**
     * Returns the sum of the sizes of the
     * {@link ReferenceDrawable}s in this pool.
     * @return The sum of the sizes, in bytes.
     * @see #maxSize()
     */
    public int size() {
        return mDrawableCache.size();
    }

    /**
     * Returns the maximum sum of the sizes of the
     * {@link ReferenceDrawable}s in this pool.
     * @return The maximum sum of the sizes, in bytes.
     * @see #size()
     */
    public int maxSize() {
        return mDrawableCache.maxSize();
    }

    /**
     * Returns the {@link ReferenceDrawable} for <em>key</em>.
     * @param key The key to find.
     * @return The {@link ReferenceDrawable}, or <tt>null</tt>
     * if the key was not found.
     * @see #put(Key, ReferenceDrawable)
     */
    public ReferenceDrawable get(Key key) {
        ReferenceDrawable drawable = mDrawableCache.get(key);
        if (drawable != null && !drawable.isBitmapValid()) {
            remove(key);
            drawable = null;
        }

        return drawable;
    }

    /**
     * Maps the specified <em>key</em> to the specified <em>drawable</em>.
     * @param key The key to put.
     * @param drawable The {@link ReferenceDrawable} to put.
     * @see #get(Key)
     */
    public void put(Key key, ReferenceDrawable drawable) {
        drawable.addRef();
        mDrawableCache.put(key, drawable);
    }

    /**
     * Removes all {@link ReferenceDrawable}s in this pool.
     * @see #trim()
     * @see #remove(Key)
     */
    public void clear() {
        mDrawableCache.evictAll();
    }

    /**
     * Removes all reference count is <tt>1</tt> (only this pool
     * contains) or invalid {@link ReferenceDrawable}s in this pool.
     * @see #clear()
     * @see #remove(Key)
     */
    public void trim() {
        final Set<Entry<Key, ReferenceDrawable>> entries = mDrawableCache.snapshot().entrySet();
        for (Entry<Key, ReferenceDrawable> entry : entries) {
            final ReferenceDrawable drawable = entry.getValue();
            if (drawable.referenceCount() == 1 || !drawable.isBitmapValid()) {
                remove(entry.getKey());
            }
        }
    }

    /**
     * Removes the {@link ReferenceDrawable} for <em>key</em>.
     * @param key The key to find.
     * @see #trim()
     * @see #clear()
     */
    public void remove(Key key) {
        mDrawableCache.remove(key);
    }

    /**
     * Nested class ReferenceDrawableCache
     */
    private static final class ReferenceDrawableCache<Key> extends LruCache<Key, ReferenceDrawable> {
        public ReferenceDrawableCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(Key key, ReferenceDrawable value) {
            return value.getByteCount();
        }

        @Override
        protected void entryRemoved(boolean evicted, Key key, ReferenceDrawable oldValue, ReferenceDrawable newValue) {
            if (oldValue != newValue) {
                oldValue.release();
            }
        }
    }
}
