package vn.ptit.quanlylichhen;

public final class Appointment {
    public final int id;
    public final String title;
    public final String location;
    public final String date;
    public final String time;
    public final boolean reminder;

    public Appointment(int id, String title, String location, String date, String time,
                       boolean reminder) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
    }

    public String displayDateTime() {
        return date.substring(8, 10) + "/" + date.substring(5, 7)
                + "/" + date.substring(0, 4) + " · " + time;
    }
}
