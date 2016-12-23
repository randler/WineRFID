package br.edu.ifba.tcc.iot.winerfid.libr900;

import android.bluetooth.BluetoothDevice;

public interface OnBtEventListener
{
	//--- synch functions.
    void onBtFoundNewDevice(BluetoothDevice device);
    void onBtScanCompleted();
    void onBtConnected(BluetoothDevice device);
    void onBtDisconnected(BluetoothDevice device);
    void onBtConnectFail(BluetoothDevice device, String msg);
    void onBtDataSent(byte[] data);
    void onBtDataTransException(BluetoothDevice device, String msg);

    //--- asynch function.
    void onNotifyBtDataRecv();

    //Andorid 6.0 permission
    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
}


