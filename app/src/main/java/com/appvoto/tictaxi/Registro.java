package com.appvoto.tictaxi;

import static com.appvoto.tictaxi.Home.EXTRA_CORREO;
import static com.appvoto.tictaxi.Home.EXTRA_NOMBRES;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appvoto.tictaxi.Util.SharedPreferencesUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

/**
 * Created by miguelangelbuenoperez on 13/11/21...
 */

public class Registro extends AppCompatActivity {

    TextInputEditText repassw, correo, passw, nombres, celular;
    TextView login_reg;
    Button registro;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String usercorreo, usernombre, userpassw, usercelular, userpasswrepeat;
    private FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        correo = findViewById(R.id.et_correo_login); //
        passw = findViewById(R.id.et_passw_reg); //
        repassw = findViewById(R.id.et_passwrepeat_reg); //
        nombres = findViewById(R.id.et_nombres_registro);
        celular = findViewById(R.id.et_celular_registro);

        registro = findViewById(R.id.btn_registrar);
        login_reg = findViewById(R.id.btn_registro_log);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        dialogo = new ProgressDialog(this);

        //region Boton de registro...
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarAuth();
            }
        });

        login_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registro.this, Login.class));
                finish();
            }
        });
        //endregion

    }
    private void registrarAuth(){

        userpassw = Objects.requireNonNull(passw.getText()).toString();
        usernombre = Objects.requireNonNull(nombres.getText()).toString();
        usercelular = Objects.requireNonNull(celular.getText()).toString();
        SharedPreferencesUtils.setvariable(Registro.this,"nombreUser", usernombre);
        SharedPreferencesUtils.setvariable(Registro.this, "celularUser",usercelular);
        usercorreo = Objects.requireNonNull(correo.getText()).toString();
        userpasswrepeat = Objects.requireNonNull(repassw.getText()).toString();

        if (!usercorreo.matches(emailPattern)){
            correo.setError("Entre un correo Correcto");
            correo.requestFocus();
        } else if (userpassw.isEmpty() || userpassw.length()<6){
            passw.setError("Entre una contraseña correcta");
            passw.requestFocus();
        } else if (!userpassw.equals(userpasswrepeat)){
            repassw.setError("Las contraseñas no son iguales");
            repassw.requestFocus();
        } else {
            dialogo.setMessage("Registrando nuevo Usuario...");
            dialogo.setTitle("Registrando");
            dialogo.setCanceledOnTouchOutside(false);
            dialogo.show();
            mAuth.createUserWithEmailAndPassword(usercorreo, userpassw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        dialogo.dismiss();
                        proximaActivity();
                        Toast.makeText(Registro.this, "Registro Exitoso.", Toast.LENGTH_SHORT).show();
                    } else {
                        dialogo.dismiss();
                        Toast.makeText(Registro.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void proximaActivity() {
        Intent intent = new Intent(Registro.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_NOMBRES, usernombre).putExtra(EXTRA_CORREO, usercorreo);
        startActivity(intent);
    }
}
