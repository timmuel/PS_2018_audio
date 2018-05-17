package tim_mueller.ps2018;

import android.content.Context;
import android.widget.Toast;

public class DspCom {
    // Communication protocol variables
    private static final byte STARTING_BYTE = (byte)0xff;

    private static final byte SET_VOLUME = 0x01;
    private static final byte SET_INPUT = 0x02;

    private static final int HEADER_SIZE = 2;
    private static final int MESSAGE_SIZE = 3;

    private Bluetooth_Handler mBluetoothHandler;
    private Context mContext;
    private int nrRecieved = 0;
    private byte[] dataReceived = new byte[3];
    Toast TOAST_VOLUME;
    Toast TOAST_INPUT;


    public DspCom(Context context, Bluetooth_Handler bluetooth_handler){
        mBluetoothHandler = bluetooth_handler;
        mContext = context;

        CharSequence TOAST_VOLUMEE = "VOLUME muss zwischen 0 und 1 sein!";
        int duration = Toast.LENGTH_SHORT;
        Toast TOAST_VOLUME = Toast.makeText(mContext, TOAST_VOLUMEE, duration);
        CharSequence TOAST_INPUTT = "Input muss zwischen 1 und 6 sein!";
        Toast TOAST_INPUT = Toast.makeText(mContext, TOAST_INPUTT, duration);
    }

    public void setVolume(float volume){                // Volume in percent
        if(volume >= 0 && volume <= 1) {
            byte[] msg = new byte[3];
            msg[0] = STARTING_BYTE;
            msg[1] = SET_VOLUME;
            msg[2] = (byte) (volume * 255);                   // Volume in byte precision
            if (!testConnected()) return;
            mBluetoothHandler.sendMessage(msg);
        }/* else
        {
            TOAST_INPUT.show();
        }
        */
    }

    private boolean testConnected(){                       // Call before sending
        boolean connected = mBluetoothHandler.isConnected();
        if(!connected)
            Toast.makeText(mContext,"not Connected to DSP",Toast.LENGTH_SHORT).show();
        return connected;
    }

    public void setInput(int input) {
        if (input > 0 && input < 7) {
            byte[] msg = new byte[3];
            msg[0] = STARTING_BYTE;
            msg[1] = SET_INPUT;
            msg[2] = (byte) input;                   // Selected input
            if (!testConnected()) return;
            mBluetoothHandler.sendMessage(msg);
        }/* else
        {
            TOAST_INPUT.show();
        }
        */
    }

    public void addReceived(byte recv){
        if(nrRecieved == 0 && recv != STARTING_BYTE) return;                                             // Message must start with 0xff
        dataReceived[nrRecieved] = recv;
        nrRecieved++;
        if(nrRecieved == MESSAGE_SIZE) handleReceived();
    }

    private void handleReceived(){
        if(dataReceived[0] != STARTING_BYTE) return;                                                    // Invalid format
    }
}
