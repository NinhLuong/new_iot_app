package com.example.iot_app.device;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.iot_app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AirFragment extends Fragment {

    Boolean ledStatus;
    TextView valueInten, txtIntensity ;
    String modeAir;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_air, container, false);
        String roomName = getArguments().getString("roomName");
        String deviceName = getArguments().getString("deviceName");

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(deviceName);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        Switch switchAir = rootView.findViewById(R.id.switchAir);
        Switch switchAuto = rootView.findViewById(R.id.switchAuto);
        TextView textTemp = rootView.findViewById(R.id.textTemp);
        TextView txtMin = rootView.findViewById(R.id.txtMin);
        TextView txtMax = rootView.findViewById(R.id.txtMax);
        RadioGroup radioGroup = rootView.findViewById(R.id.radioGroup);
        AppCompatSeekBar seekBarLamp = rootView.findViewById(R.id.seekBarAir);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rooms");
        DatabaseReference switchStatus = myRef.child(roomName).child("devices").child(deviceName).child("swithStatus");
        DatabaseReference intensityRef = myRef.child(roomName).child("devices").child(deviceName).child("detail");
        DatabaseReference modeRef = myRef.child(roomName).child("devices").child(deviceName).child("mode");
        DatabaseReference autoRef = myRef.child(roomName).child("devices").child(deviceName).child("autoStatus");

        RadioButton btnHot = rootView.findViewById(R.id.btnHot);
        RadioButton btnCold = rootView.findViewById(R.id.btnCold);
        RadioButton btnNormal = rootView.findViewById(R.id.btnNormal);

        switchStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the data from the snapshot
                ledStatus = dataSnapshot.getValue(Boolean.class);
                Log.d("ledStatus", "Value is: " + ledStatus);

                if(ledStatus != null ){

                    switchAir.setChecked(ledStatus);
                    if (!ledStatus ) {
                        seekBarLamp.setEnabled(false);
                        for (int i = 0; i < radioGroup.getChildCount(); i++) {
                            radioGroup.getChildAt(i).setEnabled(false);
                        }
                        Log.d("complete setup false", "Value is: " + ledStatus);
                    }
                    if (ledStatus ){
                        seekBarLamp.setEnabled(true);
                        for (int i = 0; i < radioGroup.getChildCount(); i++) {
                            radioGroup.getChildAt(i).setEnabled(true);
                        }
                        Log.d("complete setup true", "Value is: " + ledStatus);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ledStatus error: ", String.valueOf(error));
            }
        });
        switchAir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myRef.child(roomName).child("devices").child(deviceName).child("swithStatus").setValue(isChecked);

            }
        });

        autoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the data from the snapshot
               boolean autoStatus = dataSnapshot.getValue(boolean.class);
                Log.d("ledStatus", "Value is: " + ledStatus);

                if(ledStatus != null ){

                    switchAir.setChecked(ledStatus);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ledStatus error: ", String.valueOf(error));
            }
        });

        switchAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myRef.child(roomName).child("devices").child(deviceName).child("autoStatus").setValue(isChecked);

            }
        });
        intensityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String intensity = dataSnapshot.getValue(String.class);
                if (intensity == null) {
                    intensity = "25";
                    intensityRef.setValue(intensity);
                }

                textTemp.setText(intensity);
                seekBarLamp.setProgress(Integer.valueOf(intensity));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value intensityRef.", databaseError.toException());
            }
        });

        seekBarLamp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                if (fromUser) {
                    switch (modeAir){
                        case "hot":
                            progress = progress / 2 + 50; // changes from 50 to 100
                            break;
                        case "cold":
                            progress = progress * 80 / 100 - 50; // changes from -50 to 30
                            break;
                        case "normal":
                            progress = progress * 40 / 100; // changes from 0 to 40
                            break;
                    }
                    intensityRef.setValue(String.valueOf(progress));
                    textTemp.setText(String.valueOf(progress));
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
                modeAir = dataSnapshot.getValue(String.class);
                if (modeAir == null) {
                    modeAir = "normal";
                }
                switch (modeAir) {

                    case "hot":
                        btnHot.setChecked(true);
                        txtMin.setText("50");
                        txtMax.setText("100");
                        break;
                    case "cold":
                        btnCold.setChecked(true);
                        txtMin.setText("-50");
                        txtMax.setText("40");
                        break;
                    default:
                        btnNormal.setChecked(true);
                        txtMin.setText("0");
                        txtMax.setText("40");
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
                    String mode = AirFragment.this.getSelectedColor(selectedRadioButton);

                    // Send the color to Firebase
                    myRef.child(roomName).child("devices").child(deviceName).child("mode").setValue(mode);
                    Log.d("color choice", mode);
                    // Uncheck all other radio buttons in the group
                    AirFragment.this.clearRadioGroupSelection(group, selectedRadioButton);
                }
            }
        });



        return rootView;
    }
    private String getSelectedColor(RadioButton radioButton) {
        // Get the color associated with the selected radio button
        // You can customize this method based on your implementation

        if (radioButton.getId() == R.id.btnHot) {
            return "hot";
        } else if (radioButton.getId() == R.id.btnCold) {
            return "cold";
        } else if (radioButton.getId() == R.id.btnNormal) {
            return "normal";
        } else {
            return "normal"; // Default case
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