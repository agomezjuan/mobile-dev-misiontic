package com.appvoto.tictaxi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by miguelangelbuenoperez on 31/10/21...
 */

public class Home extends AppCompatActivity {
    public static final String EXTRA_NOMBRES = "MiguelAngel";
    public static final String EXTRA_CORREO = "fundamap";
    Button logout;
    TextView nombres, correo;
    String nombresUsu, correoUsu;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logout = findViewById(R.id.btn_logout);
        nombres = findViewById(R.id.tv_nombreUsu);
        correo = findViewById(R.id.tv_emailUsu);
        auth = FirebaseAuth.getInstance();
        correoUsu = getIntent().getStringExtra(EXTRA_CORREO);
        nombresUsu = getIntent().getStringExtra(EXTRA_NOMBRES);
        if(correoUsu == null){
            nombres.setText("Usuario no registrado Miguelito");
            correo.setText("Sin correo Miguelito");
        } else {
            nombres.setText(nombresUsu);
            correo.setText(correoUsu);
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                nombres.setText("");
                correo.setText("");
                startActivity(new Intent(Home.this, MainActivity.class));
                finish();
            }
        });
    }

    public void irRegistro(View view) {
        startActivity(new Intent(Home.this, Registro.class));
    }

}