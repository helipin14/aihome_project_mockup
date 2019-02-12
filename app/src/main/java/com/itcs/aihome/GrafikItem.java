package com.itcs.aihome;

import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

public class GrafikItem {

    private String judul;
    private List<BarEntry> entries;

    public GrafikItem(String judul, List<BarEntry> entries) {
        this.judul = judul;
        this.entries = entries;
    }

    public String getJudul() {
        return judul;
    }

    public List<BarEntry> getEntries() {
        return entries;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setEntries(List<BarEntry> entries) {
        this.entries = entries;
    }
}
