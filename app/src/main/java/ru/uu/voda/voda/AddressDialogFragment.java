package ru.uu.voda.voda;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.content.SharedPreferences;           //для работы с сохранялками

/**
 * Created by CAH ek on 15.09.2017.
 */

public class AddressDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    SharedPreferences sPref;    //объект сохранялок
    final String DISTRICT = "district"; //ключи сохранялок
    final String STREET = "street";
    final String HOUSE = "house";
    final String LEVEL = "level";

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //instantiate the NoticeDialogListener
        Activity activity = getActivity();
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }

        activity.getBaseContext();
        sPref = activity.getPreferences(Context.MODE_PRIVATE);   //получаем сохранялки

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_address, null, false); //мутим вид из xml
        //находим поля
        Spinner p_district = (Spinner) view.findViewById(R.id.Spinner1);
        EditText p_street = (EditText) view.findViewById(R.id.EditText2);
        EditText p_house = (EditText) view.findViewById(R.id.EditText3);
        EditText p_level = (EditText) view.findViewById(R.id.EditText4);

        //подгружаем значения из сохранялок
        p_district.setSelection(sPref.getInt(DISTRICT, 0));
        p_street.setText(sPref.getString(STREET, ""));
        p_house.setText(sPref.getString(HOUSE, ""));
        p_level.setText(sPref.getString(LEVEL, ""));

        return new AlertDialog.Builder(getActivity())   //творим диалог через билдер
                .setIcon(R.drawable.ic_place_black_18px)
                .setTitle(R.string.hpre1)
                .setView(view)
                .setPositiveButton(R.string.ok, this)
                //.setNegativeButton(R.string.no, this)
                .setNeutralButton(R.string.cancel, this)
                //.setMessage("message_text")
                .create();
    }

    public void onClick(DialogInterface dialog, int whichButton) {
        switch (whichButton) {
            case Dialog.BUTTON_POSITIVE:
                mListener.onDialogPositiveClick(AddressDialogFragment.this);    //отправляем нажатие через интерфейс в активити
                break;
            /*case Dialog.BUTTON_NEGATIVE:
                mListener.onDialogNegativeClick(AddressDialogFragment.this);
                break;*/
            case Dialog.BUTTON_NEUTRAL:
                mListener.onDialogNeutralClick(AddressDialogFragment.this);
                break;
        }
    }
}