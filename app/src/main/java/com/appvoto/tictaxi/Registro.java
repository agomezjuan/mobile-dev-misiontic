package com.appvoto.tictaxi;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

/**
 * Created by miguelangelbuenoperez on 13/11/21...
 */

public class Registro extends AppCompatActivity {

    TextInputEditText nombre, celular, correo, passw;
    TextView login_reg;
    ImageView btn_google, btn_facebook;
    Button registro;
    FirebaseAuth auth;
    ProgressDialog dialogo;
    GoogleSignInClient mGoogleSignInClient;

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
        btn_google = findViewById(R.id.btn_google_reg);
        btn_facebook = findViewById(R.id.btn_facebook_reg);


        auth = FirebaseAuth.getInstance();
        dialogo = new ProgressDialog(this);
        dialogo.setMessage("Registrando nuevo Usuario");
        dialogo.setCanceledOnTouchOutside(false);

        //region Boton de registro...
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
        //endregion

        //region Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //endregion

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
            }
        });

    }

    //region Loguearse con Google...

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK){
                Intent intent = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    assert account != null;
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                }
            }
        }
    });

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(Registro.this, Home.class));
                            finish();
                            Toast.makeText(Registro.this, "Registro exitoso...", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Registro.this, "No se pudo loguear...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //endregion


    //region Al arrancar...
    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(Registro.this, Home.class));
            finish();
        }
    }
    //endregion

}
