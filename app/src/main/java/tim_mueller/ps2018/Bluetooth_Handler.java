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

    boolean mScanning;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mBluetoothLeScanner;
    Context mContext;

    public Bluetooth_Handler(BluetoothAdapter ba, Context context){
        mBluetoothAdapter = ba;
        mContext = context;
    }

    ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    public void discover(Context context){
        Log.i("BTH", "Discover started");
        mDeviceList.clear();

        List<ScanFilter> filters = new ArrayList<>();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();
        BtleScanCallback mScanCallback = new BtleScanCallback(mDeviceList,mContext);

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
        mScanning = true;
    }

    public void closereceiver(Context context){

        //TODO: close scanner

    }
};
