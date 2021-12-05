package com.appvoto.tictaxi;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by miguelangelbuenoperez on 03/12/21...
 */

public class MapaBase extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {

    private static final int REQUEST_CODE_AUTOCOMPLETE = 7171;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private Button btnStart;
    private FloatingActionButton fab_location_search;
    private String geoJsonSourceLayerId = "GeoJsonSourceLayerId";
    private String symbolIconId = "SymbolIconId";
    private Point originPoint = Point.fromLngLat(4.604558, -74.070221);
    private double increHora, increDia, precio;
    private String tmpo, ppprecio;
    private SwitchMaterial festivo, terminal;
    private TextView preciocarrera, ampm;
    private LinearLayout festiTerminal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.mapa_base);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), style -> {
            enableLocationComponent(style);
            addDestinationIconSymbolLayer(style);
            mapboxMap.addOnMapClickListener(this);

            btnStart = findViewById(R.id.startButton);
            btnStart.setOnClickListener(v -> {
                boolean simulateRoute = true;
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(currentRoute)
                        .shouldSimulateRoute(simulateRoute)
                        .build();

                NavigationLauncher.startNavigation(this, options);
            });

            initSearchFab();
            iniFestivo();
            iniTerminal();

            setUpSource(style);

            setUpLayer(style);

            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.marker, null);
            Bitmap bitmap = BitmapUtils.getBitmapFromDrawable(drawable);
            style.addImage(symbolIconId, bitmap);
        });
    }

    private void iniFestivo(){
        festivo = findViewById(R.id.sw_festivo);
        festivo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    precio += 2000.0;
                    DecimalFormat formato2 = new DecimalFormat("$#,###.00");
                    ppprecio = formato2.format(precio);
                    preciocarrera.setText(ppprecio);
                } else {
                    precio -= 2000.0;
                    DecimalFormat formato2 = new DecimalFormat("$#,###.00");
                    ppprecio = formato2.format(precio);
                    preciocarrera.setText(ppprecio);
                }
            }
        });
    }

    private void iniTerminal(){
        terminal = findViewById(R.id.sw_terminal);
        terminal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    precio += 4200.0;
                    DecimalFormat formato2 = new DecimalFormat("$#,###.00");
                    ppprecio = formato2.format(precio);
                    preciocarrera.setText(ppprecio);
                } else {
                    precio -= 4200.0;
                    DecimalFormat formato2 = new DecimalFormat("$#,###.00");
                    ppprecio = formato2.format(precio);
                    preciocarrera.setText(ppprecio);
                }
            }
        });
    }

    private void setUpLayer(Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geoJsonSourceLayerId).withProperties(iconImage(symbolIconId),
                iconOffset(new Float[]{0f, -8f})));
    }

    private void setUpSource(Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geoJsonSourceLayerId));
    }

    private void initSearchFab() {
        fab_location_search = findViewById(R.id.fab_locacion_search);
        fab_location_search.setOnClickListener(v -> {
            /*
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.access_token))
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#EEEEEE"))
                            .limit(10)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(this);

            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
             */
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geoJsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                    ((Point) selectedCarmenFeature.geometry()).longitude())).zoom(14)
                            .build()), 4000);
                }
            }
        }
    }

    private void addDestinationIconSymbolLayer(Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id", BitmapFactory.decodeResource(this.getResources(),
                R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);

        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true));
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        if (locationComponent.getLastKnownLocation() != null) {
            originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                    locationComponent.getLastKnownLocation().getLatitude());
        } else {
            Toast.makeText(MapaBase.this, "Temes un problema de GPS o de actualización de la APP. Desinstala la APP, reinicie su celular y vuelva a abrir.", Toast.LENGTH_LONG).show();
        }

        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
        if (source != null) {
            source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }

        getRoute(originPoint, destinationPoint);
        btnStart.setEnabled(true);
        btnStart.setBackgroundResource(R.color.mapboxBlue);
        return true;
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this).accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                        if (response.body() == null) {
                            return;
                        } else if (response.body().routes().size() < 1) {
                            return;
                        }

                        currentRoute = response.body().routes().get(0);
                        double distance = currentRoute.distance() / 1000;
                        double tiempo = currentRoute.duration() /60;
                        double residuo = currentRoute.duration() % 60;

                        ampm = findViewById(R.id.tv_ampm);
                        precio = calcularprecio(tiempo, distance);

                        DecimalFormat formato = new DecimalFormat("$#,###.00");
                        ppprecio = formato.format(precio);

                        String st = String.format("%.2f Kms", distance);
                        if (residuo == 0){
                            tmpo = String.format("%.0f Minutos", tiempo);
                        } else {
                            tmpo = String.format("%.0f Min %.0f Seg", tiempo, residuo);
                        }
                        TextView dv = findViewById(R.id.distance_view);
                        TextView mitiempo = findViewById(R.id.d);
                        festiTerminal = findViewById(R.id.ly_festiterminal);
                        preciocarrera = findViewById(R.id.s);
                        TextView distanciatv = findViewById(R.id.tv_distancia);
                        TextView tiempotv = findViewById(R.id.tv_tiempo);
                        TextView preciotv = findViewById(R.id.tv_precio);

                        distanciatv.setText("Distancia");
                        tiempotv.setText("Tiempo");
                        preciotv.setText("Precio");
                        preciocarrera.setText(ppprecio);
                        mitiempo.setText(tmpo);
                        dv.setText(st);
                        festiTerminal.setVisibility(View.VISIBLE);

                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Timber.e("Error: " + t.getMessage());
                    }
                });
    }

    private double calcularprecio(double tiempo, double distance) {
        SimpleDateFormat diasalida = new SimpleDateFormat("E", Locale.getDefault());
        SimpleDateFormat horasalida = new SimpleDateFormat("HH", Locale.getDefault());
        Date date = new Date();

        String diamig = diasalida.format(date);
        int horamig = Integer.parseInt(horasalida.format(date));
        if(horamig > 20 || horamig <= 4 ){
            ampm.setText("Nocturno");
            increHora = 2000.0;
        } else {
            ampm.setText("Normal");
            increHora = 0.0;
        }
        if (diamig.equals("Sun")){
            increDia = 2000.0;
        } else {
            increDia = 0.0;
        }
        if (distance < 2.2){
            double minima = 4200.0;
            double puertaapuerta = 800.0;
            return minima + puertaapuerta + increDia + increHora;
        } else {
            double banderazo = 2400.0;
            double espep = 85.0 * (distance / 80.0);
            double valormts = distance * 850.0;
            double recargopuesto = 800.0;
            return banderazo + espep + valormts + recargopuesto + increDia + increHora;
        }
    }


    @SuppressWarnings("MissingPermission")
    private void enableLocationComponent(Style loadedMapStyle) {

        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Necesitamos permisos para esta app", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, "No nos has otorgado permisos", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}