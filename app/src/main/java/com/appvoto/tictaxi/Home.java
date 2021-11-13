package com.appvoto.tictaxi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by miguelangelbuenoperez on 31/10/21...
 */

public class Home extends AppCompatActivity {

    Button logout;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logout = findViewById(R.id.btn_logout);
        auth = FirebaseAuth.getInstance();

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