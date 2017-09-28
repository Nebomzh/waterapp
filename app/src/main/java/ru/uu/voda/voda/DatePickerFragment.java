package ru.uu.voda.voda;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    // интерфейс активити для обмена инфой
    DatePickerListener datePickerListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //instantiate the DatePickerListener
        Activity activity = getActivity();
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DatePickerListener so we can send events to the host
            datePickerListener = (DatePickerListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DatePickerListener");
        }

        //тащим уже имеющиеся даты с активити
        int year = datePickerListener.getYear();
        int month = datePickerListener.getMonth();
        int day = datePickerListener.getDay();

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        datePickerListener.onDateSet(year, month, day); //передаём инфу о выбранной дате в активити
    }
}