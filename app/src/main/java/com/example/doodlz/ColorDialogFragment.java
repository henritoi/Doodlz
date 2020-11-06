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

public class ColorDialogFragment extends DialogFragment {
    private static final String TAG = "ColorDialogFragment";

    private SeekBar alphaSeekBar;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private View colorView;
    private int color;

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View colorDialogView = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.fragment_color, null);

        builder.setView(colorDialogView);

        builder.setTitle(R.string.title_color_dialog);

        alphaSeekBar = (SeekBar) colorDialogView.findViewById(R.id.alphaSeekBar);
        redSeekBar = (SeekBar) colorDialogView.findViewById(R.id.redSeekBar);
        greenSeekBar = (SeekBar) colorDialogView.findViewById(R.id.greenSeekBar);
        blueSeekBar = (SeekBar) colorDialogView.findViewById(R.id.blueSeekBar);
        colorView = colorDialogView.findViewById(R.id.colorview);

        alphaSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        redSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        greenSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        blueSeekBar.setOnSeekBarChangeListener(colorChangedListener);

        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        color = doodleView.getDrawingColor();
        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        colorView.setBackgroundColor(color);

        builder.setPositiveButton(
                R.string.button_set_color,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doodleView.setDrawingColor(color);
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
                                alphaSeekBar.getProgress(),
                                redSeekBar.getProgress(), 
                                greenSeekBar.getProgress(),
                                blueSeekBar.getProgress());
                        colorView.setBackgroundColor(color);
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
