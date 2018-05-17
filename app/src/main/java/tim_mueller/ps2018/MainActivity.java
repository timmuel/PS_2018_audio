package tim_mueller.ps2018;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swiper;
    private Bluetooth_Handler Handler;
    private ListView listView;
    private SeekBar volumeBar;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<String> mDeviceList = new ArrayList<String>();
    private DspCom dspCom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        volumeBar = findViewById(R.id.volume_bar);

        swiper = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);                     // Refresh scan on pulldown
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler.discover();
                Log.i("BT", "refreshing");
                swiper.setRefreshing(false);
            }
        });

        Spinner inputview = (Spinner) findViewById(R.id.input);
        // Create an adapter from the string array resource and use
        // android's inbuilt layout file simple_spinner_item
        // that represents the default spinner in the UI
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(this, R.array.inputs, android.R.layout.simple_spinner_item);
        // Set the layout to use for each dropdown item
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputview.setAdapter(adapter1);
        Spinner outputview = (Spinner) findViewById(R.id.output);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this, R.array.inputs, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        outputview.setAdapter(adapter2);

        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        int MY_PERMISSIONS_REQUEST_ACCESS_BLUETOOTH = 1;                                    //BLUETOOTH-BERECHTIGUNG EINHOLEN
        ActivityCompat.requestPermissions(this,                                     //DAS PASSIERT NUR BEIM STARTUP!
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN
                },
                MY_PERMISSIONS_REQUEST_ACCESS_BLUETOOTH);

        mBluetoothAdapter = mBluetoothManager.getAdapter();                               //BLUETOOTH ADAPTER INITIALISIEREN
        Handler = new Bluetooth_Handler(mBluetoothAdapter, this);
        dspCom = new DspCom(getApplicationContext(), Handler);
        Handler.setDsp(dspCom);                                                           // Set DspCom of handler

        if (!mBluetoothAdapter.isEnabled()) {                                             //CHECK, OB BLUETOOTH AKTIVIERT IST
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);         //FALLS NICHT, AUFFORDERUNG ZUM AKTVIEREN
            startActivity(enableBT);
        } else
            Log.i("BT", "Bluetooth enabled!");                                  //BLUETOOTH IST AKTIVIERT

        if (mBluetoothAdapter.isDiscovering()) {                                           //CHECKT, OB EINE BLUETOOTH-SUCHE LÄUFT
            mBluetoothAdapter.cancelDiscovery();                                           //FALLS JA, STOPPEN
            Log.i("BT", "Stopped previous discoveries!");
        }

        Handler.discover();                                                                // start Scan for devices

        listView.setOnItemClickListener(                                                   // List view connect to device if clicked
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                        Log.i("CLK", "Selected Item position: " + Integer.toString(position));
                        Handler.connect(position);
                    }
                }
        );

        inputview.setOnItemSelectedListener(                                                   // List view connect to device if clicked
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                        Log.i("CLK", "Selected Item position: " + Integer.toString(position));
                        dspCom.setInput(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        outputview.setOnItemSelectedListener(                                                   // List view connect to device if clicked
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                        Log.i("CLK", "Selected Item position: " + Integer.toString(position));
                        dspCom.setVolume(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );


        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {                    // Send new Volume to dsd
                                                 boolean refreshTimeout = false;
                                                 Timer timer = new Timer();

                                                 @Override
                                                 public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                     if (!refreshTimeout) {                                                                  // Send at most 10 times per second
                                                         refreshTimeout = true;
                                                         timer.schedule(new TimerTask() {
                                                             @Override
                                                             public void run() {
                                                                 refreshTimeout = false;
                                                             }
                                                         }, 100);
                                                         dspCom.setVolume(((float) progress) / 100.0f);
                                                     }
                                                 }

                                                 @Override
                                                 public void onStartTrackingTouch(SeekBar seekBar) {

                                                 }

                                                 @Override
                                                 public void onStopTrackingTouch(SeekBar seekBar) {
                                                     dspCom.setVolume(((float) seekBar.getProgress()) / 100.0f);                                   // Send if finger lifted
                                                 }
                                             }
        );



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}

