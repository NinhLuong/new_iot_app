package com.example.iot_app.device;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.iot_app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FanFragment extends Fragment {
    boolean fanStatus;
    String modeFan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fan, container, false);
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        String roomName = getArguments().getString("roomName");
        String deviceName = getArguments().getString("deviceName");

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(deviceName);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        Switch switchFan = rootView.findViewById(R.id.switchFan);

        TextView txtIntensityFan = rootView.findViewById(R.id.txtIntensityFan);
        RadioGroup radioGroup = rootView.findViewById(R.id.radioGroup);
        AppCompatSeekBar seekBarFan = rootView.findViewById(R.id.seekBarFan);
        RadioButton fanLow = rootView.findViewById(R.id.fanLow);
        RadioButton fanMedium = rootView.findViewById(R.id.fanMedium);
        RadioButton fanHigh = rootView.findViewById(R.id.fanHigh);
        TextView txtMin = rootView.findViewById(R.id.txtMinFan);
        TextView txtMax = rootView.findViewById(R.id.txtMaxFan);
        ImageView imgFan = rootView.findViewById(R.id.imgFan);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rooms");
        DatabaseReference switchStatus = myRef.child(roomName).child("hmdevices").child(deviceName).child("swithStatus");
        DatabaseReference intensityRef = myRef.child(roomName).child("hmdevices").child(deviceName).child("intensity");
        DatabaseReference modeRef = myRef.child(roomName).child("hmdevices").child(deviceName).child("detail");

        switchFan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myRef.child(roomName).child("hmdevices").child(deviceName).child("swithStatus").setValue(isChecked);

            }
        });
        switchStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the data from the snapshot
                fanStatus = dataSnapshot.getValue(boolean.class);
                Log.d("fanStatus", "Value is: " + fanStatus);

                switchFan.setChecked(fanStatus);
                if(fanStatus){
                    imgFan.setImageResource(R.drawable.fan_on);
                    seekBarFan.setEnabled(true);
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        radioGroup.getChildAt(i).setEnabled(true);
                    }
                }else {
                    seekBarFan.setEnabled(false);
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        radioGroup.getChildAt(i).setEnabled(false);
                    }
                    imgFan.setImageResource(R.drawable.fan_off);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ledStatus error: ", String.valueOf(error));
            }
        });

        intensityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer intensity = dataSnapshot.getValue(Integer.class);
                if (intensity == null) {
                    intensity = 100;
                    intensityRef.setValue(intensity);
                }

                seekBarFan.setProgress(intensity);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value intensityRef.", databaseError.toException());
            }
        });

        seekBarFan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float opacity = progress / 100f;
                txtIntensityFan.setText(progress + "%");
                if (fromUser) {
                    intensityRef.setValue(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        modeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                modeFan = dataSnapshot.getValue(String.class);
                if (modeFan == null) {
                    modeFan = "medium";
                }
                switch (modeFan) {

                    case "low":
                        fanLow.setChecked(true);
                        txtMin.setText("0");
                        txtMax.setText("50");
                        break;
                    case "high":
                        fanHigh.setChecked(true);
                        txtMin.setText("100");
                        txtMax.setText("200");
                        break;
                    default:
                        fanMedium.setChecked(true);
                        txtMin.setText("50");
                        txtMax.setText("100");
                        break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value intensityRef.", databaseError.toException());
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Get the selected radio button
                RadioButton selectedRadioButton = rootView.findViewById(checkedId);

                if (selectedRadioButton != null) {
                    // Get the color corresponding to the selected radio button
                    String mode = FanFragment.this.getSelectedColor(selectedRadioButton);

                    // Send the color to Firebase
                    modeRef.setValue(mode);
                    Log.d("color choice", mode);
                    // Uncheck all other radio buttons in the group
                    FanFragment.this.clearRadioGroupSelection(group, selectedRadioButton);
                }
            }
        });

        return rootView;
    }

    private String getSelectedColor(RadioButton radioButton) {
        // Get the color associated with the selected radio button
        // You can customize this method based on your implementation

        if (radioButton.getId() == R.id.fanLow) {
            return "low";

        } else if (radioButton.getId() == R.id.fanHigh) {
            return "high";
        } else {
            return "medium"; // Default case
        }
    }

    private void clearRadioGroupSelection(RadioGroup group, RadioButton selectedRadioButton) {
        // Uncheck all other radio buttons in the group
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) group.getChildAt(i);
            if (radioButton != selectedRadioButton) {
                radioButton.setChecked(false);
            }
        }
    }
}