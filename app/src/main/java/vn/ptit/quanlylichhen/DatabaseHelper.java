package vn.ptit.quanlylichhen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public final class DatabaseHelper extends SQLiteOpenHelper {
    private static final String USERS = "users";
    private static final String APPOINTMENTS = "appointments";
    private static final String[] APPOINTMENT_COLUMNS =
            {"id", "title", "location", "date", "time", "reminder"};

    public DatabaseHelper(Context context) {
        super(context, "appointments.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, password TEXT NOT NULL)");
        db.execSQL("CREATE TABLE appointments (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, location TEXT NOT NULL, date TEXT NOT NULL, "
                + "time TEXT NOT NULL, reminder INTEGER NOT NULL CHECK (reminder IN (0, 1)))");

        insertUser(db, "admin", "123456");
        insertAppointment(db, new Appointment(0, "Họp nhóm đồ án", "Phòng B203",
                "2026-10-25", "09:00", true));
        insertAppointment(db, new Appointment(0, "Khám răng", "Nha khoa Minh Tâm",
                "2026-10-26", "14:30", true));
        insertAppointment(db, new Appointment(0, "Đi cà phê với bạn", "Highland Coffee",
                "2026-10-27", "19:00", false));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new IllegalStateException("Chưa có lộ trình nâng cấp từ " + oldVersion
                + " lên " + newVersion);
    }

    public boolean checkLogin(String username, String password) {
        try (Cursor cursor = getReadableDatabase().query(
                USERS, new String[]{"id"}, "username = ? AND password = ?",
                new String[]{username, password}, null, null, null, "1")) {
            return cursor.moveToFirst();
        }
    }

    public long insertUser(String username, String password) {
        return insertUser(getWritableDatabase(), username, password);
    }

    private long insertUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        return db.insertOrThrow(USERS, null, values);
    }

    public long insertAppointment(Appointment a) {
        return insertAppointment(getWritableDatabase(), a);
    }

    private long insertAppointment(SQLiteDatabase db, Appointment a) {
        ContentValues values = new ContentValues();
        values.put("title", a.title);
        values.put("location", a.location);
        values.put("date", a.date);
        values.put("time", a.time);
        values.put("reminder", a.reminder ? 1 : 0);
        return db.insertOrThrow(APPOINTMENTS, null, values);
    }

    public ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> result = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(APPOINTMENTS, APPOINTMENT_COLUMNS,
                null, null, null, null, "date ASC, time ASC, id ASC")) {
            while (cursor.moveToNext()) {
                result.add(readAppointment(cursor));
            }
        }
        return result;
    }

    public Appointment getAppointmentById(int id) {
        try (Cursor cursor = getReadableDatabase().query(APPOINTMENTS, APPOINTMENT_COLUMNS,
                "id = ?", new String[]{String.valueOf(id)}, null, null, null)) {
            return cursor.moveToFirst() ? readAppointment(cursor) : null;
        }
    }

    private Appointment readAppointment(Cursor cursor) {
        return new Appointment(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("location")),
                cursor.getString(cursor.getColumnIndexOrThrow("date")),
                cursor.getString(cursor.getColumnIndexOrThrow("time")),
                cursor.getInt(cursor.getColumnIndexOrThrow("reminder")) == 1);
    }

    public int updateReminder(int id, boolean on) {
        ContentValues values = new ContentValues();
        values.put("reminder", on ? 1 : 0);
        return getWritableDatabase().update(
                APPOINTMENTS, values, "id = ?", new String[]{String.valueOf(id)});
    }

    public int deleteAppointment(int id) {
        return getWritableDatabase().delete(
                APPOINTMENTS, "id = ?", new String[]{String.valueOf(id)});
    }

    public int countAll() {
        return count(null, null);
    }

    public int countReminders() {
        return count(" WHERE reminder = ?", new String[]{"1"});
    }

    private int count(String where, String[] args) {
        String sql = "SELECT COUNT(*) FROM appointments" + (where == null ? "" : where);
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, args)) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
    }
}
