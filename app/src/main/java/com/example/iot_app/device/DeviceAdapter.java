package com.example.iot_app.device;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_app.R;
import com.example.iot_app.home_page.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewholder> {
    private ArrayList<Device> mListDevice;

    public DeviceAdapter(ArrayList<Device> mListDevice) {
        this.mListDevice = mListDevice;
    }

   public void setDevices(ArrayList<Device> devices) {
       this.mListDevice = devices;
   }

    @NonNull
    @Override
    public DeviceViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_detail, parent, false);
        return new DeviceAdapter.DeviceViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewholder holder, int position) {
        Device device = mListDevice.get(position);
        if(device == null){
            return ;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rooms");
        DatabaseReference switchStatus = myRef.child(device.getNameRoom()).child("hmdevices").child(device.getDevice()).child("swithStatus");
        DatabaseReference detailRef = myRef.child(device.getNameRoom()).child("hmdevices").child(device.getDevice()).child("detail");

        switchStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the data from the snapshot
                Boolean ledStatus = dataSnapshot.getValue(Boolean.class);
                if(ledStatus != null ){
                    holder.switchStatus.setChecked(ledStatus);
                    if (ledStatus ) {
                        switch (device.getCategory()){
                            case "Fan":
                                holder.imageDevice.setImageResource(R.drawable.ic_fan_on);
                                break;
                            case "Air Condition":
                                holder.imageDevice.setImageResource(R.drawable.ac_on);
                                break;
                            default:
                                holder.imageDevice.setImageResource(R.drawable.led_on);
                                break;
                        }
                    }else{
                        switch (device.getCategory()){
                            case "Fan":
                                holder.imageDevice.setImageResource(R.drawable.ic_fan_off);
                                break;
                            case "Air Condition":
                                holder.imageDevice.setImageResource(R.drawable.ac_off);
                                break;
                            default:
                                holder.imageDevice.setImageResource(R.drawable.led_off);
                                break;
                        }
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ledStatus error: ", String.valueOf(error));
            }
        });

        detailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the data from the snapshot
                String detail = dataSnapshot.getValue(String.class);
                holder.txtDetail.setText(detail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("error detail in firebase: ", String.valueOf(error));
            }
        });
        holder.txtName.setText(device.getDevice());

        holder.switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    device.setSwithStatus(isChecked);
                    int imageResource;
                    if (isChecked) {
                        switch (device.getCategory()){
                            case "Fan":
                                imageResource = R.drawable.ic_fan_on;
                                break;
                            case "Air Condition":
                                imageResource = R.drawable.ac_on;
                                break;
                            default:
                                imageResource = R.drawable.led_on;
                                break;
                        }
                    } else {
                        switch (device.getCategory()){
                            case "Fan":
                                imageResource = R.drawable.ic_fan_off;
                                break;
                            case "Air Condition":
                                imageResource = R.drawable.ac_off;
                                break;
                            default:
                                imageResource = R.drawable.led_off;
                                break;
                        }
                    }
                    device.setIdDevice(imageResource);
                    myRef.child(device.getNameRoom()).child("hmdevices").child(device.getDevice()).child("swithStatus").setValue(device.isSwithStatus());
                    myRef.child(device.getNameRoom()).child("hmdevices").child(device.getDevice()).child("idDevice").setValue(imageResource);
                    holder.imageDevice.setImageResource(imageResource);  // Update the image view directly
                }
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Fragment deviceFragment;
            String category = device.getCategory();

            switch (category){
                case "Fan":
                     deviceFragment = new FanFragment();
                    break;
                case "Air Condition":
                     deviceFragment = new AirFragment();
                    break;
                default:
                     deviceFragment = new LampFragment();
                    break;
            }
            // Create a new instance of DetailFragment.
            Bundle bundle = new Bundle();
            bundle.putBoolean("switchStatus", device.isSwithStatus());
            bundle.putString("roomName", device.getNameRoom());
            bundle.putString("deviceName", device.getDevice());
            deviceFragment.setArguments(bundle);

            FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, deviceFragment)
                    .addToBackStack(null)
                    .commit();
        });

        holder.itemView.setOnLongClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Xóa thiết bị")
                    .setMessage("Bạn có muốn xóa thiết bị này không?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (currentPosition != RecyclerView.NO_POSITION) {
                                mListDevice.remove(currentPosition);
                                myRef.child(device.getNameRoom()).child("hmdevices").child(device.getDevice()).removeValue();
                                notifyDataSetChanged();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .setIcon(android.R.drawable.ic_menu_delete)
                    .show();

            return true;
        });
    }


    @Override
    public int getItemCount() {
        if (mListDevice != null) {
            return mListDevice.size();
        }
        return 0;
    }

    public void addDevice(Device device) {
        this.mListDevice.add(device);
        notifyItemInserted(mListDevice.size() - 1);
    }

    public class DeviceViewholder extends RecyclerView.ViewHolder{
        private ImageView imageDevice;
        private TextView txtName;
        private TextView txtDetail;

        private Switch switchStatus;
        public DeviceViewholder(@NonNull View itemView) {
            super(itemView);
            imageDevice = itemView.findViewById(R.id.imgDevice);
            txtName = itemView.findViewById(R.id.txtNameDevice);
            txtDetail = itemView.findViewById(R.id.txtDetail);
            switchStatus = itemView.findViewById(R.id.swtDevice);
        }
    }
}
