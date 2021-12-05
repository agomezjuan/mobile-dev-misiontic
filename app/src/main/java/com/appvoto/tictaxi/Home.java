package com.appvoto.tictaxi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by miguelangelbuenoperez on 03/12/21...
 */

public class Home extends AppCompatActivity {
    Button logout;
    TextView nombres, correo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        logout = findViewById(R.id.btn_logout);
        nombres = findViewById(R.id.tv_nombreUsu);
        correo = findViewById(R.id.tv_emailUsu);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                System.exit(0);
            }
        });
    }

    public void irRegistro(View view) {
        startActivity(new Intent(Home.this, MapaBase.class));
    }

}