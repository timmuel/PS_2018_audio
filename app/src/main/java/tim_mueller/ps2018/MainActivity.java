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

        swiper = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler.discover();
                Log.i("BT", "refreshing");
                swiper.setRefreshing(false);
            }
        });

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
        Handler = new Bluetooth_Handler(mBluetoothAdapter,this);
        dspCom = new DspCom(getApplicationContext(),Handler);
        if (!mBluetoothAdapter.isEnabled()) {                                             //CHECK, OB BLUETOOTH AKTIVIERT IST
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);         //FALLS NICHT, AUFFORDERUNG ZUM AKTVIEREN
            startActivity(enableBT);
        } else
            Log.i("BT", "Bluetooth enabled!");                                  //BLUETOOTH IST AKTIVIERT

        if (mBluetoothAdapter.isDiscovering()) {                                            //CHECKT, OB EINE BLUETOOTH-SUCHE LÄUFT
            mBluetoothAdapter.cancelDiscovery();                                            //FALLS JA, STOPPEN
            Log.i("BT", "Stopped previous discoveries!");
        }

        Handler.discover();

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                        Log.i("CLK","Selected Item position: "+Integer.toString(position));
                        Handler.connect(position);
                    }
                }
        );


        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean refreshTimeout = false;
            Timer timer = new Timer();
             @Override
             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                 if(!refreshTimeout) {                                                                  // Send at most 10 times per second
                     refreshTimeout = true;
                     timer.schedule(new TimerTask() {
                         @Override
                         public void run() {
                             refreshTimeout = false;
                         }
                     }, 100);
                     dspCom.setVolume(((float)progress)/100.0f);
                 }
             }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
             public void onStopTrackingTouch(SeekBar seekBar) {
                 dspCom.setVolume(((float)seekBar.getProgress())/100.0f);        // Send if finger lifted
             }
         }
        );

    }

        @Override
        protected void onDestroy () {
            super.onDestroy();
        }


    }

