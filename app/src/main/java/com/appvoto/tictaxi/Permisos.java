package com.appvoto.tictaxi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Permisos extends AppCompatActivity {

    private static final String PREFS_FIRST_RUN = "PREFS_FIRST_RUN";
    private static final String PREFS_NAME = "TicTaxi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permisos);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ProgressBar pgintro = findViewById(R.id.sp_permiso);
        TextView tvintrop = findViewById(R.id.tv_inipermiso);
        Button btstar = findViewById(R.id.btn_inipermiso);

        int SPLASH_DISPLAY_LENGTH = 700;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                arrancada();
                pgintro.setVisibility(View.INVISIBLE);
                tvintrop.setVisibility(View.VISIBLE);
                btstar.setVisibility(View.VISIBLE);

                btstar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Permisos.this, MainActivity.class));
                        finish();
                    }
                });

            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public void arrancada() {
        if (Build.VERSION.SDK_INT >= 23 && !checkPermissionGranted()) {
            ActivityCompat.requestPermissions(Permisos.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_STATE,
            }, 0);
        }
        disableFirstRun(Permisos.this);
    }

    private boolean checkPermissionGranted() {
        int res0 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int res1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int res3 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        return res0 == PackageManager.PERMISSION_GRANTED
                && res1 == PackageManager.PERMISSION_GRANTED
                && res2 == PackageManager.PERMISSION_GRANTED
                && res3 == PackageManager.PERMISSION_GRANTED;
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void disableFirstRun(Context context) {
        getPreferences(context).edit().putBoolean(PREFS_FIRST_RUN, false).apply();
    }

    public static Boolean isTheFirstRun(Context context) {
        return getPreferences(context).getBoolean(PREFS_FIRST_RUN, true);
    }
}