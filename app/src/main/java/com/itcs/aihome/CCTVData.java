package com.itcs.aihome;

import android.net.Uri;

public class CCTVData {

    private String videoUri;
    private String idcctv;
    private String cctvname;

    public CCTVData(String videoUri, String idcctv, String cctvname) {
        this.videoUri = videoUri;
        this.idcctv = idcctv;
        this.cctvname = cctvname;
    }

    public String getIdcctv() {
        return idcctv;
    }

    public Uri getVideoUri() {
        return Uri.parse(videoUri);
    }

    public void setIdcctv(String idcctv) {
        this.idcctv = idcctv;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public String getCctvname() {
        return cctvname;
    }

    public void setCctvname(String cctvname) {
        this.cctvname = cctvname;
    }
}
