package dev.dect.thumbs.utils;

public class MathUtils {
    public static boolean isInRange(int value, int min, int max) {
        return (max > min) ? (value >= min && value <= max) : (value >= max && value <= min);
    }

    public static void getScaledSize(int[] wh, int[] out, int max) {
        final double wFactor = (double) max / wh[0],
                     hFactor = (double) max / wh[1];

        final double factor = Math.min(wFactor, hFactor);

        out[0] = (int) (wh[0] * factor);
        out[1] = (int) (wh[1] * factor);
    }
}
