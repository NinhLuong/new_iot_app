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
//import com.example.iot_app.SharedViewModel;
import com.example.iot_app.home_page.Room;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailFragment extends Fragment {
    private RecyclerView rcvRoom;
    private DeviceAdapter deviceAdapter;
//    private SharedViewModel viewModel;
    private ArrayList<Device> listDevice;
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
        String roomName = getArguments().getString("roomName");
        edtNameRoom.setText(roomName);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = edtNameRoom.getText().toString();
                if (!newName.isEmpty()) {
//                    Room room = viewModel.getRooms().getValue().get(index);
//                    room.setRoom(newName);
//                    viewModel.getRooms().getValue().set(index, room);
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
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        rcvRoom = view.findViewById(R.id.rcv_detail);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rcvRoom.setLayoutManager(gridLayoutManager);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rooms");

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String roomName = getArguments().getString("roomName");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(roomName);
        int indexArea = getArguments().getInt("index");

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
        /*viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        listDevice = viewModel.getRooms().getValue().get(indexArea).getDevices();*/

        if (listDevice == null) {
            listDevice = new ArrayList<>();
//            viewModel.getRooms().getValue().get(indexArea).setDevices((ArrayList<Device>) listDevice);
        }
        FloatingActionButton btnAddDevice = view.findViewById(R.id.btnAddDevice);
        btnAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.add_room_layout);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

                Spinner spinnerDeviceType = dialog.findViewById(R.id.spnCategory);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.device_types_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDeviceType.setAdapter(adapter);
                spinnerDeviceType.setSelection(0);

                EditText edtNameRoom = dialog.findViewById(R.id.etNameRoom);

                Button btnAddDevice = dialog.findViewById(R.id.btnAddRoomArea);
                final String[] deviceTypeContainer = new String[1];
                btnAddDevice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name_device = "" ;
                        device_type = spinnerDeviceType.getSelectedItem().toString();

                        if(!edtNameRoom.getText().toString().equals("")){
                            name_device = edtNameRoom.getText().toString();
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

                            // Get the Room object from Firebase
                            myRef.child(roomName).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Get the Room object from the snapshot
                                    Room room = dataSnapshot.getValue(Room.class);
// Check if the hmdevices HashMap is null
                                    if (room.getHmdevices() == null) {
                                        // If it's null, initialize it
                                        room.setHmdevices(new HashMap<>());
                                    }

                                    // Add the new device to the hmdevices HashMap of the Room object
                                    room.getHmdevices().put(newDevice.getDevice(), newDevice);

                                    // Save the updated Room object back to Firebase
                                    myRef.child(roomName).setValue(room);
                                    deviceAdapter.addDevice(newDevice);
                                    deviceAdapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle error
                                }
                            });

                            deviceAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                        else {
                            Toast.makeText(getContext(), "Hãy nhập đầy đủ thông tin!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.show();

            }
        });
        myRef.child(roomName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);
                if (room != null) {
                    HashMap<String, Device> hmdevices = room.getHmdevices();
                    if (hmdevices == null) {
                        hmdevices = new HashMap<>();
                    }
                    DeviceAdapter deviceAdapter = new DeviceAdapter(new ArrayList<>(hmdevices.values()));
                    deviceAdapter.notifyDataSetChanged();
                    // rest of your code
                    rcvRoom.setAdapter(deviceAdapter);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        deviceAdapter = new DeviceAdapter(listDevice);
        deviceAdapter.notifyDataSetChanged();
        rcvRoom.setAdapter(deviceAdapter);

        return view;
    }
}