package vn.ptit.quanlylichhen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

final class AppointmentAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private List<Appointment> items = Collections.emptyList();

    AppointmentAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    void submit(List<Appointment> appointments) {
        items = appointments;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Appointment getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        if (row == null) {
            row = inflater.inflate(R.layout.item_appointment, parent, false);
        }
        Appointment appointment = getItem(position);
        TextView title = row.findViewById(R.id.row_title);
        TextView location = row.findViewById(R.id.row_location);
        TextView dateTime = row.findViewById(R.id.row_datetime);
        TextView reminder = row.findViewById(R.id.row_reminder);

        title.setText(appointment.title);
        location.setText(row.getContext().getString(R.string.row_location_format, appointment.location));
        dateTime.setText(appointment.displayDateTime());
        reminder.setText(appointment.reminder ? R.string.has_reminder : R.string.no_reminder);
        reminder.setBackgroundResource(
                appointment.reminder ? R.drawable.badge_active : R.drawable.badge_inactive);
        reminder.setTextColor(row.getContext().getColor(
                appointment.reminder ? R.color.badge_active_text : R.color.text_secondary));
        return row;
    }
}
