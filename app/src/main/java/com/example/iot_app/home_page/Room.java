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


    private ArrayList<Device> devices;

    // This is a public constructor for Room.
    // It initializes a new Room object with resource ID, room name, and device name.
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

    public ArrayList<Device> getDevices() {
        return devices;
    }
    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }
    public int getDeviceCount() {
        return devices.size();
    }
}
