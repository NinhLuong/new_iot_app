package com.example.iot_app.home_page;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_app.device.DetailFragment;
import com.example.iot_app.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    public List<Room> mListRoom;
    // A public variable for a list of Room objects.

    public  RoomAdapter(List<Room> mListRoom) {
        this.mListRoom = mListRoom;
    }

    // This is a setter method for mListRoom. It sets the list of rooms of this RoomAdapter object.
    public void setRooms(List<Room> rooms) {
        this.mListRoom = rooms;
    }

    // This method is called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
    // It returns a new ViewHolder that holds a View for this adapter.
    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        truyen item_room vao bien view co kieu View
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    // This method is called by RecyclerView to display the data at the specified position.
    // It updates the contents of the itemView to reflect the item at the given position.
    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = mListRoom.get(position);
        if (room == null){
            return ;
        }
        holder.imageAvatar.setImageResource(room.getResourceId());
        holder.txtRoom.setText(room.getRoom());
        // Create a string that represents number of devices in 'room'.
        String deviceText = room.getDeviceCount() + " device(s)";
        holder.txtDevice.setText(deviceText);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rooms");

        // Set an OnClickListener on itemView in the holder.
        // This listener gets notified when itemView is clicked or tapped.

        holder.itemView.setOnClickListener(v -> {
            DetailFragment detailFragment = new DetailFragment();
            // Create a new instance of DetailFragment.
            Bundle bundle = new Bundle();
            //Create a new instance of Bundle.
            bundle.putInt("index", position);
            // Put an integer value into the bundle with key as "index" and value as 'position'.
            detailFragment.setArguments(bundle);
            // Set arguments supplied by bundle to detailFragment.
            bundle.putString("roomName", room.getRoom());
            //get name of room with key is roomName
            detailFragment.setArguments(bundle);
            // Set arguments supplied by bundle to detailFragment.

            // Get an instance of FragmentManager for interacting with fragments associated with this activity.
            FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
            // Start a series of edit operations on the Fragments associated with this FragmentManager.
            fragmentManager.beginTransaction()
                    // Replace an existing fragment that was added to a container.
                    // This is essentially the same as calling remove(Fragment) for all currently added fragments that were added with the same containerViewId and then add(int, Fragment, String) with the same arguments given here.
                    .replace(R.id.frameLayout, detailFragment)
                    .addToBackStack(null)
                    // Add this transaction to the back stack.
                    .commit();
            //it will be scheduled as work on the main thread to be done the next time that thread is ready.
        });

        // Set an OnLongClickListener on itemView in the holder.
        // This listener gets notified when itemView is long clicked or tapped.
        holder.itemView.setOnLongClickListener(v -> {
            // Get adapter position of ViewHolder in RecyclerView and assign it to 'currentPosition'.
            int currentPosition = holder.getBindingAdapterPosition();

            // Create a builder for an alert dialog that uses default alert dialog theme.
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Xóa phòng") // Set title text for dialog.
                    .setMessage("Bạn có muốn xóa phòng này không?") // Set message text for dialog.
                    // Add positive button to dialog with text "OK" and click listener.
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        // This method is called when positive button is clicked.
                        public void onClick(DialogInterface dialog, int which) {
                            //If 'currentPosition' is a valid position

                            if (currentPosition != RecyclerView.NO_POSITION) {
                                // Remove the room at 'currentPosition' from mListRoom.
                                mListRoom.remove(currentPosition);
                                myRef.child(room.getRoom()).removeValue();
                                notifyDataSetChanged();
                                // Notify RoomAdapter that underlying data has changed and it should refresh itself.
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
        });

    }

    // This method returns the total number of items in the data set held by the adapter.
    @Override
    public int getItemCount() {
// If mListRoom is not null
        if (mListRoom != null) {
            return mListRoom.size();
            // Return the number of items in mListRoom.
        }
        return 0;
    }
    // This is a public class named RoomViewHolder.
    // It extends RecyclerView.ViewHolder, which describes a view and metadata about its place within the RecyclerView.
    public class RoomViewHolder extends RecyclerView.ViewHolder {
        //khoi tao bien
        private ImageView imageAvatar;
        private TextView txtRoom;
        private TextView txtDevice;
        // This is a public constructor for RoomViewHolder.
        // It initializes a new RoomViewHolder object with a view.
        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
//            anh xa bien
            imageAvatar = itemView.findViewById(R.id.img_avatar);
            txtRoom = itemView.findViewById(R.id.txt_room);
            txtDevice = itemView.findViewById(R.id.txt_device);
        }
    }

}
