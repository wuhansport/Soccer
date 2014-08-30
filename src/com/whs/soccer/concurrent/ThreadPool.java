package com.whs.soccer.concurrent;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Handler;
import android.os.Process;

/**
 * Class ThreadPool
 * @author antoniochen
 */
public class ThreadPool extends ThreadPoolExecutor {
    protected final Queue<Runnable> mPendingTasks;
    private final Handler mHandler;
    private WeakReference<OnExecuteListener> mOnExecuteListener;

    /**
     * Constructor
     * <P>Creates a new <tt>ThreadPool</tt> with the given initial parameters.</P>
     * @param maxThreads The maximum number of threads to allow in the pool.
     * @param handler The handler to post pending task to this <tt>ThreadPool</tt>.
     * @see #ThreadPool(int, long, TimeUnit, Handler, int)
     */
    public ThreadPool(int maxThreads, Handler handler) {
        this(maxThreads, 60, TimeUnit.SECONDS, handler, Process.THREAD_PRIORITY_DEFAULT);
    }

    /**
     * Constructor
     * <P>Creates a new <tt>ThreadPool</tt> with the given initial parameters.</P>
     * @param maxThreads The maximum number of threads to allow in the pool.
     * @param keepAliveTime The maximum time that excess idle threads will wait for new
     * tasks before terminating.
     * @param unit The time unit for the <em>keepAliveTime</em> parameter.
     * @param handler The handler to post pending task to this <tt>ThreadPool</tt>.
     * @param priority The priority to run the thread at. The value supplied must be from
     * {@link android.os.Process} and <b>not</b> from {@link java.lang.Thread}.
     * @see #ThreadPool(int, Handler)
     */
    public ThreadPool(int maxThreads, long keepAliveTime, TimeUnit unit, Handler handler, int priority) {
        super(0, maxThreads, keepAliveTime, unit, new SynchronousQueue<Runnable>(), new DefaultThreadFactory(priority));
        setRejectedExecutionHandler(new RejectedHandler());
        mHandler = handler;
        mPendingTasks = new ConcurrentLinkedQueue<Runnable>();
    }

    /**
     * Removes all tasks from the internal pending queue.
     * @see #remove(Runnable)
     */
    public void removeAll() {
        mPendingTasks.clear();
    }

    /**
     * @see #removeAll()
     */
    @Override
    public boolean remove(Runnable task) {
        return mPendingTasks.remove(task);
    }

    /**
     * Returns the {@link Handler} associated with this object.
     * @return The {@link Handler}.
     */
    public final Handler getHandler() {
        return mHandler;
    }

    /**
     * Sets the {@link OnExecuteListener} used for being notified
     * when the task execution event.
     * @param listener The <tt>OnExecuteListener</tt> to set.
     */
    public final void setOnExecuteListener(OnExecuteListener listener) {
        mOnExecuteListener = (listener != null ? new WeakReference<OnExecuteListener>(listener) : null);
    }

    @Override
    protected void beforeExecute(Thread thread, Runnable target) {
        if (mOnExecuteListener != null) {
            final OnExecuteListener listener = mOnExecuteListener.get();
            if (listener != null) {
                listener.onBeforeExecute(target, thread);
            }
        }
    }

    @Override
    protected void afterExecute(Runnable target, Throwable exception) {
        if (mOnExecuteListener != null) {
            final OnExecuteListener listener = mOnExecuteListener.get();
            if (listener != null) {
                listener.onAfterExecute(target, exception);
            }
        }

        final Runnable runnable = mPendingTasks.poll();
        if (runnable != null) {
            mHandler.post(new PendingTask(runnable));
        }
    }

    /**
     * Nested class PendingTask
     */
    private final class PendingTask implements Runnable {
        private final Runnable task;

        public PendingTask(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            execute(task);
        }
    }

    /**
     * Nested class RejectedHandler
     */
    private final class RejectedHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
            mPendingTasks.offer(runnable);
        }
    }

    /**
     * Nested class DefaultThreadFactory
     */
    private static final class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger sequence = new AtomicInteger();
        private final int priority;
        private final String namePrefix;
        private final AtomicInteger nameSuffix;

        public DefaultThreadFactory(int priority) {
            this.priority   = priority;
            this.nameSuffix = new AtomicInteger();
            this.namePrefix = "ThreadPool-" + sequence.incrementAndGet() +"-thread-";
        }

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, namePrefix + nameSuffix.incrementAndGet()) {
                @Override
                public void run() {
                    Process.setThreadPriority(priority);
                    super.run();
                }
            };
        }
    }

    /**
     * Used for being notified when the task execution event.
     * @see OnExecuteListener#onBeforeExecute(Runnable, Thread)
     * @see OnExecuteListener#onAfterExecute(Runnable, Throwable)
     */
    public static interface OnExecuteListener {
        /**
         * Callback method to be invoked when the task will be executed.
         * @param task The task that will be executed.
         * @param thread The thread that will run the <em>task</em>.
         * @see #onAfterExecute(Runnable, Throwable)
         */
        void onBeforeExecute(Runnable task, Thread thread);

        /**
         * Callback method to be invoked when the task has been completed or cancelled.
         * @param task The task that has completed.
         * @param exception The exception that caused termination, or <tt>null</tt> if
         * execution completed normally.
         * @see #onBeforeExecute(Runnable, Thread)
         */
        void onAfterExecute(Runnable task, Throwable exception);
    }
}