package com.macck209.puzzlo2;

public class Record {
    private String stringValue;
    private int intValue;

    public Record(String stringValue, int intValue) {
        this.stringValue = stringValue;
        this.intValue = intValue;
    }

    public String getString() {
        return stringValue;
    }

    public int getInt() {
        return intValue;
    }
}

