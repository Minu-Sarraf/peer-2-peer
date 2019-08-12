package com.example.peer2peer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SalutDataCallback{
    String TAG = "MainActivity";
    private SalutDataReceiver dataReceiver;
    private SalutServiceData serviceData;
    SalutDevice possibleHost;
    Salut network;
    private SalutDevice deviceToSendTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataReceiver = new SalutDataReceiver(this, this);
        serviceData = new SalutServiceData("sas", 3000, "minu");

        checkNetwork();
        connectedDevices();
        networkAccess();
        register();
        message();
        toDevice();
    }
/*
    @Override
    public void onDataReceived(Object data) {
        Log.e(TAG, "data,." + data);
    }*/

    public void checkNetwork(){
        network = new Salut(dataReceiver, serviceData, new SalutCallback() {
            @Override
            public void call() {
                Log.e(TAG, "Sorry, but this device does not support WiFi Direct.");
            }
        }) {

            @Override
            public String serialize(Object o) {
                Log.e("serialize","se");
                return "okok";
            }
        };

    }

    public void connectedDevices(){
        Log.e(TAG, "discover");
        network.startNetworkService(new SalutDeviceCallback() {
            @Override
            public void call(SalutDevice device) {
                possibleHost = device;
                Log.e(TAG, device.readableName + " has connected!");
                Log.e(TAG, "registered clients" +  network.registeredClients);
            }
        });

    //boolean to indicate whether to call function after each device gets connected or not
        network.discoverNetworkServices(new SalutDeviceCallback() {
            @Override
            public void call(SalutDevice device) {
                deviceToSendTo = device;
                //deviceToSendTo = device.deviceName;
                Log.e(TAG, "A device has connected with the name " + device.deviceName + "found devices " + network.foundDevices.toString());
            }
        }, true);

    }

     private void networkAccess(){
       // final SalutDevice[] possibleHost = new SalutDevice[1];
        network.discoverNetworkServices(new SalutCallback() {
            @Override
            public void call() {
               // possibleHost[0] = network.foundDevices.get(0);
                Log.e(TAG, "Look at all these devices! " + network.foundDevices.toString());
            }
        }, true);



    }


    public void register(){
        network.registerWithHost(possibleHost, new SalutCallback() {
            @Override
            public void call() {
                Log.d(TAG, "We're now registered.");
            }
        }, new SalutCallback() {
            @Override
            public void call() {
                Log.d(TAG, "We failed to register.");
            }
        });
    }

    public void message(){
        Message myMessage = new Message();
        myMessage.description = "See you on the other side!";

        network.sendToAllDevices(myMessage, new SalutCallback() {
            @Override
            public void call() {
                Log.e(TAG, "Oh no! The data failed to send.");
            }
        });

    }

    public void toDevice(){
        Message myMessage = new Message();
        myMessage.description = "See you on the other side!";

        network.sendToDevice(deviceToSendTo, myMessage, new SalutCallback() {
            @Override
            public void call() {
                Log.e(TAG, "Oh no! The data failed to send.");
            }
        });

        network.sendToHost(myMessage, new SalutCallback() {
            @Override
            public void call() {
                Log.e(TAG, "Oh no! The data failed to send.");
            }
        });
    }


    @Override
    public void onDataReceived(Object data) {
        Log.d(TAG, "Received network data.");
        try
        {
            Message newMessage = (Message) LoganSquare.parse(String.valueOf((Message)data), Message.class);
            Log.d(TAG, newMessage.description);  //See you on the other side!
            //Do other stuff with data.
        }
        catch (IOException ex)
        {
            Log.e(TAG, "Failed to parse network data.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(possibleHost!=null)
            network.stopNetworkService(true);
        else
            network.unregisterClient(true);
    }


}
