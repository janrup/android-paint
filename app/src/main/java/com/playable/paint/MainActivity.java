package com.playable.paint;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.playable.paint.R;

public class MainActivity extends AppCompatActivity {

    private PaintView paintView;
    private ImageButton btnBlack, btnRed, btnBlue, btnGreen;
    private Button btnClear, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        paintView = findViewById(R.id.paintView);
        btnBlack = findViewById(R.id.btnBlack);
        btnRed = findViewById(R.id.btnRed);
        btnBlue = findViewById(R.id.btnBlue);
        btnGreen = findViewById(R.id.btnGreen);
        btnClear = findViewById(R.id.btnClear);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupClickListeners() {
        btnBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setColor(Color.BLACK);
            }
        });

        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setColor(Color.RED);
            }
        });

        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setColor(Color.BLUE);
            }
        });

        btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setColor(Color.GREEN);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.clear();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.save();
                Toast.makeText(MainActivity.this, "Funcionalidad de guardado básica", Toast.LENGTH_SHORT).show();
            }
        });
    }
}