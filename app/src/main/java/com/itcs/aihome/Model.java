package com.itcs.aihome;

import android.media.Image;

import java.io.Serializable;

public class Model implements Serializable {
    private int image;
    private String status;
    private String devices;
    private String baseUrl;
    private String tag;
    private String iddevice;
    private String blynkurl, pin;

    public Model(int image, String status, String devices, String baseUrl, String tag, String iddevice, String blynkurl, String pin) {
        this.image = image;
        this.status = status;
        this.devices = devices;
        this.baseUrl = baseUrl;
        this.tag = tag;
        this.iddevice = iddevice;
        this.blynkurl = blynkurl;
        this.pin = pin;
    }

    public int getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }

    public String getDevices() {
        return devices;
    }

    public String getTag() { return tag; }

    public void setImage(int image) {
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDevices(String devices) {
        this.devices = devices;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getBlynkurl() { return blynkurl; }

    public String getIddevice() { return iddevice; }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
