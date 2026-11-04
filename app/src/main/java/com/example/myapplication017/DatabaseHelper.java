
package com.example.myapplication017;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "ReminderDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE Reminders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "date TEXT, " +
                "description TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        // Database upgrade code can be added here.
    }

    // Insert reminder
    public boolean addReminder(String title,
                               String date,
                               String description) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("title", title);
        values.put("date", date);
        values.put("description", description);

        long result = db.insert("Reminders", null, values);

        return result != -1;
    }

    // Retrieve all reminders
    public Cursor getAllReminders() {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM Reminders ORDER BY id DESC",
                null);
    }

    // Delete reminder
    public boolean deleteReminder(int id) {

        SQLiteDatabase db = getWritableDatabase();

        int result = db.delete(
                "Reminders",
                "id=?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }
}
