package com.itcs.aihome;

import android.media.Image;

import java.io.Serializable;

public class Model {
    private int image;
    private int status;
    private String devices;
    private String tag;
    private String iddevice;
    private int flag = 0;
    private String idaccess;
    private String idcontroller;

    public Model(int image, int status, String devices, String tag, String iddevice, int flag, String idaccess, String idcontroller) {
        this.image = image;
        this.status = status;
        this.devices = devices;
        this.tag = tag;
        this.iddevice = iddevice;
        this.flag = flag;
        this.idaccess = idaccess;
        this.idcontroller = idcontroller;
    }

    public int getImage() {
        return image;
    }

    public int getStatus() {
        return status;
    }

    public String getDevices() {
        return devices;
    }

    public String getTag() { return tag; }

    public void setImage(int image) {
        this.image = image;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setDevices(String devices) {
        this.devices = devices;
    }

    public String getIddevice() { return iddevice; }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getIdaccess() {
        return idaccess;
    }

    public void setIdaccess(String idaccess) {
        this.idaccess = idaccess;
    }

    public String getIdcontroller() {
        return idcontroller;
    }

    public void setIdcontroller(String idcontroller) {
        this.idcontroller = idcontroller;
    }
}
