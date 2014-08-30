package com.whs.soccer.utils;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.Reference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class ArrayUtils
 * @author antoniochen
 */
public final class ArrayUtils {
    /**
     * Returns a new <tt>List</tt> containing the elements of the
     * specified <em>array</em>.
     * @param array The elements to add.
     * @return A <tt>List</tt> view of the specified <em>array</em>.
     */
    public static <T> List<T> newList(T... array) {
        final List<T> list = new ArrayList<T>(array.length);
        Collections.addAll(list, array);
        return list;
    }

    /**
     * Returns <tt>true</tt> if the <em>array</em> is
     * <tt>null</tt> or <tt>0-length</tt>.
     * @param array The array to be examined.
     * @return <tt>true</tt> if array is <tt>null</tt>
     * or <tt>0-length</tt>, <tt>false</tt> otherwise.
     */
    public static boolean isEmpty(byte[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * Returns <tt>true</tt> if the <em>array</em> is
     * <tt>null</tt> or <tt>0-length</tt>.
     * @param array The array to be examined.
     * @return <tt>true</tt> if array is <tt>null</tt>
     * or <tt>0-length</tt>, <tt>false</tt> otherwise.
     */
    public static boolean isEmpty(char[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * Returns <tt>true</tt> if the <em>array</em> is
     * <tt>null</tt> or <tt>0-length</tt>.
     * @param array The array to be examined.
     * @return <tt>true</tt> if array is <tt>null</tt>
     * or <tt>0-length</tt>, <tt>false</tt> otherwise.
     */
    public static boolean isEmpty(short[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * Returns <tt>true</tt> if the <em>array</em> is
     * <tt>null</tt> or <tt>0-length</tt>.
     * @param array The array to be examined.
     * @return <tt>true</tt> if array is <tt>null</tt>
     * or <tt>0-length</tt>, <tt>false</tt> otherwise.
     */
    public static boolean isEmpty(int[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * Returns <tt>true</tt> if the <em>array</em> is
     * <tt>null</tt> or <tt>0-length</tt>.
     * @param array The array to be examined.
     * @return <tt>true</tt> if array is <tt>null</tt>
     * or <tt>0-length</tt>, <tt>false</tt> otherwise.
     */
    public static boolean isEmpty(float[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * Returns <tt>true</tt> if the <em>array</em> is
     * <tt>null</tt> or <tt>0-length</tt>.
     * @param array The array to be examined.
     * @return <tt>true</tt> if array is <tt>null</tt>
     * or <tt>0-length</tt>, <tt>false</tt> otherwise.
     */
    public static boolean isEmpty(long[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * Returns <tt>true</tt> if the <em>array</em> is
     * <tt>null</tt> or <tt>0-length</tt>.
     * @param array The array to be examined.
     * @return <tt>true</tt> if array is <tt>null</tt>
     * or <tt>0-length</tt>, <tt>false</tt> otherwise.
     */
    public static boolean isEmpty(double[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * Returns <tt>true</tt> if the <em>array</em> is
     * <tt>null</tt> or <tt>0-length</tt>.
     * @param array The array to be examined.
     * @return <tt>true</tt> if array is <tt>null</tt>
     * or <tt>0-length</tt>, <tt>false</tt> otherwise.
     */
    public static boolean isEmpty(boolean[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * Returns <tt>true</tt> if the <em>array</em> is
     * <tt>null</tt> or <tt>0-length</tt>.
     * @param array The array to be examined.
     * @return <tt>true</tt> if array is <tt>null</tt>
     * or <tt>0-length</tt>, <tt>false</tt> otherwise.
     */
    public static <T> boolean isEmpty(T[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * Returns <tt>true</tt> if the collection is <tt>null</tt>
     * or <tt>0-size</tt>.
     * @param collection The collection to be examined.
     * @return <tt>true</tt> if collection is <tt>null</tt> or
     * <tt>0-size</tt>, <tt>false</tt> otherwise.
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * Converts the <tt>int</tt> array to <tt>Integer ArrayList</tt>.
     * @param data The <tt>int</tt> array to convert.
     * @return The <tt>Integer ArrayList</tt>.
     */
    public static ArrayList<Integer> toArrayList(int... data) {
        final ArrayList<Integer> list = new ArrayList<Integer>(data.length);
        for (int i = 0; i < data.length; ++i) {
            list.add(data[i]);
        }

        return list;
    }

    /**
     * Checks that the range described by <tt>offset</tt> and <tt>length</tt>
     * doesn't exceed <tt>arrayLength</tt>.
     * @param offset The start position to check.
     * @param length The desired length to check.
     * @param arrayLength The array length to check.
     * @throws IndexOutOfBoundsException if {@code offset < 0} or {@code length < 0},
     * or if {@code offset + length} is bigger than the {@code arrayLength}.
     */
    public static void checkRange(int offset, int length, int arrayLength) {
        if ((offset | length) < 0 || arrayLength - offset < length) {
            throw new IndexOutOfBoundsException("Index out of bounds - [ offset = " + offset + ", length = " + length + ", array length = " + arrayLength + " ]");
        }
    }

    /**
     * Returns a hash code based on the "class" of the given object. If
     * the object is an array or a {@link Reference}, the hash code is
     * based on their contents not their identities.
     * @param object The object whose hash code to compute.
     * @return The hash code for the <tt>object</tt>.
     * @see #hashCode(Object[])
     */
    public static int hashCode(Object object) {
        if (object instanceof Reference<?>) {
            object = ((Reference<?>)object).get();
        }

        if (object == null) {
            return 0;
        }

        final Class<?> clazz = object.getClass().getComponentType();
        if (clazz == null) {
            // The object is not array.
            return object.hashCode();
        }

        if (!clazz.isPrimitive()) {
            // The object is Object array.
            return hashCode((Object[])object);
        }

        // The object is char array.
        if (clazz.equals(char.class)) {
            return Arrays.hashCode((char[])object);
        }

        // The object is byte array.
        if (clazz.equals(byte.class)) {
            return Arrays.hashCode((byte[])object);
        }

        // The object is short array.
        if (clazz.equals(short.class)) {
            return Arrays.hashCode((short[])object);
        }

        // The object is int array.
        if (clazz.equals(int.class)) {
            return Arrays.hashCode((int[])object);
        }

        // The object is float array.
        if (clazz.equals(float.class)) {
            return Arrays.hashCode((float[])object);
        }

        // The object is long array.
        if (clazz.equals(long.class)) {
            return Arrays.hashCode((long[])object);
        }

        // The object is double array.
        if (clazz.equals(double.class)) {
            return Arrays.hashCode((double[])object);
        }

        // The object is boolean array.
        return Arrays.hashCode((boolean[])object);
    }

    /**
     * Returns a hash code based on the "deep contents" of the given
     * array. If the array contains other arrays or {@link Reference}
     * as its elements, the hash code is based on their contents not
     * their identities.
     * @param array The array whose hash code to compute.
     * @return The hash code for the <tt>array</tt>.
     * @see #hashCode(Object)
     */
    public static int hashCode(Object[] array) {
        if (array == null) {
            return 0;
        }

        int hashCode = 1;
        for (int i = 0; i < array.length; ++i) {
            hashCode = 31 * hashCode + hashCode(array[i]);
        }

        return hashCode;
    }

    /**
     * Class ByteArrayBuffer
     */
    public static final class ByteArrayBuffer extends OutputStream implements Parcelable {
        private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
        private byte[] array = EMPTY_BYTE_ARRAY;
        private int size;

        /**
         * Constructor
         * @see #ByteArrayBuffer(int)
         * @see #ByteArrayBuffer(ByteArrayBuffer)
         */
        public ByteArrayBuffer() {
        }

        /**
         * Constructor
         * @param capacity The initial capacity of this buffer.
         * @see #ByteArrayBuffer()
         * @see #ByteArrayBuffer(ByteArrayBuffer)
         */
        public ByteArrayBuffer(int capacity) {
            if (capacity > 0) {
                array = new byte[capacity];
            }
        }

        /**
         * Creates a buffer of contents copied from the given buffer <em>from</em>.
         * @param from The contents to copy.
         * @see #ByteArrayBuffer()
         * @see #ByteArrayBuffer(int)
         */
        public ByteArrayBuffer(ByteArrayBuffer from) {
            if (from.size > 0) {
                array = new byte[size = from.size];
                System.arraycopy(from.array, 0, array, 0, size);
            }
        }

        /**
         * Returns the size of this buffer.
         * @return The size in bytes.
         * @see #array()
         */
        public int size() {
            return size;
        }

        /**
         * Returns the underlying byte array associated with this buffer.
         * @return The byte array.
         * @see #size()
         * @see #toByteArray()
         */
        public byte[] array() {
            return array;
        }

        /**
         * Removes all contents from this buffer, leaving it empty.
         * @see #trim()
         * @see #reset()
         */
        public void clear() {
            size  = 0;
            array = EMPTY_BYTE_ARRAY;
        }

        /**
         * Resets this buffer the underlying byte array size to 0.
         * @see #trim()
         * @see #clear()
         */
        public void reset() {
            size = 0;
        }

        /**
         * Copies this buffer removing any empty contents from this buffer,
         * leaving this buffer size equals the underlying byte array size.
         * @see #clear()
         * @see #reset()
         */
        public void trim() {
            if (size == 0) {
                array = EMPTY_BYTE_ARRAY;
            } else if (array.length > size) {
                final byte[] newArray = new byte[size];
                System.arraycopy(array, 0, newArray, 0, size);
                array = newArray;
            }
        }

        /**
         * Returns the contents of this buffer as a byte array.
         * @return A byte array.
         * @see #array()
         */
        public byte[] toByteArray() {
            byte[] data = EMPTY_BYTE_ARRAY;
            if (size > 0) {
                data = new byte[size];
                System.arraycopy(array, 0, data, 0, size);
            }

            return data;
        }

        /**
         * Returns the contents of this buffer as a string.
         * @return This buffer contents as a string.
         * @see #toString(String)
         */
        @Override
        public String toString() {
            return new String(array, 0, size);
        }

        /**
         * Returns the contents of this buffer as a string converted
         * according to the encoding declared in <em>charsetName</em>.
         * @param charsetName The charset name of this buffer contents.
         * @return This buffer contents as an encoded string.
         * @throws UnsupportedEncodingException if the charset is not supported.
         * @see #toString()
         */
        public String toString(String charsetName) throws UnsupportedEncodingException {
            return new String(array, 0, size, charsetName);
        }

        /**
         * Writes the <tt>ByteBuffer</tt> remaining contents to this buffer.
         * @param buffer The <tt>ByteBuffer</tt> to write.
         * @see #write(ByteArrayBuffer)
         */
        public void write(ByteBuffer buffer) {
            final int length = buffer.remaining();
            if (length <= 0) {
                return;
            }

            if (buffer.hasArray()) {
                write(buffer.array(), buffer.arrayOffset() + buffer.position(), length);
                buffer.position(buffer.limit());
            } else {
                final byte[] data = new byte[length];
                buffer.get(data, 0, length);
                write(data, 0, length);
            }
        }

        /**
         * Writes the contents of the specified <em>buffer</em> to this buffer.
         * @param buffer The buffer to write.
         * @see #write(ByteBuffer)
         */
        public void write(ByteArrayBuffer buffer) {
            write(buffer.array, 0, buffer.size);
        }

        @Override
        public void write(int oneByte) {
            expandCapacity(size + 1);
            array[size++] = (byte)oneByte;
        }

        @Override
        public void write(byte[] buffer, int offset, int count) {
            if (count > 0) {
                expandCapacity(size + count);
                System.arraycopy(buffer, offset, array, size, count);
                size += count;
            }
        }

        /**
         * Writes the contents of this buffer to the <tt>ByteBuffer</tt> <em>out</em>.
         * @param out The <tt>ByteBuffer</tt> write to.
         */
        public void writeTo(ByteBuffer out) {
            out.put(array, 0, size);
        }

        /**
         * Reads this buffer contents from the data stored in the specified parcel. To
         * write this buffer contents to a parcel, call {@link #writeToParcel(Parcel, int)}.
         * @param source The parcel to read the data.
         */
        public void readFromParcel(Parcel source) {
            size  = source.readInt();
            array = source.createByteArray();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(size);
            dest.writeByteArray(array, 0, size);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        private void expandCapacity(int minCapacity) {
            if (array.length < minCapacity) {
                final byte[] newArray = new byte[minCapacity << 1];
                System.arraycopy(array, 0, newArray, 0, size);
                array = newArray;
            }
        }

        public static final Creator<ByteArrayBuffer> CREATOR = new Creator<ByteArrayBuffer>() {
            @Override
            public ByteArrayBuffer createFromParcel(Parcel source) {
                final ByteArrayBuffer buffer = new ByteArrayBuffer();
                buffer.readFromParcel(source);
                return buffer;
            }

            @Override
            public ByteArrayBuffer[] newArray(int size) {
                return new ByteArrayBuffer[size];
            }
        };
    }

    /**
     * This utility class cannot be instantiated.
     */
    private ArrayUtils() {
    }
}
