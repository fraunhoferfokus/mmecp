package de.fhg.fokus.streetlife.mmecp.containers.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by csc on 02.10.2015.
 */
public class Color {

    int red, green, blue;
    float alpha;

    public Color(int r, int g, int b) {
        this(r, g, b, 1.0f);
    }

    public Color(int r, int g, int b, float a) {
        this.red = r;
        this.green = g;
        this.blue = b;
        this.alpha = a;
    }

    public Color() {
        this(0, 0, 0, 1.0f);
    }

    // support "aabbcc" and "#aabbcc"
    public Color(String colorString) {
        this(0, 0, 0, 1.0f);
        if (colorString == null || colorString.isEmpty()) {return;}
        if (colorString.length() != 6 && colorString.length() != 7) {return;}

        String input = colorString.toLowerCase();
        if (input.charAt(0) == '#') {
            input = input.substring(1); // skip a '#' at the beginning
        }
        // assume that format is now: rrggbb
        this.red = 16 * hex2dez(input.charAt(0)) + hex2dez(input.charAt(1));
        this.green = 16 * hex2dez(input.charAt(2)) + hex2dez(input.charAt(3));
        this.blue = 16 * hex2dez(input.charAt(4)) + hex2dez(input.charAt(5));
    }

    public int getRed() {return red;}
    public int getGreen() {return green;}
    public int getBlue() {return blue;}
    public float getAlpha() {return alpha;}

    public void setColor(Color col) {
        if (col == null) {return;}
        this.red = col.getRed();
        this.green = col.getGreen();
        this.blue = col.getBlue();
        this.alpha = col.getAlpha();
    }

    public void setAlpha(float f) {this.alpha = f;}
    public Color withAlpha(float f) {this.alpha = f; return this;}

    public void setMix(Color col_min, Color col_med, Color col_max, float mix) {
        if (col_min == null || col_med == null || col_max == null) {return;}
        if (mix == 0.5f) {
            setMix(col_min, col_max, mix);
        }
        if (mix < 0.5f) {
            setMix(col_min, col_med, mix * 2);
        }
        if (mix > 0.5f) {
            setMix(col_med, col_max, (mix - 0.5f) * 2);
        }
    }

    public void setMix(Color col1, Color col2, float mix) {
        if (col1 == null || col2 == null) {return;}
        if (mix < 0f) { mix = 0f; }
        if (mix > 1f) { mix = 1f; }
        final int AMP = 65536;
        final int mixer = (int) (mix * AMP);
        red = ((AMP - mixer) * col1.getRed() + mixer * col2.getRed()) >> 16;
        green = ((AMP - mixer) * col1.getGreen() + mixer * col2.getGreen()) >> 16;
        blue = ((AMP - mixer) * col1.getBlue() + mixer * col2.getBlue()) >> 16;
    }

    public static Color mixColor(Color col1, Color col2, float mix) {
        if (col1 == null || col2 == null) {return null;}
        Color col = new Color();
        col.setMix(col1, col2, mix);
        return col;
    }

    public Color random() {
        final int numcolors = 256;
        this.red = (int) (Math.random() * numcolors);
        this.green = (int) (Math.random() * numcolors);
        this.blue = (int) (Math.random() * numcolors);
        this.alpha = 0.9f;
        return this;
    }

    public final static Color RED = new Color(255, 0, 0);
    public final static Color GREEN = new Color(0, 255, 0);
    public final static Color BLUE = new Color(0, 0, 255);
    public final static Color WHITE = new Color(255, 255, 255);
    public final static Color YELLOW = new Color(255, 255, 0);
    public final static Color BLACK = new Color(0, 0, 0);

    public static final int VMZ_COLOR_SCHEME = 0;
    private static final List<Color> colorScheme_vmz = new ArrayList<Color>() {{
        add(Color.GREEN);
        add(new Color(176, 255, 176));
        add(Color.YELLOW);
        add(new Color(240, 180, 0));
        add(new Color(255, 128, 0));
        add(Color.RED);
    }};

    public static final int FOKUS_COLOR_SCHEME = 1;
    private static final List<Color> colorScheme_fokus = new ArrayList<Color>() {{
        add(new Color(255, 255, 0)); // pure yellow
        add(new Color(255, 204, 0));
        add(new Color(255, 153, 0));
        add(new Color(255, 93, 0));
        add(new Color(255, 21, 0));
        add(new Color(151, 0, 225)); // violet
    }};

    public static final int FOKUS_COLOR_SCHEME_SEVERE = 2;
    private static final List<Color> colorScheme_fokus_severe = new ArrayList<Color>() {{
        add(new Color(255, 153, 0)); // orange
        add(new Color(255, 93, 0));
        add(new Color(255, 21, 0));
        add(new Color(151, 0, 225)); // violet
        add(new Color(100, 0, 75)); // violet
        add(Color.BLACK);
    }};

    private static final List<List<Color>> colorSchemes = new ArrayList<List<Color>>() {{
        add(colorScheme_vmz);
        add(colorScheme_fokus);
        add(colorScheme_fokus_severe);
    }};


    public static Color LookupColor(int value, int scheme) {
        if (value < 0) {return Color.WHITE;}
        if (scheme < 0 || scheme >= colorSchemes.size()) {return null;}
        // else
        List<Color> cols = colorSchemes.get(scheme);
        int index = Math.min((value-1)/2, cols.size()-1);
        return cols.get(index);
    }

    public Color colorize(int value, int scheme) {
        Color col = this.LookupColor(value, scheme);
        if (col != null) {
            this.setColor(col);
        }
        return this;
    }

    public static Color LookupColor(Long val, int scheme) {return LookupColor(val.intValue(), scheme);}
    public Color colorize(Long val, int scheme) {return colorize(val.intValue(), scheme);}

    public String toString() {return String.format("#%02x%02x%02x", getRed(), getGreen(), getBlue());}

    private int hex2dez(char ch) {
        switch (ch) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'a':
                return 10;
            case 'b':
                return 11;
            case 'c':
                return 12;
            case 'd':
                return 13;
            case 'e':
                return 14;
            case 'f':
                return 15;
            default:
                return 0;
        }
    }

    private final static Color BlueLine = new Color("#00B4FF");
    private final static Color RedLine = new Color("#E65100");
    private final static Color YellowLine = new Color("#FFD600");
    private final static Color GreenLine = new Color("#44A508");
    public final static Color DarkPink = new Color("#BA63A0");
    private final static Color LtBlueLine = new Color(0, 140, 186);
    private final static Color GreyLine = new Color(144, 163, 169);
    private final static Color OtherLine = new Color("#984C0B");


    public final static Map<String, Color> simpleTrafficColorMap = new HashMap<String, Color>() {{
        // put("Walk", LtBlueLine);
        put("Bike", LtBlueLine);
        put("OwnBike", LtBlueLine);
        put("BikeSharing", LtBlueLine);
        put("Other", OtherLine);
        put("n/a", new Color("#222222"));
    }};
    public final static Map<String, Color> simpleTrafficColorMapTracked = new HashMap<String, Color>() {{
        // put("Walk", LtBlueLine);
        put("Bike", Color.YELLOW);
        put("OwnBike", Color.YELLOW);
        put("BikeSharing", Color.YELLOW);
        put("Other", Color.BLACK);
    }};

    public final static Map<String, Color> detailedTrafficColorMap = new HashMap<String, Color>() {{
        put("Walk", GreenLine);
        put("Bike", GreenLine);
        put("OwnBike", GreenLine);
        put("BikeSharing", BlueLine);
        put("CityTrain", BlueLine);
        put("Car", RedLine);
        put("OwnCar", RedLine);
        put("CarSharing", BlueLine);
        put("Taxi", RedLine);
        put("Metro", BlueLine);
        put("Train", BlueLine);
        put("Bus", BlueLine);
        put("Tram", BlueLine);
        put("RegioTrain", BlueLine);
        put("Ferry", BlueLine);
        put("Other", GreyLine);
    }};

    public static Color forTrafficType(String trafficType) {return forTrafficType(trafficType, /*mode*/1);}

    public static Color forTrafficType(String trafficType, int mode) {
        return forTrafficType(trafficType, mode, /*modifyForTracked*/false);
    }

    public static Color forTrafficType(String trafficType, int mode, boolean detailed) {
        if (trafficType == null) {return null;}
        if (trafficType.isEmpty()) {trafficType="Other";}
        String tt = trafficType.trim();
        if (tt == null || tt.isEmpty()) {
            return Color.BLACK;
        } else {
            Map<String, Color> colorMap;
            switch (mode) {
                case 1:
                    colorMap = simpleTrafficColorMap;
                    // if (detailed) {colorMap = simpleTrafficColorMapTracked;}
                    break;
                case 2:
                default:
                    colorMap = detailedTrafficColorMap;
            }
            Color col = colorMap.get(tt);
            if (col == null) {col = colorMap.get("Other");}
            if (col == null) {col = Color.BLACK;}
            return col;
        }
    }


}
