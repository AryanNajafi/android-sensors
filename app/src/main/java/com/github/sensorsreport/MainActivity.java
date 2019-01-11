package com.github.sensorsreport;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.github.sensorsreport.data.SensorData;
import com.github.sensorsreport.data.db.AppDatabase;
import com.github.sensorsreport.data.db.SensorDataDao;
import com.github.sensorsreport.databinding.MainActivityBinding;
import com.github.sensorsreport.service.ReportService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String REPORT_REQUEST_TIME = "report_request_time";
    private static final String SELECTED_INTERVAL = "selected_interval";

    private static final String[] INTERVALS = {"200", "500", "700", "1000", "1500", "2000"};

    private MainActivityBinding binding;

    private MainViewModel viewModel;

    private SharedPreferences sharedPreferences;

    private Executor diskExecutor;

    private long reportStartTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        diskExecutor = Executors.newSingleThreadExecutor();

        requestPermissions();

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getDataReport().observe(this, sensorData -> binding.setData(sensorData));

        Intent reportServiceIntent =
                new Intent(MainActivity.this, ReportService.class);

        SensorDataDao sensorDataDao = AppDatabase.getDatabase(this).sensorDataDao();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean reportRequested = sharedPreferences
                .getBoolean(ReportService.REQUESTING_REPORT, false);
        binding.setStarted(reportRequested);

        if (reportRequested) {
            reportStartTime = sharedPreferences
                    .getLong(REPORT_REQUEST_TIME, System.currentTimeMillis());
        }

        Type listOfSensorData = new TypeToken<List<SensorData>>() {}.getType();

        binding.startButton.setOnClickListener(view -> {
            reportStartTime = System.currentTimeMillis();
            ContextCompat.startForegroundService(this, reportServiceIntent);
            sharedPreferences.edit().putLong(REPORT_REQUEST_TIME, reportStartTime).apply();
        });

        binding.stopButton.setOnClickListener(view -> {
            stopService(reportServiceIntent);
            if (!isExternalStorageWritable()) {
                Toast.makeText(this, "External storage is not available!",
                        Toast.LENGTH_SHORT).show();
            } else {
                diskExecutor.execute(() -> {
                    List<SensorData> dataList = sensorDataDao.getSensorsReport(reportStartTime);
                    String json = new Gson().toJson(dataList, listOfSensorData);

                    File file = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS),
                            "sensors-report-" + reportStartTime + ".json");
                    try {
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(json);
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                binding.container.setEnabled(true);
                populateIntervalSpinner();
            } else {
                binding.container.setEnabled(false);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void populateIntervalSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, INTERVALS);
        binding.reportInterval.setAdapter(adapter);
        binding.reportInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.setReportInterval(Long.parseLong(INTERVALS[i]));
                sharedPreferences.edit().putInt(SELECTED_INTERVAL, i).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.reportInterval.setSelection(sharedPreferences.getInt(SELECTED_INTERVAL, 0));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(ReportService.REQUESTING_REPORT)) {
            binding.setStarted(sharedPreferences.getBoolean(s, false));
        }
    }
}
