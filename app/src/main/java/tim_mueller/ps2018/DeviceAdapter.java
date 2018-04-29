package tim_mueller.ps2018;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by marcrauch on 26.04.18.
 */

public class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {

    public DeviceAdapter(@NonNull Context context, ArrayList<BluetoothDevice> devices) {
        super(context,R.layout.listitem,devices);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater mInfalater = LayoutInflater.from(getContext());
        View listElement = mInfalater.inflate(R.layout.listitem, parent,false);

        BluetoothDevice singleItem = getItem(position);
        TextView name = (TextView) listElement.findViewById(R.id.textViewName);
        TextView addr = (TextView) listElement.findViewById(R.id.textViewAddr);

        name.setText(singleItem.getName());
        addr.setText(singleItem.getAddress());
        return listElement;
    }
}
