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

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    public ArrayList<Room> mListRoom;
    public  RoomAdapter(ArrayList<Room> mListRoom) {
        this.mListRoom = mListRoom;
    }
    public void setRooms(ArrayList<Room> rooms) {
        this.mListRoom = rooms;
    }
    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = mListRoom.get(position);
        if (room == null){
            return ;
        }
        holder.imageAvatar.setImageResource(room.getResourceId());
        holder.txtRoom.setText(room.getRoom());

        String deviceText = room.getDeviceCount() + " device(s)";
        holder.txtDevice.setText(deviceText);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rooms");

        holder.itemView.setOnClickListener(v -> {
            DetailFragment detailFragment = new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("index", position);
            detailFragment.setArguments(bundle);
            bundle.putString("roomName", room.getRoom());
            detailFragment.setArguments(bundle);

            FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        holder.itemView.setOnLongClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Xóa phòng")
                    .setMessage("Bạn có muốn xóa phòng này không?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (currentPosition != RecyclerView.NO_POSITION) {
                                mListRoom.remove(currentPosition);
                                myRef.child(room.getRoom()).removeValue();
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
        if (mListRoom != null) {
            return mListRoom.size();
        }
        return 0;
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageAvatar;
        private TextView txtRoom;
        private TextView txtDevice;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
//            anh xa bien
            imageAvatar = itemView.findViewById(R.id.img_avatar);
            txtRoom = itemView.findViewById(R.id.txt_room);
            txtDevice = itemView.findViewById(R.id.txt_device);
        }
    }

}
