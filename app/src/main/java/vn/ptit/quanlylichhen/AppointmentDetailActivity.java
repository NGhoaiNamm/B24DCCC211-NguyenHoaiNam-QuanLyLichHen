package vn.ptit.quanlylichhen;

import android.app.Activity;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public final class AppointmentDetailActivity extends Activity {
    static final String EXTRA_APPOINTMENT_ID = "appointment_id";

    private DatabaseHelper database;
    private int appointmentId;
    private Switch reminderSwitch;
    private TextView reminderStatus;
    private boolean reminderOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        database = new DatabaseHelper(this);
        appointmentId = getIntent().getIntExtra(EXTRA_APPOINTMENT_ID, -1);
        Appointment appointment = database.getAppointmentById(appointmentId);
        if (appointment == null) {
            showMissingAndFinish();
            return;
        }

        TextView title = findViewById(R.id.detail_title);
        TextView location = findViewById(R.id.detail_location);
        TextView dateTime = findViewById(R.id.detail_datetime);
        title.setText(appointment.title);
        location.setText(appointment.location);
        dateTime.setText(appointment.displayDateTime());

        reminderSwitch = findViewById(R.id.detail_reminder);
        reminderStatus = findViewById(R.id.detail_reminder_status);
        reminderOn = appointment.reminder;
        showReminderStatus();
        reminderSwitch.setOnCheckedChangeListener((view, checked) -> updateReminder(checked));
        findViewById(R.id.detail_toolbar_back).setOnClickListener(view -> finish());
        findViewById(R.id.detail_back).setOnClickListener(view -> finish());
        findViewById(R.id.delete_button).setOnClickListener(this::deleteAppointment);
    }

    private void showReminderStatus() {
        reminderSwitch.setChecked(reminderOn);
        reminderStatus.setText(reminderOn ? R.string.reminder_on : R.string.reminder_off);
    }

    private void updateReminder(boolean checked) {
        if (checked == reminderOn) {
            return;
        }
        try {
            if (database.updateReminder(appointmentId, checked) == 0) {
                showMissingAndFinish();
                return;
            }
            reminderOn = checked;
        } catch (SQLiteException error) {
            Toast.makeText(this, R.string.reminder_failed, Toast.LENGTH_LONG).show();
        }
        showReminderStatus();
    }

    private void deleteAppointment(View view) {
        try {
            if (database.deleteAppointment(appointmentId) == 0) {
                showMissingAndFinish();
                return;
            }
            finish();
        } catch (SQLiteException error) {
            Toast.makeText(this, R.string.delete_failed, Toast.LENGTH_LONG).show();
        }
    }

    private void showMissingAndFinish() {
        Toast.makeText(this, R.string.appointment_missing, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
