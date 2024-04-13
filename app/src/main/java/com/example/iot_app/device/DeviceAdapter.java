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

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewholder> {
    // This is a public class named DeviceAdapter.
    // It extends RecyclerView.Adapter, which is a common base class of an Adapter
    // that can be used in both RecyclerView and ListView.
    private List<Device> mListDevice;
//    private int indexArea;


    // A private variable for a list of Device objects.
/*    public DeviceAdapter(List<Device> mListDevice, String category) {
        this.mListDevice = mListDevice;
        this.category = category;
//        this.indexArea = indexArea;
    }*/

    public DeviceAdapter(List<Device> mListDevice) {
        this.mListDevice = mListDevice;
//        this.indexArea = indexArea;
    }

//    public DeviceAdapter(int indexArea) {
//        this.indexArea = indexArea;
//    }

   // Assign the list of devices to this object's mListDevice field.
   public void setDevices(List<Device> devices) {
       this.mListDevice = devices;
   }

    // This method is called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
    // It returns a new ViewHolder that holds a View for this adapter.
    @NonNull
    @Override
    public DeviceViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_detail, parent, false);
        // Inflate the XML layout file 'device_detail' and assign it to 'view'.
        return new DeviceAdapter.DeviceViewholder(view);
        // Return a new instance of DeviceViewholder that holds the 'view'.
    }

    // This method is called by RecyclerView to display the data at the specified position.
    // It updates the contents of the itemView to reflect the item at the given position.
    @Override
    public void onBindViewHolder(@NonNull DeviceViewholder holder, int position) {
        Device device = mListDevice.get(position);
        // If 'device' is null Return immediately, skipping the following statements.
        if(device == null){
            return ;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rooms");
        DatabaseReference switchStatus = myRef.child(device.getNameRoom()).child("devices").child(device.getDevice()).child("swithStatus");
        DatabaseReference detailRef = myRef.child(device.getNameRoom()).child("devices").child(device.getDevice()).child("detail");

        switchStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the data from the snapshot
                Boolean ledStatus = dataSnapshot.getValue(Boolean.class);
                Log.d("ledStatus", "Value is: " + ledStatus);

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
        // Set the resource
//        holder.imageDevice.setImageResource(device.getIdDevice());
        holder.txtName.setText(device.getDevice());
//        String deviceText = device.getDeviceCount() + " device(s)";
//        holder.txtDetail.setText(device.getDetail());
//        holder.switchStatus.setChecked(device.isSwithStatus());


        holder.switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
//                    final Device currentDevice = device;  // Declare a new final variable
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
                    myRef.child(device.getNameRoom()).child("devices").child(device.getDevice()).child("swithStatus").setValue(device.isSwithStatus());
                    myRef.child(device.getNameRoom()).child("devices").child(device.getDevice()).child("idDevice").setValue(imageResource);
                    holder.imageDevice.setImageResource(imageResource);  // Update the image view directly
                }
            }
        });



        holder.itemView.setOnClickListener(v -> {
//            LampFragment deviceFragment = new LampFragment();
/*            if (category != null){
                Log.d("onclick category: ", category);
            };*/
/*            LampFragment deviceFragment = new LampFragment();*/
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

            //Create a new instance of Bundle.
//            bundle.putInt("indexRoom", position);
//            bundle.putInt("indexArea", this.indexArea);
            // Put an integer value into the bundle with key as "index" and value as 'position'.
//            deviceFragment.setArguments(bundle);
            // Set arguments supplied by bundle to detailFragment.
            bundle.putBoolean("switchStatus", device.isSwithStatus());
            bundle.putString("roomName", device.getNameRoom());
            bundle.putString("deviceName", device.getDevice());
            //get name of room with key is roomName
            deviceFragment.setArguments(bundle);
            // Set arguments supplied by bundle to detailFragment.

            // Get an instance of FragmentManager for interacting with fragments associated with this activity.
            FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
            // Start a series of edit operations on the Fragments associated with this FragmentManager.
            fragmentManager.beginTransaction()
                    // Replace an existing fragment that was added to a container.
                    // This is essentially the same as calling remove(Fragment) for all currently added fragments that were added with the same containerViewId and then add(int, Fragment, String) with the same arguments given here.
                    .replace(R.id.frameLayout, deviceFragment)
                    .addToBackStack(null)
                    // Add this transaction to the back stack.
                    .commit();
            //it will be scheduled as work on the main thread to be done the next time that thread is ready.
        });

        // Set an OnLongClickListener on itemView in the holder.
        // This listener gets notified when itemView is long clicked or tapped.
        holder.itemView.setOnLongClickListener(v -> {

            int currentPosition = holder.getBindingAdapterPosition();
            // Get adapter position of ViewHolder in RecyclerView and assign it to 'currentPosition'.
           // Create a builder for an alert dialog that uses default alert dialog theme.
            new AlertDialog.Builder(v.getContext())
                    // Set title text for dialog.
                    .setTitle("Xóa thiết bị")
                    // Set message text for dialog.
                    .setMessage("Bạn có muốn xóa thiết bị này không?")
                    // Add positive button to dialog with text "OK" and click listener.
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        // Add positive button to dialog with text "OK" and click listener.
                        public void onClick(DialogInterface dialog, int which) {

                            if (currentPosition != RecyclerView.NO_POSITION) {
                                //  If 'currentPosition' is a valid position Remove the device at 'currentPosition' from mListDevice.
                                mListDevice.remove(currentPosition);
                                myRef.child(device.getNameRoom()).child("devices").child(device.getDevice()).removeValue();
                                // Notify DeviceAdapter that underlying data has changed and it should refresh itself.
                                notifyDataSetChanged();
                            }
                        }
                    })
                    // Add negative button to dialog with text "Cancel" and null click listener.
                    .setNegativeButton(android.R.string.cancel, null)
                    // Set icon for dialog using a drawable resource.
                    .setIcon(android.R.drawable.ic_menu_delete)
                    // Show this dialog, adding it to the screen.
                    .show();

            return true;
            // Return true because we've handled this long click event.
        });
    }


    @Override
    public int getItemCount() {
        // This method returns the total number of items in the data set held by the adapter.
        if (mListDevice != null) {
            return mListDevice.size();
        }
        return 0;
    }
// This is a public class named DeviceViewholder. It extends RecyclerView.ViewHolder, which describes a view and metadata about its place within the RecyclerView.

    public class DeviceViewholder extends RecyclerView.ViewHolder{

        private ImageView imageDevice;
        private TextView txtName;
        private TextView txtDetail;

        private Switch switchStatus;
        // This is a public constructor for DeviceViewholder.
        // It initializes a new DeviceViewholder object with a view.
        public DeviceViewholder(@NonNull View itemView) {
            super(itemView);
//            Find a view that was identified by id attribute in XML layout file
            imageDevice = itemView.findViewById(R.id.imgDevice);
            txtName = itemView.findViewById(R.id.txtNameDevice);
            txtDetail = itemView.findViewById(R.id.txtDetail);
            switchStatus = itemView.findViewById(R.id.swtDevice);
        }
    }
}
