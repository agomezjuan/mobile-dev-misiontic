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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.errorprone.annotations.FormatString;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by miguelangelbuenoperez on 13/11/21...
 */

public class Registro extends AppCompatActivity {

    TextInputEditText repassw, correo, passw, nombres, celular, panico;
    TextView login_reg;
    Button registro;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String uCorreo, uNombre, uPassw, uCelular, uPasswR, uPanico;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore mFirestore;
    ProgressDialog dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        nombres = findViewById(R.id.et_nombres_registro);
        celular = findViewById(R.id.et_celular_registro);
        panico = findViewById(R.id.et_panico_registro);
        correo = findViewById(R.id.et_correo_reg); //
        passw = findViewById(R.id.et_passw_reg); //
        repassw = findViewById(R.id.et_passwrepeat_reg); //

        registro = findViewById(R.id.btn_registrar);
        login_reg = findViewById(R.id.btn_registro_log);


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

        uNombre = Objects.requireNonNull(nombres.getText()).toString();
        uCelular = Objects.requireNonNull(celular.getText()).toString();
        uPanico = Objects.requireNonNull(panico.getText()).toString();
        uPassw = Objects.requireNonNull(passw.getText()).toString();
        SharedPreferencesUtils.setvariable(Registro.this,"nombreUser", uNombre);
        SharedPreferencesUtils.setvariable(Registro.this, "celularUser",uCelular);
        SharedPreferencesUtils.setvariable(Registro.this, "celularPanic",uPanico);
        SharedPreferencesUtils.setvariable(Registro.this,"correo", uCorreo);
        uCorreo = Objects.requireNonNull(correo.getText()).toString();
        uPasswR = Objects.requireNonNull(repassw.getText()).toString();

        if (uNombre.isEmpty()){
            nombres.setError("Entre un correo Correcto");
            nombres.requestFocus();
        } else if (!uCorreo.matches(emailPattern)){
            correo.setError("Entre un correo Correcto");
            correo.requestFocus();
        } else if (uPanico.isEmpty() || uPanico.length()<=9){
            panico.setError("Entre un número de celular(Emer) correcto");
            panico.requestFocus();
        } else if (uCelular.isEmpty() || uCelular.length()<=9){
            celular.setError("Entre un número de celular correcto");
            celular.requestFocus();
        } else if (uPassw.isEmpty() || uPassw.length()<6){
            passw.setError("Entre una contraseña correcta");
            passw.requestFocus();
        } else if (!uPassw.equals(uPasswR)){
            repassw.setError("Las contraseñas no son iguales");
            repassw.requestFocus();
        } else {
            mAuth = FirebaseAuth.getInstance();
            mFirestore = FirebaseFirestore.getInstance();
            dialogo.setMessage("Registrando nuevo Usuario...");
            dialogo.setTitle("Registrando");
            dialogo.setCanceledOnTouchOutside(false);
            dialogo.show();
            String IMEIsys = SharedPreferencesUtils.getvariable(Registro.this,"IMEIsys");
            mAuth.createUserWithEmailAndPassword(uCorreo, uPassw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        mUser = mAuth.getCurrentUser();
                        String userID = Objects.requireNonNull(mUser).getUid();
                        DocumentReference docRef = mFirestore.collection("Users").document(userID);
                        Map<String, Object> dataUser = new HashMap<>();
                        dataUser.put("nombres", uNombre);
                        dataUser.put("celular", uCelular);
                        dataUser.put("panico", uPanico);
                        dataUser.put("fechainsc", new Date().getTime());
                        dataUser.put("IMEIsys", IMEIsys);
                        docRef.set(dataUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dialogo.dismiss();
                                startActivity(new Intent(Registro.this, Home.class).putExtra(EXTRA_NOMBRES, uNombre).putExtra(EXTRA_CORREO, uCorreo));
                                Toast.makeText(Registro.this, "Registro Exitoso.", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Registro.this, "No se pudo registrar el Usuario.", Toast.LENGTH_LONG).show();
                            }
                        });


                    } else {
                        dialogo.dismiss();
                        Toast.makeText(Registro.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
