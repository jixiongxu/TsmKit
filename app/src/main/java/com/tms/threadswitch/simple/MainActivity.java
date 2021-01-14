package com.tms.threadswitch.simple;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.tms.android.TsmKit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init("onCreate");
    }

    @TsmKit(main = true)
    public void init(String arg) {
        Log.d("MainActivity:", "init" + Thread.currentThread().getName() + arg);
    }
}