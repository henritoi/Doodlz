package com.example.doodlz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import androidx.fragment.app.DialogFragment;

public class EraserWidthDialogFragment extends DialogFragment {
    private static final String TAG = "EraserWidthDialogFragme";
    private ImageView widthImageView;

    // luodaan AlertDialog ja palautetaan se
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        // luodaan dialogi
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View eraserWidthDialogView = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_eraser_width,
                null);

        builder.setView(eraserWidthDialogView);
        builder.setTitle(R.string.title_eraser_width_dialog);
        widthImageView = (ImageView) eraserWidthDialogView.findViewById(R.id.widthImageView);
        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        final SeekBar widthSeekBar = (SeekBar) eraserWidthDialogView.findViewById(R.id.widthSeekBar);
        widthSeekBar.setOnSeekBarChangeListener(eraserWidthChanged);
        widthSeekBar.setProgress(doodleView.getEraserWidth());

        builder.setPositiveButton(
                R.string.button_set_eraser_width,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        doodleView.setEraserWidth(widthSeekBar.getProgress());
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

    private final OnSeekBarChangeListener eraserWidthChanged =
            new OnSeekBarChangeListener() {
                final Bitmap bitmap = Bitmap.createBitmap(
                        400, 100, Bitmap.Config.ARGB_8888);
                final Canvas canvas = new Canvas(bitmap); // piirtää bittikarttaan

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Paint p = new Paint();
                    p.setColor(Color.BLACK);
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
