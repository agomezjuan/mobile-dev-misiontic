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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appvoto.tictaxi.Util.SharedPreferencesUtils;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
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

public class Login extends AppCompatActivity{
    TextInputEditText correo, passw;
    TextView olvido, registrar;
    ImageView btn_google, btn_facebook;
    Button ingresar;
    FirebaseAuth auth;
    ProgressDialog dialogo;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        correo = findViewById(R.id.et_correo_login);
        passw = findViewById(R.id.et_passw_login);
        olvido = findViewById(R.id.btn_olvido);
        registrar = findViewById(R.id.btn_registro_log);
        ingresar = findViewById(R.id.btn_logear);
        btn_facebook = findViewById(R.id.btn_facebook_log);
        btn_google = findViewById(R.id.btn_google_log);

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
                                    usernombres = "Miguelito";
                                }
                                startActivity(new Intent(Login.this, Home.class).putExtra(EXTRA_CORREO, usercorreo).putExtra(EXTRA_NOMBRES, usernombres));
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
                            startActivity(new Intent(Login.this, Home.class));
                            finish();
                            Toast.makeText(Login.this, "Registro exitoso...", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, "No se pudo loguear...", Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(Login.this, Home.class));
            finish();
        }
    }
    //endregion

}