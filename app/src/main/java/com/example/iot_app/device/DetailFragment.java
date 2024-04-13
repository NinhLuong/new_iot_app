package com.example.iot_app.device;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iot_app.R;
import com.example.iot_app.SharedViewModel;
import com.example.iot_app.home_page.Room;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {
    private RecyclerView rcvRoom;
    // A private variable for RecyclerView, which lets you display data in a scrolling list.
    private DeviceAdapter deviceAdapter;
    // A private variable for DeviceAdapter, which binds data to views that are displayed within a RecyclerView.
    private SharedViewModel viewModel;
    // A private variable for SharedViewModel,
    // which stores and manages UI-related data in a lifecycle conscious way.
    private List<Device> listDevice;
    // A private variable for a Room object.
    private String device_type;
    private TextView txtTemp, txtHum;
//    private Device newDevice;

    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            showEditDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showEditDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.edit_room_layout);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

        EditText edtNameRoom = dialog.findViewById(R.id.edtNameRoom);
        Button btnEdit = dialog.findViewById(R.id.btnEdit);
        int index = getArguments().getInt("index");
        Log.d("indexDetail", String.valueOf(index));
        String originalName = viewModel.getRooms().getValue().get(index).getRoom();
        edtNameRoom.setText(originalName);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = edtNameRoom.getText().toString();
                if (!newName.isEmpty()) {
                    // Update room name in SharedViewModel
                    Room room = viewModel.getRooms().getValue().get(index);
                    room.setRoom(newName);
                    viewModel.getRooms().getValue().set(index, room);

                    // Update toolbar title
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(newName);

                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Hãy nhập tên phòng mới!", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
// Find a view that was identified by 'rcv_detail' id attribute in XML layout file and assign it to 'rcvRoom'.
        rcvRoom = view.findViewById(R.id.rcv_detail);
        // Set 'rcvRoom' to use a linear layout manager (which arranges its children in a single column).
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rcvRoom.setLayoutManager(gridLayoutManager);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rooms");

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        // Find a view that was identified by the 'toolbar' id attribute in XML layout file and assign it to 'toolbar'.
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        // Set 'toolbar' to act as the ActionBar for this Activity window.
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Enable the Up button in the action bar.
        String roomName = getArguments().getString("roomName");
        // Retrieve the value associated with the key "roomName" from the arguments supplied
        // when this fragment was instantiated and assign it to 'roomName'.
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(roomName);

// Set the title text for this activity's ActionBar represented by 'toolbar'.
        int indexArea = getArguments().getInt("index");
        // Retrieve the value associated with the key "index" from the arguments supplied
        // when this fragment was instantiated and assign it to 'index'.

        txtTemp = view.findViewById(R.id.tempRoom);
        txtHum = view.findViewById(R.id.humRoom);

        DatabaseReference humiRef = myRef.child(roomName).child("Hum");
        DatabaseReference tempRef = myRef.child(roomName).child("Temp");


        humiRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the data from the snapshot
                String himi = dataSnapshot.getValue(String.class);
                 Log.d("value_himi", "Value is: " + himi);

                if(himi != null && !himi.equals("null")){
                    txtHum.setText(himi.substring(0, 2));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tempRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get the data from the snapshot
                    String temp = dataSnapshot.getValue(String.class);
                    if(temp != null && !temp.equals("null")){
                        txtTemp.setText(temp.substring(0, 2));
                       /* if(Float.parseFloat(temp)> 32){
                            Log.d("txtTemp: ", String.valueOf(Float.parseFloat(temp)));
                            myRef.child(roomName).child("SOS").setValue("true");
                        }else if (Float.parseFloat(temp) < 32) {
                            myRef.child(roomName).child("SOS").setValue("false");
                        }*/
                    }
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
//        viewModel.loadData(getContext());
        // Get an instance of SharedViewModel associated with this activity.
        /*viewModel.getRoomArea().observe(getViewLifecycleOwner(), new Observer<List<Device>>() {
            // This method is called when list of rooms changes.
            @Override
            public void onChanged(List<Device> devices) {
                deviceAdapter.setDevices(devices);
                // Update rooms in 'roomAdapter'.
                deviceAdapter.notifyDataSetChanged();
                // Notify 'roomAdapter' that underlying data has changed and it should refresh itself.
            }
        });*/

        listDevice = viewModel.getRooms().getValue().get(indexArea).getDevices();
        // Get list of devices from room at position 'index' in SharedViewModel and assign it to 'devices'.

        if (listDevice == null) {
            listDevice = new ArrayList<>();
            // Initialize 'devices' as an empty ArrayList.
            viewModel.getRooms().getValue().get(indexArea).setDevices((ArrayList<Device>) listDevice);
            // Set list of devices in room at position 'index' in SharedViewModel to be 'devices'.
        }

        // Find a view that was identified by 'btnAddDevice' id attribute in XML layout file and assign it to 'btnAddDevice'.
        FloatingActionButton btnAddDevice = view.findViewById(R.id.btnAddDevice);
        // Set an OnClickListener on 'btnAddDevice'. This listener gets notified when 'btnAddDevice' is clicked or tapped.
        // This method is called when 'btnAddDevice' is clicked or tapped.


        btnAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());


                // Create a new dialog instance with current context.
                dialog.setContentView(R.layout.add_room_layout);
                // Set the content view of this dialog. The layout resource is 'add_device_layout'.
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

                Spinner spinnerDeviceType = dialog.findViewById(R.id.spnCategory);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.device_types_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDeviceType.setAdapter(adapter);
                spinnerDeviceType.setSelection(0);

// Set the background of this dialog window using a drawable resource.
                EditText edtNameRoom = dialog.findViewById(R.id.etNameRoom);
                // Find a view that was identified by 'edtNameDevice' id attribute in XML layout file and assign it to 'edtNameRoom'.
//                EditText edtInfo = dialog.findViewById(R.id.edtInfo);
                // Find a view that was identified by 'edtInfo' id attribute in XML layout file and assign it to 'edtInfo'.
                Button btnAddDevice = dialog.findViewById(R.id.btnAddRoomArea);
                // Find a view that was identified by 'btnAddDevice' id attribute in XML layout file and assign it to 'btnAddDevice'.
                final String[] deviceTypeContainer = new String[1];
                btnAddDevice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name_device = "" ;
                        device_type = spinnerDeviceType.getSelectedItem().toString();

                        Log.d("device_type: ", device_type);

//                        String info = "";
                        // Initialize two string variables 'name_device' and 'info'.
                        if(!edtNameRoom.getText().toString().equals("")/* && !edtInfo.getText().toString().equals("")*/){
                            // If the text in 'edtNameRoom' and 'edtInfo' are not empty
                            // Assign the text in 'edtNameRoom' to 'name_device', the text in 'edtInfo' to 'info'.
                            name_device = edtNameRoom.getText().toString();
//                            info = edtInfo.getText().toString();
                            Device newDevice;
                            switch (device_type){
                                case "Fan":
                                    newDevice = new Device(R.drawable.ic_fan_off, name_device, "Medium", false,100,"Fan", roomName);
                                    break;
                                case "Air Condition":
                                    newDevice = new Device(R.drawable.ac_off, name_device, "24", false,24,"normal",false,  "Air Condition", roomName);
                                    break;
                                default:
                                    newDevice = new Device(R.drawable.led_off, name_device, "yellow", false, 100,"Light", roomName);
                                    break;
                            }
//
                            // Create a new Device object with default image, name from 'edtNameRoom', and info from 'edtInfo'.
//                            viewModel.addRoomArea(newDevice);
                            viewModel.addDeviceToRoom(indexArea, newDevice);
                            myRef.child(roomName).child("devices").child(name_device).setValue(newDevice);
                            /*myRef.child(roomName).child("devices").child(name_device).child("Switch").setValue("false");
                            myRef.child(roomName).child("devices").child(name_device).child("Detail").setValue("");*/
//                            deviceAdapter = new DeviceAdapter(listDevice, device_type );
//                            rcvRoom.setAdapter(deviceAdapter);
//                            viewModel.saveData(getContext());
                            // Create a new Device object with default image, name from 'edtNameRoom', and info from 'edtInfo'.
                            deviceAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                        else {
                            Toast.makeText(getContext(), "Hãy nhập đầy đủ thông tin!", Toast.LENGTH_LONG).show();
                        }
                        Log.d("finish: ", name_device);
                    }
                });

                dialog.show();

            }
        });
        /*myRef.child(roomName).child("devices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listDevice.clear();
                for (DataSnapshot deviceSnapshot : dataSnapshot.getChildren()) {
                    Device device = deviceSnapshot.getValue(Device.class);
                    if (device.isSwithStatus()) {
                        switch (device.getCategory()){
                            case "Fan":
                                device.setIdDevice(R.drawable.ic_fan_on);
                                break;
                            case "Air Condition":
                                device.setIdDevice(R.drawable.ac_on);
                                break;
                            default:
                                device.setIdDevice(R.drawable.led_on);
                                break;
                        }
                    } else {
                        switch (device.getCategory()){
                            case "Fan":
                                device.setIdDevice(R.drawable.ic_fan_off);
                                break;
                            case "Air Condition":
                                device.setIdDevice(R.drawable.ac_off);
                                break;
                            default:
                                device.setIdDevice(R.drawable.led_off);
                                break;
                        }
                    }
                    listDevice.add(device);
                }
                deviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/


        deviceAdapter = new DeviceAdapter(listDevice );
        rcvRoom.setAdapter(deviceAdapter);

        return view;
    }
}