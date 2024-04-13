package com.example.iot_app.sign_up;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iot_app.R;
import com.example.iot_app.sign_in.LoginActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    EditText signupUsername, signupEmail, signupPassword, signup_phone;
    Button signupButton;
    TextView signinButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    private ImageButton togglePasswordBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signupEmail = findViewById(R.id.editEmail);
        signupUsername = findViewById(R.id.editUser);
        signupPassword = findViewById(R.id.editPassword);
//        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.btnSignup);
        signinButton = findViewById(R.id.btnlogin);
        signup_phone = findViewById(R.id.editPhone);

        togglePasswordBtn = findViewById(R.id.btnTogglePassword);
        togglePasswordBtn.setOnClickListener(new View.OnClickListener() {
            boolean isPasswordVisible = false;

            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    signupPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    togglePasswordBtn.setImageResource(R.drawable.ic_view_pass);
                } else {
                    signupPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    togglePasswordBtn.setImageResource(R.drawable.ic_hide_pass);
                }
                signupPassword.setSelection(signupPassword.getText().length());
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");
                String phoneNumber = signup_phone.getText().toString();
                String email = signupEmail.getText().toString();
                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();
                Log.d("username", username);
                // Check if the inputs are valid

                if (!isValidPhoneNumber(phoneNumber)| !isValidUserName(username) | !isValidPassword(password) | !isValidEmail(email) ) {

                } else {
                    HelperClass helperClass = new HelperClass( username, email, phoneNumber, password);
                    reference.child(username).setValue(helperClass);
                    Toast.makeText(SignUpActivity.this, "You have signup successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

   /*             Intent intent = new Intent(SignUpActivity.this, OtpActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("email", email);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("password", password);
                startActivity(intent);*/
            }
        });
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isValidUserName(String username) {
        if (username.isEmpty()) {
            signupUsername.setError("Username cannot be empty");
            return false;
            }else {
            return true;
        }
    }

    private boolean isValidEmail(String email) {
        // Check if the email is valid
        if (email.isEmpty()) {
            signupEmail.setError("Email cannot be empty");
            return false;
        }  else if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signupEmail.setError(null);
            return true;
        }else{
            signupEmail.setError("Invalid email");
            return false;
        }
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=?])(?=\\S+$).{8,}$";
        // Check if the password is valid
        // Password must have at least 8 characters including lowercase letters, uppercase letters, special characters and numbers
        if (password.isEmpty()) {
            signupPassword.setError("Password cannot be empty");
            return false;
        }  else if(password.matches(passwordPattern)){
            signupPassword.setError(null);
            return true;
        }else{
            signupPassword.setError("Invalid Password");
            return false;
        }

    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Check if the phone number is valid
        // Phone number must have exactly 10 numbers starting with 0 with no characters
        String phoneNumberPattern = "^0[0-9]{9}$";
        if (phoneNumber.isEmpty()) {
            signup_phone.setError("Phone Number cannot be empty");
            return false;
        }  else if(phoneNumber.matches(phoneNumberPattern)){
            signup_phone.setError(null);
            return true;
        }else{
            signup_phone.setError("Phone Number Invalid");
            return false;
        }

    }
}
