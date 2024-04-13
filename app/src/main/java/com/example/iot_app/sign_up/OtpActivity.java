package com.example.iot_app.sign_up;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.iot_app.R;
import com.example.iot_app.sign_in.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    Long timeoutSeconds = 60L;

    String verificationCode;
    PhoneAuthProvider.ForceResendingToken  resendingToken;
    TextView resendOtpTextView, txtSentSMS;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    PinView otpInput;
    ProgressBar progressBar;
    FirebaseDatabase database;
    DatabaseReference reference;
    Button nextBtn;
    String username, password, email  ;
    String phoneNumber = "+84161214481";
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpInput = findViewById(R.id.otpViewOtpview);
        txtSentSMS = findViewById(R.id.txtSentSms);
        nextBtn = findViewById(R.id.btnVerifyOne);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTextView = findViewById(R.id.txtResend);
        ImageButton btnArrowleft = findViewById(R.id.btnArrowleft);

        btnArrowleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

/*        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            email = extras.getString("email");
            phoneNumber = extras.getString("phoneNumber");
            password = extras.getString("password");
        };*/

        Log.d("phoneNumber: ", "+84"+phoneNumber);
        txtSentSMS.setText("We\'ve sent an SMS with an activation code to your phone +84 " + phoneNumber);
        sendOtp(phoneNumber,false);

        nextBtn.setOnClickListener(v -> {
            String enteredOtp  = otpInput.getText().toString();
            Log.d("enteredOtp: ", enteredOtp);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
            signIn(credential);
        });

        resendOtpTextView.setOnClickListener((v)->{
            sendOtp(phoneNumber,true);
        });
    }

    void sendOtp(String phoneNumber,boolean isResend){
//        startResendTimer();
        setInProgress(true);
        // Turn off phone auth app verification.
        /*FirebaseAuth.getInstance().getFirebaseAuthSettings()
                .setAppVerificationDisabledForTesting(true);*/

        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
//                                resendingToken = forceResendingToken;
                                Toast.makeText(OtpActivity.this, "OTP sent successfully",Toast.LENGTH_LONG).show();
//                                AndroidUtil.showToast(getApplicationContext(),"OTP sent successfully");
//                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                Log.d("onVerificationCompleted: ", "onVerificationCompleted");
                                String code = phoneAuthCredential.getSmsCode();
                                Log.d("phoneAuthCredential", code);
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }
                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");
//                                setInProgress(false);
                            }


                        })
                ;
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential){
        //login and go to next activity
        setInProgress(true);
        String code = phoneAuthCredential.getSmsCode();
        Log.d("phoneAuthCredential", code);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    /*database = FirebaseDatabase.getInstance();
                    reference = database.getReference("users");
                    HelperClass helperClass = new HelperClass( username, email, phoneNumber, password);
                    reference.child(username).setValue(helperClass);*/
                    if (phoneAuthCredential != null) {
                        otpInput.setText(code);

                    }
                    Toast.makeText(OtpActivity.this, "You have signup successfully!", Toast.LENGTH_SHORT).show();

                    /*Intent intentSgnup = new Intent(OtpActivity.this, LoginActivity.class);
                    startActivity(intentSgnup);*/
                }else{
                    AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");
                }
            }
        });


    }

   /* void startResendTimer() {
        resendOtpTextView.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                resendOtpTextView.setText("Resend OTP in "+timeoutSeconds +" seconds");
                if(timeoutSeconds<=0){
                    timeoutSeconds =60L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        resendOtpTextView.setEnabled(true);
                    });
                }
            }
        },0,1000);
    }*/
}
