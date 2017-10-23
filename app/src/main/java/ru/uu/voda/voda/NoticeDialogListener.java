package ru.uu.voda.voda;

import android.app.DialogFragment;

/**
 * Created by CAH ek on 22.09.2017.
 */

public interface NoticeDialogListener {
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
        void onDialogPositiveClick(DialogFragment dialog);
        //void onDialogNegativeClick(DialogFragment dialog);
        void onDialogNeutralClick(DialogFragment dialog);
}
