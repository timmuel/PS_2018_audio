package tim_mueller.ps2018;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> mDeviceList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();        //BLUETOOTH ADAPTER INITIALISIEREN
        if (!mBluetoothAdapter.isEnabled()){                                              //CHECK, OB BLUETOOTH AKTIVIERT IST
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);         //FALLS NICHT, AUFFORDERUNG ZUM AKTVIEREN
            startActivity(enableBT);
        }

        else Log.i("BT", "Bluetooth enabled!");                                  //BLUETOOTH IST AKTIVIERT
        if (mBluetoothAdapter.isDiscovering()) {                                            //CHECKT, OB EINE BLUETOOTH-SUCHE LÄUFT
            mBluetoothAdapter.cancelDiscovery();                                            //FALLS JA, STOPPEN
            Log.i("BT", "Stopped previous discoveries!");
        }

        int MY_PERMISSIONS_REQUEST_ACCESS_BLUETOOTH = 1;                                    //BLUETOOTH-BERECHTIGUNG EINHOLEN
        ActivityCompat.requestPermissions(this,                                     //DAS PASSIERT NUR BEIM STARTUP!
                new String[]{Manifest.permission.BLUETOOTH},
                MY_PERMISSIONS_REQUEST_ACCESS_BLUETOOTH);

        Bluetooth_Handler help = new Bluetooth_Handler();
        help.discover(this);


        /*
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);               //MITBEKOMMEN, FALLS GERÄTE GEFUNDEN WERDEN
        registerReceiver(mReceiver, filter);
        try {
            mBluetoothAdapter.startDiscovery();                                             //SUCHE NACH GERÄTEN STARTEN
            Log.i("BT", "discovery started!");
        }
        catch (Exception e) {
            Log.i("BT", "couldn't start discovery!");
        }
        */
        System.out.println(mDeviceList);

    }


/*
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if(deviceName!= null) mDeviceList.add(deviceName);
                ArrayAdapter adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, mDeviceList);
                listView.setAdapter(adapter);
                if(deviceName != null) Log.i("BT", deviceName);
            }
        }
    };

*/

    @Override
    protected void onDestroy() {
        /*unregisterReceiver(mReceiver);*/
        super.onDestroy();
    }


}
