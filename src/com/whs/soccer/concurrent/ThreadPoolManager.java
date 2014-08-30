package com.whs.soccer.concurrent;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Process;

/**
 * Class ThreadPoolManager
 * @author antoniochen
 */
public class ThreadPoolManager extends ThreadPool {
    private final Queue<Task> mRunningTasks;

    /**
     * Constructor
     * <P>Creates a new <tt>ThreadPoolManager</tt> with the given initial parameters.</P>
     * @param maxThreads The maximum number of threads to allow in the pool.
     * @param handler The handler to post pending task to this <tt>ThreadPoolManager</tt>.
     * @see #ThreadPoolManager(int, long, TimeUnit, Handler, int)
     */
    public ThreadPoolManager(int maxThreads, Handler handler) {
        super(maxThreads, 60, TimeUnit.SECONDS, handler, Process.THREAD_PRIORITY_DEFAULT);
        mRunningTasks = new ConcurrentLinkedQueue<Task>();
    }

    /**
     * Constructor
     * <P>Creates a new <tt>ThreadPoolManager</tt> with the given initial parameters.</P>
     * @param maxThreads The maximum number of threads to allow in the pool.
     * @param keepAliveTime The maximum time that excess idle threads will wait for new
     * tasks before terminating.
     * @param unit The time unit for the <em>keepAliveTime</em> parameter.
     * @param handler The handler to post pending task to this <tt>ThreadPoolManager</tt>.
     * @param priority The priority to run the thread at. The value supplied must be from
     * {@link android.os.Process} and <b>not</b> from {@link java.lang.Thread}.
     * @see #ThreadPoolManager(int, Handler)
     */
    public ThreadPoolManager(int maxThreads, long keepAliveTime, TimeUnit unit, Handler handler, int priority) {
        super(maxThreads, keepAliveTime, unit, handler, priority);
        mRunningTasks = new ConcurrentLinkedQueue<Task>();
    }

    /**
     * Removes the task with specified identifier from the internal pending queue
     * if it is present, thus causing it not to be run if it has not already started.
     * @param id The identifier of the task to remove.
     * @return <tt>true</tt> if the task was removed, <tt>false</tt> otherwise.
     * @see #removeAll()
     * @see #remove(Runnable)
     */
    public boolean remove(long id) {
        final Iterator<Runnable> itor = mPendingTasks.iterator();
        while (itor.hasNext()) {
            final Runnable runnable = itor.next();
            if ((runnable instanceof Task) && ((Task)runnable).getId() == id) {
                itor.remove();
                return true;
            }
        }

        return false;
    }

    /**
     * Attempts to cancel all pending and executing tasks.
     * @param mayCancelIfPending <tt>true</tt> cancel the task from the the pending
     * queue and running queue, <tt>false</tt> cancel the task from the running queue.
     * @see #cancel(long, boolean)
     */
    public void cancelAll(boolean mayCancelIfPending) {
        if (mayCancelIfPending) {
            for (Runnable runnable : mPendingTasks) {
                if (runnable instanceof Task) {
                    ((Task)runnable).cancel();
                }
            }
        }

        for (Task task : mRunningTasks) {
            task.cancel();
        }
    }

    /**
     * Attempts to cancel the pending or executing task with specified identifier.
     * @param id The identifier of the task to cancel.
     * @param mayCancelIfPending <tt>true</tt> cancel the task from the the pending
     * queue and running queue, <tt>false</tt> cancel the task from the running queue.
     * @return <tt>true</tt> if the task was cancelled, <tt>false</tt> otherwise.
     * @see #cancelAll(boolean)
     */
    public boolean cancel(long id, boolean mayCancelIfPending) {
        if (mayCancelIfPending) {
            for (Runnable runnable : mPendingTasks) {
                if ((runnable instanceof Task) && ((Task)runnable).getId() == id) {
                    ((Task)runnable).cancel();
                    return true;
                }
            }
        }

        for (Task task : mRunningTasks) {
            if (task.getId() == id) {
                task.cancel();
                return true;
            }
        }

        return false;
    }

    @Override
    protected void beforeExecute(Thread thread, Runnable target) {
        if (target instanceof Task) {
            mRunningTasks.offer((Task)target);
        }

        super.beforeExecute(thread, target);
    }

    @Override
    protected void afterExecute(Runnable target, Throwable exception) {
        if (target instanceof Task) {
            mRunningTasks.remove(target);
        }

        super.afterExecute(target, exception);
    }

    /**
     * This interface should be implemented by any class whose
     * instances are intended to be executed by {@link ThreadPoolManager}.
     * @see Task#getId()
     * @see Task#cancel()
     * @see Runnable#run()
     */
    public static interface Task extends Runnable {
        /**
         * Gets the identifier associated with this task.
         * @return The task's identifier.
         */
        long getId();

        /**
         * Attempts to cancel execution of this task.
         */
        void cancel();
    }
}