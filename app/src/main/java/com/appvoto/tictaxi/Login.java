package com.appvoto.tictaxi;

import static com.appvoto.tictaxi.Home.EXTRA_CORREO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

/**
 * Created by miguelangelbuenoperez on 13/11/21...
 */

public class Login extends AppCompatActivity{
    TextInputEditText correo, passw;
    TextView olvido, registrar;
    Button ingresar;
    FirebaseAuth auth;
    ProgressDialog dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        correo = findViewById(R.id.et_correo_login);
        passw = findViewById(R.id.et_passw_login);
        olvido = findViewById(R.id.btn_olvido);
        registrar = findViewById(R.id.btn_registro_log);
        ingresar = findViewById(R.id.btn_logear);

        auth = FirebaseAuth.getInstance();
        dialogo = new ProgressDialog(this);
        dialogo.setMessage("Entrando en la APP TicTaxi...");
        dialogo.setCanceledOnTouchOutside(false);

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userpassw, usercorreo;

                userpassw = Objects.requireNonNull(passw.getText()).toString();
                usercorreo = Objects.requireNonNull(correo.getText()).toString();

                if(userpassw.isEmpty()){
                    passw.setError("No se admite este campo vacío");
                    passw.requestFocus();
                }else if(usercorreo.isEmpty()){
                    correo.setError("No se admite este campo vacío");
                    correo.requestFocus();
                } else {
                    dialogo.show();
                    auth.signInWithEmailAndPassword(usercorreo, userpassw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                dialogo.dismiss();
                                startActivity(new Intent(Login.this, Home.class).putExtra(EXTRA_CORREO, usercorreo));
                                finish();
                            } else {
                                dialogo.dismiss();
                                Toast.makeText(Login.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Registro.class));
                finish();
            }
        });

        olvido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Reset.class));
                finish();
            }
        });

    }
}