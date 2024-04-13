package com.example.iot_app.sign_in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iot_app.MainActivity;
import com.example.iot_app.R;
import com.example.iot_app.sign_up.SignUpActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
//    khai báo kiểu class của các biến
    EditText loginUsername, loginPassword;
    private Button loginBtn;
    private TextView signupBtn;
    private ImageButton togglePasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        hiển thị activity_login lên mang hình
        setContentView(R.layout.activity_login);
        //       ánh xạ các phân tử trong layout
        loginUsername = findViewById(R.id.editUser);
        loginPassword = findViewById(R.id.editPassword);
        loginBtn = findViewById(R.id.btnLogin);
        signupBtn = findViewById(R.id.btnSignup);

        togglePasswordBtn = findViewById(R.id.btnTogglePassword);
        togglePasswordBtn.setOnClickListener(new View.OnClickListener() {
            boolean isPasswordVisible = false;

            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    loginPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    togglePasswordBtn.setImageResource(R.drawable.ic_view_pass);
                } else {
                    loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    togglePasswordBtn.setImageResource(R.drawable.ic_hide_pass);
                }
                loginPassword.setSelection(loginPassword.getText().length());
            }
        });


//        xử lý xự kiện khi nhấn nút button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validatePassword()) {

                } else {
                    checkUser();
                }
            }


        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

//        initView();
    }

    public Boolean validateUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("Username cannot be empty");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }
    public Boolean validatePassword(){
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }
    public void checkUser(){
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    loginUsername.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);
                    if (passwordFromDB.equals(userPassword)) {
                        loginUsername.setError(null);
                        String phoneFromDB = snapshot.child(userUsername).child("Phone").getValue(String.class);
                        String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                        String usernameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", userUsername);
                        startActivity(intent);
                    } else {
                        loginPassword.setError("Invalid Credentials");
                        loginPassword.requestFocus();
                    }
                } else {
                    loginUsername.setError("User does not exist");
                    loginUsername.requestFocus();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error connecting to firebase!", Toast.LENGTH_LONG).show();
            }
        });
    }


}
