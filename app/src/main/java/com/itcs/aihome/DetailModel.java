package com.itcs.aihome;

public class DetailModel {
    private String judul;
    private String detail;
    public DetailModel(String judul, String detail) {
        this.judul = judul;
        this.detail = detail;
    }

    public String getJudul() {
        return judul;
    }

    public String getDetail() {
        return detail;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
