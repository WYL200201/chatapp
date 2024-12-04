package com.example.group35;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the toolbar, remove default title and use custom TextView for title
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Optionally remove the default title if needed
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set custom title text size and position via the TextView
        TextView titleTextView = findViewById(R.id.toolbar_title);
        titleTextView.setText("LOGIN"); // Title text here if needed
    }
}
