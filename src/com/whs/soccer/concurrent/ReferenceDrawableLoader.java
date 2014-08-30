package com.whs.soccer.concurrent;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Abstract class ReferenceDrawableLoader
 * @author antoniochen
 */
public abstract class ReferenceDrawableLoader<Key, Params> {
    private static final int MAX_THREADS   = 6;
    private static final int FLAG_PAUSED   = 0x01;
    private static final int FLAG_SHUTDOWN = 0x02;

    private volatile int mFlags;
    private final Context mContext;

    private final Executor mExecutor;
    private static Executor executor;

    private final Drawable mDefaultDrawable;
    private final ReferenceDrawablePool<Key> mDrawablePool;

    /**
     * Constructor
     * @param context The <tt>Context</tt>.
     * @param pool The {@link ReferenceablePool} to store the loaded image.
     * @param executor May be <tt>null</tt>. The <tt>Executor</tt> to executing load task.
     * @param defaultResId The image resource ID to be used when there is no image.
     * @see #ReferenceDrawableLoader(Context, ReferenceDrawablePool, Executor, Drawable)
     */
    public ReferenceDrawableLoader(Context context, ReferenceDrawablePool<Key> pool, Executor executor, int defaultResId) {
        this(context, pool, executor, context.getResources().getDrawable(defaultResId));
    }

    /**
     * Constructor
     * @param context The <tt>Context</tt>.
     * @param pool The {@link ReferenceablePool} to store the loaded image.
     * @param executor May be <tt>null</tt>. The <tt>Executor</tt> to executing load task.
     * @param defaultDrawable The default <tt>Drawable</tt> to be used when there is no image.
     * @see #ReferenceDrawableLoader(Context, ReferenceDrawablePool, Executor, int)
     */
    public ReferenceDrawableLoader(Context context, ReferenceDrawablePool<Key> pool, Executor executor, Drawable defaultDrawable) {
        mContext  = context.getApplicationContext();
        mExecutor = (executor != null ? executor : getDefaultExecutor());
        mDrawablePool = pool;
        mDefaultDrawable = defaultDrawable;
    }

    /**
     * Loads image into the supplied image view. If the image is already cached,
     * it is displayed immediately. Otherwise loads the image in background thread.
     * @param key The key to find image.
     * @param view The <tt>ImageView</tt> to set.
     * @param params The parameters of the load task. If the task method no arguments,
     * you can pass <em>(Params[])null</em> instead of allocating an empty array.
     * @see #load(Object, ImageView, OnLoadListener, Object[])
     */
    public void load(Key key, ImageView view, Params... params) {
        load(key, view, null, params);
    }

    /**
     * Loads image into the supplied image view. If the image is already cached,
     * it is displayed immediately. Otherwise loads the image in background thread.
     * @param key The key to find image.
     * @param view The <tt>ImageView</tt> to set.
     * @param listener May be <tt>null</tt>. The {@link OnLoadListener} used for being
     * notified when the image was load finished.
     * @param params The parameters of the load task. If the task method no arguments,
     * you can pass <em>(Params[])null</em> instead of allocating an empty array.
     * @see #load(Object, ImageView, Object[])
     */
    public void load(Key key, ImageView view, OnLoadListener<Key> listener, Params... params) {
        if (!isShutdown()) {
            final Drawable drawable = mDrawablePool.get(key);
            if (drawable != null) {
                view.setImageDrawable(drawable);
            } else if (!isTaskRunning(key, view)) {
                final LoadTask task = new LoadTask(key, view, listener);
                view.setImageDrawable(new LoadDrawable<LoadTask>(mDefaultDrawable, task));
                task.executeOnExecutor(mExecutor, params);
            }
        }
    }

    /**
     * Attempts to stop all actively loading tasks. To ensure that the
     * task is stopped as quickly as possible, you should always check
     * the return value of {@link #isTaskCancelled(AsyncTask)} periodically
     * from {@link #onLoadImage(AsyncTask, Key, Params[])}, if possible
     * (inside a loop for instance.)
     * @see #isTaskCancelled(AsyncTask)
     */
    public void shutdown() {
        mFlags |= FLAG_SHUTDOWN;
    }

    /**
     * Temporarily stops loading tasks.
     * @see #resume()
     * @see #isPaused()
     */
    public void pause() {
        mFlags |= FLAG_PAUSED;
    }

    /**
     * Resumes loading tasks.
     * @see #pause()
     * @see #isPaused()
     */
    public void resume() {
        mFlags &= ~FLAG_PAUSED;
    }

    /**
     * Returns the loading task was paused.
     * @return <tt>true</tt> if the loading
     * task was paused, <tt>false</tt> otherwise.
     * @see #pause()
     * @see #resume()
     */
    public boolean isPaused() {
        return ((mFlags & FLAG_PAUSED) == FLAG_PAUSED);
    }

    /**
     * Returns <tt>true</tt> if the <em>task</em> was cancelled before it completed normally.
     * If you are calling {@link #shutdown()} on the task, the value returned by this method
     * should be checked periodically from {@link #onLoadImage(AsyncTask, Object, Object[])}
     * to end the task as soon as possible.
     * @param task The <tt>AsyncTask</tt> to test.
     * @return <tt>true</tt> if the <em>task</em> was cancelled before it completed,
     * <tt>false</tt> otherwise.
     * @see #shutdown()
     */
    public boolean isTaskCancelled(AsyncTask<?, ?, ?> task) {
        return (isShutdown() || task.isCancelled());
    }

    /**
     * Returns the <tt>Context</tt> associated with this object.
     * @return The application <tt>Context</tt>.
     */
    public final Context getContext() {
        return mContext;
    }

    /**
     * Returns the {@link Executor} associated with this object.
     * @return The {@link Executor}.
     */
    public final Executor getExecutor() {
        return mExecutor;
    }

    /**
     * Returns the {@link ReferenceDrawablePool} associated with this object.
     * @return The {@link ReferenceDrawablePool}.
     */
    public final ReferenceDrawablePool<Key> getDrawablePool() {
        return mDrawablePool;
    }

    /**
     * Returns the default {@link Executor} associated with this class.
     * @return The {@link Executor}.
     */
    public static Executor getDefaultExecutor() {
        if (executor == null) {
            synchronized (ReferenceDrawableLoader.class) {
                // Check again, this time in synchronized.
                if (executor == null) {
                    executor = new ThreadPool(MAX_THREADS, EventBus.getHandler());
                }
            }
        }

        return executor;
    }

    /**
     * Loading image on a background thread.
     * @param task The <tt>AsyncTask</tt> that executing load task.
     * @param key The key, passed earlier by {@link #load(Key, ImageView, Params[])}.
     * @param params The parameter, passed earlier by {@link #load(Key, ImageView, Params[])}.
     * @return The loaded image, or <tt>null</tt> if the image could't be loaded or the
     * <em>task</em> was cancelled.
     */
    protected abstract Bitmap onLoadImage(AsyncTask<?, ?, ?> task, Key key, Params[] params);

    private boolean isShutdown() {
        return ((mFlags & FLAG_SHUTDOWN) == FLAG_SHUTDOWN);
    }

    @SuppressWarnings("unchecked")
    private LoadTask getLoadTask(ImageView view) {
        final Drawable drawable = view.getDrawable();
        if (drawable instanceof LoadDrawable) {
            return ((LoadDrawable<LoadTask>)drawable).task.get();
        }

        return null;
    }

    private boolean isTaskRunning(Key key, ImageView view) {
        boolean isRunning = false;
        final LoadTask task = getLoadTask(view);
        if (task != null && !task.isCancelled()) {
            if (!(isRunning = task.key.equals(key))) {
                task.cancel(true);
            }
        }

        return isRunning;
    }

    /**
     * Nested class LoadTask
     */
    private final class LoadTask extends AsyncTask<Params, Object, Drawable> {
        private final Key key;
        private final WeakReference<ImageView> view;
        private final WeakReference<OnLoadListener<Key>> listener;

        public LoadTask(Key key, ImageView view, OnLoadListener<Key> listener) {
            this.key  = key;
            this.view = new WeakReference<ImageView>(view);
            this.listener = (listener != null ? new WeakReference<OnLoadListener<Key>>(listener) : null);
        }

        @Override
        protected Drawable doInBackground(Params... params) {
            ReferenceDrawable drawable = null;
            if (!isTaskCancelled(this)) {
                final Bitmap bitmap = onLoadImage(this, key, params);
                if (bitmap != null && !isShutdown()) {
                    mDrawablePool.put(key, drawable = new ReferenceDrawable(mContext.getResources(), bitmap));
                }
            }

            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            // Checks the ImageView is valid.
            final ImageView view = this.view.get();
            if (view != null && view.getParent() != null && !isShutdown()) {
                // Checks the drawable is valid and the task was not cancelled.
                if (drawable != null && !isPaused() && getLoadTask(view) == this) {
                    view.setImageDrawable(drawable);
                }

                if (this.listener != null) {
                    final OnLoadListener<Key> listener = this.listener.get();
                    if (listener != null) {
                        listener.onLoadFinished(key, view, drawable);
                    }
                }
            }
        }
    }

    /**
     * Nested class LoadDrawable
     */
    private static final class LoadDrawable<T> extends InsetDrawable {
        private final WeakReference<T> task;

        public LoadDrawable(Drawable drawable, T task) {
            super(drawable, 0);
            this.task = new WeakReference<T>(task);
        }
    }

    /**
     * Used for being notified when the image was load finished.
     * @see OnLoadListener#onLoadFinished(Key, ImageView, Drawable)
     */
    public static interface OnLoadListener<Key> {
        /**
         * Runs on the UI thread after the image was load finished.
         * @param key The key, passed earlier by {@link ReferenceDrawableLoader#load}.
         * @param view The <tt>ImageView</tt> to set.
         * @param drawable The loaded image.
         */
        void onLoadFinished(Key key, ImageView view, Drawable drawable);
    }
}
