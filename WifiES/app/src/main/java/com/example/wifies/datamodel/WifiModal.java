package com.example.wifies.datamodel;

public class WifiModal {
    private String ssid;
    private String uuid;
    private String user_id;
    private String password;
    private String ect;
    private String krack;

    public WifiModal(String ssid, String uuid, String user_id, String password, String ect, String krack)
    {
        this.setSsid(ssid);
        this.setUuid(uuid);
        this.setUser_id(user_id);
        this.setPassword(password);
        this.setEct(ect);
        this.setKrack(krack);
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEct() {
        return ect;
    }

    public void setEct(String ect) {
        this.ect = ect;
    }

    public String getKrack() {
        return krack;
    }

    public void setKrack(String krack) {
        this.krack = krack;
    }
}
