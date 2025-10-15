package com.playable.paint;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.dhaval2404.colorpicker.ColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.google.android.material.slider.RangeSlider;
import com.mihir.drawingcanvas.drawingView;
import com.playable.paint.R;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private drawingView draw;
    private ImageButton undo, redo, color, stroke;
    private Button clear, save;
    private RangeSlider rangeSlider;
    private static final int STORAGE_PERMISSION_CODE = 100;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request storage permissions if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }

        // Initialize views
        draw = findViewById(R.id.draw_view);
        undo = findViewById(R.id.btn_undo);
        redo = findViewById(R.id.btn_redo);
        color = findViewById(R.id.btn_color);
        clear = findViewById(R.id.btn_clean);
        stroke = findViewById(R.id.btn_stroke);
        save = findViewById(R.id.btn_save);
        rangeSlider = findViewById(R.id.rangebar);

        // Initially hide the range slider
        rangeSlider.setVisibility(View.GONE);

        undo.setOnClickListener(v -> draw.undo());
        redo.setOnClickListener(v -> draw.redo());
        clear.setOnClickListener(v -> draw.clearDrawingBoard());

        // Toggle visibility of the brush size slider
        stroke.setOnClickListener(v -> {
            if (rangeSlider.getVisibility() == View.VISIBLE) {
                rangeSlider.setVisibility(View.GONE);
            } else {
                rangeSlider.setVisibility(View.VISIBLE);
            }
        });

        // Adjust brush size based on slider value
        rangeSlider.addOnChangeListener((slider, value, fromUser) ->
                draw.setSizeForBrush((int) (20 * value))
        );

        // Show color picker
        color.setOnClickListener(v ->
                new ColorPickerDialog.Builder(this)
                        .setTitle("Choose color")
                        .setColorShape(ColorShape.SQAURE)
                        .setDefaultColor(getResources().getColor(R.color.black))
                        .setColorListener((ColorListener) (selectedColor, colorHex) -> draw.setBrushColor(selectedColor))
                        .show()
        );

        // Save drawing to storage
        save.setOnClickListener(v -> draw.post(() -> {
            Bitmap bitmap = getBitmapFromView(draw);
            Uri imageUri = saveBitmapToStorage(bitmap);

            if (imageUri != null) {
                Log.e("draw", " Image saved at: " + imageUri);
                Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT).show();
                shareImage(imageUri);
            } else {
                Log.e("draw", " Saving failed!");
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (view.getBackground() != null) {
            view.getBackground().draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return bitmap;
    }

    private Uri saveBitmapToStorage(Bitmap bitmap) {
        String filename = "Drawing_" + System.currentTimeMillis() + ".png";
        Uri uri = null;

        try {
            File imagesDir = new File(getExternalFilesDir(null) + "/Pictures/DrawingApp");

            if (!imagesDir.exists()) {
                imagesDir.mkdirs(); // Create folder if not exists
            }

            File imageFile = new File(imagesDir, filename);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            // Update gallery
            MediaStore.Images.Media.insertImage(getContentResolver(), imageFile.getAbsolutePath(), filename, null);
            uri = Uri.fromFile(imageFile);

        } catch (Exception e) {
            Log.e("draw", " Error saving image: " + e.getMessage());
            e.printStackTrace();
        }

        return uri;
    }

    private void shareImage(Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}