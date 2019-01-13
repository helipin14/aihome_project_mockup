package com.itcs.aihome;

public class KWHModel {
    private String name;
    private String value;
    private String biaya;
    public KWHModel(String name, String value, String biaya) {
        this.name = name;
        this.value = value;
        this.biaya = biaya;
    }

    public String getBiaya() {
        return biaya;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setBiaya(String biaya) {
        this.biaya = biaya;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
