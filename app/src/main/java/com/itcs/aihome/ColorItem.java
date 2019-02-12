package com.itcs.aihome;

public class ColorItem {
    private int color;
    private int flag;
    public ColorItem(int flag, int color) {
        this.flag = flag;
        this.color = color;
    }

    public int getFlag() {
        return flag;
    }

    public int getColor() {
        return color;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
