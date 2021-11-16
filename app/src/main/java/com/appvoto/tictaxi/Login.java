package com.appvoto.tictaxi;

import static com.appvoto.tictaxi.Home.EXTRA_CORREO;
import static com.appvoto.tictaxi.Home.EXTRA_NOMBRES;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appvoto.tictaxi.Util.SharedPreferencesUtils;
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
    ImageView btn_google, btn_facebook, btn_github, btn_phone, btn_twitter;
    Button ingresar;
    FirebaseAuth auth;
    ProgressDialog dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        correo = findViewById(R.id.et_correo_login);
        passw = findViewById(R.id.et_passw_reg);
        olvido = findViewById(R.id.btn_olvido);
        registrar = findViewById(R.id.btn_registro_log);
        ingresar = findViewById(R.id.btn_logear);
        btn_google = findViewById(R.id.btn_google_logo);
        btn_facebook = findViewById(R.id.btn_facebook_logo);
        btn_github = findViewById(R.id.btn_github_log);
        btn_phone = findViewById(R.id.btn_phone_log);
        btn_twitter = findViewById(R.id.btn_twitter_log);

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
                                String usernombres = SharedPreferencesUtils.getvariable(Login.this, "NombreUs");
                                if(usernombres.isEmpty() || usernombres == null){
                                    usernombres = "No tienes nombres Miguelito";
                                }
                                startActivity(new Intent(Login.this, Home.class)
                                        .putExtra(EXTRA_CORREO, usercorreo)
                                        .putExtra(EXTRA_NOMBRES, usernombres)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
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

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, GoogleSignInActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });

        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, FacebookAuthActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });

        btn_github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, GithubAuthActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });

        btn_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, PhoneAuthActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });

        btn_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, TwitterAuthActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
    }

    //region Al arrancar...
    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(Login.this, Home.class));
            finish();
        }
    }
    //endregion

}