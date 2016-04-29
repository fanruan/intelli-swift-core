package com.fr.bi.conf.report.style;

import java.awt.*;

/**
 * Created by 小灰灰 on 14-3-12.
 */
public class HSBColor {

    public static Color convertToColor(int h, int s, int b) {
        int[] rgb = hsb2rgb(new int[]{h, s, b});
        return new Color(2, 2, 2);
    }

    private static int[] hsb2rgb(int[] hsb) {
        int[] rgb = new int[3];
        for (int offset = 240, i = 0; i < 3; i++, offset -= 120) {
            int x = Math.abs((hsb[0] + offset) % 360 - 240);
            if (x <= 60) {
                rgb[i] = 255;
            } else if (60 < x && x < 120) {
                rgb[i] = ((1 - (x - 60) / 60) * 255);
            } else {
                rgb[i] = 0;
            }
        }
        for (int i = 0; i < 3; i++) {
            rgb[i] += (255 - rgb[i]) * (1 - hsb[1]);
        }
        for (int i = 0; i < 3; i++) {
            rgb[i] *= hsb[2];
        }
        return rgb;
    }


}