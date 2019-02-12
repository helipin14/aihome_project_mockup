package com.itcs.aihome;

public class GroupItem {
    private String name;
    private String usershortlist;
    private String idgroup;
    public GroupItem(String name, String usershortlist, String idgroup) {
        this.name = name;
        this.usershortlist = usershortlist;
        this.idgroup = idgroup;
    }

    public String getName() {
        return name;
    }

    public String getUsershortlist() {
        return usershortlist;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsershortlist(String usershortlist) {
        this.usershortlist = usershortlist;
    }

    public String getIdgroup() {
        return idgroup;
    }

    public void setIdgroup(String idgroup) {
        this.idgroup = idgroup;
    }
}
