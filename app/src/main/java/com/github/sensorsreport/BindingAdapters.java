package com.github.sensorsreport;

import android.databinding.BindingAdapter;
import android.support.v7.widget.AppCompatSpinner;

public class BindingAdapters {

    @BindingAdapter("spinnerEnabled")
    public static void enabled(AppCompatSpinner spinner, boolean enabled) {
        spinner.setEnabled(enabled);
    }
}
