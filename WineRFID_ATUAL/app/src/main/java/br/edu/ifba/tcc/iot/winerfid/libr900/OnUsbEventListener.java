package br.edu.ifba.tcc.iot.winerfid.libr900;

/***********************************************************************************
* OnUsbEventListener revision history                                              *
*************+*************+********+***********************************************
* 2012.12.12	ver 1.0.0  	  eric     1. Generated(First release)                 *
************************************************************************************/

import android.hardware.usb.UsbDevice;

public interface OnUsbEventListener
{
	//--- synch functions.
	void onUsbConnected(UsbDevice device);
	void onUsbDisconnected(UsbDevice device);
	void onUsbConnectFail(UsbDevice device, String msg);
	/*
    abstract void onBtFoundNewDevice( BluetoothDevice device );
    abstract void onBtScanCompleted();
    abstract void onBtConnected( BluetoothDevice device );
    abstract void onBtDisconnected( BluetoothDevice device );
    abstract void onBtConnectFail( BluetoothDevice device, String msg );
    abstract void onBtDataSent( byte[] data );
    abstract void onBtDataTransException( BluetoothDevice device, String msg );
    */
    //--- asynch function.
	void onNotifyUsbDataRecv();
}