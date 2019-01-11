package com.github.sensorsreport;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import com.github.sensorsreport.data.SensorLiveData;
import com.github.sensorsreport.data.SensorData;

public class MainViewModel extends AndroidViewModel {

    private MutableLiveData<Long> reportInterval = new MutableLiveData<>();
    private final LiveData<SensorData> dataReport;

    public MainViewModel(Application application) {
        super(application);

        dataReport = Transformations.switchMap(reportInterval, input ->
                SensorLiveData.getInstance(application).reportInterval(input));
    }

    LiveData<SensorData> getDataReport() {
        return dataReport;
    }

    void setReportInterval(long reportInterval) {
        this.reportInterval.setValue(reportInterval);
    }
}
