package dev.dect.thumbs.utils;

public class ConverterUtils {
    public static class Color {
        public static int hexColorToInt(String hex) {
            return argbColorToInt(hexColorToArgb(hex));
        }

        public static String intColorToHex(int i, int a) {
            final int r = android.graphics.Color.red(i),
                    g = android.graphics.Color.green(i),
                    b = android.graphics.Color.blue(i);

            final String rH = Integer.toHexString(r),
                    gH = Integer.toHexString(g),
                    bH = Integer.toHexString(b),
                    aH = Integer.toHexString(a);

            return "#" + FormatterUtils.hexIndividualNumber(rH) + FormatterUtils.hexIndividualNumber(gH) + FormatterUtils.hexIndividualNumber(bH) + FormatterUtils.hexIndividualNumber(aH);
        }

        public static int argbColorToInt(int[] argb) {
            return android.graphics.Color.argb(argb[0], argb[1], argb[2], argb[3]);
        }

        public static int[] hexColorToArgb(String hex) {
            final int color = android.graphics.Color.parseColor(hex.substring(0, 7)),
                      alpha = Integer.valueOf(hex.substring(7), 16);

            return new int[] {alpha, android.graphics.Color.red(color), android.graphics.Color.green(color), android.graphics.Color.blue(color)};
        }
    }
}
