package apps.in.hackerarticle.data;

import android.content.Context;
import android.content.res.Resources;

import java.util.Calendar;
import java.util.Date;

import apps.in.hackerarticle.R;

public class AppUtils {

    public static final String SHARED_PREFS = "HAApp";
    public static final String SHARED_SYNC = "synApp";

    public static final String ACTION = "action";
    public static final int ACTION_VIEW = 555;

    public static String getUniqueId() {
        Long tsLong = System.currentTimeMillis() / 1000;
        return tsLong.toString();
    }

    public static String timeAgo(String timestamp, Context context) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(timestamp) * 1000);

            long diff = new Date().getTime() - calendar.getTime().getTime();

            Resources r = context.getResources();

            String prefix = r.getString(R.string.time_ago_prefix);
            String suffix = r.getString(R.string.time_ago_suffix);

            double seconds = Math.abs(diff) / 1000;
            double minutes = seconds / 60;
            double hours = minutes / 60;
            double days = hours / 24;
            double years = days / 365;

            String words;

            if (seconds < 45) {
                words = r.getString(R.string.time_ago_seconds, Math.round(seconds));
            } else if (seconds < 90) {
                words = r.getString(R.string.time_ago_minute, 1);
            } else if (minutes < 45) {
                words = r.getString(R.string.time_ago_minutes, Math.round(minutes));
            } else if (minutes < 90) {
                words = r.getString(R.string.time_ago_hour, 1);
            } else if (hours < 24) {
                words = r.getString(R.string.time_ago_hours, Math.round(hours));
            } else if (hours < 42) {
                words = r.getString(R.string.time_ago_day, 1);
            } else if (days < 30) {
                words = r.getString(R.string.time_ago_days, Math.round(days));
            } else if (days < 45) {
                words = r.getString(R.string.time_ago_month, 1);
            } else if (days < 365) {
                words = r.getString(R.string.time_ago_months, Math.round(days / 30));
            } else if (years < 1.5) {
                words = r.getString(R.string.time_ago_year, 1);
            } else {
                words = r.getString(R.string.time_ago_years, Math.round(years));
            }

            StringBuilder sb = new StringBuilder();

            if (prefix != null && prefix.length() > 0) {
                sb.append(prefix).append(" ");
            }

            sb.append(words);

            if (suffix != null && suffix.length() > 0) {
                sb.append(" ").append(suffix);
            }

            return sb.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
