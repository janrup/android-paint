package com.playable.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PaintView extends View {

    private static final float TOUCH_TOLERANCE = 4f;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private final ArrayList<Stroke> paths = new ArrayList<>();
    private int currentColor;
    private int strokeWidth = 12;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private final Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    public PaintView(Context context) {
        super(context);
        init();
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(strokeWidth);
        currentColor = Color.BLACK;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        }

        for (Stroke fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            canvas.drawPath(fp.path, mPaint);
        }
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        Stroke fp = new Stroke(currentColor, strokeWidth, mPath);
        paths.add(fp);

        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2f, (y + mY) / 2f);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
        if (mCanvas != null && mPath != null) {
            mPaint.setColor(currentColor);
            mPaint.setStrokeWidth(strokeWidth);
            mCanvas.drawPath(mPath, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    public void setColor(int color) {
        currentColor = color;
    }

    public void clear() {
        paths.clear();
        if (mBitmap != null) {
            mBitmap.eraseColor(Color.TRANSPARENT);
        }
        invalidate();
    }

    public void save() {
        try {
            // Crear un nuevo bitmap con el mismo tamaño del lienzo
            Bitmap savedBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(savedBitmap);

            // Fondo blanco
            canvas.drawColor(Color.WHITE);

            // Dibujar el contenido actual del PaintView (igual que en onDraw)
            for (Stroke fp : paths) {
                mPaint.setColor(fp.color);
                mPaint.setStrokeWidth(fp.strokeWidth);
                canvas.drawPath(fp.path, mPaint);
            }

            // Guardar el bitmap en la galería
            String filename = "Paint_" + System.currentTimeMillis() + ".jpg";

            String savedImageURL = MediaStore.Images.Media.insertImage(
                    getContext().getContentResolver(),
                    savedBitmap,
                    filename,
                    "Dibujo creado en PaintApp"
            );

            if (savedImageURL != null) {
                Toast.makeText(getContext(), "Imagen guardada en la galería", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private static class Stroke {
        public int color;
        public int strokeWidth;
        public Path path;

        public Stroke(int color, int strokeWidth, Path path) {
            this.color = color;
            this.strokeWidth = strokeWidth;
            this.path = path;
        }
    }
}
