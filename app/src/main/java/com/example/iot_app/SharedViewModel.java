package com.example.iot_app;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.iot_app.device.Device;
import com.example.iot_app.home_page.Room;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {


    private final MutableLiveData<ArrayList<Room>> rooms = new MutableLiveData<>(new ArrayList<>());

    public LiveData<ArrayList<Room>> getRooms() {
        return rooms;
    }

    public void addRoom(Room room) {
        ArrayList<Room> currentRooms = rooms.getValue();
        currentRooms.add(room);
        rooms.setValue(currentRooms);
    }
    public void addDeviceToRoom(int index, Device device) {
        ArrayList<Room> currentRooms = rooms.getValue();
        Room room = currentRooms.get(index);
        room.getDevices().add(device);
        rooms.setValue(currentRooms);
    }
    public String roomsToJson() {
        Gson gson = new Gson();
        // Create a new Gson object and assign it to 'gson'.
        return gson.toJson(rooms.getValue());
        // Convert the current value of rooms (which is a list of Room objects) into JSON format using 'gson' and return it.
    }
    public void jsonToRooms(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Room>>() {}.getType();
        ArrayList<Room> roomList = gson.fromJson(json, type);
        rooms.setValue(roomList);
    }
}

