package vn.ptit.quanlylichhen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public final class MainActivity extends Activity {
    private DatabaseHelper database;
    private AppointmentAdapter adapter;
    private TextView totalCount;
    private TextView reminderCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new DatabaseHelper(this);
        totalCount = findViewById(R.id.total_count);
        reminderCount = findViewById(R.id.reminder_count);

        ListView appointments = findViewById(R.id.appointments);
        adapter = new AppointmentAdapter(this);
        appointments.setAdapter(adapter);
        appointments.setEmptyView(findViewById(R.id.empty_list));
        appointments.setOnItemClickListener((parent, row, position, id) -> {
            Appointment appointment = adapter.getItem(position);
            Intent intent = new Intent(this, AppointmentDetailActivity.class);
            intent.putExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_ID, appointment.id);
            startActivity(intent);
        });
        findViewById(R.id.add_button).setOnClickListener(view ->
                startActivity(new Intent(this, AddAppointmentActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.submit(database.getAllAppointments());
        totalCount.setText(getString(R.string.total_count, database.countAll()));
        reminderCount.setText(getString(R.string.reminder_count, database.countReminders()));
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
