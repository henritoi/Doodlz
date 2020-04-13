package com.example.doodlz;

import androidx.fragment.app.DialogFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class EraseImageDialogFragment extends DialogFragment {
    private static final String TAG = "EraseImageDialogFragment";

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.message_erase);
        builder.setPositiveButton(
                R.string.button_erase,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        getDoodleFragmetn().getDoodleView().clear();
                    }
                }
        );

        builder.setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    private MainFragment getDoodleFragmetn() {
        return (MainFragment) getFragmentManager().findFragmentById(
                R.id.doodleFragment);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        MainFragment fragment = getDoodleFragmetn();

        if (fragment != null)
            fragment.setDialogOnScreen(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainFragment fragment = getDoodleFragmetn();

        if (fragment != null)
            fragment.setDialogOnScreen(false);
    }
}
