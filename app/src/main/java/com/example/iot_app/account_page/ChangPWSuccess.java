package com.example.iot_app.account_page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.example.iot_app.R;
import com.example.iot_app.sign_in.LoginActivity;

public class ChangPWSuccess extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_changed);

        AppCompatButton btnBackLogin = findViewById(R.id.btnBackLogin);
        btnBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangPWSuccess.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
    }
