package com.example.doodlz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.print.PrintHelper;

import java.util.HashMap;
import java.util.Map;

public class DoodleView extends View {
    private static final String TAG = "DoodleView";

    private static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private final Paint paintScreen;
    private final Paint paintLine;

    private final Map<Integer, Path> pathMap = new HashMap<>();
    private final Map<Integer, Point> previousPointMap = new HashMap<>();

    // For new actions
    private DrawAction drawAction = DrawAction.DRAW;

    // Store colors for draw and erase
    private int backgroundColor = Color.WHITE;
    private int drawingColor = Color.BLACK;

    // Store widths for draw and erase
    private int lineWidth;
    private int eraserWidth;

    // Store settings for rects
    private int rectBorderWidth;
    private boolean rectIsFilled = false;
    private int rectFillColor = Color.WHITE;

    // Store settings for ellipses
    private int ellipseBorderWidth;
    private boolean ellipseIsFilled = false;
    private int ellipseFillColor = Color.WHITE;

    private float shapeStartX;
    private float shapeStartY;
    private float shapeEndX;
    private float shapeEndY;


    public DoodleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paintScreen = new Paint();

        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.BLACK);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(5);
        paintLine.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }

    public void clear() {
        pathMap.clear();
        previousPointMap.clear();
        bitmap.eraseColor(Color.WHITE);
        backgroundColor = Color.WHITE;
        invalidate();
    }

    public void setRectBorderWidth(int width) {
        rectBorderWidth = width;
    }

    public int getRectBorderWidth() {
        return rectBorderWidth;
    }
    public void setRectFillColor(int color) {
        rectFillColor = color;
    }

    public int getRectFillColor() {
        return rectFillColor;
    }

    public void setRectIsFilled(boolean isFilled) {
        rectIsFilled = isFilled;
    }

    public boolean getRectIsFilled() {
        return rectIsFilled;
    }

    public void setEllipseBorderWidth(int width) {
        ellipseBorderWidth = width;
    }

    public int getEllipseBorderWidth() {
        return ellipseBorderWidth;
    }
    public void setEllipseFillColor(int color) {
        ellipseFillColor = color;
    }

    public int getEllipseFillColor() {
        return ellipseFillColor;
    }

    public void setEllipseIsFilled(boolean isFilled) {
        ellipseIsFilled = isFilled;
    }

    public boolean getEllipseIsFilled() {
        return ellipseIsFilled;
    }

    public void setBackgroundColor(int color) {
        pathMap.clear();
        previousPointMap.clear();
        bitmap.eraseColor(color);
        backgroundColor = color;
        invalidate();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setDrawingColor(int color) {
        drawingColor = color;
        paintLine.setColor(color);
    }

    public int getDrawingColor() {
        return drawingColor;
    }

    public void setLineWidth(int width) {
        lineWidth = width;
        paintLine.setStrokeWidth(width);
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setEraserWidth(int width) {
        eraserWidth = width;
        paintLine.setStrokeWidth(width);
    }

    public int getEraserWidth() {
        return eraserWidth;
    }

    public void setDrawAction(DrawAction action) {
        this.drawAction = action;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, paintScreen);
        switch(drawAction) {
            case DRAW:
                paintLine.setColor(drawingColor);
                for (Integer key : pathMap.keySet()) canvas.drawPath(pathMap.get(key), paintLine);
                break;
            case ERASER:
                paintLine.setColor(backgroundColor);
                for (Integer key : pathMap.keySet()) canvas.drawPath(pathMap.get(key), paintLine);
                break;
            case ELLIPSE:
                // TODO: Preview
                break;
            case RECTANGLE:
                // TODO: Preview
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int actionIndex = event.getActionIndex();

        if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            touchStarted(
                    event.getX(actionIndex),
                    event.getY(actionIndex),
                    event.getPointerId(actionIndex));
        }else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));
        }else {
            touchMoved(event);
        }
        invalidate();
        return true;
    }

    private void touchStarted(float x, float y, int lineID) {
        Path path;
        Point point;

        if(pathMap.containsKey(lineID)) {
            path = pathMap.get(lineID);
            path.reset();
            point = previousPointMap.get(lineID);
        }else {
            path = new Path();
            pathMap.put(lineID, path);
            point = new Point();
            previousPointMap.put(lineID, point);
        }

        path.moveTo(x,y);
        point.x = (int) x;
        point.y = (int) y;

        if(drawAction == DrawAction.RECTANGLE || drawAction == DrawAction.ELLIPSE) {
            shapeStartX = point.x;
            shapeStartY = point.y;
        }
    }

    private void touchMoved(MotionEvent event) {
        for (int i = 0; i < event.getPointerCount(); i++) {
            int pointerID = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerID);

            if(pathMap.containsKey(pointerID)) {
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = pathMap.get(pointerID);
                Point point = previousPointMap.get(pointerID);

                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    path.quadTo(point.x,
                            point.y,
                            (newX + point.x) / 2,
                            (newY + point.y) / 2);
                    point.x = (int) newX;
                    point.y = (int) newY;

                    if(drawAction == DrawAction.RECTANGLE || drawAction == DrawAction.ELLIPSE) {
                        shapeEndX = point.x;
                        shapeEndY = point.y;
                    }
                }
            }
        }
    }

    private void touchEnded(int lineID) {
        Path path = pathMap.get(lineID);

        switch(drawAction) {
            case DRAW:
                bitmapCanvas.drawPath(path, paintLine);
                break;
            case ERASER:
                bitmapCanvas.drawPath(path, paintLine);
                break;
            case ELLIPSE:
                Paint ellipsePaint = new Paint();
                if(ellipseIsFilled) {
                    ellipsePaint.setColor(ellipseFillColor);
                    ellipsePaint.setStyle(Paint.Style.FILL);
                    bitmapCanvas.drawOval(shapeStartX, shapeStartY, shapeEndX, shapeEndY, ellipsePaint);
                }

                ellipsePaint.setStrokeWidth(ellipseBorderWidth);
                ellipsePaint.setColor(Color.BLACK);
                ellipsePaint.setStyle(Paint.Style.STROKE);
                bitmapCanvas.drawOval(shapeStartX, shapeStartY, shapeEndX, shapeEndY, ellipsePaint);
                break;
            case RECTANGLE:
                Paint rectPaint = new Paint();
                if(rectIsFilled) {
                    rectPaint.setColor(rectFillColor);
                    rectPaint.setStyle(Paint.Style.FILL);
                    bitmapCanvas.drawRect(shapeStartX, shapeStartY, shapeEndX, shapeEndY, rectPaint);
                }

                rectPaint.setStrokeWidth(rectBorderWidth);
                rectPaint.setColor(Color.BLACK);
                rectPaint.setStyle(Paint.Style.STROKE);
                bitmapCanvas.drawRect(shapeStartX, shapeStartY, shapeEndX, shapeEndY, rectPaint);
                break;
        }
        resetShape();
        path.reset();
    }

    private void resetShape() {
        shapeStartX = 0.0f;
        shapeStartY = 0.0f;
        shapeEndX = 0.0f;
        shapeEndY = 0.0f;
    }

    public void saveImage() {
        final String name = "Doodlz" + System.currentTimeMillis() + ".jpg";

        String location = MediaStore.Images.Media.insertImage(
                getContext().getContentResolver(),
                bitmap,
                name,
                "Doodlz Drawing");

        if (location != null) {
            Toast message = Toast.makeText(
                    getContext(),
                    R.string.message_saved,
                    Toast.LENGTH_SHORT);

            message.setGravity(
                    Gravity.CENTER,
                    message.getXOffset() / 2,
                    message.getYOffset() / 2);

            message.show();
        }else {
            Toast message = Toast.makeText(
                    getContext(),
                    R.string.message_error_saving,
                    Toast.LENGTH_SHORT);

            message.setGravity(
                    Gravity.CENTER,
                    message.getXOffset() / 2,
                    message.getYOffset() / 2);

            message.show();
        }
    }

    public void printImage() {
        if (PrintHelper.systemSupportsPrint()) {
            PrintHelper printHelper = new PrintHelper(getContext());

            printHelper.setScaleMode(printHelper.SCALE_MODE_FIT);
            printHelper.printBitmap("Doodlz Image", bitmap);
        }else {
            Toast message = Toast.makeText(
                    getContext(),
                    R.string.message_error_printing,
                    Toast.LENGTH_SHORT);

            message.setGravity(
                    Gravity.CENTER,
                    message.getXOffset() / 2,
                    message.getYOffset() / 2);

            message.show();
        }
    }
}
