package Intro;


import static Util.constante.bckAppTicTaxi;
import static Util.constante.inkAppTicTaxi;
import static Util.constante.raizAppTicTaxi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.appvoto.tictaxi.MainActivity;
import com.appvoto.tictaxi.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import Util.Device;
import Util.SharedPreferencesUtils;

/**
 * Created by miguelangelbuenoperez on 31/10/21...
 */

public class LosPermisos extends AppCompatActivity {

    public static final String EXTRA_CLUF = "datoCLUF";
    public static final String EXTRA_FCHPERM = "datoFECHA";
    private Button acepta;
    private TextView mensajePermisos, titulopermisos;
    private File dirOut, dirApp, dirIn, dirZip;
    private String dirBase, clufbase, IMEIsys, anIDsys;
    private CheckBox avis;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.los_permisos);

        acepta = findViewById(R.id.btn_perm_ini);
        mensajePermisos = findViewById(R.id.txt_perm_ini);
        titulopermisos = findViewById(R.id.txt_perm_iniTit);
        avis = findViewById(R.id.chbx_aceptcluf);

        //region OK... Cambios en las pantallas...
        final String elcluf = getIntent().getStringExtra(EXTRA_CLUF);
        if (elcluf == null) {
            clufbase = "base";
        } else {
            clufbase = elcluf;
        }

        switch (clufbase) {
            case "base":
                acepta.setText("Autorizar");
                titulopermisos.setText("Autorizaciones");
                mensajePermisos.setText("Debes Autorizarme los siguientes permisos para funcionar correctamente:\nTel??fono: Para emergencias.\nArchivos: Para crear tu Base de Datos.\nDale clic en Autorizar.");
                break;
            case "CLUF":
                acepta.setText("Contenido del CLUF");
                recuperarContrato("CLUF");
                titulopermisos.setText("CONTRATO DE LICENCIA DE USUARIO FINAL");
                break;
            case "AVIS":
                acepta.setText("Aviso Privacidad");
                recuperarContrato("AVIS");
                titulopermisos.setText("AVISO DE PRIVACIDAD");
                break;
        }
        //endregion

        //region OK... BOTON ACEPTAR ??nico bot??n que hay...
        acepta.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HardwareIds")
            @Override
            public void onClick(View view) {
                switch (acepta.getText().toString()) {
                    case "Autorizar":
                        arrancada();
                        acepta.setText("Crea Directorios");
                        titulopermisos.setText("Directorios de TicTaxi");
                        mensajePermisos.setText("Se va a crear la siguiente Carpeta en tu directorio DCIM/AppTicTaxi_bck...\nPara enviarte all??, todos tus archivos comprimidos.\nMBueno admin.");
                        break;
                    case "Crea Directorios":
                        // Directorio para exportar la base de datos...
                        dirOut = new File(String.format("%s%s%s%s", Environment.getExternalStorageDirectory(), "/",
                                Environment.DIRECTORY_DCIM, bckAppTicTaxi));
                        if (!dirOut.exists()) {
                            dirOut.mkdirs();
                        }
                        dirIn = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + inkAppTicTaxi);
                        if (!dirIn.exists()) {
                            dirIn.mkdirs();
                        }
                        // Verifica que tenga SDCard en el Celular y crear un archivo...
                        dirBase = isRemovableSDCardAvailable();
                        if (dirBase == null || dirBase.equals("/storage/extSdCard") || dirBase.equals("/storage/sdcard1")) {
                            File externalStorageList[] = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                externalStorageList = getApplicationContext().getExternalFilesDirs(null);
                            }
                            dirBase = null;
                            if (externalStorageList != null) {
                                dirBase = externalStorageList[0].getAbsolutePath();
                            }
                            String montaInicial = canCreateFile(dirBase);
                            dirApp = new File(String.format("%s%s", dirBase, raizAppTicTaxi));
                            if (!dirApp.exists()) {
                                dirApp.mkdirs();
                            }
                        } else {
                            String montaInicial = canCreateFile(dirBase);
                            dirApp = new File(String.format("%s%s", dirBase, raizAppTicTaxi));
                            if (!dirApp.exists()) {
                                dirApp.mkdirs();
                            }
                        }
                        acepta.setText("Aceptar el CLUF");
                        recuperarContrato("CLUF");
                        avis.setVisibility(View.VISIBLE);
                        avis.setText("Acepto los t??rminos del contrato");
                        titulopermisos.setText("CONTRATO DE LICENCIA DE USUARIO FINAL");

                        anIDsys = Settings.Secure.getString(LosPermisos.this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                        IMEIsys = Device.idUUID(LosPermisos.this);
                        String fechaInstala = getIntent().getStringExtra(EXTRA_FCHPERM);
                        SharedPreferencesUtils.setvariable(LosPermisos.this, "IMEIsys", IMEIsys);
                        SharedPreferencesUtils.setvariable(LosPermisos.this, "anIDsys", anIDsys);
                        SharedPreferencesUtils.setvariable(LosPermisos.this, "dirOut", String.valueOf(dirOut));
                        SharedPreferencesUtils.setvariable(LosPermisos.this, "dirIn", String.valueOf(dirIn));
                        SharedPreferencesUtils.setvariable(LosPermisos.this, "dirApp", String.valueOf(dirApp));
                        SharedPreferencesUtils.setvariable(LosPermisos.this, "fchIn", fechaInstala);
                        break;
                    case "Aceptar el CLUF":
                        if (avis.isChecked()) {
                            titulopermisos.setText("LEY DATAS PERSONAL");
                            mensajePermisos.setText("Es bueno leer mas sobre la Ley de protecci??n de los datos personales, Ley 1581 de 2002. Usted ser?? ahora responsable de los datos que aqu?? cree.\nCondideramos que usted entiende los alcances de la ley");
                            acepta.setText("Leer Aviso Privacidad");
                            avis.setVisibility(View.INVISIBLE);
                            avis.setChecked(false);
                        } else {
                            Toast.makeText(LosPermisos.this, "Por favor acepta los t??rminos del contrato o cancela la instalaci??n", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case "Leer Aviso Privacidad":
                        recuperarContrato("AVIS");
                        avis.setVisibility(View.VISIBLE);
                        avis.setText("Estoy entendido del Aviso de Privacidad");
                        avis.setChecked(false);
                        acepta.setText("Aceptar el Data");
                        titulopermisos.setText("AVISO DE PRIVACIDAD");
                        acepta.setSelected(true);
                        SharedPreferencesUtils.setvariable(LosPermisos.this, "CLUF", "OK");
                        break;
                    case "Aceptar el Data":
                        if (avis.isChecked()) {
                            acepta.setText("iniciar TicTaxi");
                            titulopermisos.setText("INSCRIPCI??N");
                            avis.setVisibility(View.INVISIBLE);
                            SharedPreferencesUtils.setvariable(LosPermisos.this, "AVIS", "OK");
                            mensajePermisos.setText("Gracias por ser parte de TicTaxi ?? 2021\nSeguidamente lo invitamos a registrarse como Usuario PRINCIPAL y propietario de la base de datos que usted mismo cree.");
                        } else {
                            Toast.makeText(LosPermisos.this, "Por favor acepta el Aviso de Privacidad o cancela la instalaci??n", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case "iniciar TicTaxi":
                        SharedPreferencesUtils.disableFirstRun(LosPermisos.this);
                        startActivity(new Intent(LosPermisos.this, MainActivity.class));
                        finish();
                        break;
                    case "Contenido del CLUF":
                        finish();
                        break;
                    case "Aviso Privacidad":
                        finish();
                        break;
                }
            }
        });
        //endregion
    }

    //region Ok... SETEAR LOS PERMISOS QUE NECESITA LA TicTaxi
    public void arrancada() {
        if (Build.VERSION.SDK_INT >= 23 && !checkPermissionGranted()) {
            ActivityCompat.requestPermissions(LosPermisos.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            }, 0);
        }
    }

    private boolean checkPermissionGranted() {
        int res0 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int res1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int res3 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int res4 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return res0 == PackageManager.PERMISSION_GRANTED
                && res1 == PackageManager.PERMISSION_GRANTED
                && res2 == PackageManager.PERMISSION_GRANTED
                && res3 == PackageManager.PERMISSION_GRANTED
                && res4 == PackageManager.PERMISSION_GRANTED;
    }

    public String isRemovableSDCardAvailable() {
        final String FLAG = "mnt";
        final String SECONDARY_STORAGE = System.getenv("SECONDARY_STORAGE");
        final String EXTERNAL_STORAGE_DOCOMO = System.getenv("EXTERNAL_STORAGE_DOCOMO");
        final String EXTERNAL_SDCARD_STORAGE = System.getenv("EXTERNAL_SDCARD_STORAGE");
        final String EXTERNAL_SD_STORAGE = System.getenv("EXTERNAL_SD_STORAGE");
        final String EXTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE");
        Map<Integer, String> listEnvironmentVariableStoreSDCardRootDirectory = new HashMap<Integer, String>();
        listEnvironmentVariableStoreSDCardRootDirectory.put(0, SECONDARY_STORAGE);
        listEnvironmentVariableStoreSDCardRootDirectory.put(1, EXTERNAL_STORAGE_DOCOMO);
        listEnvironmentVariableStoreSDCardRootDirectory.put(2, EXTERNAL_SDCARD_STORAGE);
        listEnvironmentVariableStoreSDCardRootDirectory.put(3, EXTERNAL_SD_STORAGE);
        listEnvironmentVariableStoreSDCardRootDirectory.put(4, EXTERNAL_STORAGE);
        File externalStorageList[] = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            externalStorageList = getApplicationContext().getExternalFilesDirs(null);
        }
        String directory = null;
        int size = listEnvironmentVariableStoreSDCardRootDirectory.size();
        for (int i = 0; i < size; i++) {
            if (externalStorageList != null && externalStorageList.length > 1 && externalStorageList[1] != null) {
                directory = externalStorageList[1].getAbsolutePath();
            } else {
                directory = listEnvironmentVariableStoreSDCardRootDirectory.get(i);
            }
            if (directory != null && directory.length() != 0) {
                if (i == size - 1) {
                    if (directory.contains(FLAG)) {
                        return directory;
                    } else {
                        return null;
                    }
                }
                return directory;
            }
        }
        return null;
    }

    public String canCreateFile(String directory) {
        final String FILE_DIR = directory + File.separator + "TicTaxi.txt";
        File tempFlie = null;
        try {
            tempFlie = new File(FILE_DIR);
            FileOutputStream fos = new FileOutputStream(tempFlie);
            fos.write(new byte[1024]);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            ;
            return null;
        }
        return FILE_DIR;
    }
    //endregion

    //region OK... CONTRATO DE LICENCIA DE USURIO FINAL Y/O AVISO DE PRIVACIDAD...
    public void recuperarContrato(@NonNull String nomFile) {
        if (nomFile.equals("CLUF")) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(this.getResources().openRawResource(R.raw.cluf_tictaxi)));
                String linea = br.readLine();
                String mDato = "";
                while (linea != null) {
                    mDato = String.format("%s%s\n", mDato, linea);
                    linea = br.readLine();
                }
                br.close();
                mensajePermisos.setText(Html.fromHtml(mDato));
            } catch (IOException e) {
                Toast.makeText(this, "No se pudo leer",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(this.getResources().openRawResource(R.raw.avprivacidad)));
                String linea = br.readLine();
                String mDato = "";
                while (linea != null) {
                    mDato = String.format("%s%s\n", mDato, linea);
                    linea = br.readLine();
                }
                br.close();
                mensajePermisos.setText(Html.fromHtml(mDato));
            } catch (IOException e) {
                Toast.makeText(this, "No se pudo leer",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
//endregion