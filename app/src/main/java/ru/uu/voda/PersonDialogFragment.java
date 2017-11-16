package ru.uu.voda;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by CAH ek on 22.09.2017.
 */

public class PersonDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    SharedPreferences sPref;    //объект сохранялок
    final String NEED_CALLBACK = "need_callback"; //ключи сохранялок
    final String PHONE_NUMBER = "phone_number";
    final String NAME = "name";

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

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_person, null, false); //мутим вид из xml
        //находим поля
        CheckBox p_need_callback = (CheckBox) view.findViewById(R.id.CheckBox9);
        EditText p_phone_number = (EditText) view.findViewById(R.id.EditText10);
        EditText p_name = (EditText) view.findViewById(R.id.EditText11);

        //подгружаем значения из сохранялок
        p_need_callback.setChecked(sPref.getBoolean(NEED_CALLBACK, false));
        p_phone_number.setText(sPref.getString(PHONE_NUMBER, ""));
        p_name.setText(sPref.getString(NAME, ""));

        return new AlertDialog.Builder(getActivity())   //творим диалог через билдер
                .setIcon(R.drawable.ic_face_black_18px)
                .setTitle(R.string.hpre9)
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
                mListener.onDialogPositiveClick(PersonDialogFragment.this);    //отправляем нажатие через интерфейс в активити
                break;
            /*case Dialog.BUTTON_NEGATIVE:
                mListener.onDialogNegativeClick(PersonDialogFragment.this);
                break;*/
            case Dialog.BUTTON_NEUTRAL:
                mListener.onDialogNeutralClick(PersonDialogFragment.this);
                break;
        }
    }
}