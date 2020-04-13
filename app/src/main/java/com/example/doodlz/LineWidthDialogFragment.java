package com.example.doodlz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import androidx.fragment.app.DialogFragment;

public class LineWidthDialogFragment extends DialogFragment {
    private static final String TAG = "LineWidthFragment";
    private ImageView widthImageView;

    // luodaan AlertDialog ja palautetaan se
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        // luodaan dialogi
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View lineWidthDialogView = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_line_width,
                null);

        builder.setView(lineWidthDialogView);
        builder.setTitle(R.string.title_line_width_dialog);
        widthImageView = (ImageView) lineWidthDialogView.findViewById(R.id.widthImageView);
        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        final SeekBar widthSeekBar = (SeekBar) lineWidthDialogView.findViewById(R.id.widthSeekBar);
        widthSeekBar.setOnSeekBarChangeListener(lineWidthChanged);
        widthSeekBar.setProgress(doodleView.getLineWidth());

        builder.setPositiveButton(
                R.string.button_set_line_width,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        doodleView.setLineWidth(widthSeekBar.getProgress());
                    }
                }
        );
        return builder.create();
    }

    private MainFragment getDoodleFragment() {
        return (MainFragment) getFragmentManager().findFragmentById(
                R.id.doodleFragment);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(false);
    }

    private final OnSeekBarChangeListener lineWidthChanged =
            new OnSeekBarChangeListener() {
                final Bitmap bitmap = Bitmap.createBitmap(
                        400, 100, Bitmap.Config.ARGB_8888);
                final Canvas canvas = new Canvas(bitmap); // piirtää bittikarttaan

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Paint p = new Paint();
                    p.setColor(
                            getDoodleFragment().getDoodleView().getDrawingColor());
                    p.setStrokeCap(Paint.Cap.ROUND);
                    p.setStrokeWidth(progress);

                    // pyyhitään bittikartta ja piirretään viiva
                    bitmap.eraseColor(
                                    getResources().getColor(android.R.color.transparent,
                                    getContext().getTheme()));

                    canvas.drawLine(30, 50, 370, 50, p);
                    widthImageView.setImageBitmap(bitmap);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { // tarvitaan

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { // tarvitaan

                }
            };
}
