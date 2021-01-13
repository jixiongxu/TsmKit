package com.tms.threadswitch.simple;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.tms.threadswitch.tsm_android.TsmKit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @TsmKit(main = true)
    public void init() {
        Log.d("MainActivity:", "init" + Thread.currentThread().getName());
    }
}