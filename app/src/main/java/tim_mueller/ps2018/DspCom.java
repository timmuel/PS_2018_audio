package tim_mueller.ps2018;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DspCom {
    // Communication protocol variables
    public static final byte STARTING_BYTE = (byte)0xff;

    public static final byte SET_VOLUME = 0x01;
    public static final byte SET_INPUT = 0x02;
    public static final byte SET_OUTPUT = 0x03;
    public static final byte GET_VOLUME = 0x04;
    public static final byte GET_INPUT = 0x05;
    public static final byte GET_OUTPUT = 0x06;


    public static final int HEADER_SIZE = 2;
    public static final int MESSAGE_SIZE = 3;


    private int nrResync = 0;
    private Bluetooth_Handler mBluetoothHandler;
    private Context mContext;
    private int nrRecieved = 0;
    private byte[] dataReceived = new byte[3];
    Toast TOAST_VOLUME;
    Toast TOAST_INPUT;

    SeekBar volumeBar;
    Spinner spinner1;
    Spinner spinner2;
    TextView inputjetzt;
    TextView outputjetzt;
    TextView volumejetzt;


    public DspCom(Context context, Bluetooth_Handler bluetooth_handler){
        mBluetoothHandler = bluetooth_handler;
        mContext = context;

        inputjetzt = (TextView) ((Activity) mContext).findViewById(R.id.inputcurrent);
        outputjetzt = (TextView) ((Activity) mContext).findViewById(R.id.outputcurrent);
        volumejetzt = (TextView) ((Activity) mContext).findViewById(R.id.volumecurrent);
        volumeBar = (SeekBar) ((Activity) mContext).findViewById(R.id.volume_bar);
        spinner1 = (Spinner) ((Activity) mContext).findViewById(R.id.input);
        spinner2 = (Spinner) ((Activity) mContext).findViewById(R.id.output);


        CharSequence TOAST_VOLUMEE = "VOLUME muss zwischen 0 und 1 sein!";
        int duration = Toast.LENGTH_SHORT;
        TOAST_VOLUME = Toast.makeText(mContext, TOAST_VOLUMEE, duration);
        CharSequence TOAST_INPUTT = "Input muss zwischen 1 und 6 sein!";
        TOAST_INPUT = Toast.makeText(mContext, TOAST_INPUTT, duration);
    }

    public void setVolume(float volume){                // Volume in percent
        if(volume >= 0 && volume <= 1) {
            byte[] msg = new byte[3];
            msg[0] = STARTING_BYTE;
            msg[1] = SET_VOLUME;
            msg[2] = (byte) (255 * Math.pow(10,volume)/10);                   // Volume in byte precision
            Log.i("FAG",Float.toString(volume));
            if (!testConnected()) return;
            mBluetoothHandler.sendMessage(msg);
        }else
        {
            TOAST_INPUT.show();
        }
    }

    public void getVolume(){                // Volume in percent

            byte[] msg = new byte[3];
            msg[0] = STARTING_BYTE;
            msg[1] = GET_VOLUME;
            msg[2] = (byte) 0;                   // Volume in byte precision
            if (!testConnected()) return;
            mBluetoothHandler.sendMessage(msg);
    }

    private boolean testConnected(){                       // Call before sending
        boolean connected = mBluetoothHandler.isConnected();
        if(!connected)
            Toast.makeText(mContext,"not Connected to DSP",Toast.LENGTH_SHORT).show();
        return connected;
    }

    public void setInput(int input) {
        if (input >= 0 && input <= 3) {
            byte[] msg = new byte[3];
            msg[0] = STARTING_BYTE;
            msg[1] = SET_INPUT;
            msg[2] = (byte) input;                   // Selected input
            if (!testConnected()) return;
            mBluetoothHandler.sendMessage(msg);
        } else
        {
            TOAST_INPUT.show();
        }
    }

    public void getInput() {
            byte[] msg = new byte[3];
            msg[0] = STARTING_BYTE;
            msg[1] = GET_INPUT;
            msg[2] = (byte) 0;                   // Selected input
            if (!testConnected()) return;
            mBluetoothHandler.sendMessage(msg);
    }

    public void setOutput(int output) {
        if (output >= 0 && output <= 1) {
            byte[] msg = new byte[3];
            msg[0] = STARTING_BYTE;
            msg[1] = SET_OUTPUT;
            msg[2] = (byte) output;                   // Selected output
            if (!testConnected()) return;
            mBluetoothHandler.sendMessage(msg);
        } else
        {
            TOAST_INPUT.show();
        }
    }

    public void getOutput() {
            byte[] msg = new byte[3];
            msg[0] = STARTING_BYTE;
            msg[1] = GET_OUTPUT;
            msg[2] = (byte) 0;                   // Selected output
            if (!testConnected()) return;
            mBluetoothHandler.sendMessage(msg);
    }

    public void addReceived(byte recv){
        if(nrRecieved == 0 && recv!=0xff ){
            Log.i("BT","not in sync!!");
        }
        dataReceived[nrRecieved] = recv;
        nrRecieved++;
        if(nrRecieved == MESSAGE_SIZE){
            nrRecieved = 0;
            handleReceived();
        }
    }

    private void handleReceived(){
        if(dataReceived[0] != STARTING_BYTE) return;                                                    // Invalid format -> get back in sync
        if(dataReceived[1] == SET_VOLUME){
            ((Activity)mContext).runOnUiThread(new Runnable() {                                                  //Run text update on ui thread
                @Override
                public void run() {
                    volumejetzt.setText(Integer.toString((int) (100 * Math.log10((float)Byte.toUnsignedLong(dataReceived[2])/255 * 10))));
                }
            });
        }
        if(dataReceived[1] == SET_INPUT){
            ((Activity)mContext).runOnUiThread(new Runnable() {                                                  //Run text update on ui thread
                @Override
                public void run() {
                    spinner1.setSelection(dataReceived[2]);
                    inputjetzt.setText(Integer.toString(Byte.toUnsignedInt(dataReceived[2])+1));
                }
            });
        }
        if(dataReceived[1] == SET_OUTPUT){
            ((Activity)mContext).runOnUiThread(new Runnable() {                                                  //Run text update on ui thread
                @Override
                public void run() {
                    outputjetzt.setText(Integer.toString(Byte.toUnsignedInt(dataReceived[2])+1));
                    spinner2.setSelection(dataReceived[2]);
                }
            });
        }
        if(dataReceived[1] == GET_VOLUME){
            volumeBar.setProgress(Byte.toUnsignedInt(dataReceived[2])*100/255);
            ((Activity)mContext).runOnUiThread(new Runnable() {                                                  //Run text update on ui thread
                @Override
                public void run() {
                    volumejetzt.setText(Integer.toString((int) (100 * Math.log10((float)Byte.toUnsignedLong(dataReceived[2])/255 * 10))));
                }
            });

        }
        if(dataReceived[1] == GET_INPUT){
            ((Activity)mContext).runOnUiThread(new Runnable() {                                                  //Run text update on ui thread
                @Override
                public void run() {
                    spinner1.setSelection(dataReceived[2]);
                    inputjetzt.setText(Integer.toString(Byte.toUnsignedInt(dataReceived[2])+1));
                }
            });
        }
        if(dataReceived[1] == GET_OUTPUT){
            ((Activity)mContext).runOnUiThread(new Runnable() {                                                  //Run text update on ui thread
                @Override
                public void run() {
                    spinner2.setSelection(dataReceived[2]);
                    outputjetzt.setText(Integer.toString(Byte.toUnsignedInt(dataReceived[2])+1));
                }
            });
        }
    }


}
