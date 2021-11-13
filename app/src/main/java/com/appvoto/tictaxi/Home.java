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
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logout = findViewById(R.id.btn_logout);
        nombres = findViewById(R.id.tv_nombreUsu);
        correo = findViewById(R.id.tv_emailUsu);
        auth = FirebaseAuth.getInstance();
        if(correo.getText().equals("MiguelAngel")){
            correo.setText(EXTRA_CORREO);
        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            String nombre = account.getDisplayName();
            String email = account.getEmail();

            nombres.setText(nombre);
            correo.setText(email);
        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(Home.this, MainActivity.class));
                finish();
            }
        });
    }

    public void irRegistro(View view) {
        startActivity(new Intent(Home.this, Registro.class));
    }

}