package com.github.sensorsreport.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class SensorData {

    @PrimaryKey
    @SerializedName("time")
    private long id;

    @SerializedName("gps")
    @Embedded
    private Gps location;

    @SerializedName("accelerometer")
    @Embedded(prefix = "accelerometer-")
    private Position accelerometer;

    @SerializedName("gyroscope")
    @Embedded(prefix = "gyroscope-")
    private Position gyroscope;

    @SerializedName("magnetic_field")
    @Embedded(prefix = "magnetic-field-")
    private Position magneticField;

    public SensorData() {
    }

    public SensorData(Gps location, Position accelerometer, Position gyroscope,
                      Position magneticField) {
        this.location = location;
        this.accelerometer = accelerometer;
        this.gyroscope = gyroscope;
        this.magneticField = magneticField;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Gps getLocation() {
        return location;
    }

    public void setLocation(Gps location) {
        this.location = location;
    }

    public Position getAccelerometer() {
        return accelerometer;
    }

    public void setAccelerometer(Position accelerometer) {
        this.accelerometer = accelerometer;
    }

    public Position getGyroscope() {
        return gyroscope;
    }

    public void setGyroscope(Position gyroscope) {
        this.gyroscope = gyroscope;
    }

    public Position getMagneticField() {
        return magneticField;
    }

    public void setMagneticField(Position magneticField) {
        this.magneticField = magneticField;
    }

    public static class Gps {

        public double latitude;
        public double longitude;
        public double altitude;
        public long time;
        public float speed;

        public Gps(double latitude, double longitude, double altitude, long time, float speed) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
            this.time = time;
            this.speed = speed;
        }
    }

    public static class Position {

        @SerializedName("x-axis")
        private float x;
        @SerializedName("y-axis")
        private float y;
        @SerializedName("z-axis")
        private float z;

        public Position(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getZ() {
            return z;
        }

        public void setZ(float z) {
            this.z = z;
        }
    }
}
