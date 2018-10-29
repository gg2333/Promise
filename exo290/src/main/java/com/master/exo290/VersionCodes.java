package com.master.exo290;

import android.os.Build;

public class VersionCodes {


    public static boolean ge21() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean ge23() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean g23() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.M;
    }

    public static boolean le23() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.M;
    }


}
