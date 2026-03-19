package dev.dect.thumbs.utils;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.Locale;

public class FormatterUtils {
    public static String hexIndividualNumber(String n) {
        return (n.length() == 1 ? "0" + n : n).toUpperCase(Locale.ROOT);
    }

    public static class DateTime {
        public static String timeInMillis(long millis) {
            return timeInMicro(millis * 1000L);
        }

        public static String timeInMicro(long micro) {
            final long duration = micro / 1000000,
                       hours = duration / 3600,
                       minutes = (duration / 60) - (hours * 60),
                       seconds = duration - (hours * 3600) - (minutes * 60);

            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        }
    }

    public static class File {
        private static final String TAG = FormatterUtils.class.getSimpleName() + "." + File.class.getSimpleName();

        public static String toReadableSize(long l) {
            try {
                final int i = l == 0 ? 0 : (int) Math.floor(Math.log(l) / Math.log(1024));

                final double result = (l / Math.pow(1024, i));

                final DecimalFormat format = new DecimalFormat("0.0");

                final String s = format.format(result);

                return (s.endsWith(".0") ? s.replaceFirst(".0", "") : s) + " " + new String[]{"B", "kB", "MB", "GB", "TB"}[i];
            } catch (Exception e) {
                Log.e(TAG, "formatFileSize: " + e.getMessage());

                return "?";
            }
        }
    }
}
