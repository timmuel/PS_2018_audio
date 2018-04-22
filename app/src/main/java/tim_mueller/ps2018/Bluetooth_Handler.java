package tim_mueller.ps2018;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tim on 12/04/18.
 */

public class Bluetooth_Handler{

    ListView deviceview;
    boolean mScanning;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mBluetoothLeScanner;
    public Bluetooth_Handler(ListView listView, BluetoothAdapter ba){
        deviceview = listView;
        mBluetoothAdapter = ba;
    }

    ArrayList<String> mDeviceList = new ArrayList<String>();

    public ArrayList<String> discover(Context context){
        mDeviceList.clear();

        List<ScanFilter> filters = new ArrayList<>();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();
        HashMap mScanResults = new HashMap<>();
        BtleScanCallback mScanCallback = new BtleScanCallback(mScanResults);

        mScanCallback = new BtleScanCallback(mScanResults);
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
        mScanning = true;
        return mDeviceList;
    }

    public void closereceiver(Context context){

        context.unregisterReceiver(mReceiver);

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if(deviceName != null && !mDeviceList.contains(deviceName + " , " + deviceHardwareAddress)) mDeviceList.add(deviceName + " , " + deviceHardwareAddress);
                ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, mDeviceList);
                deviceview.setAdapter(adapter);
                if (deviceName != null) Log.i("BT", deviceName);
            }
        }
    };
};
