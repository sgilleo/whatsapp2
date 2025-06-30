package es.upv.teleco.whatsapp2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.MessageFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

    TextView heading, latitud, longitud, altitud;
    ImageView compass;
    TextView unidades;

    //Variable para mostrar la altitud en metros si es true y en pies si es false
    boolean isM = true;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        heading = findViewById(R.id.heading);
        compass = findViewById(R.id.compass);
        latitud = findViewById(R.id.latitud);
        longitud = findViewById(R.id.longitud);
        altitud = findViewById(R.id.altitud);
        unidades = findViewById(R.id.unidades);

        unidades.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                isM = !isM;

                if(isM){
                    unidades.setText("Cambiar a FT");
                    compass.setImageResource(R.drawable.rosa_de_los_vientos);

                }
                else{
                    unidades.setText("Cambiar a M");
                    compass.setImageResource(R.drawable.sus);
                }
            }
        });


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        //Gestion de permisos de ubicacion

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Explicacion de la necesidad de proporcionar permiso (y poder proporcionarlo)
                latitud.setText("Permisos denegados");
                longitud.setText("Permisos denegados");
            } else {
                // Petición de permiso por primera vez (por lo tanto, sin haber sido negado previamente)
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        100);
            }
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magnetometer);
        locationManager.removeUpdates(this);
    }


    //Actualizar Brújula a partir de los cambios del magnetómetro y acelerometro
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, gravity,
                    0, gravity.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, geomagnetic,
                    0, geomagnetic.length);
        }

        if(gravity != null && geomagnetic != null){
            float[] R = new float[9];
            float[] I = new float[9];
            if(SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)){
                float[] orientation = new float[3];

                SensorManager.getOrientation(R, orientation);

                float azimuth = (float) Math.toDegrees(orientation[0]);

                azimuth = (azimuth + 360) % 360;

                heading.setText((int)azimuth+"º");

                compass.setRotation(-azimuth);
                
            }
        }



    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double alt = location.getAltitude();
        if(isM){
            altitud.setText((int)alt+" m");
        }
        else{
            altitud.setText((int)(alt*3.281)+" ft");
        }

        latitud.setText(String.format("%.6f", location.getLatitude()));
        longitud.setText(String.format("%.6f", location.getLongitude()));

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}