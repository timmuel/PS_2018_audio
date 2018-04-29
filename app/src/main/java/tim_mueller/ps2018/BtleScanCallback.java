package tim_mueller.ps2018;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcrauch on 19.04.18.
 */

class BtleScanCallback extends ScanCallback{
    private ArrayList<BluetoothDevice> mDeviceList;
    private DeviceAdapter mAdapter;
    private Context mContext;

    public BtleScanCallback(ArrayList deviceList, Context context){
        mContext = context;
        mDeviceList = deviceList;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {         // Single Scan result -> add to hashmap
        if(result.getDevice().getName()!=null) {
            addScanResult(result);
        }
    }
    @Override
    public void onBatchScanResults(List<ScanResult> results) {              // Multiple Scan results -> add all to hashmap
        for (ScanResult result : results) {
            if(result.getDevice().getName()!=null) {
                addScanResult(result);
            }
        }
    }
    @Override
    public void onScanFailed(int errorCode) {
        Log.e("BTS", "BLE Scan Failed with code " + errorCode);
    }
    private void addScanResult(ScanResult result) {                          // Add result to hashmap if not already contained
        BluetoothDevice device = result.getDevice();
        String deviceAddress = device.getAddress();
        if(!mDeviceList.contains(device)){
            mDeviceList.add(device);
            Log.i("BTS", "Device found");
            Log.i("BTS", device.getName());
            Log.i("BTS", device.getAddress());
            mAdapter = new DeviceAdapter(mContext,mDeviceList);
            ListView listView = (ListView) ((Activity) mContext).findViewById(R.id.listView);
            listView.setAdapter(mAdapter);
        }else{
            mDeviceList.remove(device);
            mDeviceList.add(device);
        }
    }
};
