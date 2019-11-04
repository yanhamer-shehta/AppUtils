package com.yanhamer.apputils;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.yanhamer.app_utils.logs.FBLogEvents;
import com.yanhamer.app_utils.service.MyFirebaseMessagingService;
import com.yanhamer.app_utils.update.InAppUpdate;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
