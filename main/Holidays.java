package calendar.main;

import java.time.LocalDate;
import java.time.Month;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Holidays {
    public abstract static class Holiday {
        protected final String name;
        protected final boolean isNoWorkDay;

        public Holiday(String name, boolean isNoWorkDay) {
            this.name = name;
            this.isNoWorkDay = isNoWorkDay;
        }

        public abstract LocalDate getDateForYear(int year);

        public String getName() {
            return name;
        }

        public boolean isNoWorkDay() {
            return isNoWorkDay;
        }
    }

    // Fixed date holiday (e.g., Jan 1 for New Year's)
    public static class FixedHoliday extends Holiday {
        private final Month month;
        private final int day;

        public FixedHoliday(Month month, int day, String name, boolean isNoWorkDay) {
            super(name, isNoWorkDay);
            this.month = month;
            this.day = day;
        }

        @Override
        public LocalDate getDateForYear(int year) {
            return LocalDate.of(year, month, day);
        }
    }

    // Easter-based holiday (calculated from Easter Sunday)
    public static class EasterBasedHoliday extends Holiday {
        private final int daysFromEaster; // negative for before Easter, positive for after

        public EasterBasedHoliday(String name, int daysFromEaster, boolean isNoWorkDay) {
            super(name, isNoWorkDay);
            this.daysFromEaster = daysFromEaster;
        }

        @Override
        public LocalDate getDateForYear(int year) {
            LocalDate easter = calculateEaster(year);
            return easter.plusDays(daysFromEaster);
        }
    }

    // Specific day of week in a month (e.g., 3rd Monday in January)
    public static class NthDayOfWeekHoliday extends Holiday {
        private final Month month;
        private final DayOfWeek dayOfWeek;
        private final int occurrence; // 1 = first, -1 = last, etc.

        public NthDayOfWeekHoliday(Month month, DayOfWeek dayOfWeek, int occurrence, String name, boolean isNoWorkDay) {
            super(name, isNoWorkDay);
            this.month = month;
            this.dayOfWeek = dayOfWeek;
            this.occurrence = occurrence;
        }

        @Override
        public LocalDate getDateForYear(int year) {
            if (occurrence > 0) {
                // Find nth occurrence from start of month
                LocalDate date = LocalDate.of(year, month, 1);
                int count = 0;
                while (date.getMonth() == month) {
                    if (date.getDayOfWeek() == dayOfWeek) {
                        count++;
                        if (count == occurrence) {
                            return date;
                        }
                    }
                    date = date.plusDays(1);
                }
            } else if (occurrence == -1) {
                // Last occurrence in month
                LocalDate date = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);
                while (date.getMonth() == month) {
                    if (date.getDayOfWeek() == dayOfWeek) {
                        return date;
                    }
                    date = date.minusDays(1);
                }
            }
            return null;
        }
    }

    // Holiday on or before a specific date (e.g., Victoria Day = Monday on or before May 25)
    public static class OnOrBeforeHoliday extends Holiday {
        private final Month month;
        private final int day;
        private final DayOfWeek preferredDay;

        public OnOrBeforeHoliday(Month month, int day, DayOfWeek preferredDay, String name, boolean isNoWorkDay) {
            super(name, isNoWorkDay);
            this.month = month;
            this.day = day;
            this.preferredDay = preferredDay;
        }

        @Override
        public LocalDate getDateForYear(int year) {
            LocalDate date = LocalDate.of(year, month, day);
            while (date.getDayOfWeek() != preferredDay) {
                date = date.minusDays(1);
            }
            return date;
        }
    }

    private static final Map<String, List<Holiday>> COUNTRY_HOLIDAYS = new HashMap<>();

    static {
        // Canada
        List<Holiday> canadaHolidays = new ArrayList<>();
        canadaHolidays.add(new FixedHoliday(Month.JANUARY, 1, "New Year's Day", true));
        canadaHolidays.add(new NthDayOfWeekHoliday(Month.FEBRUARY, DayOfWeek.MONDAY, 3, "Family Day", true));
        canadaHolidays.add(new EasterBasedHoliday("Good Friday", -2, true));
        canadaHolidays.add(new OnOrBeforeHoliday(Month.MAY, 25, DayOfWeek.MONDAY, "Victoria Day", true));
        canadaHolidays.add(new FixedHoliday(Month.JULY, 1, "Canada Day", true));
        canadaHolidays.add(new NthDayOfWeekHoliday(Month.AUGUST, DayOfWeek.MONDAY, 1, "Civic Holiday", true));
        canadaHolidays.add(new NthDayOfWeekHoliday(Month.SEPTEMBER, DayOfWeek.MONDAY, 1, "Labour Day", true));
        canadaHolidays.add(new FixedHoliday(Month.NOVEMBER, 11, "Remembrance Day", false));
        canadaHolidays.add(new NthDayOfWeekHoliday(Month.OCTOBER, DayOfWeek.MONDAY, 2, "Thanksgiving", true));
        canadaHolidays.add(new FixedHoliday(Month.DECEMBER, 25, "Christmas Day", true));
        canadaHolidays.add(new FixedHoliday(Month.DECEMBER, 26, "Boxing Day", true));
        canadaHolidays.add(new FixedHoliday(Month.OCTOBER, 31, "Halloween", false));
        canadaHolidays.add(new FixedHoliday(Month.FEBRUARY, 14, "Valentine's Day", false));
        COUNTRY_HOLIDAYS.put("Canada", canadaHolidays);

        // United States
        List<Holiday> usaHolidays = new ArrayList<>();
        usaHolidays.add(new FixedHoliday(Month.JANUARY, 1, "New Year's Day", true));
        usaHolidays.add(new NthDayOfWeekHoliday(Month.JANUARY, DayOfWeek.MONDAY, 3, "MLK Jr. Day", true));
        usaHolidays.add(new NthDayOfWeekHoliday(Month.FEBRUARY, DayOfWeek.MONDAY, 3, "Presidents' Day", true));
        usaHolidays.add(new EasterBasedHoliday("Good Friday", -2, true));
        usaHolidays.add(new NthDayOfWeekHoliday(Month.MAY, DayOfWeek.MONDAY, -1, "Memorial Day", true));
        usaHolidays.add(new FixedHoliday(Month.JUNE, 19, "Juneteenth", true));
        usaHolidays.add(new FixedHoliday(Month.JULY, 4, "Independence Day", true));
        usaHolidays.add(new NthDayOfWeekHoliday(Month.SEPTEMBER, DayOfWeek.MONDAY, 1, "Labor Day", true));
        usaHolidays.add(new NthDayOfWeekHoliday(Month.NOVEMBER, DayOfWeek.THURSDAY, 4, "Thanksgiving", true));
        usaHolidays.add(new FixedHoliday(Month.DECEMBER, 25, "Christmas", true));
        usaHolidays.add(new FixedHoliday(Month.OCTOBER, 31, "Halloween", false));
        usaHolidays.add(new FixedHoliday(Month.FEBRUARY, 14, "Valentine's Day", false));
        COUNTRY_HOLIDAYS.put("America", usaHolidays);

        // Korea
        List<Holiday> koreaHolidays = new ArrayList<>();
        koreaHolidays.add(new FixedHoliday(Month.JANUARY, 1, "New Year's Day", true));
        koreaHolidays.add(new FixedHoliday(Month.FEBRUARY, 9, "Seollal Eve", true));  // Approximate fixed date
        koreaHolidays.add(new FixedHoliday(Month.FEBRUARY, 10, "Seollal", true));  // Approximate fixed date
        koreaHolidays.add(new FixedHoliday(Month.FEBRUARY, 11, "Seollal Holiday", true));  // Approximate fixed date
        koreaHolidays.add(new FixedHoliday(Month.FEBRUARY, 12, "Additional Seollal Holiday", true));  // Approximate fixed date
        koreaHolidays.add(new FixedHoliday(Month.MARCH, 1, "Independence Movement Day", true));
        koreaHolidays.add(new FixedHoliday(Month.APRIL, 10, "Parliamentary Election Day", true));
        koreaHolidays.add(new FixedHoliday(Month.MAY, 5, "Children's Day", true));
        koreaHolidays.add(new FixedHoliday(Month.MAY, 15, "Buddha's Birthday", true));
        koreaHolidays.add(new FixedHoliday(Month.JUNE, 6, "Memorial Day", true));
        koreaHolidays.add(new FixedHoliday(Month.AUGUST, 15, "Liberation Day", true));
        koreaHolidays.add(new FixedHoliday(Month.SEPTEMBER, 16, "Chuseok Eve", true));  // Approximate fixed date
        koreaHolidays.add(new FixedHoliday(Month.SEPTEMBER, 17, "Chuseok", true));  // Approximate fixed date
        koreaHolidays.add(new FixedHoliday(Month.SEPTEMBER, 18, "Chuseok Holiday", true));  // Approximate fixed date
        koreaHolidays.add(new FixedHoliday(Month.OCTOBER, 3, "National Foundation Day", true));
        koreaHolidays.add(new FixedHoliday(Month.OCTOBER, 9, "Hangeul Day", true));
        koreaHolidays.add(new FixedHoliday(Month.DECEMBER, 25, "Christmas", true));
        koreaHolidays.add(new FixedHoliday(Month.OCTOBER, 31, "Halloween", false));
        koreaHolidays.add(new FixedHoliday(Month.FEBRUARY, 14, "Valentine's Day", false));
        koreaHolidays.add(new FixedHoliday(Month.MARCH, 14, "White Day", false));
        COUNTRY_HOLIDAYS.put("Korea", koreaHolidays);

        // Japan
        List<Holiday> japanHolidays = new ArrayList<>();
        japanHolidays.add(new FixedHoliday(Month.JANUARY, 1, "New Year's Day", true));
        japanHolidays.add(new NthDayOfWeekHoliday(Month.JANUARY, DayOfWeek.MONDAY, 2, "Coming of Age Day", true));
        japanHolidays.add(new FixedHoliday(Month.FEBRUARY, 11, "Foundation Day", true));
        japanHolidays.add(new FixedHoliday(Month.MARCH, 20, "Vernal Equinox Day", true));  // Approximate
        japanHolidays.add(new FixedHoliday(Month.APRIL, 29, "Showa Day", true));
        japanHolidays.add(new FixedHoliday(Month.MAY, 3, "Constitution Day", true));
        japanHolidays.add(new FixedHoliday(Month.MAY, 4, "Greenery Day", true));
        japanHolidays.add(new FixedHoliday(Month.MAY, 5, "Children's Day", true));
        japanHolidays.add(new NthDayOfWeekHoliday(Month.JULY, DayOfWeek.MONDAY, 3, "Marine Day", true));
        japanHolidays.add(new FixedHoliday(Month.AUGUST, 11, "Mountain Day", true));
        japanHolidays.add(new NthDayOfWeekHoliday(Month.SEPTEMBER, DayOfWeek.MONDAY, 3, "Respect for the Aged Day", true));
        japanHolidays.add(new FixedHoliday(Month.SEPTEMBER, 22, "Autumnal Equinox Day", true));  // Approximate
        japanHolidays.add(new NthDayOfWeekHoliday(Month.OCTOBER, DayOfWeek.MONDAY, 2, "Sports Day", true));
        japanHolidays.add(new FixedHoliday(Month.NOVEMBER, 3, "Culture Day", true));
        japanHolidays.add(new FixedHoliday(Month.NOVEMBER, 23, "Labor Thanksgiving Day", true));
        japanHolidays.add(new FixedHoliday(Month.OCTOBER, 31, "Halloween", false));
        japanHolidays.add(new FixedHoliday(Month.FEBRUARY, 14, "Valentine's Day", false));
        japanHolidays.add(new FixedHoliday(Month.MARCH, 14, "White Day", false));
        COUNTRY_HOLIDAYS.put("Japan", japanHolidays);

        // China
        List<Holiday> chinaHolidays = new ArrayList<>();
        chinaHolidays.add(new FixedHoliday(Month.JANUARY, 1, "New Year's Day", true));
        chinaHolidays.add(new FixedHoliday(Month.FEBRUARY, 10, "Spring Festival", true));  // Approximate fixed date
        chinaHolidays.add(new FixedHoliday(Month.APRIL, 5, "Qingming Festival", true));  // Approximate
        chinaHolidays.add(new FixedHoliday(Month.MAY, 1, "Labour Day", true));
        chinaHolidays.add(new FixedHoliday(Month.JUNE, 10, "Dragon Boat Festival", true));  // Approximate
        chinaHolidays.add(new FixedHoliday(Month.SEPTEMBER, 17, "Mid-Autumn Festival", true));  // Approximate
        chinaHolidays.add(new FixedHoliday(Month.OCTOBER, 1, "National Day", true));
        chinaHolidays.add(new FixedHoliday(Month.OCTOBER, 31, "Halloween", false));
        chinaHolidays.add(new FixedHoliday(Month.FEBRUARY, 14, "Valentine's Day", false));
        chinaHolidays.add(new FixedHoliday(Month.MARCH, 14, "White Day", false));
        COUNTRY_HOLIDAYS.put("China", chinaHolidays);

        // France
        List<Holiday> franceHolidays = new ArrayList<>();
        franceHolidays.add(new FixedHoliday(Month.JANUARY, 1, "New Year's Day", true));
        franceHolidays.add(new EasterBasedHoliday("Easter Monday", 1, true));
        franceHolidays.add(new FixedHoliday(Month.MAY, 1, "Labour Day", true));
        franceHolidays.add(new FixedHoliday(Month.MAY, 8, "Victory in Europe Day", true));
        franceHolidays.add(new EasterBasedHoliday("Ascension Day", 39, true));
        franceHolidays.add(new EasterBasedHoliday("Whit Monday", 50, true));
        franceHolidays.add(new FixedHoliday(Month.JULY, 14, "Bastille Day", true));
        franceHolidays.add(new FixedHoliday(Month.AUGUST, 15, "Assumption of Mary", true));
        franceHolidays.add(new FixedHoliday(Month.NOVEMBER, 1, "All Saints' Day", true));
        franceHolidays.add(new FixedHoliday(Month.NOVEMBER, 11, "Armistice Day", true));
        franceHolidays.add(new FixedHoliday(Month.DECEMBER, 25, "Christmas Day", true));
        franceHolidays.add(new FixedHoliday(Month.OCTOBER, 31, "Halloween", false));
        COUNTRY_HOLIDAYS.put("France", franceHolidays);

        // Mexico
        List<Holiday> mexicoHolidays = new ArrayList<>();
        mexicoHolidays.add(new FixedHoliday(Month.JANUARY, 1, "New Year's Day", true));
        mexicoHolidays.add(new FixedHoliday(Month.FEBRUARY, 5, "Constitution Day", true));
        mexicoHolidays.add(new FixedHoliday(Month.MARCH, 18, "Benito Juárez's Birthday", true));
        mexicoHolidays.add(new EasterBasedHoliday("Maundy Thursday", -3, true));
        mexicoHolidays.add(new EasterBasedHoliday("Good Friday", -2, true));
        mexicoHolidays.add(new FixedHoliday(Month.MAY, 1, "Labour Day", true));
        mexicoHolidays.add(new FixedHoliday(Month.SEPTEMBER, 16, "Independence Day", true));
        mexicoHolidays.add(new FixedHoliday(Month.NOVEMBER, 18, "Revolution Day", true));
        mexicoHolidays.add(new FixedHoliday(Month.DECEMBER, 25, "Christmas", true));
        mexicoHolidays.add(new FixedHoliday(Month.OCTOBER, 31, "Halloween", false));
        COUNTRY_HOLIDAYS.put("Mexico", mexicoHolidays);

        // Spain
        List<Holiday> spainHolidays = new ArrayList<>();
        spainHolidays.add(new FixedHoliday(Month.JANUARY, 1, "New Year's Day", true));
        spainHolidays.add(new FixedHoliday(Month.JANUARY, 6, "Epiphany", true));
        spainHolidays.add(new EasterBasedHoliday("Good Friday", -2, true));
        spainHolidays.add(new FixedHoliday(Month.MAY, 1, "Labour Day", true));
        spainHolidays.add(new FixedHoliday(Month.AUGUST, 15, "Assumption of Mary", true));
        spainHolidays.add(new FixedHoliday(Month.OCTOBER, 12, "Hispanic Day", true));
        spainHolidays.add(new FixedHoliday(Month.NOVEMBER, 1, "All Saints' Day", true));
        spainHolidays.add(new FixedHoliday(Month.DECEMBER, 6, "Constitution Day", true));
        spainHolidays.add(new FixedHoliday(Month.DECEMBER, 25, "Christmas Day", true));
        spainHolidays.add(new FixedHoliday(Month.OCTOBER, 31, "Halloween", false));
        COUNTRY_HOLIDAYS.put("Spain", spainHolidays);

        // Germany
        List<Holiday> germanyHolidays = new ArrayList<>();
        germanyHolidays.add(new FixedHoliday(Month.JANUARY, 1, "New Year's Day", true));
        germanyHolidays.add(new EasterBasedHoliday("Good Friday", -2, true));
        germanyHolidays.add(new EasterBasedHoliday("Easter Sunday", 0, true));
        germanyHolidays.add(new EasterBasedHoliday("Easter Monday", 1, true));
        germanyHolidays.add(new FixedHoliday(Month.MAY, 1, "Labour Day", true));
        germanyHolidays.add(new EasterBasedHoliday("Ascension Day", 39, true));
        germanyHolidays.add(new EasterBasedHoliday("Whit Sunday", 49, true));
        germanyHolidays.add(new EasterBasedHoliday("Corpus Christi", 60, true));
        germanyHolidays.add(new FixedHoliday(Month.OCTOBER, 3, "German Unity Day", true));
        germanyHolidays.add(new FixedHoliday(Month.OCTOBER, 31, "Halloween", false));
        germanyHolidays.add(new FixedHoliday(Month.DECEMBER, 25, "Christmas Day", true));
        germanyHolidays.add(new FixedHoliday(Month.DECEMBER, 26, "Boxing Day", true));
        COUNTRY_HOLIDAYS.put("Germany", germanyHolidays);
    }

    /**
     * Calculate Easter Sunday for a given year using Computus algorithm.
     * Based on the Meeus Julian algorithm.
     */
    private static LocalDate calculateEaster(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(year, month, day);
    }

    public static Holiday getHolidayForDate(String country, LocalDate date) {
        List<Holiday> holidays = COUNTRY_HOLIDAYS.get(country);
        if (holidays != null) {
            for (Holiday holiday : holidays) {
                LocalDate holidayDate = holiday.getDateForYear(date.getYear());
                if (holidayDate != null && holidayDate.equals(date)) {
                    return holiday;
                }
            }
        }
        return null;
    }

    public static boolean isNoWorkDay(String country, LocalDate date) {
        Holiday holiday = getHolidayForDate(country, date);
        return holiday != null && holiday.isNoWorkDay();
    }

    public static String[] getCountriesArray() {
        return new String[]{"Canada", "America", "Korea", "Japan", "China", "France", "Mexico", "Spain", "Germany"};
    }
}
