package org.marco45.polarheartmonitor;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * This thread to the connection with the bluetooth device
 * @author Marco
 *
 */
public class ConnectThread extends Thread {
	BluetoothAdapter mBluetoothAdapter;
    private final BluetoothSocket mmSocket;
    MainActivity ac;
    public ConnectThread(BluetoothDevice device, MainActivity ac) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
    	this.ac=ac;
        BluetoothSocket tmp = null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
 
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
        	UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
        
    }
 
    public void run() {
        // Cancel discovery because it will slow down the connection
       // mBluetoothAdapter.cancelDiscovery();
 
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
        	if(mmSocket.isConnected())
        		mmSocket.close();
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
        	ac.connectionError();
        	Log.d("Error Bluetooth",connectException.toString());
        	        	
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }
 
        // Do work to manage the connection (in a separate thread)
        while (true){
        	try {
				DataHandler.getInstance().acqui(mmSocket.getInputStream().read());
			} catch (IOException e) {
				ac.connectionError();
	        	Log.d("Error Bluetooth",e.toString());
	        	        	
	            try {
	            	mmSocket.getInputStream().close();
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
			}
        }
    }
 
    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
    	
        try {
        	if(mmSocket!=null && mmSocket.isConnected())
        		mmSocket.close();
        } catch (IOException e) { }
    }
}