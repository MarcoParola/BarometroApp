package com.example.marco.sensorapp;
import android.content.Context;
import android.hardware.*;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private boolean inizializzato;
    private SensorManager sensorManager;
    private Sensor barometro;
    TextView tv_barometro;


    // Callback in cui istanzio un SensorManager che mi fa
    // istanziare i sensori (in questo caso barometro)
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inizializzato = false;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        barometro = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        tv_barometro = (TextView) findViewById(R.id.tv_asse_barometro);
    }


    // Callback che fa ripartire SensorManager
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, barometro, SensorManager.SENSOR_DELAY_FASTEST);
    }


    // Stoppo sennò continuo a registrare
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent event) {
        registerData(event.sensor, event.values);
    }


    // Funzione in cui ricevo dati dal barometro e li scrivo su un csv
    public void registerData(Sensor s, float[] values) {

        String csv_string = "";

        if(s == barometro){
            float bar = values[0];
            long unixTime = System.currentTimeMillis();
            String currentTime;
            currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
            Log.i("current time: ", currentTime);
            tv_barometro.setText(String.valueOf(bar));
            csv_string = Long.toString(unixTime) + ',' + Float.toString(bar) + ',' + currentTime + '\n';

            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "log.csv");

                // if file doesnt exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileOutputStream fw = new FileOutputStream(file.getAbsoluteFile(), true);
                fw.write(csv_string.getBytes());
                fw.close();
                Log.i("CSV", csv_string);
            } catch (IOException e) {
                Log.e("Exception", e.getLocalizedMessage(), e);
            }
        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
