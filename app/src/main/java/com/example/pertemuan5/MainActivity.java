package com.example.pertemuan5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //Membuat Sensor Manager
    private SensorManager mSensorManager;

    //Sensor medan geomagnetik yang dikombinasikan dengan akselerometer untuk menentukan posisi perangkat yang relatif dengan kutub utara magnetik.
    //Membuat variabel untuk sensor
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnometer;

    //Mengambil elemen Text View
    private TextView mTextSensorAzimuth;
    private TextView mTextSensorPitch;
    private TextView mTextSensorRoll;

    //Membuat variabel baru untuk atribut
    private float[] mAcclerometerData = new float[3];
    private float[] mMagnetometerData= new float[3];

    //Mengambil nilai image view
    private ImageView mSpotTop;
    private ImageView mSpotBottom;
    private ImageView mSpotRight;
    private ImageView mSpotLeft;

    private static final float VALUE_DRIFT = 0.05f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Merubah orientation dari activity
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Menginisiasi atau memanggil elemen text view untuk menampilkan value sensor
        mTextSensorAzimuth = findViewById(R.id.value_azimuth);
        mTextSensorPitch = findViewById(R.id.value_pitch);
        mTextSensorRoll = findViewById(R.id.value_roll);

        //Menginisiasi Sensor Manager dan Tipe sensor
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorMagnometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //Menginisiasi Imange View yang digunakan
        mSpotTop = findViewById(R.id.spot_top);
        mSpotBottom = findViewById(R.id.spot_bottom);
        mSpotRight = findViewById(R.id.spot_right);
        mSpotLeft = findViewById(R.id.spot_left);
    }

    //Mmebuat method untuk memulai sensor dan menghentikan sensor
    public void onStart() {
        super.onStart();

        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorMagnometer != null) {
            mSensorManager.registerListener(this, mSensorMagnometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onStop(){
        super.onStop();
        //Menghentikan event, dengan mengunregister listenernya
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mAcclerometerData = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = event.values.clone();
                break;
            default:
                return;
        }

        //Sensor medan geomagnetik dan akselerometer menampilkan array multi-dimensi dari nilai sensor untuk masing-masing SensorEvent
        //Mentranslate koordinate realtiv sebuah device menjadi realtive bumi
        //Untuk menampung sebuah matrix;
        float[] rotationMatrix = new float[9];
        boolean rotationOk = SensorManager.getRotationMatrix(rotationMatrix, null, mAcclerometerData, mMagnetometerData);

        float orientationValues[] = new float[3];
        if (rotationOk){
            SensorManager.getOrientation(rotationMatrix, orientationValues);
        }
        //Mengambil value sensor
        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];

        //Menampilkan value
        mTextSensorRoll.setText(getResources().getString(R.string.value_format, roll));
        mTextSensorPitch.setText(getResources().getString(R.string.value_format, pitch));
        mTextSensorAzimuth.setText(getResources().getString(R.string.value_format, azimuth));

        //Membuat kondisi
        if (Math.abs(pitch) < VALUE_DRIFT){
            pitch = 0;
        }
        if (Math.abs(roll) < VALUE_DRIFT){
            roll = 0;
        }

        mSpotTop.setAlpha(0f);
        mSpotRight.setAlpha(0f);
        mSpotLeft.setAlpha(0f);
        mSpotBottom.setAlpha(0f);

        //Membuat kondisi kecenderungan device menghadap kemana
        if (pitch > 0){
            mSpotBottom.setAlpha(pitch);
        }else{
            mSpotTop.setAlpha(Math.abs(pitch));
        }

        if (roll > 0){
            mSpotLeft.setAlpha(roll);
        }else{
            mSpotRight.setAlpha(Math.abs(roll));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}