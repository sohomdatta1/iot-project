package com.example.wifies.datamodel;

public class DeviceModal {
    private String uuid;
    private String name;
    private String user_id;

    public DeviceModal(String uuid, String name, String user_id)
    {
        this.uuid = uuid;
        this.name = name;
        this.user_id = user_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
