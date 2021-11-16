package com.appvoto.tictaxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private static final String TAG = "PhoneAuthActivity";
    FirebaseAuth mAuthPhone;
    TextInputEditText phoneSms;
    Button regresar, enviarSms;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_auth);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        phoneSms = findViewById(R.id.et_celular_phone);
        enviarSms = findViewById(R.id.btn_send_phone);
        regresar = findViewById(R.id.btn_regresar_phone);
        progressBar = findViewById(R.id.progressPhone);

        mAuthPhone = FirebaseAuth.getInstance();

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PhoneAuthActivity.this, Login.class));
            }
        });
        enviarSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneSms.getText().toString().trim().isEmpty()){
                    Toast.makeText(PhoneAuthActivity.this, "Entra un número Celular", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                enviarSms.setVisibility(View.INVISIBLE);
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+57" + phoneSms.getText().toString(),
                        60,
                        TimeUnit.SECONDS,
                        PhoneAuthActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressBar.setVisibility(View.GONE);
                                enviarSms.setVisibility(View.VISIBLE);

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.GONE);
                                enviarSms.setVisibility(View.VISIBLE);
                                Toast.makeText(PhoneAuthActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verifcacionId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                progressBar.setVisibility(View.GONE);
                                enviarSms.setVisibility(View.VISIBLE);
                                startActivity(new Intent(PhoneAuthActivity.this, PhoneSMSActivity.class)
                                        .putExtra("movil", phoneSms.getText().toString())
                                .putExtra("verificacionId",verifcacionId));
                            }
                        }
                );

                }
        });

    }
}