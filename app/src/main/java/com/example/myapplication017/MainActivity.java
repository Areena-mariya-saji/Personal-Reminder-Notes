
package com.example.myapplication017;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends Activity {

    EditText etTitle, etDescription;
    TextView tvDate;
    Button btnSave;
    LinearLayout reminderList;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect XML components
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        tvDate = findViewById(R.id.tvDate);
        btnSave = findViewById(R.id.btnSave);
        reminderList = findViewById(R.id.reminderList);

        // Initialize database
        dbHelper = new DatabaseHelper(this);

        // Set today's date
        Calendar calendar = Calendar.getInstance();

        setSelectedDate(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)
        );

        // Open date picker
        tvDate.setOnClickListener(v -> openDatePicker());

        // Save reminder
        btnSave.setOnClickListener(v -> saveReminder());

        // Display saved reminders
        loadReminders();
    }

    // Open date picker
    private void openDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) ->
                        setSelectedDate(day, month, year),

                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    // Set selected date
    private void setSelectedDate(int day, int month, int year) {

        String date = String.format(
                Locale.getDefault(),
                "%02d-%02d-%04d",
                day, month + 1, year
        );

        tvDate.setText(date);
    }

    // Save reminder
    private void saveReminder() {

        String title = etTitle.getText().toString().trim();

        String date = tvDate.getText().toString();

        String description =
                etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Enter reminder title");
            return;
        }

        boolean result = dbHelper.addReminder(
                title, date, description
        );

        if (result) {

            Toast.makeText(
                    this,
                    "Reminder saved successfully",
                    Toast.LENGTH_SHORT
            ).show();

            etTitle.setText("");
            etDescription.setText("");

            loadReminders();

        } else {

            Toast.makeText(
                    this,
                    "Failed to save reminder",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // Display all reminders
    private void loadReminders() {

        reminderList.removeAllViews();

        Cursor cursor = dbHelper.getAllReminders();

        try {

            if (cursor.getCount() == 0) {

                TextView empty = new TextView(this);

                empty.setText("No reminders found");
                empty.setTextSize(16);
                empty.setPadding(10, 15, 10, 15);

                reminderList.addView(empty);
            }

            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String title = cursor.getString(
                        cursor.getColumnIndexOrThrow("title")
                );

                String date = cursor.getString(
                        cursor.getColumnIndexOrThrow("date")
                );

                String description = cursor.getString(
                        cursor.getColumnIndexOrThrow("description")
                );

                // Create reminder card
                LinearLayout card = new LinearLayout(this);

                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(20, 15, 20, 15);

                GradientDrawable shape = new GradientDrawable();

                shape.setColor(Color.WHITE);
                shape.setCornerRadius(20);

                card.setBackground(shape);

                LinearLayout.LayoutParams cardParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                cardParams.setMargins(0, 8, 0, 8);

                card.setLayoutParams(cardParams);

                // Reminder title
                TextView titleView = new TextView(this);

                titleView.setText(title);
                titleView.setTextSize(18);
                titleView.setTextColor(
                        Color.rgb(23, 37, 84)
                );

                titleView.setTypeface(null, Typeface.BOLD);

                // Reminder date
                TextView dateView = new TextView(this);

                dateView.setText(date);
                dateView.setTextSize(14);
                dateView.setTextColor(Color.BLUE);

                // Reminder description
                TextView descView = new TextView(this);

                descView.setText(description);
                descView.setTextSize(15);
                descView.setTextColor(Color.DKGRAY);

                // Delete button
                Button deleteButton = new Button(this);

                deleteButton.setText("Delete");
                deleteButton.setTextColor(Color.RED);

                deleteButton.setOnClickListener(v -> {

                    boolean deleted =
                            dbHelper.deleteReminder(id);

                    if (deleted) {

                        Toast.makeText(
                                MainActivity.this,
                                "Reminder deleted",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadReminders();
                    }
                });

                // Add views to card
                card.addView(titleView);
                card.addView(dateView);
                card.addView(descView);
                card.addView(deleteButton);

                // Add card to reminder list
                reminderList.addView(card);
            }

        } finally {

            cursor.close();
        }
    }

    // Close database helper
    @Override
    protected void onDestroy() {

        if (dbHelper != null) {
            dbHelper.close();
        }

        super.onDestroy();
    }
}
