package com.github.sensorsreport.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.github.sensorsreport.data.SensorData;

import java.util.List;

@Dao
public interface SensorDataDao {

    @Query("SELECT * FROM sensordata WHERE id > :reportStartTime")
    List<SensorData> getSensorsReport(long reportStartTime);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SensorData data);
}
