package com.whs.soccer.utils;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class Pools
 * @author antoniochen
 */
public final class Pools {
    /**
     * Creates a new <b>one</b> size pool.
     * @param factory The {@link Factory} to create
     * a new element when the pool is empty.
     * @return A newly {@link Pool}.
     * @see #newPool(Factory)
     * @see #newPool(Factory, int)
     * @see #newWeakPool(Factory)
     * @see #newWeakPool(Factory, int)
     */
    public static <T> Pool<T> newSimplePool(Factory<T> factory) {
        return new SimplePool<T>(factory);
    }

    /**
     * Creates a new unlimited-size pool.
     * @param factory The {@link Factory} to create
     * a new element when the pool is empty.
     * @return A newly {@link Pool}.
     * @see #newPool(Factory, int)
     * @see #newSimplePool(Factory)
     * @see #newWeakPool(Factory)
     * @see #newWeakPool(Factory, int)
     */
    public static <T> Pool<T> newPool(Factory<T> factory) {
        return new RecyclablePool<T>(factory, Integer.MAX_VALUE);
    }

    /**
     * Creates a new fixed-size pool.
     * @param factory The {@link Factory} to create
     * a new element when the pool is empty.
     * @param maxSize The max size of the pool.
     * @return A newly {@link Pool}.
     * @see #newPool(Factory)
     * @see #newSimplePool(Factory)
     * @see #newWeakPool(Factory)
     * @see #newWeakPool(Factory, int)
     */
    public static <T> Pool<T> newPool(Factory<T> factory, int maxSize) {
        return new RecyclablePool<T>(factory, maxSize);
    }

    /**
     * Creates a new unlimited-size pool which contains {@link WeakReference} elements.
     * @param factory The {@link Factory} to create a new element when this pool is empty.
     * @return A newly {@link Pool}.
     * @see #newPool(Factory)
     * @see #newPool(Factory, int)
     * @see #newSimplePool(Factory)
     * @see #newWeakPool(Factory, int)
     */
    public static <T> Pool<T> newWeakPool(Factory<T> factory) {
        return new WeakPool<T>(factory, Integer.MAX_VALUE);
    }

    /**
     * Creates a new fixed-size pool which contains {@link WeakReference} elements.
     * @param factory The {@link Factory} to create a new element when this pool is empty.
     * @param maxSize The max size of the pool.
     * @return A newly {@link Pool}.
     * @see #newPool(Factory)
     * @see #newPool(Factory, int)
     * @see #newWeakPool(Factory)
     * @see #newSimplePool(Factory)
     */
    public static <T> Pool<T> newWeakPool(Factory<T> factory, int maxSize) {
        return new WeakPool<T>(factory, maxSize);
    }

    /**
     * Interface Recyclable
     * @see Recyclable#onRecycled()
     */
    public static interface Recyclable<T> {
        /**
         * Callback method to be invoked when this element has been recycled.
         * Subclasses should override this method to clear data.
         */
        void onRecycled();
    }

    /**
     * Interface Factory
     * @see Factory#newInstance()
     */
    public static interface Factory<T> {
        /**
         * Creates a new element.
         * @return A new element.
         */
        T newInstance();
    }

    /**
     * Interface Pool
     * @see Pool#obtain()
     * @see Pool#recycle(T)
     */
    public static interface Pool<T> {
        /**
         * Retrieves a new element from this pool. Allows us to avoid allocating new
         * elements in many cases. When this <tt>Pool</tt> can no longer be used, The
         * caller should be call {@link #recycle(T)} to recycles the element. When this
         * <tt>Pool</tt> is empty, should be call {@link Factory#newInstance()} to create
         * a new element.
         * @return A newly element.
         * @see #recycle(T)
         */
        T obtain();

        /**
         * Recycles the specified <em>element</em> to the pool.
         * @param element The element to recycle.
         * @see #obtain()
         */
        void recycle(T element);
    }

    /**
     * Nested class SimplePool
     */
    private static final class SimplePool<T> implements Pool<T> {
        private final Factory<T> factory;
        private final AtomicReference<T> referent;

        /**
         * Constructor
         * <P>Creates a new <b>one</b> size pool.</P>
         * @param factory The {@link Factory} to create
         * a new element when this pool is empty.
         */
        public SimplePool(Factory<T> factory) {
            this.factory  = factory;
            this.referent = new AtomicReference<T>();
        }

        @Override
        public T obtain() {
            final T element = referent.getAndSet(null);
            return (element != null ? element : factory.newInstance());
        }

        @Override
        public void recycle(T element) {
            referent.compareAndSet(null, element);
        }
    }

    /**
     * Nested class RecyclablePool
     */
    private static final class RecyclablePool<T> implements Pool<T> {
        private final Factory<T> factory;
        private final int maxSize;
        private final LinkedList<T> elements;

        /**
         * Constructor
         * <P>Creates a new pool.</P>
         * @param factory The {@link Factory} to create
         * a new element when this pool is empty.
         * @param maxSize The max size of this pool.
         */
        public RecyclablePool(Factory<T> factory, int maxSize) {
            if (maxSize <= 0) {
                throw new IllegalArgumentException("The pool max size must be > 0");
            }

            this.factory  = factory;
            this.maxSize  = maxSize;
            this.elements = new LinkedList<T>();
        }

        @Override
        public T obtain() {
            T element = null;
            synchronized (elements) {
                element = elements.pollFirst();
            }

            return (element != null ? element : factory.newInstance());
        }

        @Override
        public void recycle(T element) {
            synchronized (elements) {
                if (elements.size() < maxSize) {
                    elements.addFirst(element);
                }
            }

            onRecycled(element);
        }
    }

    /**
     * Nested class WeakPool
     */
    private static final class WeakPool<T> implements Pool<T> {
        private final Factory<T> factory;
        private final int maxSize;
        private final LinkedList<WeakReference<T>> elements;

        /**
         * Constructor
         * <P>Creates a new pool contains {@link WeakReference} elements.</P>
         * @param factory The {@link Factory} to create a new element when
         * this pool is empty.
         * @param maxSize The max size of this pool.
         */
        public WeakPool(Factory<T> factory, int maxSize) {
            if (maxSize <= 0) {
                throw new IllegalArgumentException("The pool max size must be > 0");
            }

            this.factory  = factory;
            this.maxSize  = maxSize;
            this.elements = new LinkedList<WeakReference<T>>();
        }

        @Override
        public T obtain() {
            WeakReference<T> elementRef = null;
            synchronized (elements) {
                elementRef = elements.pollFirst();
            }

            T element = null;
            if (elementRef != null) {
                element = elementRef.get();
            }

            return (element != null ? element : factory.newInstance());
        }

        @Override
        public void recycle(T element) {
            synchronized (elements) {
                if (elements.size() < maxSize) {
                    elements.addFirst(new WeakReference<T>(element));
                }
            }

            onRecycled(element);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void onRecycled(T element) {
        if (element instanceof Recyclable) {
            ((Recyclable<T>)element).onRecycled();
        }
    }

    /**
     * This class cannot be instantiated.
     */
    private Pools() {
    }
}