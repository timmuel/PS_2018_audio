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
    private DspCom mDspCom;


    public Bluetooth_Handler(BluetoothAdapter ba, Context context){
        mBluetoothAdapter = ba;
        mContext = context;
        mConnected = false;
    }

    public void setDsp(DspCom com){                                                                                           // Set the Dsp comm object
        mDspCom = com;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt,status,newState);
            if(newState == BluetoothProfile.STATE_CONNECTED){                                                       // Connected to new device
                ((Activity)mContext).runOnUiThread(new Runnable() {                                                  //Run text update on ui thread
                    @Override
                    public void run() {
                        TextView connectedText = (TextView) ((Activity) mContext).findViewById(R.id.textView);      // Set text on top of display
                        connectedText.setText("Connected To: " + mConnectedDevice.getName());
                        TextView statusText = (TextView) ((Activity) mContext).findViewById(R.id.textView2);
                        statusText.setText("Status: Connected");
                    }
                });
                Log.i("BT", "Connected to bluetooth Device Starting service discovery");
                mConnected = true;
                mBluetoothGatt.discoverServices();                                                                  // Looking for service of Serial of bluno
            }
            if(newState == BluetoothProfile.STATE_DISCONNECTED){                                                    // Called if a connected device gets disconnected
                ((Activity)mContext).runOnUiThread(new Runnable() {                                                  //Run text update on ui thread
                    @Override
                    public void run() {
                        TextView connectedText = (TextView) ((Activity) mContext).findViewById(R.id.textView);      // Update text on top of display
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
            BluetoothGattService service = gatt.getService(SERIAL_SERV);                                        // Get the service to communicate with bluno
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(SERIAL_CHAR);
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            mInitialized = gatt.setCharacteristicNotification(characteristic, true);                     // Subscribe to messages incoming from bluno
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {   // New message from Bluno
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] rec = characteristic.getValue();
            for(int i=0;i<rec.length;i++){                                                                       // Add all received bytes to com
                Log.i("BT","message recieved Byte" + i +": "+ (long)rec[i]);
            }
        }

    };


    public void discover(){                                                                                     // Start device discovery
        Log.i("BTH", "Discover started");
        mDeviceList.clear();

        List<ScanFilter> filters = new ArrayList<>();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();
        BtleScanCallback mScanCallback = new BtleScanCallback(mDeviceList,mContext);                            // Call if device found

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
        mScanning = true;
    }

    public void connect(int deviceId){
        mConnectedDevice =  mDeviceList.get(deviceId);
        mBluetoothGatt = mDeviceList.get(deviceId).connectGatt(mContext,false, mGattCallback);      // Connect to device with deviceid in arraylist
    }

    public void sendMessage(byte[] msg){                                                                        // Send an array of bytes to bluno (max 20 bytes)
        if(!mConnected || !mInitialized) return;
        BluetoothGattService service = mBluetoothGatt.getService(SERIAL_SERV);
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(SERIAL_CHAR);
        characteristic.setValue(msg);
        boolean success = mBluetoothGatt.writeCharacteristic(characteristic);
        Log.i("BT", "message sent"+success);
    }

    public boolean isConnected(){                                                                                 // Return connection state
        return mConnected;
    }

    public void closereceiver(Context context){

        //TODO: close scanner

    }
};
