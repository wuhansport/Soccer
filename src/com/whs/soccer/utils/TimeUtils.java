package com.whs.soccer.utils;

import java.util.Calendar;

import com.whs.soccer.utils.Pools.Factory;
import com.whs.soccer.utils.Pools.Pool;

/**
 * Class TimeUtils
 * @author antoniochen
 */
public final class TimeUtils {
    /**
     * Retrieves a new <tt>Calendar</tt> instance from the underlying pool.
     * Allows us to avoid allocating new <tt>Calendar</tt> in many cases.
     * When the <tt>Calendar</tt> can no longer be used, The caller should
     * be call {@link #recycle(Calendar)} to recycles the <tt>Calendar</tt>.
     * @return A <tt>Calendar</tt> instance set to the current date and
     * time in the default <tt>TimeZone</tt>.
     * @see #getCalendar(long)
     * @see #recycle(Calendar)
     */
    public static Calendar getCalendar() {
        return getCalendar(System.currentTimeMillis());
    }

    /**
     * Same as {@link #getCalendar()}, but the returns <tt>Calendar</tt>
     * instance use <em>timeInMillis</em> parameter to initialization.
     * @param timeInMillis The initial time as the number of milliseconds.
     * @return A <tt>Calendar</tt> instance.
     * @see #getCalendar()
     * @see #recycle(Calendar)
     */
    public static Calendar getCalendar(long timeInMillis) {
        final Calendar calendar = POOL.obtain();
        calendar.setTimeInMillis(timeInMillis);
        return calendar;
    }

    /**
     * Recycles the specified <tt>Calendar</tt> <em>calendar</em> to
     * the underlying pool.
     * @param calendar The <tt>Calendar</tt> to recycle.
     * @see #getCalendar()
     * @see #getCalendar(long)
     */
    public static void recycle(Calendar calendar) {
        POOL.recycle(calendar);
    }

    /**
     * Converts the date based on current date and time.
     * @param yearDelta The {@link Calendar#YEAR} field delta.
     * @param monthDelta The {@link Calendar#MONTH} field delta.
     * @param dayDelta The {@link Calendar#DAY_OF_MONTH} field delta.
     * @return The converted date in milliseconds.
     * @see #convertTime(int, int, int)
     * @see #convertDateTime(int, int, int, int, int, int)
     */
    public static long convertDate(int yearDelta, int monthDelta, int dayDelta) {
        return convertDateTime(yearDelta, monthDelta, dayDelta, 0, 0, 0);
    }

    /**
     * Converts the time based on current date and time.
     * @param hourDelta The {@link Calendar#HOUR_OF_DAY} field delta.
     * @param minuteDelta The {@link Calendar#MINUTE} field delta.
     * @param secondDelta The {@link Calendar#SECOND} field delta.
     * @return The converted time in milliseconds.
     * @see #convertDate(int, int, int)
     * @see #convertDateTime(int, int, int, int, int, int)
     */
    public static long convertTime(int hourDelta, int minuteDelta, int secondDelta) {
        return convertDateTime(0, 0, 0, hourDelta, minuteDelta, secondDelta);
    }

    /**
     * Converts the date and time based on current date and time.
     * @param yearDelta The {@link Calendar#YEAR} field delta.
     * @param monthDelta The {@link Calendar#MONTH} field delta.
     * @param dayDelta The {@link Calendar#DAY_OF_MONTH} field delta.
     * @param hourDelta The {@link Calendar#HOUR_OF_DAY} field delta.
     * @param minuteDelta The {@link Calendar#MINUTE} field delta.
     * @param secondDelta The {@link Calendar#SECOND} field delta.
     * @return The converted date and time in milliseconds.
     * @see #convertDate(int, int, int)
     * @see #convertTime(int, int, int)
     */
    public static long convertDateTime(int yearDelta, int monthDelta, int dayDelta, int hourDelta, int minuteDelta, int secondDelta) {
        final Calendar calendar = getCalendar(System.currentTimeMillis());
        calendar.add(Calendar.YEAR, yearDelta);
        calendar.add(Calendar.MONTH, monthDelta);
        calendar.add(Calendar.DAY_OF_MONTH, dayDelta);
        calendar.add(Calendar.HOUR_OF_DAY, hourDelta);
        calendar.add(Calendar.MINUTE, minuteDelta);
        calendar.add(Calendar.SECOND, secondDelta);

        final long dateTime = calendar.getTimeInMillis();
        POOL.recycle(calendar);

        return dateTime;
    }

    /**
     * Converts the time and sets the date based on current date and time.
     * @param year The {@link Calendar#YEAR} field value.
     * @param month The {@link Calendar#MONTH} field value (0-11).
     * @param day The {@link Calendar#DAY_OF_MONTH} field value (1-based).
     * @param hourDelta The {@link Calendar#HOUR_OF_DAY} field delta.
     * @param minuteDelta The {@link Calendar#MINUTE} field delta.
     * @param secondDelta The {@link Calendar#SECOND} field delta.
     * @return The converted date and time in milliseconds.
     * @see #convertAndSetTime(int, int, int, int, int, int)
     */
    public static long convertAndSetDate(int year, int month, int day, int hourDelta, int minuteDelta, int secondDelta) {
        final Calendar calendar = getCalendar(System.currentTimeMillis());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.add(Calendar.HOUR_OF_DAY, hourDelta);
        calendar.add(Calendar.MINUTE, minuteDelta);
        calendar.add(Calendar.SECOND, secondDelta);

        final long dateTime = calendar.getTimeInMillis();
        POOL.recycle(calendar);

        return dateTime;
    }

    /**
     * Converts the date and sets the time based on current date and time.
     * @param yearDelta The {@link Calendar#YEAR} field delta.
     * @param monthDelta The {@link Calendar#MONTH} field delta.
     * @param dayDelta The {@link Calendar#DAY_OF_MONTH} field delta.
     * @param hour The {@link Calendar#HOUR_OF_DAY} field value (0-23).
     * @param minute The {@link Calendar#MINUTE} field value (0-59).
     * @param second The {@link Calendar#SECOND} field value (0-59).
     * @return The converted date and time in milliseconds.
     * @see #convertAndSetDate(int, int, int, int, int, int)
     */
    public static long convertAndSetTime(int yearDelta, int monthDelta, int dayDelta, int hour, int minute, int second) {
        final Calendar calendar = getCalendar(System.currentTimeMillis());
        calendar.add(Calendar.YEAR, yearDelta);
        calendar.add(Calendar.MONTH, monthDelta);
        calendar.add(Calendar.DAY_OF_MONTH, dayDelta);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        final long dateTime = calendar.getTimeInMillis();
        POOL.recycle(calendar);

        return dateTime;
    }

    private static final Pool<Calendar> POOL = Pools.newSimplePool(new Factory<Calendar>() {
        @Override
        public Calendar newInstance() {
            return Calendar.getInstance();
        }
    });

    /**
     * This utility class cannot be instantiated.
     */
    private TimeUtils() {
    }
}
