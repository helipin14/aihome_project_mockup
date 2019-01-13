package com.itcs.aihome;

public class Data {
    private String status, iddevice;
    public Data(String status, String iddevice) {
        this.status = status;
        this.iddevice = iddevice;
    }

    public String getStatus() {
        return status;
    }

    public String getIddevice() {
        return iddevice;
    }
}
