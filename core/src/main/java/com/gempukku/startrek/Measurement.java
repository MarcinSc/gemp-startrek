package com.gempukku.startrek;

public class Measurement {
    private static final int pixelWidth = 512;
    private static final int pixelHeight = 512;

    private static final float gameWidth = 0.497570f;
    private static final float gameHeight = 0.497570f;

    private static final int textPadding = 5;

    public static void main(String[] args) {
        calculate("Affiliation icon", false, 18, 18, 94, 94);
        calculate("Mission image", false, 18, 87, 491, 381);
        calculate("Noun image", false, 18, 87, 492, 394);
        calculate("Verb image", false, 18, 87, 493, 393);

        calculate("Icon1 image", false, 18, 381, 18 + 56, 381 + 56);
        calculate("Icon2 image", false, 18 + 56 + 4, 381, 18 + 56 + 4 + 56, 381 + 56);
        calculate("Icon3 image", false, 18, 381 + 56 + 4, 18 + 56, 381 + 56 + 4 + 56);
        calculate("Icon4 image", false, 18 + 56 + 4, 381 + 56 + 4, 18 + 56 + 4 + 56, 381 + 56 + 4 + 56);

        calculate("Title small", true, 114, 17, 494, 78);
        calculate("Mission points", true, 18, 361, 78, 421);
        calculate("Mission quadrant", true, 35, 432, 95, 492);
        calculate("Mission affiliation", true, 114, 395, 494, 493);
        calculate("Noun stats", true, 271, 384, 494, 493);
        calculate("Verb title", true, 17, 17, 494, 78);
    }

    private static void calculate(String type, boolean text, int x1, int y1, int x2, int y2) {
        if (text) {
            x1 += textPadding;
            y1 += textPadding;
            x2 -= textPadding;
            y2 -= textPadding;
        }
        float centerX = gameWidth * ((x1 + x2) / 2f - pixelWidth / 2f) / pixelWidth;
        float centerY = gameHeight * ((y1 + y2) / 2f - pixelHeight / 2f) / pixelHeight;

        float width = 1f * (x2 - x1) / pixelWidth * gameWidth;
        float height = 1f * (y2 - y1) / pixelHeight * gameHeight;

        System.out.println(type);
        System.out.println("Center: " + centerX + ", " + centerY);
        System.out.println("Size: " + width + ", " + height);
    }
}
