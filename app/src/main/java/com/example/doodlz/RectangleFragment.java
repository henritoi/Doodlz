package com.example.doodlz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import org.w3c.dom.Text;

public class RectangleFragment extends DialogFragment {
    private static final String TAG = "BGColorDialogFragment";

    private ImageView previewImageView;
    private SeekBar widthSeekbar;
    private Switch isFilledSwitch;

    private TextView alphaTextView, redTextView, greenTextView, blueTextView;
    private SeekBar bgAlphaSeekBar;
    private SeekBar bgRedSeekBar;
    private SeekBar bgGreenSeekBar;
    private SeekBar bgBlueSeekBar;

    private final int PREVIEW_WIDTH = 150;
    private final int PREVIEW_HEIGHT = 150;

    private boolean isFilled;
    private int color;

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rectangleDialogView = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.fragment_rectangle, null);

        builder.setView(rectangleDialogView);
        builder.setTitle(R.string.title_rectangle_dialog);

        previewImageView = (ImageView) rectangleDialogView.findViewById(R.id.rectanglePreview);
        widthSeekbar = (SeekBar) rectangleDialogView.findViewById(R.id.widthSeekBar);
        isFilledSwitch = (Switch) rectangleDialogView.findViewById(R.id.isFilledSwitch);

        isFilledSwitch.setOnCheckedChangeListener(isFilledChangeListener);

        bgAlphaSeekBar = (SeekBar) rectangleDialogView.findViewById(R.id.bgAlphaSeekBar);
        bgRedSeekBar = (SeekBar) rectangleDialogView.findViewById(R.id.bgRedSeekBar);
        bgGreenSeekBar = (SeekBar) rectangleDialogView.findViewById(R.id.bgGreenSeekBar);
        bgBlueSeekBar = (SeekBar) rectangleDialogView.findViewById(R.id.bgBlueSeekBar);

        alphaTextView = (TextView) rectangleDialogView.findViewById(R.id.alphaTextView);
        redTextView = (TextView) rectangleDialogView.findViewById(R.id.redTextView);
        greenTextView = (TextView) rectangleDialogView.findViewById(R.id.greenTextView);
        blueTextView = (TextView) rectangleDialogView.findViewById(R.id.blueTextView);

        bgAlphaSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        bgRedSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        bgGreenSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        bgBlueSeekBar.setOnSeekBarChangeListener(colorChangedListener);

        final DoodleView doodleView = getDoodleFragment().getDoodleView();

        isFilled = doodleView.getRectIsFilled();
        isFilledSwitch.setChecked(isFilled);

        color = doodleView.getRectFillColor();

        bgAlphaSeekBar.setProgress(Color.alpha(color));
        bgRedSeekBar.setProgress(Color.red(color));
        bgGreenSeekBar.setProgress(Color.green(color));
        bgBlueSeekBar.setProgress(Color.blue(color));

        widthSeekbar.setOnSeekBarChangeListener(borderWidthListener);
        widthSeekbar.setProgress(doodleView.getRectBorderWidth());

        // TODO: Alkuun neliön piirto
        drawPreview();

        toggleColorSeekbars(isFilled); // Hide fill color selections by default

        builder.setPositiveButton(
                R.string.button_set_settings,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doodleView.setRectFillColor(Color.argb(
                                bgAlphaSeekBar.getProgress(),
                                bgRedSeekBar.getProgress(),
                                bgGreenSeekBar.getProgress(),
                                bgBlueSeekBar.getProgress()));
                        doodleView.setRectBorderWidth(widthSeekbar.getProgress());
                        doodleView.setRectIsFilled(isFilled);
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
                        drawPreview();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };

    private final SeekBar.OnSeekBarChangeListener borderWidthListener =
            new SeekBar.OnSeekBarChangeListener() {
                final Bitmap bitmap = Bitmap.createBitmap(
                        400, 100, Bitmap.Config.ARGB_8888);
                final Canvas canvas = new Canvas(bitmap); // piirtää bittikarttaan

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    drawPreview();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { // tarvitaan

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { // tarvitaan

                }
            };

    private final CompoundButton.OnCheckedChangeListener isFilledChangeListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    toggleColorSeekbars(isChecked);
                    isFilled = isChecked;
                    drawPreview();
                }
            };

    private void drawPreview() {
        final Bitmap bitmap = Bitmap.createBitmap(
                PREVIEW_WIDTH, PREVIEW_HEIGHT, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap); // piirtää bittikarttaan

        int borderWidth = widthSeekbar.getProgress();

        // pyyhitään bittikartta ja piirretään viiva
        bitmap.eraseColor(
                getResources().getColor(android.R.color.transparent,
                        getContext().getTheme()));

        Paint p = new Paint();
        // border
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.BLACK);
        canvas.drawRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT, p);

        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.WHITE);
        canvas.drawRect(borderWidth, borderWidth, PREVIEW_WIDTH - borderWidth, PREVIEW_HEIGHT - borderWidth, p);

        // fill
        if(isFilled) {
            int color = Color.argb(
                    bgAlphaSeekBar.getProgress(),
                    bgRedSeekBar.getProgress(),
                    bgGreenSeekBar.getProgress(),
                    bgBlueSeekBar.getProgress());

            p.setStyle(Paint.Style.FILL);
            p.setColor(color);
            canvas.drawRect(borderWidth, borderWidth, PREVIEW_WIDTH - borderWidth, PREVIEW_HEIGHT - borderWidth, p);
        }


        previewImageView.setImageBitmap(bitmap);
    }

    private void toggleColorSeekbars(Boolean isShown) {
        if(isShown) {
            bgAlphaSeekBar.setVisibility(View.VISIBLE);
            bgRedSeekBar.setVisibility(View.VISIBLE);
            bgGreenSeekBar.setVisibility(View.VISIBLE);
            bgBlueSeekBar.setVisibility(View.VISIBLE);
            alphaTextView.setVisibility(View.VISIBLE);
            redTextView.setVisibility(View.VISIBLE);
            greenTextView.setVisibility(View.VISIBLE);
            blueTextView.setVisibility(View.VISIBLE);
        }else {
            bgAlphaSeekBar.setVisibility(View.GONE);
            bgRedSeekBar.setVisibility(View.GONE);
            bgGreenSeekBar.setVisibility(View.GONE);
            bgBlueSeekBar.setVisibility(View.GONE);
            alphaTextView.setVisibility(View.GONE);
            redTextView.setVisibility(View.GONE);
            greenTextView.setVisibility(View.GONE);
            blueTextView.setVisibility(View.GONE);
        }
    }
}
