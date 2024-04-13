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
//    This is a public class named SharedViewModel.
//    It extends ViewModel, which is designed to store and manage UI-related data in a lifecycle conscious way.

    private final MutableLiveData<List<Room>> rooms = new MutableLiveData<>(new ArrayList<>());
//    private final MutableLiveData<List<Device>> roomsArea = new MutableLiveData<>(new ArrayList<>());
    // A private final variable for a MutableLiveData that holds a list of Room objects.
    // It is initialized with an empty ArrayList.
    public LiveData<List<Room>> getRooms() {
        return rooms;
    }
//    public LiveData<List<Device>> getRoomArea() {
//        return roomsArea;
//    }
    // This is a getter method for rooms. It returns a LiveData that holds a list of Room objects.

    public void addRoom(Room room) {
        // This is a method to add a Room object to rooms. It takes a Room object as parameter.
        List<Room> currentRooms = rooms.getValue();
        // Get the current value of rooms (which is a list of Room objects) and assign it to 'currentRooms'.
        currentRooms.add(room);
        // Add 'room' to 'currentRooms'.
        rooms.setValue(currentRooms);
        // Set the value of rooms to be 'currentRooms'.
    }
    public void addDeviceToRoom(int index, Device device) {
        // This is a method to add a Device object to a specific room in rooms.
        // It takes an index and a Device object as parameters.
        List<Room> currentRooms = rooms.getValue();
        // Get the current value of rooms (which is a list of Room objects) and assign it to 'currentRooms'.
        Room room = currentRooms.get(index);
        // Get the room at position 'index' in 'currentRooms' and assign it to 'room'.
        room.getDevices().add(device);
        // Add 'device' to the devices of 'room'.
        rooms.setValue(currentRooms);
        // Set the value of rooms to be 'currentRooms'.
    }
/*        public void addRoomArea(Device roomArea) {
        // This is a method to add a Room object to rooms. It takes a Room object as parameter.
        List<Device> currentRoomsArea = roomsArea.getValue();
        // Get the current value of rooms (which is a list of Room objects) and assign it to 'currentRooms'.
        currentRoomsArea.add(roomArea);
        // Add 'room' to 'currentRooms'.
        roomsArea.setValue(currentRoomsArea);
        // Set the value of rooms to be 'currentRooms'.
    }*/

/*    public void adDevicesToRooms(int indexArea,int indexRoom ,DeviceRoom deviceRoom){
        List<Room> currentRooms = rooms.getValue();
        Room room = currentRooms.get(indexArea);
        rooms.setValue(currentRooms);

        List<Device> currentRoomsArea = roomsArea.getValue();

        Device devicesRoom = currentRoomsArea.get(indexRoom);
        devicesRoom.getDeviceRoomArrayList().add(deviceRoom);
        roomsArea.setValue(currentRoomsArea);

    }*/
    // This is a method to convert rooms into JSON format. It returns a string.
    public String roomsToJson() {
        Gson gson = new Gson();
        // Create a new Gson object and assign it to 'gson'.
        return gson.toJson(rooms.getValue());
        // Convert the current value of rooms (which is a list of Room objects) into JSON format using 'gson' and return it.
    }
    public void jsonToRooms(String json) {
        Gson gson = new Gson();
        // Create a new Gson object and assign it to 'gson'.
        Type type = new TypeToken<List<Room>>() {}.getType();
        // Get the type of List<Room> and assign it to 'type'.
        List<Room> roomList = gson.fromJson(json, type);
        // Convert 'json' into a list of Room objects using 'gson' and assign it to 'roomList'.
        rooms.setValue(roomList);
        // Set the value of rooms to be 'roomList'.
    }

    /*public String roomsAreaToJson() {
        Gson gson = new Gson();
        // Create a new Gson object and assign it to 'gson'.
        return gson.toJson(roomsArea.getValue());
        // Convert the current value of rooms (which is a list of Room objects) into JSON format using 'gson' and return it.
    }
    // This is a method to convert JSON format into rooms. It takes a string as parameter.

    public void jsonToRoomsArea(String json) {
        Gson gson = new Gson();
        // Create a new Gson object and assign it to 'gson'.
        Type type = new TypeToken<List<Device>>() {}.getType();
        // Get the type of List<Room> and assign it to 'type'.
        List<Device> devicesList = gson.fromJson(json, type);
        // Convert 'json' into a list of Room objects using 'gson' and assign it to 'roomList'.
        roomsArea.setValue(devicesList);
        // Set the value of rooms to be 'roomList'.
    }*/


}

