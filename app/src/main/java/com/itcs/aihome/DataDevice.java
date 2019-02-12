package com.itcs.aihome;

public class DataDevice {
    private String name;
    private String iddevice;
    private int flag = 0;
    public DataDevice(String name, String iddevice, int flag) {
        this.name = name;
        this.iddevice = iddevice;
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public String getIddevice() {
        return iddevice;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIddevice(String iddevice) {
        this.iddevice = iddevice;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
