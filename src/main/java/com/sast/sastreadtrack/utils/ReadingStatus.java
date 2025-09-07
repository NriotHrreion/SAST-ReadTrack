package com.sast.sastreadtrack.utils;

public class ReadingStatus {
    public static final String PLANNED = "0";
    public static final String READING = "1";
    public static final String FINISHED = "2";

    public static boolean isValid(String status) {
        return status.equals(PLANNED) || status.equals(READING) || status.equals(FINISHED);
    }
}
