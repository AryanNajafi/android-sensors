package com.github.sensorsreport.data;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;

public class SensorLiveData extends LiveData<SensorData> implements
        SensorEventListener, LocationListener {

    private static SensorLiveData instance;

    private SensorManager sensorManager;

    private Sensor accelerometerSensor;
    private Sensor gyroscopeSensor;
    private Sensor magneticFieldSensor;

    private LocationManager locationManager;

    private CountDownTimer timer;

    private boolean isDelivered;

    private SensorData sensorData;

    public static SensorLiveData getInstance(Context context) {
        if (instance == null) {
            instance = new SensorLiveData(context.getApplicationContext());
        }
        return instance;
    }

    private SensorLiveData(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        initTimer(1000);

        sensorData = new SensorData();
    }

    public SensorLiveData reportInterval(long interval) {
        timer.cancel();
        initTimer(interval);
        timer.start();
        return this;
    }

    private void initTimer(long interval) {
        timer = new CountDownTimer(3600000, interval) {
            @Override
            public void onTick(long l) {
                isDelivered = false;
            }

            @Override
            public void onFinish() {

            }
        };
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActive() {
        super.onActive();
        sensorManager.registerListener(this, accelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscopeSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magneticFieldSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, this);
        timer.start();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onInactive() {
        super.onInactive();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
        timer.cancel();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;

        SensorData.Position data = new SensorData.Position(values[0], values[1], values[2]);

        switch (sensorEvent.sensor.getType()) {

            case  Sensor.TYPE_ACCELEROMETER:
                sensorData.setAccelerometer(data);
                break;

            case Sensor.TYPE_GYROSCOPE:
                sensorData.setGyroscope(data);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorData.setMagneticField(data);
                break;

            default:
                break;
        }
        if (!isDelivered) {
            sensorData.setId(System.currentTimeMillis());
            setValue(sensorData);
            isDelivered = true;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        sensorData.setLocation(new SensorData.Gps(
                location.getLatitude(),
                location.getLongitude(),
                location.getAltitude(),
                location.getTime(),
                location.getSpeed()));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
