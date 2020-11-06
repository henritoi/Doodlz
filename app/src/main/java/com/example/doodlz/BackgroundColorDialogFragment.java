package com.example.doodlz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class BackgroundColorDialogFragment extends DialogFragment {
    private static final String TAG = "BGColorDialogFragment";

    private SeekBar bgAlphaSeekBar;
    private SeekBar bgRedSeekBar;
    private SeekBar bgGreenSeekBar;
    private SeekBar bgBlueSeekBar;
    private View bgColorView;
    private int color;

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View backgroundColorDialogView = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.fragment_background_color, null);

        builder.setView(backgroundColorDialogView);

        builder.setTitle(R.string.title_background_color_dialog);
        bgAlphaSeekBar = (SeekBar) backgroundColorDialogView.findViewById(R.id.bgAlphaSeekBar);
        bgRedSeekBar = (SeekBar) backgroundColorDialogView.findViewById(R.id.bgRedSeekBar);
        bgGreenSeekBar = (SeekBar) backgroundColorDialogView.findViewById(R.id.bgGreenSeekBar);
        bgBlueSeekBar = (SeekBar) backgroundColorDialogView.findViewById(R.id.bgBlueSeekBar);
        bgColorView = backgroundColorDialogView.findViewById(R.id.bgColorView);

        bgAlphaSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        bgRedSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        bgGreenSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        bgBlueSeekBar.setOnSeekBarChangeListener(colorChangedListener);

        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        color = doodleView.getBackgroundColor();
        bgAlphaSeekBar.setProgress(Color.alpha(color));
        bgRedSeekBar.setProgress(Color.red(color));
        bgGreenSeekBar.setProgress(Color.green(color));
        bgBlueSeekBar.setProgress(Color.blue(color));

        bgColorView.setBackgroundColor(color);

        builder.setPositiveButton(
                R.string.button_set_color,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doodleView.setBackgroundColor(color);
                    }
                });
        return builder.create();
    }

    private MainFragment getDoodleFragment() {
        return (MainFragment) getFragmentManager().findFragmentById(R.id.doodleFragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        MainFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(true);

    }

    // kerrotaan MainActivityFragment:lle ettei dialogia enää näytetä
    @Override
    public void onDetach() {
        super.onDetach();
        MainFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(false);
    }

    // Kuuntelija OnSeekBarChangeListener, joka reagoi tapahtumiin
    private final SeekBar.OnSeekBarChangeListener colorChangedListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.d(TAG, "onProgressChanged: Changing color...");
                    if (fromUser) {
                        color = Color.argb(
                                bgAlphaSeekBar.getProgress(),
                                bgRedSeekBar.getProgress(),
                                bgGreenSeekBar.getProgress(),
                                bgBlueSeekBar.getProgress());

                        bgColorView.setBackgroundColor(color);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };
}
