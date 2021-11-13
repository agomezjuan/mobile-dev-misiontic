package com.appvoto.tictaxi;

import static com.appvoto.tictaxi.Intro.LosPermisos.EXTRA_FCHPERM;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import java.util.Calendar;

import com.appvoto.tictaxi.Intro.LosPermisos;
import com.appvoto.tictaxi.Util.Device;
import com.appvoto.tictaxi.Util.SharedPreferencesUtils;

/**
 * Created by miguelangelbuenoperez on 31/10/21...
 */

public class MainActivity extends AppCompatActivity {

    private String FechaHoy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int SPLASH_DISPLAY_LENGTH = 700;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sacaFecha();
                if (SharedPreferencesUtils.isTheFirstRun(MainActivity.this)) {
                    startActivity(new Intent(MainActivity.this, LosPermisos.class).putExtra(EXTRA_FCHPERM, FechaHoy));
                    finish();
                } else {
                    @SuppressLint("HardwareIds") String anIDsys = Settings.Secure.getString(MainActivity.this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    String IMEIsys = Device.idUUID(MainActivity.this);
                    //String dirApp = SharedPreferencesUtils.getvariable(MainActivity.this, "dirApp");
                    SharedPreferencesUtils.setvariable(MainActivity.this, "IMEIsys", IMEIsys);
                    SharedPreferencesUtils.setvariable(MainActivity.this, "anIDsys", anIDsys);
                    startActivity(new Intent(MainActivity.this,Home.class));
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    //region OK... SACA LAS FECHAS DE HOY Y LAS CREA...
    public void sacaFecha(){
        final Calendar midate = Calendar.getInstance();
        String yearmi = String.valueOf(midate.get(Calendar.YEAR));
        String mesmi = String.valueOf(midate.get(Calendar.MONTH) + 1);
        String HORAMI = String.valueOf(midate.get(Calendar.HOUR));
        String MINUTEMI = String.valueOf(midate.get(Calendar.MINUTE));
        String SECONDMI = String.valueOf(midate.get(Calendar.SECOND));
        String mesmiMas = "";
        if (Integer.parseInt(mesmi) <= 9) {
            mesmi = "0" + mesmi;
        }
        String diami = String.valueOf(midate.get(Calendar.DAY_OF_MONTH));
        if (Integer.parseInt(diami) <= 9) {
            diami = "0" + diami;
        }
        if (Integer.parseInt(HORAMI) <= 9) {
            HORAMI = "0" + HORAMI;
        }
        if (Integer.parseInt(MINUTEMI) <= 9) {
            MINUTEMI = "0" + MINUTEMI;
        }
        if (Integer.parseInt(SECONDMI) <= 9) {
            SECONDMI = "0" + SECONDMI;
        }
        FechaHoy = yearmi + mesmi + diami;
    }
    //endregion

}