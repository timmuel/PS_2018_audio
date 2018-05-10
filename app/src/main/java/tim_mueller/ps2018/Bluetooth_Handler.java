package tim_mueller.ps2018;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tim on 12/04/18.
 */

public class Bluetooth_Handler{

    private boolean mScanning;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothGatt mBluetoothGatt;
    private boolean mConnected = false;
    private boolean mInitialized = false;
    private BluetoothDevice mConnectedDevice;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    private Context mContext;
    private UUID SERIAL_SERV = UUID.fromString("0000dfb0-0000-1000-8000-00805f9b34fb");
    private UUID SERIAL_CHAR = UUID.fromString("0000dfb1-0000-1000-8000-00805f9b34fb");


    public Bluetooth_Handler(BluetoothAdapter ba, Context context){
        mBluetoothAdapter = ba;
        mContext = context;
        mConnected = false;
    }


    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt,status,newState);
            if(newState == BluetoothProfile.STATE_CONNECTED){
                ((Activity)mContext).runOnUiThread(new Runnable() {                                                  //Run text update on ui thread
                    @Override
                    public void run() {
                        TextView connectedText = (TextView) ((Activity) mContext).findViewById(R.id.textView);
                        connectedText.setText("Connected To: " + mConnectedDevice.getName());
                        TextView statusText = (TextView) ((Activity) mContext).findViewById(R.id.textView2);
                        statusText.setText("Status: Connected");
                    }
                });
                Log.i("BT", "Connected to bluetooth Device Starting service discovery");
                mConnected = true;
                mBluetoothGatt.discoverServices();
            }
            if(newState == BluetoothProfile.STATE_DISCONNECTED){
                ((Activity)mContext).runOnUiThread(new Runnable() {                                                  //Run text update on ui thread
                    @Override
                    public void run() {
                        TextView connectedText = (TextView) ((Activity) mContext).findViewById(R.id.textView);
                        connectedText.setText("Connected To: ");
                        TextView statusText = (TextView) ((Activity) mContext).findViewById(R.id.textView2);
                        statusText.setText("Status: Not Connected");
                    }
                });
                Log.i("BT", "Disconnected from bluetooth Device");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                return;
            }
            BluetoothGattService service = gatt.getService(SERIAL_SERV);
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(SERIAL_CHAR);
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            mInitialized = gatt.setCharacteristicNotification(characteristic, true);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] rec = characteristic.getValue();
            Log.i("BT","message recieved Byte1:"+ rec[0] + " Byte2: " + rec[1] + " Byte3:" + rec[2]);
        }

    };


    public void discover(){
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

    public void connect(int deviceId){
        mConnectedDevice =  mDeviceList.get(deviceId);
        mBluetoothGatt = mDeviceList.get(deviceId).connectGatt(mContext,false, mGattCallback);
    }

    public void sendMessage(byte[] msg){
        if(!mConnected || !mInitialized) return;
        BluetoothGattService service = mBluetoothGatt.getService(SERIAL_SERV);
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(SERIAL_CHAR);
        characteristic.setValue(msg);
        boolean success = mBluetoothGatt.writeCharacteristic(characteristic);
        Log.i("BT", "message sent"+success);
    }

    public boolean isConnected(){
        return mConnected;
    }

    public void closereceiver(Context context){

        //TODO: close scanner

    }
};
