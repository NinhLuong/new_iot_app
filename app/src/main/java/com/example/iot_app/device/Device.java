package com.example.iot_app.device;



import java.util.ArrayList;

public class Device {
    private int idDevice, intensity;
    private String device, mode;
    private String detail;
    private String category;
    private String nameRoom;
    private boolean swithStatus, autoStatus;


// Fan device
// Lamp device
    public Device(int idDevice, String device, String detail, boolean swithStatus,int  intensity, String category, String nameRoom) {
        this.idDevice = idDevice;
        this.device = device;
        this.detail = detail;
        this.swithStatus = swithStatus;
        this.nameRoom = nameRoom;
        this.category = category;
        this.intensity = intensity;
    }

    //    Air conditon device
    public Device(int idDevice, String device, String detail, boolean swithStatus,int intensity, String mode, boolean autoStatus ,String category, String nameRoom) {
        this.idDevice = idDevice;
        this.device = device;
        this.detail = detail;
        this.swithStatus = swithStatus;
        this.nameRoom = nameRoom;
        this.category = category;
        this.intensity = intensity;
        this.mode = mode;
        this.autoStatus = autoStatus;
    }
    public boolean isSwithStatus() {
        return this.swithStatus;
    }

    public void setSwithStatus(final boolean swithStatus) {
        this.swithStatus = swithStatus;
    }

    public int getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(int idDevice) {
        this.idDevice = idDevice;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }


    public void setCategory(final String category) {
        this.category = category;
    }

    public String getCategory() {
        return this.category;
    }

//    public int getDeviceCount() {
//        return deviceRoomArrayList.size();
//    }

    public String getNameRoom() {
        return this.nameRoom;
    }


    public void setNameRoom(final String nameRoom) {
        this.nameRoom = nameRoom;
    }

    public int getIntensity() {
        return this.intensity;
    }

    public String getMode() {
        return this.mode;
    }

    public boolean isAutoStatus() {
        return this.autoStatus;
    }

    public void setIntensity(final int intensity) {
        this.intensity = intensity;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

    public void setAutoStatus(final boolean autoStatus) {
        this.autoStatus = autoStatus;
    }
}


