package com.itcs.aihome;

import java.io.Serializable;
import java.util.List;

public class RoomModel implements Serializable {

    String namaruangan;
    List<Model> models;

    public RoomModel(String namaruangan, List<Model> models) {
        this.namaruangan = namaruangan;
        this.models = models;
    }

    public int getCount() {
        return models.size();
    }

    public String getNamaruangan() {
        return namaruangan;
    }

    public List<Model> getModels() {
        return models;
    }

    public void setNamaRuangan(String namaruangan) {
        this.namaruangan = namaruangan;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }


}
