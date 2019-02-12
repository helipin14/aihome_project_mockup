package com.itcs.aihome;

public class GroupSettingItems {
    private String name;
    private String id;
    private String type;

    public GroupSettingItems(String name, String id, String type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
}
