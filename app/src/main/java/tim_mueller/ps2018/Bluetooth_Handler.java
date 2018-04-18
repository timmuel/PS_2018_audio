package tim_mueller.ps2018;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Tim on 12/04/18.
 */

public class Bluetooth_Handler{

    ArrayList<String> mDeviceList = new ArrayList<String>();
    private ListView listView;

    public ArrayList<String> discover(Context context) {

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.i("BT", "Stopped previous discoveries!");
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter);
        try {
            mBluetoothAdapter.startDiscovery();
            Log.i("BT", "discovery started!");
        } catch (Exception e) {
            Log.i("BT", "couldn't start discovery!");
        }

        return mDeviceList;

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                Log.i("BT", "gaygaygaygaygaygay");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if (deviceName != null) mDeviceList.add(deviceName);
                if (deviceHardwareAddress != null) mDeviceList.add(deviceHardwareAddress);
                ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, mDeviceList);
                listView.setAdapter(adapter);
                if (deviceName != null) Log.i("BT", deviceName);
            }
        }
    };
};
