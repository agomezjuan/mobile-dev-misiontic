package com.appvoto.tictaxi;

import static com.appvoto.tictaxi.Home.EXTRA_CORREO;
import static com.appvoto.tictaxi.Home.EXTRA_NOMBRES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneSMSActivity extends AppCompatActivity {

    EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    TextView celular, nuevoSms;
    ProgressBar progresms;
    Button verificado;
    String verificarId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_smsactivity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);
        progresms = findViewById(R.id.progressPhoneSms);
        verificado = findViewById(R.id.btn_sms_phone);
        celular = findViewById(R.id.tv_celular);
        nuevoSms = findViewById(R.id.tv_newsms);

        celular.setText(String.format(
                "+57-%s", getIntent().getStringExtra("movil")
        ));

        setSMSinput();

        verificarId = getIntent().getStringExtra("verificacionId");
        verificado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputCode1.getText().toString().trim().isEmpty()
                || inputCode2.getText().toString().trim().isEmpty()
                || inputCode3.getText().toString().trim().isEmpty()
                || inputCode4.getText().toString().trim().isEmpty()
                || inputCode5.getText().toString().trim().isEmpty()
                || inputCode6.getText().toString().trim().isEmpty()){
                    Toast.makeText(PhoneSMSActivity.this, "Por favor agregar el Código recibido", Toast.LENGTH_SHORT).show();
                    return;
                }

                String code = inputCode1.getText().toString() +
                        inputCode2.getText().toString() +
                        inputCode3.getText().toString() +
                        inputCode4.getText().toString() +
                        inputCode5.getText().toString() +
                        inputCode6.getText().toString();
                if (verificarId != null){
                    progresms.setVisibility(View.VISIBLE);
                    verificado.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificarId,
                            code
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progresms.setVisibility(View.GONE);
                                    verificado.setVisibility(View.VISIBLE);
                                    if (task.isSuccessful()){
                                        startActivity(new Intent(PhoneSMSActivity.this, Home.class)
                                        .putExtra(EXTRA_NOMBRES, "Su propio Celular")
                                        .putExtra(EXTRA_CORREO, celular.getText().toString()));
                                    } else {
                                        Toast.makeText(PhoneSMSActivity.this, "Código de verificación Inválido", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        nuevoSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+57" + getIntent().getStringExtra("movil"),
                        60,
                        TimeUnit.SECONDS,
                        PhoneSMSActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(PhoneSMSActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newverifcacionId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificarId = newverifcacionId;
                                Toast.makeText(PhoneSMSActivity.this, "Reenviando Código... Debes esperar un minuto", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });

    }

    private void setSMSinput(){
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charS, int i, int i1, int i2) {
                if (!charS.toString().trim().isEmpty()){
                    inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charS, int i, int i1, int i2) {
                if (!charS.toString().trim().isEmpty()){
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charS, int i, int i1, int i2) {
                if (!charS.toString().trim().isEmpty()){
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charS, int i, int i1, int i2) {
                if (!charS.toString().trim().isEmpty()){
                    inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charS, int i, int i1, int i2) {
                if (!charS.toString().trim().isEmpty()){
                    inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}