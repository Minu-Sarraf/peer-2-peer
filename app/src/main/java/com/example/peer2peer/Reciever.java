package com.example.peer2peer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.peak.salut.Callbacks.SalutDataCallback;

public class Reciever extends AppCompatActivity implements SalutDataCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciever);
    }

    @Override
    public void onDataReceived(Object data) {

    }
}
