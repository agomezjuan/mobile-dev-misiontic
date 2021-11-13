package com.appvoto.tictaxi;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

/**
 * Created by miguelangelbuenoperez on 13/11/21...
 */

public class Registro extends AppCompatActivity {

    TextInputEditText nombre, celular, correo, passw;
    TextView login_reg;
    Button registro;
    FirebaseAuth auth;
    ProgressDialog dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        nombre = findViewById(R.id.et_nombres_registro);
        celular = findViewById(R.id.et_celular_registro);
        correo = findViewById(R.id.et_correo_registro);
        passw = findViewById(R.id.et_passw_registro);
        registro = findViewById(R.id.btn_registrar);
        login_reg = findViewById(R.id.btn_login_reg);

        auth = FirebaseAuth.getInstance();
        dialogo = new ProgressDialog(this);
        dialogo.setMessage("Registrando nuevo Usuario");
        dialogo.setCanceledOnTouchOutside(false);

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernombre, usercelular, userpassw, usercorreo;

                usernombre = Objects.requireNonNull(nombre.getText()).toString();
                usercelular = Objects.requireNonNull(celular.getText()).toString();
                userpassw = Objects.requireNonNull(passw.getText()).toString();
                usercorreo = Objects.requireNonNull(correo.getText()).toString();

                if(usernombre.isEmpty()){
                    nombre.setError("No se admite este campo vacío");
                    nombre.requestFocus();
                } else if(usercelular.isEmpty()){
                    celular.setError("No se admite este campo vacío");
                    celular.requestFocus();
                } else if(userpassw.isEmpty()){
                    passw.setError("No se admite este campo vacío");
                    passw.requestFocus();
                }else if(usercorreo.isEmpty()){
                    correo.setError("No se admite este campo vacío");
                    correo.requestFocus();
                } else {
                    dialogo.show();
                    auth.createUserWithEmailAndPassword(usercorreo, userpassw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                dialogo.dismiss();
                                startActivity(new Intent(Registro.this, Home.class));
                                finish();
                            } else {
                                dialogo.dismiss();
                                Toast.makeText(Registro.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        login_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registro.this, Login.class));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(Registro.this, Home.class));
            finish();
        }
    }
}
