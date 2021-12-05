package com.appvoto.tictaxi;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

/**
 * Created by miguelangelbuenoperez on 03/12/21...
 */

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_FIRST_RUN = "PREFS_FIRST_RUN";
    private static final String PREFS_NAME = "TicTaxi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        int SPLASH_DISPLAY_LENGTH = 900;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isTheFirstRun(MainActivity.this)) {
                    startActivity(new Intent(MainActivity.this, Permisos.class));
                    finish();
                } else {
                    startActivity(new Intent(MainActivity.this, Home.class));
                }
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static Boolean isTheFirstRun(Context context) {
        return getPreferences(context).getBoolean(PREFS_FIRST_RUN, true);
    }
}