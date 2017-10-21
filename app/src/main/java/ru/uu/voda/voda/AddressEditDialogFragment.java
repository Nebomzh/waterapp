package ru.uu.voda.voda;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.content.SharedPreferences;           //для работы с сохранялками

public class AddressEditDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    SharedPreferences sPref;    //объект сохранялок
    final String ADDRESS = "address"; //ключи сохранялок

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

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_addressedit, null, false); //мутим вид из xml
        //находим поля
        EditText addressedit = (EditText) view.findViewById(R.id.addressedit);

        //подгружаем значения из сохранялок
        addressedit.setText(sPref.getString(ADDRESS, "")); //отображаем уже введённые данные в поле ввода

        return new AlertDialog.Builder(getActivity())   //творим диалог через билдер
                .setIcon(R.drawable.ic_mode_edit_black_18px)
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
                mListener.onDialogPositiveClick(AddressEditDialogFragment.this);    //отправляем нажатие через интерфейс в активити
                break;
            /*case Dialog.BUTTON_NEGATIVE:
                mListener.onDialogNegativeClick(AddressEditDialogFragment.this);
                break;*/
            case Dialog.BUTTON_NEUTRAL:
                mListener.onDialogNeutralClick(AddressEditDialogFragment.this);
                break;
        }
    }
}