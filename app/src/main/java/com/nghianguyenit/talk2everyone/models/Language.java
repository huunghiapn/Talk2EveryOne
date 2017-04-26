package com.nghianguyenit.talk2everyone.models;

import java.util.Locale;

/**
 * Created by NghiaNH on 4/21/2017.
 */

public class Language {


    public static final Locale VN = new Locale("vi", "VN");
    public static final Locale TH = new Locale("th", "TH");
    public static final Locale JP = Locale.JAPAN;
    public static final Locale EN = Locale.US;
    public static final Locale CN = Locale.CHINA;
    public static final Locale TW = Locale.TAIWAN;
    public static final Locale ML = new Locale("ms", "MY");
    public static final Locale KR = Locale.KOREA;
    public static final Locale CA = Locale.CANADA;
    public static final Locale FR = Locale.FRANCE;

    private String label;
    private Locale code;

    public Language(String label, Locale code) {
        this.label = label;
        this.code = code;
    }

    public Language() {

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Locale getCode() {
        return code;
    }

    public void setCode(Locale code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return label;
    }
}
