## Android GPS and sensors data

An Android application that displays and records GPS and Sensors data.
Exports recorded data in a JSON file in Android external storage Downloads Folder.

> Application is not depends on Play services FusedLocationApi. GPS data gather from Android Location Service

## Currently supported data

1. GPS: Latitude, Longitude, Altitude, Time and Speed

2. Accelorometer: x-axis, y-axis and z-axis in m/s^2

3. Gyroscope: x-axis, y-axis and z-axis in rad/s 

4. Magnetic field: x-axis, y-axis and z-axis in μT

## Exported data JSON format
```json
[
  {
    "gps": {
      "altitude": 1234,
      "latitude": 35.0,
      "longitude": 51.0,
      "speed": 0.0,
      "time": 1547231318000
    },
    "accelerometer": {
      "x-axis": -0.28977966,
      "y-axis": 9.585098,
      "z-axis": -0.651062
    },
    "gyroscope": {
      "x-axis": -0.03378296,
      "y-axis": -0.118652344,
      "z-axis": 0.004623413
    },
    "magnetic_field": {
      "x-axis": 14.596558,
      "y-axis": -79.14429,
      "z-axis": -10.694885
    },
    "time": 1547231318427
  }
]
```

## Prerequisites
- Android API Level > 19
- Android device with above-mentioned hardware sensors
