package com.itcs.aihome;

public class StatusModel {
    private String relay, idcontroller, blynkkey;

    public StatusModel(String relay, String idcontroller, String blynkkey) {
        this.relay = relay;
        this.idcontroller = idcontroller;
        this.blynkkey = blynkkey;
    }

    public String getRelay() {
        return relay;
    }

    public String getIdcontroller() {
        return idcontroller;
    }

    public void setRelay(String relay) {
        this.relay = relay;
    }

    public void setIdcontroller(String idcontroller) {
        this.idcontroller = idcontroller;
    }

    public String getBlynkkey() {
        return blynkkey;
    }

    public void setBlynkkey(String blynkkey) {
        this.blynkkey = blynkkey;
    }
}
