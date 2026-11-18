package vn.ptit.quanlylichhen;

import android.app.Activity;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public final class AddAppointmentActivity extends Activity {
    private DatabaseHelper database;
    private EditText titleInput;
    private EditText locationInput;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Switch reminderSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        database = new DatabaseHelper(this);
        titleInput = findViewById(R.id.appointment_title);
        locationInput = findViewById(R.id.appointment_location);
        datePicker = findViewById(R.id.date_picker);
        timePicker = findViewById(R.id.time_picker);
        reminderSwitch = findViewById(R.id.add_reminder);
        timePicker.setIs24HourView(true);

        if (savedInstanceState == null) {
            Calendar initial = Calendar.getInstance();
            initial.add(Calendar.HOUR_OF_DAY, 1);
            datePicker.updateDate(initial.get(Calendar.YEAR), initial.get(Calendar.MONTH),
                    initial.get(Calendar.DAY_OF_MONTH));
            timePicker.setHour(initial.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(initial.get(Calendar.MINUTE));
        }

        findViewById(R.id.add_back).setOnClickListener(view -> finish());
        findViewById(R.id.save_button).setOnClickListener(view -> saveAppointment());
    }

    private void saveAppointment() {
        String title = titleInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();

        if (title.isEmpty() || location.isEmpty()) {
            if (title.isEmpty()) {
                titleInput.setError(getString(R.string.title_required));
            }
            if (location.isEmpty()) {
                locationInput.setError(getString(R.string.location_required));
            }
            (title.isEmpty() ? titleInput : locationInput).requestFocus();
            return;
        }

        datePicker.clearFocus();
        timePicker.clearFocus();
        Calendar appointmentTime = Calendar.getInstance();
        appointmentTime.clear();
        appointmentTime.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                timePicker.getHour(), timePicker.getMinute());

        if (appointmentTime.getTimeInMillis() <= System.currentTimeMillis()) {
            Toast.makeText(this, R.string.future_required, Toast.LENGTH_LONG).show();
            return;
        }

        String date = String.format(Locale.ROOT, "%04d-%02d-%02d", datePicker.getYear(),
                datePicker.getMonth() + 1, datePicker.getDayOfMonth());
        String time = String.format(Locale.ROOT, "%02d:%02d",
                timePicker.getHour(), timePicker.getMinute());
        Appointment appointment = new Appointment(0, title, location, date, time,
                reminderSwitch.isChecked());

        try {
            database.insertAppointment(appointment);
        } catch (SQLiteException error) {
            Toast.makeText(this, R.string.save_failed, Toast.LENGTH_LONG).show();
            return;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
