package com.example.rohan.ecg;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class Details extends ActionBarActivity implements View.OnClickListener {
    ImageButton b1, b2, b3;
    SharedPreferences sharedPreferences;
    String docmail, docnum, myname;
    Button a1, a2, a3, a4;


    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;
    Set<BluetoothDevice> pairedDevice;
    String address = "94:00:70:06:E3:74";
    android.os.Handler bluetoothIn;
    int HandlerState = 0;
    UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ConnectedThread connectedThread;


    XYPlot plot;
    SimpleXYSeries something;
    int max_size = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        plot = (XYPlot) findViewById(R.id.ecgPlot);

        something = new SimpleXYSeries("ECG");
        something.useImplicitXVals();
        plot.setRangeBoundaries(0, 250, BoundaryMode.FIXED);
        plot.setDomainBoundaries(0, 20, BoundaryMode.FIXED);
//        LineAndPointFormatter formatter1 = new LineAndPointFormatter(
//                Color.rgb(0, 0, 0), null, null, null);
//        formatter1.getLinePaint().setStrokeJoin(Paint.Join.ROUND);
//        formatter1.getLinePaint().setStrokeWidth(10);
        plot.addSeries(something, new LineAndPointFormatter(Color.rgb(0, 0, 1), null, null, null));
        plot.setDomainStepValue(5);
        plot.setTicksPerRangeLabel(3);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        a1 = (Button) findViewById(R.id.button);
        a2 = (Button) findViewById(R.id.button2);
        a3 = (Button) findViewById(R.id.button3);
        a4 = (Button) findViewById(R.id.button4);
        b1 = (ImageButton) findViewById(R.id.imageButton);
        b2 = (ImageButton) findViewById(R.id.imageButton2);
        b3 = (ImageButton) findViewById(R.id.map);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        sharedPreferences = getSharedPreferences("Yes", Context.MODE_PRIVATE);
        myname = sharedPreferences.getString("myname", "Invalid");
        docmail = sharedPreferences.getString("docmail", "Invalid");
        docnum = sharedPreferences.getString("docnumber", "Invalid");

        a1.setText("Enable bluetooth");
        a1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent on = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(on, 5);
                } else {
                    Toast.makeText(Details.this, "Already on", Toast.LENGTH_SHORT).show();
                }

            }
        });

        a2.setText("Pair");
        a2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevice = bluetoothAdapter.getBondedDevices();
                for (BluetoothDevice device : pairedDevice) {
                    if (device.getName().equals("HC-05")) {
                        address = device.getAddress();
                        Toast.makeText(Details.this, address, Toast.LENGTH_SHORT).show();
                        bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
                        try {
                            bluetoothSocket = createBluetoothSocket(bluetoothDevice);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            bluetoothSocket.connect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        connectedThread = new ConnectedThread(bluetoothSocket);
                        connectedThread.start();

                    }
                }


            }
        });


        a3.setText("Send A");
        a3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("A");

            }
        });

        a4.setText("Send S");
        a4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("S");

            }
        });

        bluetoothIn = new Handler() {

            public void HandleMessage(Message msg) {
                if (msg.what == HandlerState) {
                }
            }

        };



    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton:
                Intent in = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ docnum ));
                startActivity(in);
                break;

            case R.id.imageButton2:
                String abc = "file://" + Environment.getExternalStorageDirectory() + "/ECG/ecg.txt";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"rohan.carmel@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "ECG Report");
                intent.putExtra(Intent.EXTRA_TEXT, "PFA");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(abc));
                intent.setType("*/*");
                startActivity(intent);
                break;

            case R.id.map:
                String search = "geo:0,0?q=nearby hospital";
                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(search));
                startActivity(intent1);

                break;

        }
    }
    public class ConnectedThread extends Thread {
        InputStream inputStream;
        OutputStream outputStream;
        FileWriter writer;

        public ConnectedThread(BluetoothSocket bluetoothSocket1) {
            try {
                inputStream = bluetoothSocket1.getInputStream();
                outputStream = bluetoothSocket1.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try
            {
                File root = new File(Environment.getExternalStorageDirectory(), "ECG");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File gpxfile = new File(root, "ecg.txt");
                writer = new FileWriter(gpxfile);
                writer.append("PFA report of "+myname+"\n");
//                writer.flush();
//                writer.close();
//                Toast.makeText(MainActivity4.this, "Saved", Toast.LENGTH_SHORT).show();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

        }

        public void run() {

            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {

                    bytes = inputStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    int xyz = buffer[0];
                    writer.append(xyz+" ");
//                    writer.flush();
//                    writer.close();


                    Log.d("tag", readMessage + " xyz" + xyz);




                    if (something.size() > max_size) {
                        something.removeFirst();
                    }
                    something.addLast(null, xyz);
                    plot.redraw();
                    bluetoothIn.obtainMessage(HandlerState, bytes, -1, readMessage).sendToTarget();



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public void write(String input) {
            byte[] msg = input.getBytes();
            try {
                outputStream.write(msg);
                if (input.equals("S")){
                    writer.flush();
                    writer.close();

                }

            } catch (IOException e) {
                Toast.makeText(Details.this, "Failed to send", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

    }


}

