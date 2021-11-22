package com.appvoto.tictaxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Reset extends AppCompatActivity {

    TextInputEditText correo;
    TextView registro_rest, salir;
    Button restablecer;
    FirebaseAuth mAuth;
    ProgressDialog dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        correo = findViewById(R.id.et_correo_rest);
        restablecer = findViewById(R.id.btn_restaurar);
        registro_rest = findViewById(R.id.btn_registro_rest);
        salir = findViewById(R.id.salir_restablecer);

        mAuth = FirebaseAuth.getInstance();
        dialogo = new ProgressDialog(this);
        dialogo.setMessage("Restaurando su clave...");
        dialogo.setCanceledOnTouchOutside(false);

        restablecer.setOnClickListener(new View.OnClickListener() {
            String usercorreo;
            @Override
            public void onClick(View view) {
                usercorreo = Objects.requireNonNull(correo.getText()).toString();
                if(!usercorreo.isEmpty()){
                    dialogo.show();
                    mAuth.sendPasswordResetEmail(usercorreo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                dialogo.dismiss();
                                Toast.makeText(Reset.this, "Se te ha enviado al correo un enlace para restablecer tu contraseña.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Reset.this, Login.class));
                                finish();
                            } else {
                                dialogo.dismiss();
                                Toast.makeText(Reset.this, "No se pudo enviar el correo de restablecimiento de contraseña", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(Reset.this, "Ingrese un correo válido", Toast.LENGTH_LONG).show();
                }

            }
        });
        registro_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Reset.this, Registro.class));
                finish();
            }
        });
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Reset.this, Login.class));
                finish();
            }
        });
    }
}