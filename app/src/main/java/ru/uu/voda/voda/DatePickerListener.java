package ru.uu.voda.voda;

/**
 * Created by CAH ek on 28.09.2017.
 */

public interface DatePickerListener {
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks. */
    void onDateSet(int year, int month, int day);
    int getYear();
    int getMonth();
    int getDay();
}
