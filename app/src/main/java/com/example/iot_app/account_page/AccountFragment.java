package com.example.iot_app.account_page;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.iot_app.R;
import com.example.iot_app.sign_in.LoginActivity;
import com.example.iot_app.sign_up.SignUpActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        Button btnLogout = view.findViewById(R.id.btnLogout);
        Button btnChangePW = view.findViewById(R.id.btnChangePass);
        TextView getUsername = view.findViewById(R.id.getUsername);
        TextView titleUsername = view.findViewById(R.id.titleUsername);
        TextView getEnail = view.findViewById(R.id.getEnail);
        TextView getPhone = view.findViewById(R.id.getPhone);


        DatabaseReference emailref = FirebaseDatabase.getInstance().getReference("users").child(username).child("email");
        DatabaseReference numberref = FirebaseDatabase.getInstance().getReference("users").child(username).child("phoneNumber");

        getUsername.setText(username);
        titleUsername.setText(username);
        emailref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the data from the snapshot
                String emailfb = dataSnapshot.getValue(String.class);

                if(emailfb != null){
                    getEnail.setText(emailfb);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        numberref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the data from the snapshot
                String numberfb = dataSnapshot.getValue(String.class);

                if(numberfb != null){
                    getPhone.setText(numberfb);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Add an action listener to the button

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exit the app with a normal exit code
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });


        btnChangePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exit the app with a normal exit code
                Intent intent = new Intent(getActivity(), ChangePW.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        return view;
    }
}