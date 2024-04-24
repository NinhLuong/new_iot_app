package com.example.iot_app.home_page;

import com.example.iot_app.device.Device;

import java.util.ArrayList;

public class Room {
    // This is a public constructor for Room.
    private int resourceId;
    private String room;
    private String device;
    // A private variable for a list of Device objects.
    private String temp;
    private String hum;
    private String gas;

    private ArrayList<Device> devices;

    public Room() {

    }
    public Room(String temp, String hum, String gas, ArrayList<Device> devices) {
        this.temp = temp;
        this.hum = hum;
        this.gas = gas;
        this.devices = devices;
    }
    public Room(int resourceId, String room, String device) {
        this.resourceId = resourceId;
        this.room = room;
        this.device = device;
        this.devices = new ArrayList<>();
    }

    // create getter setter
    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getTemp() {
        return this.temp;
    }

    public String getHum() {
        return this.hum;
    }

    public void setTemp(final String temp) {
        this.temp = temp;
    }

    public void setHum(final String hum) {
        this.hum = hum;
    }

    public String getGas() {
        return this.gas;
    }

    public void setGas(final String gas) {
        this.gas = gas;
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }
    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }

    public int getDeviceCount() {
        if (devices == null) {
            return 0;
        } else {
            return devices.size();
        }
    }
}
