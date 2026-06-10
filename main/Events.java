package calendar.main;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Events {
    public static class Event {
        private final String name;
        private final Date start;
        private final Date end;
        private final String location;
        private final String notes;

        public Event(String name, Date start, Date end, String location, String notes) {
            this.name = name;
            this.start = start;
            this.end = end;
            this.location = location;
            this.notes = notes;
        }

        public String getName() {
            return name;
        }

        public Date getStart() {
            return start;
        }

        public Date getEnd() {
            return end;
        }

        public String getLocation() {
            return location;
        }

        public String getNotes() {
            return notes;
        }

        public String formatDetails(String startLabel, String endLabel, String locationLabel, String notesLabel) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            StringBuilder builder = new StringBuilder();
            builder.append(name).append("\n");
            builder.append(startLabel).append(" ").append(dateFormat.format(start)).append(" ").append(timeFormat.format(start)).append("\n");
            builder.append(endLabel).append("   ").append(dateFormat.format(end)).append(" ").append(timeFormat.format(end)).append("\n");
            if (!location.isEmpty()) {
                builder.append(locationLabel).append(" ").append(location).append("\n");
            }
            if (!notes.isEmpty()) {
                builder.append(notesLabel).append(" ").append(notes);
            }
            return builder.toString();
        }

        public String formatUpcomingSummary(String dateLabel, String startLabel, String endLabel, String locationLabel, String notesLabel,
                                           SimpleDateFormat dateFormat, SimpleDateFormat timeFormat) {
            StringBuilder builder = new StringBuilder();
            builder.append("* ").append(name).append("\n");
            String startDateText = dateFormat.format(start);
            String endDateText = dateFormat.format(end);
            builder.append("  ").append(dateLabel).append(" ");
            if (startDateText.equals(endDateText)) {
                builder.append(startDateText).append("\n");
            } else {
                builder.append(startDateText).append(" - ").append(endDateText).append("\n");
            }
            builder.append("  ").append(startLabel).append(" ").append(timeFormat.format(start)).append("\n");
            builder.append("  ").append(endLabel).append(" ").append(timeFormat.format(end)).append("\n");
            if (!location.isEmpty()) {
                builder.append("  ").append(locationLabel).append(" ").append(location).append("\n");
            }
            if (!notes.isEmpty()) {
                builder.append("  ").append(notesLabel).append(" ").append(notes).append("\n");
            }
            return builder.toString();
        }

        @Override
        public String toString() {
            return formatDetails("Start:", "End:", "Location:", "Notes:");
        }
    }

    public static Date combineDateAndTime(Date datePart, Date timePart) {
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(datePart);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(timePart);
        dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        dateCal.set(Calendar.SECOND, 0);
        dateCal.set(Calendar.MILLISECOND, 0);
        return dateCal.getTime();
    }

    public static List<LocalDate> getDateRange(Date start, Date end) {
        LocalDate startDate = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        return dates;
    }
}
