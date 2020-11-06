package com.example.doodlz;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    private DoodleView doodleView;
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    private boolean dialogOnScreen = false;

    private static final int ACCELERATION_THRESHOLD = 100000;
    private static final int SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1;

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view  = inflater.inflate(R.layout.fragment_main, container, false);

        Log.d(TAG, "onCreateView: Inflated");

        setHasOptionsMenu(true);

        doodleView = (DoodleView) view.findViewById(R.id.doodleView);

        acceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        enableAccelerometerListener();
    }

    private void enableAccelerometerListener() {
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        disableAccelometerListener();
    }

    private void disableAccelometerListener() {
        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(dialogOnScreen) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                lastAcceleration = currentAcceleration;
                currentAcceleration = x * x + y * y + z * z;
                acceleration = currentAcceleration * (currentAcceleration - lastAcceleration);

                if(acceleration > ACCELERATION_THRESHOLD) confirmErase();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //
        }
    };

    private void confirmErase() {
        Log.d(TAG, "confirmErase: Erase");

        EraseImageDialogFragment fragment = new EraseImageDialogFragment();
        fragment.show(getFragmentManager(), "erase dialog");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "onCreateOptionsMenu: Inflating");
        inflater.inflate(R.menu.doodle_fragment_menu, menu);

        Log.d(TAG, "onCreateOptionsMenu: Inflated");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.color:
                Log.d(TAG, "onOptionsItemSelected: Color");
                ColorDialogFragment colorDialogFragment = new ColorDialogFragment();
                colorDialogFragment.show(getFragmentManager(), "color dialog");
                return true;
            case R.id.rectangle:
                Log.d(TAG, "onOptionsItemSelected: Rectangle");
                // Set drawing action to rectangle
                doodleView.setDrawAction(DrawAction.RECTANGLE);
                RectangleFragment rectangleFragment = new RectangleFragment();
                rectangleFragment.show(getFragmentManager(), "rectangle dialog");
                return true;
            case R.id.ellipse:
                Log.d(TAG, "onOptionsItemSelected: Ellipse");
                // Set drawing action to ellipse
                doodleView.setDrawAction(DrawAction.ELLIPSE);
                EllipseFragment ellipseFragment = new EllipseFragment();
                ellipseFragment.show(getFragmentManager(), "ellipse dialog");
                return true;
            case R.id.eraser:
                Log.d(TAG, "onOptionsItemSelected: Eraser");
                // Set drawing action to eraser
                doodleView.setDrawAction(DrawAction.ERASER);
                EraserWidthDialogFragment eraserWidthDialogFragment = new EraserWidthDialogFragment();
                eraserWidthDialogFragment.show(getFragmentManager(), "eraser width dialog");
                return true;
            case R.id.line_width:
                Log.d(TAG, "onOptionsItemSelected: Line Width");
                // Set drawing action to draw
                doodleView.setDrawAction(DrawAction.DRAW);
                LineWidthDialogFragment lineWidthFragment = new LineWidthDialogFragment();
                lineWidthFragment.show(getFragmentManager(), "line width dialog");
                return true;
            case R.id.delete_drawing:
                Log.d(TAG, "onOptionsItemSelected: Delete drawing");
                confirmErase();
                return true;
            case R.id.save:
                Log.d(TAG, "onOptionsItemSelected: Save");
                saveImage();
                return true;
            case R.id.background_color:
                Log.d(TAG, "onOptionsItemSelected: Background");
                BackgroundColorDialogFragment backgroundColorDialogFragment =
                        new BackgroundColorDialogFragment();

                backgroundColorDialogFragment.show(
                        getFragmentManager(),
                        "background color dialog");

                return true;
            case R.id.print:
                Log.d(TAG, "onOptionsItemSelected: Print");
                doodleView.printImage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImage() {
        Log.d(TAG, "saveImage: Save");
        if(getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
        PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.permission_explanation);

                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(
                                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                SAVE_IMAGE_PERMISSION_REQUEST_CODE);
                    }
                });

                builder.create().show();
            }else {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        SAVE_IMAGE_PERMISSION_REQUEST_CODE);
            }
        }else {
            doodleView.saveImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case SAVE_IMAGE_PERMISSION_REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) doodleView.saveImage();
                return;
        }
    }

    public DoodleView getDoodleView() {
        return doodleView;
    }

    public void setDialogOnScreen(boolean visible) {
        dialogOnScreen = visible;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
