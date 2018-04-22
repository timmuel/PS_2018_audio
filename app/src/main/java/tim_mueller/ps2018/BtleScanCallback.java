package tim_mueller.ps2018;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

/**
 * Created by marcrauch on 19.04.18.
 */

class BtleScanCallback extends ScanCallback{
    private HashMap mScanResults;

    public BtleScanCallback(HashMap sr){
        mScanResults = sr;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        addScanResult(result);
        Log.i("BTS", "Device found");
        Log.i("BTS", result.toString());
    }
    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        Log.i("BTS", "Devices found");
        for (ScanResult result : results) {
            addScanResult(result);
        }
    }
    @Override
    public void onScanFailed(int errorCode) {
        Log.e("BTS", "BLE Scan Failed with code " + errorCode);
    }
    private void addScanResult(ScanResult result) {
        BluetoothDevice device = result.getDevice();
        String deviceAddress = device.getAddress();
        mScanResults.put(deviceAddress, device);
    }
};
