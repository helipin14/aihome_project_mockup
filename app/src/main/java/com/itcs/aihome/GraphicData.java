package com.itcs.aihome;

import java.util.List;

public class GraphicData {
    public Double kwh;
    public String tanggal;
    public GraphicData(Double kwh, String tanggal) {
        this.kwh = kwh;
        this.tanggal = tanggal;
    }

    public Double getKwh() {
        return kwh;
    }

    public void setKwh(Double kwh) {
        this.kwh = kwh;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
