package tim_mueller.ps2018;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by marcrauch on 19.04.18.
 */

class BtleScanCallback extends ScanCallback{
    private HashMap mScanResults;
    private DeviceAdapter mAdapteter;

    public BtleScanCallback(HashMap sr){
        mScanResults = sr;
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
        if(!mScanResults.containsKey(deviceAddress)){
            mScanResults.put(deviceAddress, device);
            Log.i("BTS", "Device found");
            Log.i("BTS", device.getName());
            Log.i("BTS", device.getAddress());
            mAdapteter = new DeviceAdapter(mScanResults);

        }else{
            mScanResults.replace(deviceAddress,device);
        }
    }
};
